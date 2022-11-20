package com.team3.beans;

import java.util.ArrayList;

public class OrdersBean {
	private String storeCode;
	private String employeeCode;
	private String orderDate;
	private String caCode;
	private ArrayList<OrdersDetailBean> orderList;
	private String memberCode;
	private int amount;
	private int action;
	
	
	public ArrayList<OrdersDetailBean> getOrderList() {
		return orderList;
	}
	public void setOrderList(ArrayList<OrdersDetailBean> orderList) {
		this.orderList = orderList;
	}
	public String getStoreCode() {
		return storeCode;
	}
	public void setStoreCode(String storeCode) {
		this.storeCode = storeCode;
	}
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getCaCode() {
		return caCode;
	}
	public void setCaCode(String caCode) {
		this.caCode = caCode;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
}
