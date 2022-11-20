<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pos</title>
<script src="js/main.js" type="text/javascript"></script>
<script src="js/pos.js" type="text/javascript"></script>
<link rel="stylesheet" href="css/main.css" />
<link rel="stylesheet" href="css/pos.css" />
<style>
#container {
	position: relative; 
	width: 100%; height: 52.5rem;
	top:3rem;left:0;right:0;
	text-align: center;
}
#salesInfo	{
	float:left; position: relative;
	padding: 1%;
	min-width: min-content;
	width: 64%; height:90%; margin:0 0.5%;
	border:2px solid #C2D8DC;
	overflow: auto;
}
#payInfo	{
	float:right; position: relative;
	padding: 1%;
	width: 28%; height:90%; margin: 0 0.5%;
	border:2px solid #C2D8DC;
	overflow: auto;
}
.title {
	width: 100%; height: 2rem;
	padding: 1rem 0;
	color: #FFFFFF;	background-color: #C2D8DC;
	font-size: 1.5rem; font-weight: 800;
}
.list {
	width: 100%; padding-bottom: 1rem;
	color: #4C4C4C;
	font-size: 0.9rem; font-weight: 600;
}
</style>
</head>
<body>
	<div id="header">
		<span class="top-span">
			${employeeName}(${levelName}) Last Access : ${accessTime} <input
			type="button" value="로그아웃" class="small-btn"
			onMouseOver="changeBtnCss(this, 'small-btn-over')"
			onMouseOut="changeBtnCss(this, 'small-btn')"
			onClick="accessOut()" />
		</span>
	</div>
	<div id="container">
		<div id="salesInfo">
			<div class="title">상품판매 정보</div>
			<div class="goodsBox">
				<div class="goods">
					<div class="goodsHeader">순번</div>
					<div class="goodsHeader">코드</div>
					<div class="goodsHeader">상품명</div>
					<div class="goodsHeader">판매가격</div>
					<div class="goodsHeader">수량</div>
					<div class="goodsHeader">합계</div>
				</div>
				<div id="goodsList">
				</div>
			</div>
			<div id="optionBtn">
				<div id="sBtnBox">
					<input class="btns sBtn" type="button" value="▲" onMouseOver="changeBtnCss(this, 'btns-over sBtn')"
					onmouseout="changeBtnCss(this,'btns sBtn')" onclick="increase(1)" />
					<input class="btns sBtn" type="button" value="▼" onMouseOver="changeBtnCss(this, 'btns-over sBtn')"
					onmouseout="changeBtnCss(this,'btns sBtn')" onclick="increase(-1)"/>
				</div>
				<input class="btns mBtn" type="button" value="상품삭제" onMouseOver="changeBtnCss(this, 'btns-over mBtn')"
				onmouseout="changeBtnCss(this,'btns mBtn')" onclick="delRecord()"/>
				<input class="btns mBtn" type="button" value="주문보류" onMouseOver="changeBtnCss(this, 'btns-over mBtn')"
				onmouseout="changeBtnCss(this,'btns mBtn')" onclick="modifyOrder()"/>
				<input class="btns mBtn" type="button" value="보류목록" onMouseOver="changeBtnCss(this, 'btns-over mBtn')"
				onmouseout="changeBtnCss(this,'btns mBtn')"onclick="modifyOrderList('보류목록')"/>
				<input class="btns mBtn" type="button" value="결제" onMouseOver="changeBtnCss(this, 'btns-over mBtn')"
				onmouseout="changeBtnCss(this,'btns mBtn')" onclick="orderComplete() "/>
			</div>
			<div class="search">
				<input name="goodsCode" id="searchBox" type="text"  placeholder="바코드" onkeyup="searchGoods(event)" />
			</div>
		</div>
		<div id="payInfo">
			<div class="title">결제정보</div>
			<div><div class="title subtitle">주문금액</div><input type="text" class="title subtitle contents" id="total" readonly></input></div>
			<div><div class="title subtitle">받은돈</div><input type="text" class="title subtitle contents" id="money" onkeyup="setChange(this, event)"></input></div>
			<div><div class="title subtitle">거스름돈</div><input type="text" class="title subtitle contents" id="change" readonly></input></div>
			<div class="search">
				<input id="payBtn" class="btns mBtn" type="button" value="결제완료" onMouseOver="changeBtnCss(this, 'btns-over mBtn')"
			onmouseout="changeBtnCss(this,'btns mBtn')" onclick="member('회원조회')" />
			</div>
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