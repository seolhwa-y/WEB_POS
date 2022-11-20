package com.team3.services;

import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.team3.beans.ActionBean;
import com.team3.beans.AuthBean;
import com.team3.beans.GoodsBean;
import com.team3.dao.DataAccessObject;

public class ManagementInfomation {
	public ManagementInfomation() { }
	
	public ActionBean backController(HttpServletRequest request) {
		ActionBean action = null;
		
		switch(request.getRequestURI().substring(request.getContextPath().length()+1)) {
		case "MoveStat": action = this.movePage(request); break;
		case "AccessInfo": action = this.movePage(request); break;
		case "GoodsProfit": action = this.movePage(request); break;
		}
		return action;
	}
	
	private ActionBean movePage(HttpServletRequest req) {
		ActionBean action = new ActionBean();
		AuthBean auth = null;
		GoodsBean goBean = null;
		DataAccessObject dao = null;
		Connection conn = null;
		boolean tf = true;

		/* Session 정보 확인 */
		HttpSession session = req.getSession();
		auth = new AuthBean();

		/* DataAccessObject */
		dao = new DataAccessObject();
		conn = dao.connectionOpen();

		if (session.getAttribute("employeeCode") != null) {
			auth.setEmployeeCode((String) session.getAttribute("employeeCode"));

			if (this.convertToBool(dao.accessState(conn, auth))) {
				this.getAccessInfo(req, auth, dao, conn);
				switch (req.getRequestURI().substring(req.getContextPath().length() + 1)) {
				case "MoveStat":
					action.setPage("statistics.jsp");
					action.setDispatcher(true);
					req.setAttribute("epInfo", this.getEmployeeInfo(req, conn, dao));
					req.setAttribute("goInfo", this.getGoodsInfo(req, conn, dao));
					break;

				case "AccessInfo":
					action.setAjaxData((tf) ? this.getAccessHistory(req, conn, dao, auth) : "실패");
					break;

				case "GoodsProfit":
					action.setAjaxData((tf) ? this.getGoodsProfit(req, conn, dao, goBean) : "실패");
					break;

				default:
					action.setPage("index.jsp");
					action.setDispatcher(false);
					break;
				}
			} else {
				action = new ActionBean();
				action.setPage("index.jsp");
				action.setDispatcher(false);
			}
		} else {
			action = new ActionBean();
			action.setPage("index.jsp");
			action.setDispatcher(false);
		}

		dao.connectionClose(conn);
		return action;

	}

	private void getAccessInfo(HttpServletRequest req, AuthBean auth, DataAccessObject dao, Connection conn) {
		ArrayList<AuthBean> list = null;

		/* 직원이름 + AccessTime + 직원등급 (SELECT) */
		list = dao.getUserInfo(conn, auth);
		req.setAttribute("employeeName", list.get(0).getEmployeeName());
		req.setAttribute("accessTime", list.get(0).getAccessTime());
		req.setAttribute("employeeLevel", list.get(0).getEmployeeLevel());
		if (list.get(0).getEmployeeLevel().equals("MA")) {
			req.setAttribute("levelName", "매니저");
		} else if (list.get(0).getEmployeeLevel().equals("PR")) {
			req.setAttribute("levelName", "사장");
		} else {
			req.setAttribute("levelName", "아르바이트");
		}

	}

	private String getEmployeeInfo(HttpServletRequest req, Connection conn, DataAccessObject dao) {
		ArrayList<AuthBean> list = null;

		list = dao.getEPInfo(conn);
		StringBuffer sb = new StringBuffer();

		for (int idx = 0; idx < list.size(); idx++) {
			String data = list.get(idx).getEmployeeCode() + ":" + list.get(idx).getEmployeeName() + ":"
					+ list.get(idx).getEmployeeLevel() + ":" + list.get(idx).getEmployHiredate();
			sb.append("<tr>");
			sb.append("<td>" + list.get(idx).getEmployeeCode() + "</td>");
			sb.append("<td>" + list.get(idx).getEmployeeName() + "</td>");
			sb.append("<td>" + list.get(idx).getEmployeeLevel() + "</td>");
			sb.append("<td>" + list.get(idx).getEmployHiredate() + "</td>");
			sb.append("<td name=\"mmBtn\">" + "<input type = \"button\" value =\"출퇴근기록\" onClick =\"noMeanfunction(\'"
					+ data
					+ "\')\" class=\"objbtn\" onMouseOver = \"changeBtnCss(this,'objbtn-over')\" onMouseOut=\"changeBtnCss(this,'objbtn')\"></td>");
			sb.append("</tr>");

		}
		return sb.toString();
	}

