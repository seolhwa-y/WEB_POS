package com.team3.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.team3.beans.AuthBean;
import com.team3.beans.GoodsBean;
import com.team3.beans.MemberBean;
import com.team3.beans.OrdersBean;

/* 데이터 베이스 연동 */
public class DataAccessObject {
	public DataAccessObject() {
	}
	
	/* Oracle DBMS에 연결 */
	public Connection connectionOpen() {
		/* DB연결에 필요한 사항
		 * jdbc:oracle:thin --> oracle과 mySql구분을 위함
		   		+
		   @IP:Port:SID
		 * (db)USER
		 * (db)PASSWORD */
		String url = "jdbc:oracle:thin:@192.168.0.137:1521:xe"; //ip, port, sid 정보 저장 *@ = at
		String user = "TEAM_3";
		String pw = "3333";
		Connection connect = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			connect = DriverManager.getConnection(url,user,pw);
			System.out.println("Oracle DBMS 연결 성공");
		} catch (ClassNotFoundException e) {
			System.out.println("OJDBC없음");
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("해당 주소 정보에 Oracle이 없음");
			e.printStackTrace();
		}
		return connect;
	}
	
	public void connectionClose(Connection conn) {
		try {
			conn.close();
			System.out.println("DB Close");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/* 직원코드의 유무 > employeeCode */
	public int isEmpCode(Connection conn, AuthBean auth) {
		String sql = "SELECT COUNT(*) FROM EMP WHERE EMP_CODE = ?";
		ResultSet rs = null;
		int result = -1;
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, auth.getEmployeeCode());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				result = rs.getInt(1); //하나의 레코드 하나의 데이터 = 스칼라데이터
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/* Access 가능 여부 > employeeCode, employeePassword */
	public int isAccess(Connection conn, AuthBean auth) {
		String sql = "SELECT COUNT(*) FROM EMP WHERE EMP_CODE = ? AND EMP_PASSWORD = ?";
		ResultSet rs = null;
		int result = -1;
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, auth.getEmployeeCode());
			pstmt.setNString(2, auth.getEmployeePassword());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/* 로그기록 생성 AccessHistory > insert employeeCode, date, action */
	public int insAccessHistory(Connection conn, AuthBean auth) {
		String sql = "INSERT INTO AHT(AHT_EMPCODE, AHT_DATE, AHT_ACTION) "
				+ "VALUES(?, SYSDATE , ?)";
		int result = -1;
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, auth.getEmployeeCode());
			pstmt.setInt(2, auth.getAccessAction());
			result = pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/* 계정 정보 가져오기 : 직원이름, 로그인 시간, 직원등급 > employeeCode */
	public ArrayList<AuthBean> getUserInfo(Connection conn, AuthBean auth) {
		ArrayList<AuthBean> accessList = new ArrayList<AuthBean>();
		AuthBean ab = null;
		PreparedStatement pstmt;
		ResultSet rs;
		
		String sql = "SELECT * FROM ACCESSINFO WHERE EMPLOYEECODE = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, auth.getEmployeeCode());
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ab = new AuthBean();
				ab.setEmployeeCode(rs.getNString("EMPLOYEECODE"));
				ab.setEmployeeName(rs.getNString("EMPLOYEENAME"));
				ab.setAccessTime(rs.getNString("ACCESSTIME"));
				ab.setEmployeeLevel(rs.getNString("EMPLOYEELEVEL"));
				
				accessList.add(ab);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return accessList;
	}
	
	public ArrayList<AuthBean> getEPInfo(Connection conn) {
		ArrayList<AuthBean> accessList = new ArrayList<AuthBean>();
		AuthBean ab = null;
		PreparedStatement pstmt;
		ResultSet rs;
		
		String sql = "SELECT * FROM EMP ";
		try {
			pstmt = conn.prepareStatement(sql);
			
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ab = new AuthBean();
				ab.setEmployeeCode(rs.getNString("EMP_CODE"));
				ab.setEmployeeName(rs.getNString("EMP_NAME"));
				ab.setEmployeeLevel(rs.getNString("EMP_LEVEL"));
				ab.setEmployHiredate(rs.getNString("EMP_HIREDATE"));
				accessList.add(ab);
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		return accessList;
	}
	
	public ArrayList<MemberBean> getMMInfo(Connection conn) {
		ArrayList<MemberBean> accessList = new ArrayList<MemberBean>();
		MemberBean ab = null;
		PreparedStatement pstmt;
		ResultSet rs;
		
		String sql = "SELECT * FROM MEM";
		try {
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ab = new MemberBean();
				ab.setMemberCode(rs.getNString("MEM_CODE"));
				ab.setMemberName(rs.getNString("MEM_NAME"));
				ab.setMemberPhone(rs.getNString("MEM_PHONE"));
				accessList.add(ab);
			}
		} catch (SQLException e) {e.printStackTrace();}
		
		return accessList;
	}
	
	public ArrayList<GoodsBean> getGOInfo(Connection conn) {
		ArrayList<GoodsBean> accessList = new ArrayList<GoodsBean>();
		GoodsBean ab = null;
		PreparedStatement pstmt;
		ResultSet rs;
		String sql = "SELECT * FROM GOODSINFO";
		try {
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				ab = new GoodsBean();
				ab.setGoCode(rs.getNString("GOCODE"));
				ab.setGoName(rs.getNString("GONAME"));
				ab.setCaCode(rs.getNString("CACODE"));;
				ab.setCaName(rs.getNString("CANAME"));
				ab.setGoCost(rs.getInt("GOCOST"));
				ab.setGoPrice(rs.getInt("GOPRICE"));
				ab.setGoStock(rs.getInt("GOSTOCK"));
				
				accessList.add(ab);
			}
		} catch (SQLException e) {e.printStackTrace();}
		return accessList;
	}
	
	public int accessState(Connection conn, AuthBean auth) {
		int result = -1;
		ResultSet rs;
		PreparedStatement pstmt;
		String sql = "SELECT SUM(AHT_ACTION) FROM AHT WHERE AHT_EMPCODE = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, auth.getEmployeeCode());
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int updEmployeePassword(Connection conn, AuthBean auth) {
		int result = -1;
		PreparedStatement pstmt = null;
		String sql = "UPDATE EMP SET EMP_PASSWORD = ? WHERE EMP_CODE = ?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, auth.getEmployeePassword());
			pstmt.setNString(2, auth.getEmployeeCode());
			
			result = pstmt.executeUpdate(); //update된 개수
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
//"goodsCode", "goodsName", "goodsCost", "goodsPrice", "goodsStock", "categoryName"
	public int updGoodsInfo(Connection conn, GoodsBean goods) {
		int result = -1;
		PreparedStatement pstmt = null;
		String sql = "UPDATE GOO SET GOO_NAME = ?, GOO_COST = ?, GOO_PRICE = ?, GOO_STOCK = ? WHERE  GOO_CODE = ?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, goods.getGoName());
			pstmt.setInt(2, goods.getGoCost());
			pstmt.setInt(3, goods.getGoPrice());
			pstmt.setInt(4, goods.getGoStock());
			pstmt.setNString(5, goods.getGoCode());
			
			result = pstmt.executeUpdate(); //update된 개수
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("dao: "+result);
		return result;
	}
	
	public int insOrders(Connection conn, OrdersBean orders) {
		int result = -1;
		PreparedStatement pstmt;
		String sql = "INSERT INTO ORD(ORD_EMPCODE, ORD_DATE, ORD_STATE) "
				+ "VALUES(?, SYSDATE, ?)";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, orders.getEmployeeCode());
			pstmt.setNString(2, orders.getCaCode());
			
			result = pstmt.executeUpdate();
		} catch (SQLException e) {e.printStackTrace();}
		return result;
	}
	
	public void getOrderDate(Connection conn, OrdersBean orders) {
		PreparedStatement pstmt;
		ResultSet rs;
		String sql = "SELECT TO_CHAR(MAX(ORD_DATE), 'YYYYMMDDHH24MISS') AS ODDATE FROM ORD "
				+ "WHERE ORD_EMPCODE=?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, orders.getEmployeeCode());
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				orders.setOrderDate(rs.getNString("ODDATE"));	
			}
			 
			
		} catch (SQLException e) {e.printStackTrace();}
	}
	
	public int insOrdersDetail(Connection conn, OrdersBean orders) {
		int result = -1;
		PreparedStatement pstmt;
		String sql = "INSERT INTO ODT(ODT_ORDEMPCODE, ODT_ORDDATE, ODT_GOOCODE, ODT_QUANTITY) "
				+ "VALUES(?, TO_DATE(?, 'YYYYMMDDHH24MISS'), ?, ?)";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, orders.getEmployeeCode());
			pstmt.setNString(2, orders.getOrderDate());
			for(int recordIdx=0; recordIdx<orders.getOrderList().size(); recordIdx++) {
				pstmt.setNString(3, orders.getOrderList().get(recordIdx).getGoodsCode());
				pstmt.setInt(4, orders.getOrderList().get(recordIdx).getGoodsQty());
				if(pstmt.executeUpdate() == 0) {
					result = 0;
					break;
				}else {result = 1;}
			}
						
		} catch (SQLException e) {e.printStackTrace();}
		
		return result;
	}
	
	public int updOrders(Connection conn, OrdersBean order) {
		int result = -1;
		PreparedStatement pstmt;
		String sql = "UPDATE ORD SET ORD_STATE = 'OC' "
				+ "WHERE ORD_EMPCODE = ? AND ORD_DATE = TO_DATE(?, 'YYYYMMDDHH24MISS')";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, order.getEmployeeCode());
			pstmt.setNString(2, order.getOrderDate());
			
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int isMember(Connection conn, OrdersBean order) {
		int result = -1;
		String sql = "SELECT COUNT(*) FROM MEM WHERE MEM_CODE = ?";
		ResultSet rs = null;
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, order.getMemberCode());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				result = rs.getInt(1); //하나의 레코드 하나의 데이터 = 스칼라데이터
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public int insPoint(Connection conn, OrdersBean order) {
		int result = -1;
		String sql = "INSERT INTO POI(POI_ORDEMPCODE, POI_ORDDATE, POI_MEMCODE, POI_AMOUNT, POI_ACTION) "
				+ "VALUES(?, TO_DATE(?, 'YYYYMMDDHH24MISS'), ?, ?, ?)";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, order.getEmployeeCode());
			pstmt.setNString(2, order.getOrderDate());
			pstmt.setNString(3, order.getMemberCode());
			pstmt.setInt(4, order.getAmount());
			pstmt.setInt(5, order.getAction());
			
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public int updateMemberPhone(Connection conn, MemberBean mb) {
		int result = -1;
		PreparedStatement pstmt;
		String sql = "UPDATE MEM SET MEM_PHONE = ? WHERE MEM_CODE = ?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1,mb.getMemberPhone());
			pstmt.setNString(2, mb.getMemberCode());
			
			result = pstmt.executeUpdate();
			
		}catch(SQLException e) {e.printStackTrace();}
		return result;
	}
	
	public void getAmount(Connection conn, OrdersBean order) {
		ResultSet rs;
		String sql = "SELECT (GOO.GOO_PRICE * ODT.ODT_QUANTITY)*10/100 AS AMOUNT "
				+ "FROM ODT INNER JOIN GOO ON ODT.ODT_GOOCODE = GOO.GOO_CODE "
				+ "WHERE ODT_ORDEMPCODE = ? AND ODT_ORDDATE = TO_DATE(?, 'YYYYMMDDHH24MISS')";
		PreparedStatement pstmt;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setNString(1, order.getEmployeeCode());
			pstmt.setNString(2, order.getOrderDate());
			
			rs = pstmt.executeQuery();
			while(rs.next()) {
				order.setAmount(Integer.parseInt(rs.getNString("AMOUNT")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public int insGoods(Connection connection, GoodsBean goods) {
		int result = -1;
		PreparedStatement prst;
		
		String sql = "INSERT INTO GOO(GOO_CODE, GOO_NAME, GOO_COST, GOO_PRICE, GOO_STOCK, GOO_CGICODE) VALUES(?, ?, ?, ?, ?, ?)";

		System.out.println("인설트 굿즈 체크4");
		System.out.println(goods.getGoCode());
		System.out.println(goods.getGoName());
		System.out.println(goods.getGoCost());
		System.out.println(goods.getGoPrice());
		System.out.println(goods.getGoStock());
		System.out.println(goods.getCaCode());
		
		try {
			prst = connection.prepareStatement(sql);
			prst.setNString(1, goods.getGoCode());
			prst.setNString(2, goods.getGoName());
			prst.setInt(3, goods.getGoCost());
			prst.setInt(4, goods.getGoPrice());
			prst.setInt(5, goods.getGoStock());
			prst.setNString(6, goods.getCaCode());
			
			result = prst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(result);
		return result;
	}
	
	/* 직원 추가 */
	public int insEmployee(Connection conn, AuthBean auth) {
		int result = -1;
		String sql = "INSERT INTO EMP(EMP_CODE, EMP_NAME, EMP_LEVEL, EMP_PASSWORD, EMP_HIREDATE) " + "VALUES(?,?,?,?,SYSDATE)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(sql);
			
			pstmt.setNString(1, auth.getEmployeeCode());
			pstmt.setNString(2, auth.getEmployeeName());
			pstmt.setNString(3, auth.getEmployeeLevel());
			pstmt.setNString(4, auth.getEmployeePassword());
			result = pstmt.executeUpdate();
			} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	/* 직원레벨 수정 */
	public int updEmployeeLevel(Connection conn, AuthBean auth) {
		int result = -1;
		PreparedStatement pstmt;
		String sql = "UPDATE EMP SET EMP_LEVEL = ? WHERE EMP_CODE = ?";
		
		try {
			pstmt=conn.prepareStatement(sql);
			pstmt.setNString(1, auth.getEmployeeLevel());
			pstmt.setNString(2, auth.getEmployeeCode());
			result = pstmt.executeUpdate();
		} catch (SQLException e) {e.printStackTrace();}
			return result;
	}
	
	/* 출근 기록 */
	public ArrayList<AuthBean> getAccessInfo(Connection conn, AuthBean auth) {
		ArrayList<AuthBean> accessList = new ArrayList<AuthBean>();
		PreparedStatement pstmt;
		ResultSet rs;
		
		String sql = "SELECT * FROM AHT WHERE AHT_EMPCODE=? AND TO_DATE(TO_CHAR(SYSDATE,'YYYYMM'),'YYYYMM') = TO_DATE(TO_CHAR(AHT_DATE,'YYYYMM'),'YYYYMM')";
		try {
			pstmt = conn.prepareStatement(sql);		
			pstmt.setNString(1, auth.getAccessEpCode());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				auth = new AuthBean();
				auth.setAccessTime(rs.getNString("AHT_DATE"));
				auth.setAccessAction(rs.getInt("AHT_ACTION"));
				accessList.add(auth);
			}
		} catch (SQLException e) {e.printStackTrace();}		
		return accessList;
	}
	
	/* 금월 매출 일일 판매 */
	public ArrayList<GoodsBean> getProfit(Connection conn, HttpServletRequest req, GoodsBean goBean) {
		ArrayList<GoodsBean> goodsList = new ArrayList<GoodsBean>();
		PreparedStatement pstmt;
		ResultSet rs;
		String sql = "SELECT  GOCODE, GONAME,  ODDATE,  SUM(GOPRICE*QUANTITY) AS DDSALES,  SUM((GOPRICE-GOCOST)*QUANTITY) AS PROFIT"
				+ "  FROM GOODSSALESINFO  WHERE GOCODE = ?  AND SUBSTR(ODDATE,0,6) = TO_CHAR(SYSDATE,'YYYYMM') GROUP BY GOCODE,GONAME,ODDATE";
		try {
			pstmt = conn.prepareStatement(sql);		
			pstmt.setNString(1, goBean.getGoCode());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				goBean = new GoodsBean();
				goBean.setGoCode(rs.getNString("GOCODE"));
				goBean.setGoName(rs.getNString("GONAME"));
				goBean.setOdDate(rs.getNString("ODDATE"));
				goBean.setDdSales(rs.getNString("DDSALES"));
				goBean.setProfit(rs.getNString("PROFIT"));
					goodsList.add(goBean);
			}
		} catch (SQLException e) {e.printStackTrace();}		
		return goodsList;
	}
}