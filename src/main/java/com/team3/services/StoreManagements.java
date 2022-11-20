package com.team3.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.team3.beans.ActionBean;
import com.team3.beans.AuthBean;
import com.team3.beans.GoodsBean;
import com.team3.beans.MemberBean;
import com.team3.dao.DataAccessObject;

/* 매장관련 업무 */
public class StoreManagements {
	public StoreManagements() {
	}
	
	public ActionBean backController(HttpServletRequest request) {
		ActionBean action = null;
		
		switch(request.getRequestURI().substring(request.getContextPath().length()+1)) {
		case "MoveMgr": action = this.access(request); break;
		case "RegGo": action = this.regGo(request); break;
		case "UpdGoInfo": action = this.updGoods(request); break;
		}
		return action;
	}
	private ActionBean updGoods(HttpServletRequest req) {
		ActionBean action = new ActionBean();
		GoodsBean goods = null;
		DataAccessObject dao = null;
		Connection conn = null;
		String page = null;
		boolean isDispatcher = false;
		
		try {
			page = "index.jsp?message="+URLEncoder.encode("로그인 후 사용하실 수 있습니다.","UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		HttpSession session = req.getSession();
		if(session.getAttribute("employeeCode") != null) {
			goods = new GoodsBean();
			goods.setGoCode(req.getParameter("goodsCode"));
			goods.setGoName(req.getParameter("goodsName"));
			goods.setGoCost(Integer.parseInt(req.getParameter("goodsCost")));
			goods.setGoPrice(Integer.parseInt(req.getParameter("goodsPrice")));
			goods.setGoStock(Integer.parseInt(req.getParameter("goodsStock")));
			
			dao = new DataAccessObject();
			conn = dao.connectionOpen();
			
			if(this.convertBool(dao.updGoodsInfo(conn, goods))) {
				page = "MoveMgr";
				isDispatcher = true;
			} else {
				
			}
			
			dao.connectionClose(conn);
		}
		
		action.setPage(page);
		action.setDispatcher(isDispatcher);		
		return action;
	}

	private ActionBean access(HttpServletRequest req) {
		/* 현재 로그인 상태에 따라 분기
		 * 1. Session 저장 정보 확인
		 * 2. db log 확인
		 * 3. 분기
		 * */
		ActionBean action = new ActionBean();
		AuthBean auth = null;
		DataAccessObject dao = null;
		Connection conn = null;
		
		/* Session 정보 확인 */
		HttpSession session = req.getSession();
		auth = new AuthBean();
		
		/* DataAccessObject */
		dao = new DataAccessObject();
		conn = dao.connectionOpen();
		
		/* session 정보 존재 (로그인중) --> 정보를 불러올때 필요한 데이터(매장코드, 직원코드)만 set */
		if(session.getAttribute("employeeCode") != null) {
			auth = new AuthBean();
			auth.setEmployeeCode((String)session.getAttribute("employeeCode"));//downcast Object->String
			//정보 불러오기
			this.getAccessInfo(req, auth, dao, conn);
			this.getMgrInfo(req, auth, dao, conn);
			//정보 넘겨줌 --> request(post)
			action = new ActionBean();
			action.setPage("storeMgr.jsp");
			action.setDispatcher(true);
		} 
		/* session 정보 없음 (로그인안됨) 로그인 요청 */
		else {
			action = new ActionBean();
			action.setPage("index.jsp");
			action.setDispatcher(false);
		}
		dao.connectionClose(conn);
		
		return action;
	}
	
	private void getAccessInfo(HttpServletRequest req, AuthBean auth, DataAccessObject dao, Connection conn) {
		ArrayList<AuthBean> list = null;
		
		/* 매장이름 + 직원이름 +  AccessTime + 직원등급 >> (SELECT) */
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
	
	private void getMgrInfo(HttpServletRequest req, AuthBean auth, DataAccessObject dao, Connection conn) {
		ArrayList<AuthBean> list = null;
		ArrayList<MemberBean> mmList = null;
		ArrayList<GoodsBean> goList = null;
		
		/* 매장이름 + 직원이름 + AccessTime + 직원등급 >> (SELECT) */
		list = dao.getEPInfo(conn);
		mmList = dao.getMMInfo(conn);
		goList = dao.getGOInfo(conn);
		
		req.setAttribute("epInfo", makeepHtml(list));
		req.setAttribute("mmInfo", makemmHtml(mmList));
		req.setAttribute("goInfo", makegoHtml(goList));
	}
	
	private String makeepHtml(ArrayList<AuthBean> ab) {
		/* 상품판매와 매장관리로 분기 */
		StringBuffer sb = new StringBuffer();
		sb.append("<table>");
		sb.append("<thead><tr><th>직원코드</th><th>직원이름</th><th>직원등급</th>"
				+ "<th>직원추가</th><th>비밀번호변경</th><th>직원등급변경</th></tr></thead>");
		for(AuthBean a : ab) {
			String data = (a.getEmployeeCode()+":"+a.getEmployeeName()+":"+a.getEmployeeLevel());
			sb.append("<tr>");
			sb.append("<td>"+a.getEmployeeCode()+"</td>");
			sb.append("<td>"+a.getEmployeeName()+"</td>");
			sb.append("<td>"+a.getEmployeeLevel()+"</td>");
			sb.append("<td><input class='btn' type='button' value='추가' onclick='insEmployee(\"직원추가\")' onmouseover='' /></td>");
			sb.append("<td><input class='btn' type='button' value='변경' onclick='changePw(\"직원정보변경\",\""+data+"\")' onmouseover='' /></td>");
			sb.append("<td><input class='btn' type='button' value='변경' onclick='changeLevel(\"직원등급변경\",\""+data+"\")' onmouseover='' /></td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	private String makemmHtml(ArrayList<MemberBean> ab) {
		/* 상품판매와 매장관리로 분기 */
		StringBuffer sb = new StringBuffer();
		sb.append("<table>");

		sb.append("<thead><tr><th>회원코드</th><th>회원이름</th><th>전화번호</th>"
				+ "<th>전화번호변경</th></tr></thead>");
		for(MemberBean a : ab) {
			String data = (a.getMemberCode()+":"+a.getMemberName()+":"+a.getMemberPhone());
			sb.append("<tr>");
			sb.append("<td>"+a.getMemberCode()+"</td>");
			sb.append("<td>"+a.getMemberName()+"</td>");
			sb.append("<td>"+a.getMemberPhone()+"</td>");
			sb.append("<td><input class='btn' type='button' value='변경' onclick='changePhone(\"전화번호변경\",\""+data+"\")'/></td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	private String makegoHtml(ArrayList<GoodsBean> ab) {
		/* 상품판매와 매장관리로 분기 */
		StringBuffer sb = new StringBuffer();
		sb.append("<input class='btn' type='button' value='상품추가' onclick='insGoods(\"상품추가\")'/>");
		sb.append("<table>");
		sb.append("<thead><tr><th>상품코드</th><th>상품이름</th><th>매입가</th><th>판매가</th><th>재고</th><th>카테고리</th>"
				+ "<th>상품정보변경</th></thead>");
		for(GoodsBean a : ab) {
			String data = (a.getGoCode()+":"+a.getGoName()+":"+a.getGoCost()+":"+a.getGoPrice()+":"+a.getGoStock()+":"+a.getCaName());
			sb.append("<tr>");
			sb.append("<td>"+a.getGoCode()+"</td>");
			sb.append("<td>"+a.getGoName()+"</td>");
			sb.append("<td>"+a.getGoCost()+"</td>");
			sb.append("<td>"+a.getGoPrice()+"</td>");
			sb.append("<td>"+a.getGoStock()+"</td>");
			sb.append("<td>"+a.getCaName()+"</td>");
			sb.append("<td><input class='btn' type='button' value='변경' onclick='changeGoods(\"상품정보변경\",\""+data+"\")'/></td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		return sb.toString();
	}
	
	private ActionBean regGo (HttpServletRequest req) {
		ActionBean action = new ActionBean();
		GoodsBean goods = null;
		DataAccessObject dao = null;
		Connection connection = null;
		String page = null;
		boolean isDispatcher = false;
		
		try {
			page = "index.jsp?message="+URLEncoder.encode("로그인 후 사용하실 수 있습니다.", "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		HttpSession session = req.getSession();
		
		if(session.getAttribute("employeeCode") != null) {

			goods = new GoodsBean();
			goods.setGoName(req.getParameter("goodsName"));
			goods.setGoCost(Integer.parseInt(req.getParameter("goodsCost")));
			goods.setGoPrice(Integer.parseInt(req.getParameter("goodsPrice")));
			goods.setGoStock(Integer.parseInt(req.getParameter("goodsStock")));
			goods.setGoCode(req.getParameter("goodsCode"));
			goods.setCaCode(req.getParameter("categoryCode"));

			dao = new DataAccessObject();
			connection = dao.connectionOpen();
			
			if (this.convertBool((dao.insGoods(connection, goods)))) {
				page = "MoveMgr";
				isDispatcher = true;
			} else {
				try {
					page = "storeMgr.jsp?message="+URLEncoder.encode("상품정보를 확인해주세요.", "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			dao.connectionClose(connection);
		}
		
		System.out.println("인설트 굿즈 페이지 : "+action.getPage());
		
		action.setPage(page);
		action.setDispatcher(isDispatcher);
		
		return action;
	}
	
	/* 0/1 데이터 변환 */
	private boolean convertBool(int value) {
		return (value == 1)? true : false; //1보다 크거나 같으면 로그인
	}
}
