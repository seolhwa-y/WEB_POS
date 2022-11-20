<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>매장관리</title>
<script type="text/javascript" src="js/main.js"></script>
<script>
	function accessOut() {
		let form = document.getElementsByName("serverCall")[0];
		form.action = "AccessOut";
		form.submit();
	}
	function insEmployee(title) {
		const modal = document.querySelector(".modalBox");
		let modalTitle = document.querySelector(".modalTitle");
		let modalCommand = document.querySelector(".modalCommand");
		
		modalTitle.innerHTML = title + "<div class=\'closeBox\' onClick=\'modalClose()\'> X</div>";
	
		/* Table 추가 */
		let table = "<table><tr><th>직원코드</th><th>직원이름</th><th>직원등급</th><th>비밀번호</th></tr>";

		modalCommand.innerHTML = table;
		
		/* HTML Object 추가 : JS영역에 개체 생성 수 -> HTML영역에 추가 : appendChild()*/
		let obj = [];
		obj[0]= createInput("text", "employeeCode", "box", null, "직원코드", null);
		modalCommand.appendChild(obj[0]);
		obj[1]= createInput("text", "employeeName", "box", null, "직원이름", null);
		modalCommand.appendChild(obj[1]);
		obj[2]= createInput("text", "employeeLevel", "box", null, "직원등급", null);
		modalCommand.appendChild(obj[2]);
		obj[3] = createInput("password", "employeePassword", "box", null, "비밀번호", null);
		modalCommand.appendChild(obj[3]);
	
		/* 줄바꿈 */
		modalCommand.appendChild(document.createElement("br"));
		
		/* HTML Object 추가 :  JS영역에 개체 생성 후 --> HTML 영역에 추가 : appendChild() */
		let objBtn = createInput("button", null, "small-btn", "업데이트", null, null);
		objBtn.addEventListener("click", function(){
			serverTransfer(obj, "RegEp");
		});
		modalCommand.appendChild(objBtn);
		modal.style.display = "block";
	}
	
	function changePw(title, data) {
		const modal = document.querySelector(".modalBox");
		let modalTitle = document.querySelector(".modalTitle");
		let modalCommand = document.querySelector(".modalCommand");
		let input = document.createElement("input");
		modalCommand.innerText = "";
		
		modalTitle.innerHTML = title+"<div class='closeBox' onclick='modalClose()'>X</div>";

		let info = data.split(":");
		
		/* Table */
		let table = "<table><tr><th>직원코드</th><th>직원이름</th><th>직원등급</th></tr>"
		table += "<tr><td>"+info[0]+"</td><td>"+info[1]+"</td><td>"+info[2]+"</td></tr></table>"
		modalCommand.innerHTML = table;
		
		/* html Object 추가: code, password 두개의 개체를 배열로 접근 JS영역에 개체 생성 후 --> HTML 영역에 추가: appendChild() */
		//type, name, className, value, placeholder, isRead
		let obj = [];
		obj[0] = createInput("password","employeePassword","inputBox",null,"변경 할 패스워드",null); //수정할 패스워드
		modalCommand.appendChild(obj[0]);
		obj[1] = createInput("hidden","employeeCode","inputBox",info[0],null,null); //수정하려는 직원의 코드 - hidden
		modalCommand.appendChild(obj[1]);
		
		/* 줄바꿈 */
		modalCommand.appendChild(document.createElement("br"));
		
		/* html Object 추가: 업데이트, 업데이트 버튼*/
		let objBtn = createInput("button",null,"btn","업데이트",null,null);
		// 동적으로 생성된 개체에 이벤트 추가 addEventListener(event, function)
		objBtn.addEventListener("click", function() {
			serverTransfer(obj, "UpdPassword");			
		});
		modalCommand.appendChild(objBtn);
		
		objBtn = createInput("button",null,"btn","초기화",null,null);
		modalCommand.appendChild(objBtn);
		
		modal.style.display = "block";
		/* let form = document.getElementsByName("serverCall")[0];
		form.action = "updPw";
		form.submit(); */
	}
	
	function changeLevel(title, data) {
		const modal = document.querySelector(".modalBox");
		let modalTitle = document.querySelector(".modalTitle");
		let modalCommand = document.querySelector(".modalCommand");
		
		modalTitle.innerHTML = title + "<div class=\'closeBox\' onClick=\'modalClose()\'> X</div>";
		
		let info = data.split(":");
	
		/* Table 추가 */
		let table = "<table><tr><th>직원코드</th><th>직원이름</th><th>직원등급</th></tr>";
		table += "<tr><td>" + info[0] + "</td><td>" + info[1] + "</td><td>" + info[2] + "</td></tr></table>";
		modalCommand.innerHTML = table;
		
		/* HTML Object 추가 : JS영역에 개체 생성 수 -> HTML영역에 추가 : appendChild()*/
		let obj = [];
		obj[0]= createInput("text", "employeeLevel", "box", null, "변경 할 등급", null);
		modalCommand.appendChild(obj[0]);
		obj[1] = createInput("hidden", "employeeCode", "box", info[0], null, null);
		modalCommand.appendChild(obj[1]);
	
		/* 줄바꿈 */
		modalCommand.appendChild(document.createElement("br"));
		
		/* HTML Object 추가 :  JS영역에 개체 생성 후 --> HTML 영역에 추가 : appendChild() */
		let objBtn = createInput("button", null, "small-btn", "업데이트", null, null);
		// 동적으로 생성된 개체에 이벤트 추가  addEventListener(event, function)
		objBtn.addEventListener("click", function(){
			serverTransfer(obj, "UpdLevel");
		});
		modalCommand.appendChild(objBtn);
		
		objBtn = createInput("button", null, "small-btn", "초기화", null, null);
		modalCommand.appendChild(objBtn);
		modal.style.display = "block";
	}
	
	function changePhone(title, data) {
		let info = data.split(":");
		
		const modal = document.querySelector(".modalBox");
		let modalTitle = document.querySelector(".modalTitle");
		let modalCommand = document.querySelector(".modalCommand");
		let input = document.createElement("input");

		modalTitle.innerHTML = title+"<div class='closeBox' onclick='modalClose()'>X</div>";

		/* Table */
		let table = "<table><tr><th>회원코드</th><th>회원이름</th><th>전화번호</th></tr>"
		table += "<tr><td>"+info[0]+"</td><td>"+info[1]+"</td><td>"+info[2]+"</td></tr></table>"
		modalCommand.innerHTML = table;
		
		/* html Object 추가: JS영역에 개체 생성 후 --> HTML 영역에 추가: appendChild() */
		//type, name, className, value, placeholder, isRead
		let obj = [];
		obj[0] = createInput("text","memberPhone","inputBox",info[2],"변경 할 전화번호",null);
		modalCommand.appendChild(obj[0]);
		obj[1] = createInput("hidden","memberCode","inputBox",info[0],null,null); //수정하려는 직원의 코드 - hidden
		modalCommand.appendChild(obj[1]);
		
		/* 줄바꿈 */
		modalCommand.appendChild(document.createElement("br"));
		
		let objBtn = createInput("button",null,"btn","업데이트",null,null);
		objBtn.addEventListener("click", function () {
			serverTransfer(obj, "ChangePhone");			
		});
		modalCommand.appendChild(objBtn);
		
		objBtn = createInput("button",null,"btn","초기화",null,null);
		modalCommand.appendChild(objBtn);
		
		modal.style.display = "block";
	}
	function insGoods(title) {
		let item = ["goodsCode", "goodsName", "goodsCost", "goodsPrice", "goodsStock", "categoryCode"];
		const modal = document.querySelector(".modalBox");
		let modalTitle = document.querySelector(".modalTitle");
		let modalCommand = document.querySelector(".modalCommand");

		modalTitle.innerHTML = title + "<div class=\'closeBox\' onClick=\'modalClose()\'> X</div>";
		let table = "<table class='goTable'><tr><th>상품 코드</th><th>상품 이름</th><th>구매 비용</th><th>판매 가격</th><th>재고</th><th>분류 코드</th></tr>";
		table += "<tr class='goTr'>";
		for(idx=0; idx<item.length; idx++){
			table += "<td>";
			table += ("<input type='text' class='goInput' name='" + item[idx] + "' value=''/>");
			table += "</td>";
		}
		table += "</tr></table><br/><div>상품코드는 기본 코드에 대표 아이디(00003)+분류 코드를 앞에 추가해서 입력</div><br/><div>상품 분류 코드는 아래와 같음</div><br/>";
		
		table += "<table class = 'CGICODE'><tr><th>음료</th><th>농축산물</th><th>과자류</th><th>식자재</th><th>인스턴트</th></tr>";
		table += "<tr><td>GB</td><td>GF</td><td>GS</td><td>GG</td><td>GI</td><tr></table>"
		
		modalCommand.innerHTML = table;
		/* HTML Object 추가 :  JS영역에 개체 생성 후 --> HTML 영역에 추가 : appendChild() */
		let objBtn = createInput("button", null, "sub-btn", "추가", null, null);
		// 동적으로 생성된 개체에 이벤트 추가  addEventListener(event, function)
		objBtn.addEventListener("click", function(){
			let goCode = document.getElementsByName(item[0])[0];
			if(goCode.name=="goodsCode"&&goCode.value.length==10) {
				transfer(item, "RegGo");
			} else {
				alert("상품코드는 10자리 입니다.");
				return;
			}
		});
		modalCommand.appendChild(objBtn);
		
		objBtn = createInput("button", null, "sub-btn", "초기화", null, null);
		modalCommand.appendChild(objBtn);
		
		modal.style.display = "block";
	}
	
	function changeGoods(title, data) {
		let item = ["goodsCode", "goodsName", "goodsCost", "goodsPrice", "goodsStock", "categoryName"];
		let info = data.split(":");
		
		const modal = document.querySelector(".modalBox");
		let modalTitle = document.querySelector(".modalTitle");
		let modalCommand = document.querySelector(".modalCommand");
		
		modalTitle.innerHTML = title+"<div class='closeBox' onclick='modalClose()'>X</div>";

		/* Table */
		let table = "<table><tr><th>상품코드</th><th>상품이름</th><th>구매비용</th><th>판매가격</th><th>재고</th><th>상품분류</th></tr>";
		table += "<tr>";
		//type, name, className, value, placeholder, isRead
		for(idx=0;idx<info.length;idx++) {
			table += "<td>";
			table += ((idx<=4)
					?"<input class='goInput' type='text' name='"+item[idx]+"' value='"+info[idx] + "' " + ((idx==0)? "readOnly />":"/>")
					:info[idx]); //변경X(재고, 상품분류)
			table += "</td>";
		}
		table += "</tr></table>"
		
		modalCommand.innerHTML = table;
		
		/* 줄바꿈 */
		modalCommand.appendChild(document.createElement("br"));

		/* html Object 추가: JS영역에 개체 생성 후 --> HTML 영역에 추가: appendChild() */
		let objBtn = createInput("button",null,"btn","업데이트",null,null);
		objBtn.addEventListener("click", function () {
			let form = document.getElementsByName("serverCall")[0];
			form.action = "UpdGoInfo";
			for(idx=0;idx<item.length-1;idx++){
				form.appendChild(document.getElementsByName(item[idx])[0]);
			}
			form.submit();
		});
		
		modalCommand.appendChild(objBtn);
		
		objBtn = createInput("button",null,"btn","초기화",null,null);
		objBtn.addEventListener("click", function() {
		});
		modalCommand.appendChild(objBtn);
		
		modal.style.display = "block";
	}
	
	function transfer(item,action) {
		let form = document.getElementsByName("serverCall")[0];
		form.setAttribute("action", action);
		for (idx = 0; idx<item.length; idx++) {
			form.appendChild(document.getElementsByName(item[idx])[0]);
		}
		form.submit();
	}
	
	function serverTransfer(obj, action) {
		let form = document.getElementsByName("serverCall")[0];
		form.action = action;
		
		for(idx=0; idx<obj.length; idx++){
			if(obj[idx].value.length <= 0) {
				alert("입력값 없음");
				return;
			}
			form.appendChild(obj[idx]);
		}
		form.submit();	
	}
	
