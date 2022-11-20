<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Pos Menu</title>
<script src="js/main.js" type="text/javascript"></script>
<script>
function movePage(level, action){
	if((action == "MoveMgr" || action == "MoveStatus") && level == "아르바이트"){
		alert("매니저 등급 이상만 접근가능합니다.");
		return;
	}
	
	let form = document.getElementsByName("serverCall")[0];
	form.setAttribute("action", action);
	form.submit();
	
}
</script>
<link rel="stylesheet" href="css/main.css"/>
<style>
#outline { position:absolute; top:50%; left:50%; transform:translate(-50%, -50%);
		   width:70%; height:210px;}
#space	 { float:left; width:5%; height:200px;}
.moveBtn { float:left; margin:0px;
		   width:32%; height:200px;
		   text-align:center; line-height:200px;
		   font-size:30pt; font-weight :900;
		   color:#C2D8DC; background-color: #FFFFFF;
		   border:5px solid #C2D8DC;
		  }
.moveBtnOver { float:left; margin:0px;
		   		width:32%; height:200px;
		   		text-align:center; line-height:200px;
		   		font-size:30pt; font-weight :900;
		   		color:#FFFFFF; background-color:#5D8C95;
		   		border:5px solid #5D8C95;
		   		cursor:pointer;}
</style>
</head>
<body>
	<div id="header">
		<span class="top-span"> ${employeeName}(${levelName}) Last Access : ${accessTime}
			<input type="button" value="로그아웃" class="small-btn" onMouseOver="changeBtnCss(this, 'small-btn-over')" onMouseOut="changeBtnCss(this, 'small-btn')" onClick="accessOut('${hiddenData }')" />
		</span>
	</div>
	${btn}
	<div id="footer"></div>
	<form name="serverCall" method="post"></form>
</body>
</html>