package com.team3.services;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.team3.beans.ActionBean;
import com.team3.beans.AuthBean;
import com.team3.beans.MemberBean;
import com.team3.dao.DataAccessObject;

public class Authentication {
	public Authentication() {
		//servlet
	}
	
	public ActionBean backController(HttpServletRequest request) {
		ActionBean action = null;
		
		switch(request.getRequestURI().substring(request.getContextPath().length()+1)) {
		case "Access": action = this.access(request); break;
		case "AccessOut": action = this.accessOutCtl(request); break;
		case "RegEp": action = this.addEmployee(request); break;
		case "ChangePhone": action = this.changePhone(request); break;
		case "UpdLevel": action = this.changeLevel(request); break;
		case "UpdPassword": action = this.changePassword(request); break;
		case "Landing": action = this.access(request); break;
		}
		return action;
	}
	
	/* 직원추가 - 수정*/
	private ActionBean addEmployee(HttpServletRequest req) {
		HttpSession session = null;
		ActionBean action = new ActionBean();
		AuthBean auth = new AuthBean();
		String page = "MoveMgr";
		boolean isDispatcher = false;
		String message = null;
		Connection conn = null;
		DataAccessObject dao = new DataAccessObject();
		
		conn = dao.connectionOpen();
		
		auth.setEmployeeCode(req.getParameter("employeeCode"));
		auth.setEmployeeName(req.getParameter("employeeName"));
		auth.setEmployeeLevel(req.getParameter("employeeLevel"));
		auth.setEmployeePassword(req.getParameter("employeePassword"));

		if(!this.convertBool(dao.isEmpCode(conn, auth))) {
			dao.insEmployee(conn, auth);

			isDispatcher = true;

		}else {
			message = "직원코드가 존재합니다.";
		}
		if(message !=null) {
			try{
				page += "?message=" + URLEncoder.encode(message, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}	dao.connectionClose(conn);

		
		action.setPage(page);
		action.setDispatcher(isDispatcher);
		
		return action;

	}

	/* 직원등급변경 - 수정*/
	private ActionBean changeLevel(HttpServletRequest req) {
		ActionBean action = new ActionBean();
		AuthBean auth = null;
		DataAccessObject dao = null;
		Connection conn = null;
		String page = "storeMgr.jsp";
		boolean isDispatcher = false;

		try {
			page = "?message=" + URLEncoder.encode("로그인 후 사용하실 수 있습니다.", "UTF-8");
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}

		HttpSession session = req.getSession();

		if(session.getAttribute("employeeCode")!= null) {
			auth = new AuthBean();
			auth.setEmployeeCode(req.getParameter("employeeCode"));
			auth.setEmployeeLevel(req.getParameter("employeeLevel"));

			dao = new DataAccessObject();
			conn = dao.connectionOpen();

			if(this.convertBool(dao.updEmployeeLevel(conn, auth))) {
				page = "MoveMgr";
				isDispatcher = true;
			}
			dao.connectionClose(conn);
		}

		action.setPage(page);
		action.setDispatcher(isDispatcher);
		return action;
	}
	
	private ActionBean changePassword(HttpServletRequest req) {
		ActionBean action = new ActionBean();
		AuthBean auth = null;
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
			auth = new AuthBean();
			auth.setEmployeeCode((String)session.getAttribute("employeeCode"));
			auth.setEmployeePassword(req.getParameter("employeePassword"));

			dao = new DataAccessObject();
			connection = dao.connectionOpen();
			
			if (this.convertBool(dao.updEmployeePassword(connection, auth))) {
				page = "MoveMgr";
				isDispatcher = true;
			}
			
			dao.connectionClose(connection);
		}
		action.setPage(page);
		action.setDispatcher(isDispatcher);
		System.out.println("어...."+action.getPage());
		return action;
	}
	
	/* 전화번호 변경 */
	private ActionBean changePhone(HttpServletRequest req) {
		ActionBean action = new ActionBean();
		MemberBean mem = null;
		DataAccessObject dao = null;
		Connection conn = null;
		HttpSession session = req.getSession();
		String page = null;
		boolean isDispatcher = false;

		try {
			page = "index.jsp?message="+URLEncoder.encode("로그인 후 사용하실 수 있습니다.","UTF-8");
		} catch (UnsupportedEncodingException e) { e.printStackTrace();	}
		
		if(session.getAttribute("employeeCode") != null) {
			mem = new MemberBean();
			mem.setMemberCode(req.getParameter("memberCode"));
			mem.setMemberPhone(req.getParameter("memberPhone"));
			
			dao = new DataAccessObject();
			conn = dao.connectionOpen();
			
			if(this.convertBool(dao.updateMemberPhone(conn, mem))) {
				page = "MoveMgr"; //jobCode, 새로고침
				isDispatcher = true;
			} else {
				//message 처리
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
		ActionBean action = null;
		AuthBean auth = null;
		DataAccessObject dao = null;
		Connection conn = null;
		
		/* Session 정보 확인 */
		HttpSession session = req.getSession();
		auth = new AuthBean();
		
		/* DataAccessObject */
		dao = new DataAccessObject();
		conn = dao.connectionOpen();
		
		/* session 정보 존재 (로그인중) --> 정보를 불러올때 필요한 데이터(직원코드)만 set */
		if(session.getAttribute("employeeCode") != null) {
			auth = new AuthBean();
			auth.setEmployeeCode((String)session.getAttribute("employeeCode"));//downcast Object->String
			//정보 불러오기
			this.getAccessInfo(req, auth, dao, conn);
			//정보 넘겨줌 --> request(post)
			action = new ActionBean();
			action.setPage("success.jsp");
			action.setDispatcher(true);
			this.getBtn(req);
		} 
		/* session 정보 없음 (로그인안됨) 로그인 요청 */
		else {
			if(req.getParameter("employeeCode") != null) {
				/* request >> bean */
				auth.setEmployeeCode(req.getParameter("employeeCode"));
				auth.setEmployeePassword(req.getParameter("employeePassword"));
				auth.setAccessAction(1);
				//accessCtl로 request 수행 -> action(page,dispatcher) return
				action = this.accessCtl(req, auth, dao, conn);
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
	
	private void getBtn(HttpServletRequest req) {
		StringBuffer sb = new StringBuffer();
		String level = (String)req.getAttribute("levelName");
		sb.append("<div id=\"outline\">");
		sb.append("<div id=\"movePos\" class=\"moveBtn\" class=\"moveBtnOver\" onmouseover=\"changeBtnCss(this, 'moveBtnOver')\" onmouseout=\"changeBtnCss(this, 'moveBtn')\" onclick=\"movePage('"+level+"','MovePos')\">상품판매</div>");
		sb.append("<div id=\"movePos\" class=\"moveBtn\" class=\"moveBtnOver\" onmouseover=\"changeBtnCss(this, 'moveBtnOver')\" onmouseout=\"changeBtnCss(this, 'moveBtn')\" onclick=\"movePage('"+level+"','MoveStat')\">통계</div>");
		sb.append("<div id=\"moveMgr\" class=\"moveBtn\" class=\"moveBtnOver\" onmouseover=\"changeBtnCss(this, 'moveBtnOver')\" onmouseout=\"changeBtnCss(this, 'moveBtn')\" onclick=\"movePage('"+level+"','MoveMgr')\">매장관리</div>");
		sb.append("</div>");
		req.setAttribute("btn", sb.toString());
	}
	
	private void getAccessInfo(HttpServletRequest req, AuthBean auth, DataAccessObject dao, Connection conn) {
		ArrayList<AuthBean> list = null;
		
		/* 직원이름 +  AccessTime + 직원등급 >> (SELECT) */
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
	
	/* 로그인 : 매장코드, 직원코드, 패스워드 >> request */
	private ActionBean accessCtl(HttpServletRequest req, AuthBean auth, DataAccessObject dao, Connection conn) {
		/* 응답 */
		HttpSession session = null;
		ActionBean action = new ActionBean();
		String page = "index.jsp", message = null;
		boolean isDispatcher = false;
		/* DAO */
		/* Process */
		/* 1. 직원코드의 유무(SELECT) pk */
		if(this.convertBool(dao.isEmpCode(conn, auth))) {
			/* 2. 직원의 액세스 가능 여부(SELECT) pk */
			if(this.convertBool(dao.isAccess(conn, auth))) {
				/* 3. 로그 기록 생성(INSERT) */
				//로그인이 되어있는 상태라면 로그아웃 처리 우선
				if(dao.accessState(conn, auth) == 1) {
					auth.setAccessAction(-1);
					dao.insAccessHistory(conn, auth);
					auth.setAccessAction(1);
				}
				if(this.convertBool(dao.insAccessHistory(conn, auth))) {
					/* session 저장 */
					session = req.getSession();
					session.setAttribute("employeeCode", req.getParameter("employeeCode"));
					this.getBtn(req);
					page = "success.jsp";
					isDispatcher = true;
				} else {
					//네트워크가 불안정 (사기)
					message = "네트워크가 불안정합니다. 잠시후 다시 로그인해주세요.";
				}
			} else {
				//해당 직원이 없거나 패스워드가 틀린 경우 -> index.html
				message = "아이디나 패스워드가 틀렸습니다.";
			}
		} else {
			//StoreCodeX -> index.html
			message = "해당 직원은 존재하지 않습니다.";
		}

		if(message != null)
			try {
				page += "?message=" + URLEncoder.encode(message, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		action.setPage(page);
		action.setDispatcher(isDispatcher);
		
		return action;
	}
	
	/* 로그아웃 : 직원코드 >> request*/
	private ActionBean accessOutCtl(HttpServletRequest req) {
		/* 응답 */
		HttpSession session = null;
		ActionBean action = new ActionBean();
		boolean isDispatcher = false;
		String page = "index.jsp", message = null;
		/* DAO */
		AuthBean auth = new AuthBean();
		DataAccessObject dao = null;
		Connection conn = null;
		
		session = req.getSession();
		auth.setEmployeeCode((String)session.getAttribute("employeeCode"));
		auth.setAccessAction(-1);
		
		dao = new DataAccessObject();
		conn = dao.connectionOpen();
		/* DAO */
		/* Process */
		/* 1. 로그아웃 하려는 계정의 현재 상태 확인 후 로그기록 생성 */
		if(this.convertBool(dao.accessState(conn, auth))) {
			
			if(this.convertBool(dao.insAccessHistory(conn, auth))) {
				session.invalidate(); //세션정보 삭제
				message = "로그아웃이 완료되었습니다.";
			}
			else {
				message = "로그아웃 기록이 입력되지 않았습니다.";
			}
		} else {
			message = "이미 로그아웃 되었습니다.";
		}
		dao.connectionClose(conn);
		
		if(message != null)
			try {
				page += "?message=" + URLEncoder.encode(message, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		
		action.setDispatcher(isDispatcher);
		action.setPage(page);
				
		return action;
	}
		
	/* 0/1 데이터 변환 */
	private boolean convertBool(int value) {
		return (value == 1)? true : false; //1보다 크거나 같으면 로그인
	}
}