</script>
<link rel="stylesheet" href="css/main.css" />
<style>
table {
	margin: 0 auto;
}
tr {
	height: 2rem;
}
#dashboard {
	position: relative; margin: 0 auto;
	width: 100%; height: 37.5rem;
	top:3rem;left:0;right:0; padding-left: 0.5%;
	text-align: center;
}

#left	{
	float:left;
	width: 25%; height:90%; margin-right:1%;
	border:2px solid #C2D8DC;
	overflow: auto;
}

#middle	{
	float:left;
	width: 25%; height:90%; margin-right:1%;
	border:2px solid #C2D8DC;
	overflow: auto;
}

#right	{
	float:left;
	width: 46%; height:90%;
	border:2px solid #C2D8DC;
	overflow:auto;
}

.title {
	width: 100%; height: 2rem;
	padding: 1rem 0;
	color: #FFFFFF;	background-color: #C2D8DC;
	font-size: 1.5rem; font-weight: 800;
}

.list {
	width: 100%; padding-bottom: 1rem;
	color: #AEC2C6;
	font-size: 0.9rem; font-weight: 600;
}

.btn {
	width:50%; height:25px;
	background-color:#C2D8DC; color:#ffffff;
	border: 1px solid #ffffff;
	font-weight: 800;
}
</style>
</head>
<body>
	<div id="header">
		<span class="top-span">${storeName} 매장에
			${employeeName}(${levelName})님이 ${accessTime}에 로그인 하셨습니다. <input
			type="button" class="small-btn" value="로그아웃"
			onClick="accessOut()"
			onMouseOver="changeBtnCss(this, 'small-btn-over')"
			onmouseout="changeBtnCss(this,'small-btn')" />
		</span>
	</div>
	<div id="dashboard">
		<div id="left">
			<div class="title">직원정보</div>
			<div class="list">${epInfo}</div>
		</div>
		<div id="middle">
			<div class="title">회원정보</div>
			<div class="list">${mmInfo}</div>
		</div>
		<div id="right">
			<div class="title">상품정보</div>
			<div class="list" id="lastInfo">${goInfo}</div>
		</div>
	</div>
	<div id="footer"></div>
	<div class="modalBox">
		<div class="modalBody">
			<div class="modalTitle"></div>
			<div class="modalCommand"></div>
		</div>
	</div>
	<form name="serverCall" method="post"></form>
</body>
</html>