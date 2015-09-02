<%@ taglib prefix="s" uri="/WEB-INF/struts-tags.tld"%>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt"%>
<%@page import="org.egov.lib.admbndry.CityWebsiteDAO,org.egov.lib.admbndry.CityWebsite"%>
<html>
	<head>
		<title>Trade License Certificate</title>
		<script>
			function refreshInbox() {
				if (opener && opener.top.document.getElementById('inboxframe')) {
					opener.top.document.getElementById('inboxframe').contentWindow.egovInbox.refresh();
				}
			}
			
			function printLicense(){
				document.getElementById('printDiv').style.display = 'none';
   				window.print();
   				//document.getElementById('printDiv').style.display = '';
			}
      
	      function submitandclose(){
		      var printcomplete=confirm("Are you sure License Certificate print completed?","YES","NO");
		      if(printcomplete) {
		      	document.getElementById('workflowBean.actionName').value='generatedcertificate';
		      	return true;
		      }else{
		      	return false;
		      }	      
	      }
      
		</script>
		<% 
			String cityUrl = (String) session.getAttribute("cityurl");
		   	CityWebsiteDAO cityWebsiteDAO = new CityWebsiteDAO();
			CityWebsite cityWebsite = cityWebsiteDAO.getCityWebSiteByURL(cityUrl);
			String cityName = cityWebsite.getCityName();
			String logoName = cityWebsite.getLogo();
		%>
	</head>
	<body onload="refreshInbox()">
		<div id="content">
			<center>
				<s:form name="certificateform" action="viewTradeLicense" theme="simple">
				<s:token/>
					<s:push value="model">
						<table width="100%" border="0" cellpadding="3" cellspacing="3" style="margin-left: 14px; margin-right: 14px; font-size: 13px">
							<tr>
								<td colspan="4" align="center">
									<img src="/egi/images/<%=logoName%>" width="91" height="90" />
								</td>
							</tr>
							<tr>
								<td colspan="4" align="center" style="font-size: 15px; font-weight: bolder;">
									<%=cityName%>
									<br />
									<br/>
									<s:text name="license.pfacertificate.publicHealthDepartment" />
									<br />
									<s:if test="%{#parameters.duplicate}">
										<s:text name="license.certificate.watermark.duplicate" />
									</s:if>
									<br />

								</td>
							</tr>
							<tr>
								<td>
									<s:text name="license.pfacertificate.title" />
								</td>
							</tr>
							<tr>
								<td>
									<table>
										<tr>
											<td>
												<s:text name="license.license.number" />
											</td>
											<td>
												:
											</td>
											<td>
												<b><s:property value="licenseNumber" />&nbsp;</b>
											</td>

										</tr>

										<tr>
											<td>
												<s:text name="licence.pfacertificate.applicantName" />
											</td>
											<td>
												:
											</td>
											<td>
												<b><s:property value="licensee.applicantName" /> </b>
											</td>
										</tr>
										<tr>
											<td>
												<s:text name="licence.pfacertificate.applicantage" />
											</td>
											<td>
												:
											</td>
											<td>

												<b><s:property value="licensee.age" /> </b>
											</td>
										</tr>
										<tr>
											<td>
												<s:text name="licensee.pfacertificate.residentialaddress" />
											</td>
											<td>
												:
											</td>
											<td>
												<b><s:property value="licensee.address.houseNo" />
													<s:if test="licensee.address.streetAddress1!=null">, </s:if>
													<s:property value="licensee.address.streetAddress1" />
													<s:if test="licensee.address.streetAddress2!=null">, </s:if>
													<s:property value="licensee.address.streetAddress2" />, <s:property value="licensee.boundary.parent.name" />, <s:property value="licensee.boundary.name" />
													<s:if test="licensee.address.pinCode!=null">,  </s:if>
													<s:property value="licensee.address.pinCode" /> </b>
											</td>

										</tr>
										<tr>
											<td>
												<s:text name="license.establishmentname" />
											</td>
											<td>
												:
											</td>
											<td>
												<b><s:property value="nameOfEstablishment" />
												</b>
											</td>
										</tr>
										<tr>
											<td>
												<s:text name="licensee.pfacertificate.completeaddress" />
											</td>
											<td>
												:
											</td>
											<td>
												<b><s:property value="address.houseNo" />
													<s:if test="address.streetAddress1!=null">, </s:if>
													<s:property value="address.streetAddress1" />
													<s:if test="address.streetAddress2!=null">, </s:if>
													<s:property value="address.streetAddress2" />, <s:property value="boundary.parent.name" />, <s:property value="boundary.name" />
													<s:if test="address.pinCode!=null">, </s:if>
													<s:property value="address.pinCode" />
												</b>
											</td>
										</tr>
									</table>
								</td>
							</tr>

							<tr>
								<td colspan="4" align="center" style="font-size: 15px; font-weight: bolder;">
									<br />
									<s:text name="license.pfacertificate.license" />
								</td>
							</tr>
							<tr>
								<td colspan="4" align="center" style="font-size: 15px; font-weight: bolder;">
									<s:text name="license.pfacertificate.formB" />
								</td>
							</tr>
							<tr>
								<td align="center">
									<s:text name="license.pfacertificate.seerule" />
								</td>
							</tr>
							<tr>
								<td>
									<s:text name="license.pfacertificate.shrismt" />
									&nbsp;
									<b><s:property value="licensee.applicantName" /> <s:if test="licensee.age!=null">
									</b> &nbsp;
									<s:text name="license.pfacertificate.aged" />
									&nbsp;
									<b><s:property value="licensee.age" />
										</s:if> </b>
									<s:text name="license.pfacertificate.residingat" />
									&nbsp;
									<b><s:property value="licensee.address.houseNo" />
										<s:if test="licensee.address.streetAddress1!=null">, </s:if>
										<s:property value="licensee.address.streetAddress1" />
										<s:if test="licensee.address.streetAddress2!=null">, </s:if>
										<s:property value="licensee.address.streetAddress2" />, <s:property value="licensee.boundary.parent.name" />, <s:property value="licensee.boundary.name" />
										<s:if test="licensee.address.pinCode!=null">,  </s:if>
										<s:property value="licensee.address.pinCode" /> </b>
									<s:text name="license.pfacertificate.ishereby" />
									<b>&nbsp;<s:property value="tradeName.name" />
									</b> &nbsp;
									<s:text name="license.pfacertificate.herein" />
									&nbsp;
									<b><s:property value="nameOfEstablishment" />
									</b>
									<s:text name="license.pfacertificate.situatedin" />
									<b><s:property value="address.houseNo" />
										<s:if test="address.streetAddress1!=null">, </s:if>
										<s:property value="address.streetAddress1" />
										<s:if test="address.streetAddress2!=null">, </s:if>
										<s:property value="address.streetAddress2" />, <s:property value="boundary.parent.name" />, <s:property value="boundary.name" />
										<s:if test="address.pinCode!=null">, </s:if>
										<s:property value="address.pinCode" />
									</b>
								</td>
							</tr>
							<tr>
								<td align="justify">
									<s:text name="license.pfacertificate.content1" />
									<b>&nbsp;<s:property value="tradeName.name" />
									</b>&nbsp;
									<s:text name="license.pfacertificate.content2" />
								</td>
							</tr>
							<tr>
								<td align="right">
									<br />
									<br />
									<s:text name="license.pfacertificate.healthOfficer" />
								</td>
							</tr>
							<tr>
								<td align="right">
									<s:text name="license.pfacertificate.licensingauthority" />
								</td>
							</tr>
							<tr>
								<td align="right">
									<s:text name="license.pfacertificate.cityofnagpurcorporation" />
								</td>
							</tr>
						</table>
						<s:hidden name="model.id" />
						<s:hidden name="workflowBean.actionName" id="workflowBean.actionName" />
						<div align="center" id="printDiv">
							<table width="30%" align="center">
								<tr>
									<td>
										<input type="button" id="print" value="Print" onclick="return printLicense()" />
									</td>
									<s:if test="%{!#parameters.duplicate}">
										<td>
											<s:submit value="Print Complete" id="printcmplt" method="approve" onclick="return submitandclose();" />
										</td>
									</s:if>
									<td>
										<input type="button" id="close" value="Close" onclick="javascript:window.close();" />
									</td>
								</tr>
							</table>
						</div>
					</s:push>
				</s:form>
			</center>
		</div>
	</body>
</html>
