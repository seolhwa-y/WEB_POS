package com.team3.beans;

public class ActionBean {
	private String page;
	private boolean dispatcher;
	private String ajaxData;
	
	public String getAjaxData() {
		return ajaxData;
	}
	public void setAjaxData(String ajaxData) {
		this.ajaxData = ajaxData;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}
	public boolean isDispatcher() {
		return dispatcher;
	}
	public void setDispatcher(boolean dispatcher) {
		this.dispatcher = dispatcher;
	}
}
