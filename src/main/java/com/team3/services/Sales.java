package com.team3.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.team3.beans.ActionBean;
import com.team3.beans.AuthBean;
import com.team3.beans.GoodsBean;
import com.team3.beans.OrdersBean;
import com.team3.beans.OrdersDetailBean;
import com.team3.dao.DataAccessObject;

/* 상품판매 관련 업무 */
public class Sales {
	public Sales() {
	}
	
	public ActionBean backController(HttpServletRequest request) {
		ActionBean action = null;
		
		switch(request.getRequestURI().substring(request.getContextPath().length()+1)) {
		case "MovePos": action = this.access(request); break;
		case "SearchGoodsInfo": action = this.getGoodsInfo(request); break;
		case "Orders": action = this.paymentCtl(request); break;
		}
		return action;
	}
	
	/* 주문 입력 */
	private ActionBean paymentCtl(HttpServletRequest req) {
		ActionBean action = new ActionBean();
		HttpSession session = req.getSession();
		OrdersBean orders = new OrdersBean();
		ArrayList<OrdersDetailBean> orderList = new ArrayList<OrdersDetailBean>();
		OrdersDetailBean ordersDetail = null;
		String message = "적립실패";
		
		boolean tran = false;
		boolean point = false;
		DataAccessObject dao = new DataAccessObject();
		Connection conn = null;
		
		/* Client Data --> Bean */
		/* OrdersDetail */
		for(int rec=0;rec<req.getParameterValues("goodsCode").length;rec++) {
			ordersDetail = new OrdersDetailBean();
			ordersDetail.setGoodsCode(req.getParameterValues("goodsCode")[rec]);
			ordersDetail.setGoodsQty(Integer.parseInt(req.getParameterValues("goodsQty")[rec]));
			orderList.add(ordersDetail);
		}
		
		/* Orders */
		orders.setEmployeeCode((String)session.getAttribute("employeeCode"));
		orders.setCaCode("OS"); //결제중: OS, 결제완료: OC
		orders.setOrderList(orderList);
		
		/* Points */
		/* memberCode가 null이 아니면 회원조회
		 * 회원 존재 -> po에 ins -> "적립완료"
		 * 회원 존재X -> "회원 존재X" */
		if(req.getParameterValues("memberCode") != null) {
			orders.setMemberCode(req.getParameterValues("memberCode")[0]);
			orders.setAction(1);
			point = true;
		}
		
		conn = dao.connectionOpen();
		try {
			conn.setAutoCommit(false);//jdbc 기본값 = true
			if(this.convertBool(dao.insOrders(conn, orders))) { //OD에 입력
				dao.getOrderDate(conn, orders); //날짜 가져오기
				if (orders.getOrderDate() != null) {
					if(this.convertBool(dao.insOrdersDetail(conn, orders))) { //OT에 입력
						if(this.convertBool(dao.updOrders(conn, orders))) { //주문상태 업데이트
							if(point) { //회원코드가 존재할때: 회원조회 후 적립/실패
								if(this.convertBool(dao.isMember(conn, orders))) { //회원조회
									dao.getAmount(conn, orders);
									if(this.convertBool(dao.insPoint(conn, orders))) { //PO
										message = "적립완료";
										tran = true;
									} else { message = "적립오류"; }
								} else { message = "존재하지 않는 회원번호입니다."; }
							} else tran = true; //회원코드가 없을 때: 바로 결제
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if(!conn.isClosed()) {
					if(tran) {
						conn.commit();
					} else {
						conn.rollback();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		dao.connectionClose(conn);
		action.setAjaxData((tran?"주문완료:":"주문실패:")+message);
		
		return action;
	}
	
	/* 상품코드에 따른 상품정보 가져오기 */
	private ActionBean getGoodsInfo(HttpServletRequest req) {
		ActionBean action = new ActionBean();
		AuthBean auth = new AuthBean();
		ArrayList<GoodsBean> list = null;
		HttpSession session = req.getSession();
		
		auth.setEmployeeCode((String)session.getAttribute("employeeCode"));
		
		DataAccessObject dao = new DataAccessObject();
		Connection conn = null;
		conn = dao.connectionOpen();
		
		list = dao.getGOInfo(conn);
		dao.connectionClose(conn);
		
		for(GoodsBean go:list) {
			if(go.getGoCode().equals(req.getParameter("goodsCode"))) {
				action.setAjaxData(go.getGoCode()+":"+go.getGoName()+":"+go.getGoPrice());
				break;
			} else {
				action.setAjaxData("none");
			}
		}
		
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
			auth.setEmployeeCode((String)session.getAttribute("employeeCode")); //downcast Object->String
			//정보 불러오기
			this.getAccessInfo(req, auth, dao, conn);
			//정보 넘겨줌 --> request(post)
			action = new ActionBean();
			action.setPage("pos.jsp");
			action.setDispatcher(true);
		} 
		/* session 정보 없음 (로그인안됨) 로그인 요청 */
		else {
			if(req.getParameter("employeeCode") != null) {
				/* request >> bean */
				auth.setEmployeeCode(req.getParameter("employeeCode"));
				auth.setEmployeePassword(req.getParameter("employeePassword"));
				auth.setAccessAction(1);
				//정보 불러오기
				if(action.isDispatcher())
					this.getAccessInfo(req, auth, dao, conn);
			} else {
				action = new ActionBean();
				action.setPage("index.jsp");
				action.setDispatcher(false);
			}
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
	
	/* 0/1 데이터 변환 */
	private boolean convertBool(int value) {
		return (value == 1)? true : false; //1보다 크거나 같으면 로그인
	}
}
