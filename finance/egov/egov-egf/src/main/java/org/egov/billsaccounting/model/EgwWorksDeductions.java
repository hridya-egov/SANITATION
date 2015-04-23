package org.egov.billsaccounting.model;

// Generated Feb 13, 2007 1:14:54 PM by Hibernate Tools 3.1.0.beta5

import java.math.BigDecimal;
import java.util.Date;
import org.egov.commons.CChartOfAccounts;
import org.egov.model.recoveries.Recovery;

/**
 * EgwWorksDeductions generated by hbm2java
 */
public class EgwWorksDeductions implements java.io.Serializable {

	// Fields

	private Integer id; 

	private Worksdetail worksdetail;

	private CChartOfAccounts chartofaccounts;

	private Recovery recovery;

	private BigDecimal amount;

	private Double perc;

	private char dedtype;

	private Date lastmodifieddate;

	

	// Property accessors
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Worksdetail getWorksdetail() {
		return this.worksdetail;
	}

	public void setWorksdetail(Worksdetail worksdetail) {
		this.worksdetail = worksdetail;
	}

	public CChartOfAccounts getChartofaccounts() {
		return this.chartofaccounts;
	}

	public void setChartofaccounts(CChartOfAccounts chartofaccounts) {
		this.chartofaccounts = chartofaccounts;
	}

	public Recovery getRecovery() {
		return recovery;
	}

	public void setRecovery(Recovery recovery) {
		this.recovery = recovery;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Double getPerc() {
		return this.perc;
	}

	public void setPerc(Double perc) {
		this.perc = perc;
	}

	public char getDedtype() {
		return this.dedtype;
	}

	public void setDedtype(char dedtype) {
		this.dedtype = dedtype;
	}

	public Date getLastmodifieddate() {
		return this.lastmodifieddate;
	}

	public void setLastmodifieddate(Date lastmodifieddate) {
		this.lastmodifieddate = lastmodifieddate;
	}

}