	private String getAccessHistory(HttpServletRequest req, Connection conn, DataAccessObject dao, AuthBean auth) {
		ArrayList<AuthBean> list = null;
		auth = new AuthBean();
		auth.setAccessEpCode(req.getParameter("epCode"));
		list = dao.getAccessInfo(conn, auth);
		StringBuffer sb = new StringBuffer();

		for (int idx = 0; idx < list.size(); idx++) {

			sb.append("<tr>");
			sb.append("<td>" + list.get(idx).getAccessTime() + "</td>");
			if(list.get(idx).getAccessAction() == 1) {
				sb.append("<td>출근</td>");
			}else if(list.get(idx).getAccessAction() == -1) {
				sb.append("<td>퇴근</td>");
			}
			sb.append("</tr>");

		}
		return sb.toString();
	}
	
	//220623 추가
	private String getGoodsProfit(HttpServletRequest req, Connection conn, DataAccessObject dao, GoodsBean goBean) {
		ArrayList<GoodsBean> list = null;
		goBean = new GoodsBean();
		goBean.setGoCode(req.getParameter("goCode"));
		list = dao.getProfit(conn, req, goBean);
		StringBuffer sb = new StringBuffer();

		for (int idx = 0; idx < list.size(); idx++) {

			sb.append("<tr>");
			sb.append("<td>" + list.get(idx).getGoCode() + "</td>");
			sb.append("<td>" + list.get(idx).getGoName() + "</td>");
			sb.append("<td>" + list.get(idx).getOdDate() + "</td>");
			sb.append("<td>" + list.get(idx).getDdSales() + "원</td>");
			sb.append("<td>" + list.get(idx).getProfit() + "원</td>");
			sb.append("</tr>");

		}
		return sb.toString();
	}

	//220623 추가
	private String getGoodsInfo(HttpServletRequest req, Connection conn, DataAccessObject dao) {
		ArrayList<GoodsBean> list = null;
		list = dao.getGOInfo(conn);
		StringBuffer sb = new StringBuffer();
		System.out.print(req.getParameter("goCode"));
		for (int idx = 0; idx < list.size(); idx++) {
			String data = list.get(idx).getGoCode() + ":" + list.get(idx).getGoName() + ":" + list.get(idx).getGoCost()
					+ ":" + list.get(idx).getGoPrice() + ":" + list.get(idx).getGoStock() + ":"
					+ list.get(idx).getCaName();
			sb.append("<tr>");
			sb.append("<td>" + list.get(idx).getGoCode() + "</td>");
			sb.append("<td>" + list.get(idx).getGoName() + "</td>");
			sb.append("<td>" + list.get(idx).getGoCost() + "</td>");
			sb.append("<td>" + list.get(idx).getGoPrice() + "</td>");
			sb.append("<td>" + list.get(idx).getGoStock() + "</td>");
			sb.append("<td>" + list.get(idx).getCaName() + "</td>");
			sb.append("<td name=\"mmBtn\">"
					+ "<input type = \"button\" value =\"매출통계\" onClick =\"meanFunction(\'" +data
					+ "')\" class=\"objbtn\" onMouseOver = \"changeBtnCss(this,'objbtn-over')\" onMouseOut=\"changeBtnCss(this,'objbtn')\"></td>");
			sb.append("</tr>");

		}
		return sb.toString();

	}

	private boolean convertToBool(int value) {
		return (value == 1) ? true : false;
	}
}
