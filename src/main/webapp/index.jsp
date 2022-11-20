<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Access Page</title>
<script>
function accessCtl() {
	//document 현재 실행되고 있는 문서
	const accessInfo = [];
	accessInfo.push(document.getElementsByName("employeeCode"));
	accessInfo.push(document.getElementsByName("employeePassword"));
	
	//유효성 검사
	/* employeeCode.lenth == 5 */
	if(accessInfo[0][0].value.length != 5) {
		alert("직원코드는 5자리 입니다.");
		return;
	}
	/* Server 요청 */
	const form = document.getElementsByName("accessForm")[0];
	form.submit();
}

function getMessage(message) {
	if(message != "" && message != null){
		alert(message);
	}
}
</script>
</head>
<body onload="getMessage('${param.message}')">
	<h1>Hello! welcome</h1>
	<form name="accessForm" action="Access" method="post">
		<input type="text" name = "employeeCode" placeholder="Employee Code" /> 
		<input type="password" name = "employeePassword" placeholder="Access Code" /> 
		<input type="button" value="Access" onClick = "accessCtl()"/>
	</form>
</body>
</html>