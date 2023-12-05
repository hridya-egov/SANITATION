package org.egov.pqm.service;

import static org.egov.pqm.util.Constants.*;
import static org.egov.pqm.util.ErrorConstants.*;
import static org.egov.pqm.util.MDMSUtils.parseJsonToTestList;
import static org.egov.pqm.web.model.Pagination.SortOrder.DESC;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import net.minidev.json.JSONArray;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.repository.TestRepository;
import org.egov.pqm.util.Constants;
import org.egov.pqm.util.ErrorConstants;
import org.egov.pqm.util.MDMSUtils;
import org.egov.pqm.validator.MDMSValidator;
import org.egov.pqm.validator.PqmValidator;
import org.egov.pqm.web.model.Document;
import org.egov.pqm.web.model.DocumentResponse;
import org.egov.pqm.web.model.Pagination;
import org.egov.pqm.web.model.QualityCriteria;
import org.egov.pqm.web.model.SortBy;
import org.egov.pqm.web.model.SourceType;
import org.egov.pqm.web.model.Test;
import org.egov.pqm.web.model.TestRequest;
import org.egov.pqm.web.model.TestResponse;
import org.egov.pqm.web.model.TestResultStatus;
import org.egov.pqm.web.model.TestSearchCriteria;
import org.egov.pqm.web.model.TestSearchRequest;
import org.egov.pqm.web.model.mdms.MdmsTest;
import org.egov.pqm.web.model.workflow.BusinessService;
import org.egov.pqm.workflow.ActionValidator;
import org.egov.pqm.workflow.WorkflowIntegrator;
import org.egov.pqm.workflow.WorkflowService;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PqmService {

  @Autowired
  private WorkflowIntegrator workflowIntegrator;

  @Autowired
  private WorkflowService workflowService;

  @Autowired
  private ActionValidator actionValidator;

  @Autowired
  private TestRepository repository;

  @Autowired
  private EnrichmentService enrichmentService;

  @Autowired
  private PqmValidator pqmValidator;

  @Autowired
  private MDMSUtils mdmsUtils;

  @Autowired
  private ServiceConfiguration config;
  
  @Autowired
  private QualityCriteriaEvaluationService qualityCriteriaEvaluation;

  @Autowired
  private MDMSValidator mdmsValidator;

  /**
   * search the PQM applications based on the search criteria
   *
   * @param criteria
   * @param requestInfo
   * @return
   */
  public TestResponse testSearch(TestSearchRequest criteria, RequestInfo requestInfo) {

    List<Test> testList = new LinkedList<>();

    if (requestInfo.getUserInfo()!=null &&requestInfo.getUserInfo().getType().equalsIgnoreCase("Employee")) {
      checkRoleInValidateSearch(criteria, requestInfo);
    }
    TestResponse testResponse = repository.getPqmData(criteria);
    List<String> idList = testResponse.getTests().stream().map(Test::getTestId)
        .collect(Collectors.toList());

    List<QualityCriteria> qualityCriteriaList = repository.getQualityCriteriaData(idList);

    testList = testResponse.getTests().stream().map(test -> {
      List<QualityCriteria> QualityCriterias = qualityCriteriaList.stream()
          .filter(qualityCriteria -> test.getTestId().equalsIgnoreCase(qualityCriteria.getTestId()))
          .collect(Collectors.toList());
      test.setQualityCriteria(QualityCriterias);
      return test;
    }).collect(Collectors.toList());

    DocumentResponse documentResponse = repository.getDocumentData(idList);
    List<Document> documentList = documentResponse.getDocuments();

    testList = testResponse.getTests().stream().map(test -> {
      List<Document> documents = documentList.stream()
          .filter(document -> test.getTestId().equalsIgnoreCase(document.getTestId()))
          .collect(Collectors.toList());
      test.setDocuments(documents);
      return test;
    }).collect(Collectors.toList());

    return testResponse;

  }

  private void checkRoleInValidateSearch(TestSearchRequest criteria, RequestInfo requestInfo) {
    List<Role> roles = requestInfo.getUserInfo().getRoles();
    TestSearchCriteria testSearchCriteria = criteria.getTestSearchCriteria();
    List<String> masterNameList = new ArrayList<>();
    masterNameList.add(null);
    if (roles.stream().anyMatch(role -> Objects.equals(role.getCode(), Constants.FSTPO_EMPLOYEE))) {

    }

  }


  /**
   * Creates Test
   *
   * @param testRequest The Create Request
   * @return New Test
   */
  public Test create(TestRequest testRequest) {
    if(Objects.isNull(testRequest.getTests()) || testRequest.getTests().isEmpty() )
      throw new CustomException(TEST_NOT_PRESENT_CODE, TEST_NOT_PRESENT_MESSAGE);
    pqmValidator.validateTestTypeAdhocCreate(testRequest);
    pqmValidator.validateTestCriteriaAndDocument(testRequest);
    mdmsValidator.validateMdmsData(testRequest);
    enrichmentService.enrichPQMCreateRequest(testRequest);
    qualityCriteriaEvaluation.evalutateQualityCriteria(testRequest);
    enrichmentService.setTestResultStatus(testRequest);
    enrichmentService.pushToAnomalyDetectorIfTestResultStatusFail(testRequest);
    repository.save(testRequest);
    return testRequest.getTests().get(0);
  }


  public Test createTestViaScheduler(TestRequest testRequest) {
    pqmValidator.validateTestTypeScheduleCreateAndUpdate(testRequest);
    pqmValidator.validateTestCriteriaAndDocument(testRequest);
    mdmsValidator.validateMdmsData(testRequest);
    enrichmentService.enrichPQMCreateRequestForLabTest(testRequest);
    qualityCriteriaEvaluation.validateQualityCriteriaResult(testRequest);
    workflowIntegrator.callWorkFlow(testRequest);
    repository.save(testRequest);
    return testRequest.getTests().get(0);
  }


  public TestResponse fetchFromDb(TestRequest testRequest) {
    List<String> ids = new ArrayList<>();  //fetching  the test response with given id and tenantId from database
    ids.add(testRequest.getTests().get(0).getTestId());
    TestSearchCriteria criteria = TestSearchCriteria.builder()
        .testIds(ids).tenantId(testRequest.getTests().get(0).getTenantId())
        .build();
    Pagination Pagination = new Pagination();
    TestSearchRequest request = TestSearchRequest.builder()
        .testSearchCriteria(criteria).pagination(Pagination)
        .build();
    return testSearch(request, testRequest.getRequestInfo());
  }

  /**
   * Updates the Test
   *
   * @param testRequest The update Request
   * @return Updated Test
   */
  @SuppressWarnings("unchecked")
  public Test update(TestRequest testRequest) {
    if(Objects.isNull(testRequest.getTests()) || testRequest.getTests().isEmpty() )
      throw new CustomException(TEST_NOT_PRESENT_CODE, TEST_NOT_PRESENT_MESSAGE);
    List<Test> tests = testRequest.getTests();
    Test test = tests.get(0);
    if (test.getTestId() == null) { // validate if application exists
      throw new CustomException(UPDATE_ERROR, "Application Not found in the System" + test);
    }
    if (test.getSourceType().equals(SourceType.LAB_SCHEDULED)) {
      if (test.getWorkflow() == null || test.getWorkflow().getAction() == null) {
        throw new CustomException(UPDATE_ERROR,
            "Workflow action cannot be null." + String.format("{Workflow:%s}", test.getWorkflow()));
      }
    }
    TestResponse testResponse = fetchFromDb(testRequest);
    List<Test> oldTests = testResponse.getTests();
    if (tests.size() != oldTests.size()) // checking for the list of all ids to be present in DB
    {
      throw new CustomException(TEST_NOT_IN_DB,
          "test not present in database which we want to update ");
    }
    mdmsValidator.validateMdmsData(testRequest);
    pqmValidator.validateTestTypeScheduleCreateAndUpdate(testRequest);
    pqmValidator.validateTestCriteriaAndDocument(testRequest);
    pqmValidator.validateTestRequestFieldsWhileupdate(tests, oldTests);
    // Fetching actions from businessService
    BusinessService businessService = workflowService.getBusinessService(test, testRequest,
        PQM_BUSINESS_SERVICE,
        null);
    actionValidator.validateUpdateRequest(testRequest, businessService);
    if (test.getWorkflow().getAction().equals(UPDATE_RESULT)) {
      // calculate test result
      qualityCriteriaEvaluation.evalutateQualityCriteria(testRequest);
      enrichmentService.setTestResultStatus(testRequest);
      enrichmentService.pushToAnomalyDetectorIfTestResultStatusFail(testRequest);
    }
    if (test.getWorkflow().getAction().equals(SUBMIT_SAMPLE)) {
        // calculate test result
        qualityCriteriaEvaluation.validateQualityCriteriaResult(testRequest);
      }
    enrichmentService.enrichPQMUpdateRequest(testRequest);// enrich update request
    workflowIntegrator.callWorkFlow(testRequest);// updating workflow during update
    repository.update(testRequest);
    return testRequest.getTests().get(0);
  }

  /**
   * Schedules Test for a tenant
   */
  public void scheduleTest(RequestInfo requestInfo) {
    String stateLevelTenantId = config.getEgovStateLevelTenantId();
    Object mdmsRes = mdmsUtils.fetchMdmsData(requestInfo, stateLevelTenantId, MDMS_MODULE_TENANT, Collections.singletonList(MDMS_MASTER_TENANTS));

    String jsonString = "";

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      jsonString = objectMapper.writeValueAsString(mdmsRes);
    } catch (Exception e) {
      throw new CustomException(ErrorConstants.PARSING_ERROR,
              "Unable to parse Tenant mdms data ");
    }

    List<String> tenantList = mdmsUtils.extractTenantCode(jsonString);
    log.info("tenantList -> " + tenantList.toString());
    if (tenantList != null && tenantList.isEmpty()) {
      throw new CustomException(ErrorConstants.NO_TENANT_PRESENT_ERROR,
              NO_TENANT_PRESENT_ERROR_DESC);
    }
    for (String tenantId : tenantList) {
      scheduleTestForTenant(requestInfo, tenantId);
    }
  }

    /**
     * Schedules Test for a tenant
     */
  public void scheduleTestForTenant(RequestInfo requestInfo, String tenantId) {

    // get mdms TestStandardData
    //fetch mdms data for TestStandard Master
    log.info("Scheduler Starts for Tenant -> "+ tenantId);
    Object jsondata = mdmsUtils.mdmsCallV2(requestInfo,
            tenantId, SCHEMA_CODE_TEST_STANDARD);
    String jsonString = "";

    try {
      ObjectMapper objectMapper = new ObjectMapper();
      jsonString = objectMapper.writeValueAsString(jsondata);
    } catch (Exception e) {
      throw new CustomException(ErrorConstants.PARSING_ERROR,
          "Unable to parse QualityCriteria mdms data ");
    }

    List<MdmsTest> mdmsTestList = parseJsonToTestList(jsonString);

    for (MdmsTest mdmsTest : mdmsTestList) {
      TestSearchCriteria testSearchCriteria = TestSearchCriteria.builder().sourceType(
              Collections.singletonList(String.valueOf(SourceType.LAB_SCHEDULED)))
          .wfStatus(Arrays.asList(WFSTATUS_PENDINGRESULTS, WFSTATUS_SCHEDULED)).tenantId(tenantId)
          .testCode(Collections.singletonList(mdmsTest.getCode())).build();
      Pagination pagination = Pagination.builder().limit(2).sortBy(SortBy.scheduledDate)
          .sortOrder(DESC).build();
      TestSearchRequest testSearchRequest = TestSearchRequest.builder().requestInfo(requestInfo)
          .testSearchCriteria(testSearchCriteria).pagination(pagination).build();

      //search from DB for any pending tests
      List<Test> testListFromDb = testSearch(testSearchRequest, requestInfo).getTests();


      int frequency = Integer.parseInt(mdmsTest.getFrequency().split("_")[0]);

      LocalDate currentDate = LocalDate.now();
      LocalDate calculatedDate = currentDate.plusDays(frequency);
      Instant instant = calculatedDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

      List<QualityCriteria> qualityCriteriaList = new ArrayList<>();

      for (String mdmsQualityCriteria : mdmsTest.getQualityCriteria()) {
        QualityCriteria qualityCriteria = QualityCriteria.builder()
            .criteriaCode(mdmsQualityCriteria).resultStatus(TestResultStatus.PENDING)
            .isActive(Boolean.TRUE).build();
        qualityCriteriaList.add(qualityCriteria);
      }

      if (CollectionUtils.isEmpty(testListFromDb)) {
        //case 1: when no pending tests exist in DB
        Test createTest = Test.builder()
            .testCode(mdmsTest.getCode())
            .tenantId(tenantId)
            .plantCode(mdmsTest.getPlant())
            .processCode(mdmsTest.getProcess())
            .stageCode(mdmsTest.getStage())
            .materialCode(mdmsTest.getMaterial())
            .qualityCriteria(qualityCriteriaList)
            .sourceType(SourceType.LAB_SCHEDULED)
            .isActive(Boolean.TRUE)
            .scheduledDate(instant.toEpochMilli())
            .build();

        TestRequest testRequest = TestRequest.builder().tests(Collections.singletonList(createTest))
            .requestInfo(requestInfo).build();

        //send to create function
        createTestViaScheduler(testRequest);
      } else {
        //case 2: when pending test exist in DB
        Test testFromDb = testListFromDb.get(0);

        Long scheduleDate = testFromDb.getScheduledDate();

        if (isPastScheduledDate(scheduleDate)) {
          Test createTest = Test.builder()
              .tenantId(testFromDb.getTenantId())
              .testCode(mdmsTest.getCode())
              .plantCode(testFromDb.getPlantCode())
              .processCode(testFromDb.getProcessCode())
              .stageCode(testFromDb.getStageCode())
              .materialCode(testFromDb.getMaterialCode())
              .qualityCriteria(qualityCriteriaList)
              .sourceType(SourceType.LAB_SCHEDULED)
              .isActive(Boolean.TRUE)
              .scheduledDate(instant.toEpochMilli())
              .build();

          TestRequest testRequest = TestRequest.builder()
              .tests(Collections.singletonList(createTest)).requestInfo(requestInfo)
              .build();

          //send to create function
          createTestViaScheduler(testRequest);
        }
      }


    }

  }

  public static boolean isPastScheduledDate(Long scheduleDateEpoch) {
    // Convert epoch time to LocalDate for scheduleDate
    LocalDate scheduledDate = Instant.ofEpochMilli(scheduleDateEpoch)
        .atZone(ZoneId.systemDefault())
        .toLocalDate();

    // Get today's date
    LocalDate currentDate = LocalDate.now();

    // Check if the currentDate date is after or equal to the scheduled date
    return (currentDate.isAfter(scheduledDate) || currentDate.isEqual(scheduledDate));
  }

  /**
   * Creates Scheduled Tests
   *
   * @param testRequest The Create Request
   * @return New Test
   */
  public Test buildTestObject(TestRequest testRequest, Object mdmsData) {
    RequestInfo requestInfo = testRequest.getRequestInfo();
    repository.save(testRequest);
    return testRequest.getTests().get(0);
  }

	public TestResponse searchTestPlainSearch(TestSearchCriteria testSearchCriteria, RequestInfo requestInfo) {
		if (testSearchCriteria.getLimit() != null
				&& testSearchCriteria.getLimit() > config.getMaxSearchLimit())
			testSearchCriteria.setLimit(config.getMaxSearchLimit());

		List<String> ids = null;

		if (testSearchCriteria.getIds() != null
				&& !testSearchCriteria.getIds().isEmpty())
			ids = testSearchCriteria.getIds();
		else
			ids = repository.fetchTestIds(testSearchCriteria);

		if (ids.isEmpty())
			return TestResponse.builder().build();
		 TestSearchCriteria.builder().ids(ids).build();
		return testSearch(TestSearchRequest.builder().testSearchCriteria(testSearchCriteria).build(), requestInfo);
	}

}