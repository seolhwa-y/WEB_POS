<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>매장통계</title>
<script src="js/main.js" type = "text/javascript"></script>
<link rel="stylesheet" href = "css/main.css"/>
<link rel="stylesheet" href = "css/statistics.css"/>
<script>
	function noMeanfunction(data) {
		
		let em = data.split(":");
		
		const formData = "epCode"+"="+em[0];
		const jobCode = "AccessInfo";
		sendAjax(jobCode,formData,"searchAccess");
		
	}
	
	function meanFunction(data) {
		
		let em = data.split(":");
		const formData = "goCode"+"="+em[0];
		const jobCode = "GoodsProfit";
		sendAjax(jobCode,formData,"searchProfit");
		
	}

	function sendAjax(jobcode,formData,funcName){
		
		const xhr = new XMLHttpRequest();
		xhr.open('POST',jobcode);
		//ajax시작 xhr.send(formData);
		xhr.setRequestHeader("Content-type","application/x-www-form-urlencoded;charset=UTF-8");
		xhr.send(formData);
		
		xhr.onreadystatechange = function(){		
			if(xhr.readyState == xhr.DONE && xhr.status == "200"){			
					//setOrderList(xhr.responseText);
					window[funcName](xhr.responseText);
					}
				}	
		}

	function searchAccess(title){
		const modal = document.querySelector(".modalBox");
		let modalTitle = document.querySelector(".modalTitle");
		let modalCommand = document.querySelector(".modalCommand");
		
		
		modalTitle.innerHTML = "금월 출퇴근 기록" + "<div class =\'closeBox\' onClick = \'modalClose()\'>X</div>";
		
		
		
		let table = "<table id=\"goodsProfit\"><tr><th>날짜</th><th>상태</th></tr>";
		
		table += title+"</table>";
		modalCommand.innerHTML = table;
		
		modal.style.display = "block";
	}
	
	
	function searchProfit(title){
		const modal = document.querySelector(".modalBox");
		let modalTitle = document.querySelector(".modalTitle");
		let modalCommand = document.querySelector(".modalCommand");

		modalTitle.innerHTML = "매출 통계" + "<div class =\'closeBox\' onClick = \'modalClose()\'>X</div>";
		
		
		
		let table = "<table id=\"goodsProfit\"><tr><th>상품코드</th><th>상품명</th><th>날짜</th><th>매출</th><th>영업이익</th></tr>";
		
		table += title+"</table>";
		modalCommand.innerHTML = table;
		
		modal.style.display = "block";
	}
	
	
	
	function searchSales(title,data){
		const modal = document.querySelector(".modalBox");
		let modalTitle = document.querySelector(".modalTitle");
		let modalCommand = document.querySelector(".modalCommand");
		
		modalTitle.innerHTML = title + "<div class =\'closeBox\' onClick = \'modalClose()\'>X</div>";
		modal.style.display = "block";
	}
	
	
</script>

</head>
<body>
	<div id="header">
		<span class="top-span"> ${employeeName}(${levelName}) Last Access : ${accessTime}
			<input type="button" value="로그아웃" class="small-btn" onMouseOver="changeBtnCss(this, 'small-btn-over')" onMouseOut="changeBtnCss(this, 'small-btn')" onClick="accessOut('${hiddenData }')" />
		</span>
	</div>
	<form name="serverCall" method="post"></form>
<div id="container">
	<div id="ep">
		<div class = "title">직원정보</div>
	<table>	
			<tr>
			<th>직원코드</th>
			<th>직원이름</th>
			<th>직원등급</th>
			<th>직원입사일</th>
			<th>출퇴근기록</th>
			</tr>
			${epInfo}		
		</table>
	</div>
	
	<div id="st">
		<div class = "title">매출정보</div>
	<table >	
			<tr>
			<th>상품코드</th>
			<th>상품명</th>
			<th>매입가</th>
			<th>판매가</th>
			<th>재고</th>
			<th>카테고리</th>
			<th>금월매출</th>

			</tr>
			${goInfo}		
		</table>
	</div>
	</div>
	<div class = "modalBox">
		<div class = "modalBody">
			<div class = "modalTitle"></div>				
			<div class = "modalCommand"></div>
			
		</div>
	</div>

	<div id="footer"></div>
</body>
</html>