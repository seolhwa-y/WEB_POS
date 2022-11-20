package com.team3.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.team3.beans.ActionBean;
import com.team3.services.Authentication;
import com.team3.services.ManagementInfomation;
import com.team3.services.Sales;
import com.team3.services.StoreManagements;

@WebServlet({"/Landing", "/Access", "/AccessOut", "/MoveMgr", "/MovePos", "/MoveStat", "/RegEp", "/UpdPassword", "/UpdLevel", "/RegGo", "/UpdGoInfo", "/ChangePhone", "/SearchGoodsInfo", "/Orders", "/GoodsProfit", "/AccessInfo" })
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public Controller() {
        super();
    }
    
    private void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Authentication auth = null;
		StoreManagements sm = null;
		Sales pos = null;
		ManagementInfomation mi = null;
		
		String jobCode = null;
		ActionBean action = null;
		/* 한글화 지원 --> request.setCharacterEncoding("UTF-8") */
		request.setCharacterEncoding("UTF-8");
		/* jobCode 분리 */
		jobCode = request.getRequestURI().substring(request.getContextPath().length() + 1);
		/* jobCode에 따른 서비스 분기 */
		if (jobCode.equals("Landing")) {
			auth = new Authentication();
			action = auth.backController(request);
		} else if (jobCode.equals("Access")) {
			auth = new Authentication();
			action = auth.backController(request);
		} else if (jobCode.equals("AccessOut")) {
			auth = new Authentication();
			action = auth.backController(request);
		} else if(jobCode.equals("MoveMgr")) {
			sm = new StoreManagements();
			action = sm.backController(request);
		} else if(jobCode.equals("MovePos")) {
			pos = new Sales();
			action = pos.backController(request);
		} else if(jobCode.equals("MoveStat")) {
			mi = new ManagementInfomation();
			action = mi.backController(request);
		} else if(jobCode.equals("RegEp")) {
			auth = new Authentication();
			action = auth.backController(request);
		} else if(jobCode.equals("UpdPassword")) {
			auth = new Authentication();
			action = auth.backController(request);
		} else if(jobCode.equals("UpdLevel")) {
			auth = new Authentication();
			action = auth.backController(request);
		} else if(jobCode.equals("RegGo")) {
			sm = new StoreManagements();
			action = sm.backController(request);
		} else if(jobCode.equals("UpdGoInfo")) {
			sm = new StoreManagements();
			action = sm.backController(request);
		} else if(jobCode.equals("ChangePhone")) {
			auth = new Authentication();
			action = auth.backController(request);
		} else if(jobCode.equals("SearchGoodsInfo")) {
			pos = new Sales();
			action = pos.backController(request);
		} else if (jobCode.equals("Orders")) {
			pos = new Sales();
			action = pos.backController(request);
		} else if(jobCode.equals("GoodsProfit")) {
			mi = new ManagementInfomation();
			action = mi.backController(request);
		} else if(jobCode.equals("AccessInfo")) {
			mi = new ManagementInfomation();
			action = mi.backController(request);
		}
		
		if(action.getAjaxData()!=null) {
			/* Ajax에 대한 응답 처리 */
			response.setCharacterEncoding("UTF-8");
			PrintWriter pw = response.getWriter();
			pw.print(action.getAjaxData());
		} else {
			if(action.isDispatcher()) {
				//클라이언트에 대한 응답 지원: HttpServletRequest >> Dispatcher
				RequestDispatcher dispatcher = request.getRequestDispatcher(action.getPage());
				dispatcher.forward(request, response);
			} else	
				//클라이언트에 대한 응답 지원: HttpServletResponse
				response.sendRedirect(action.getPage());
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String page = null;
		String jobCode = request.getRequestURI().substring(request.getContextPath().length()+1);
		
		switch(jobCode) {
		case "Landing": 
			this.doProcess(request, response);
			break;
		default:
			page = "index.jsp?message=" + URLEncoder.encode("잘못된 경로로 접근","UTF-8");
			response.sendRedirect(page);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doProcess(request, response);
	}

}
