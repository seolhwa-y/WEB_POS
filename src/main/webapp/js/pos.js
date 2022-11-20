let list = [];
let items = ["num", "code", "name", "price", "qty", "amount"];
let selectedIdx = -1;
let totAmount = 0;
function searchGoods(event) {
	if (event.key != "Enter") return;

	let goodsCode = document.getElementsByName("goodsCode")[0];
	/* 바코드 유효성 검사: 입력 문자의 길이가 10자리 --> isCode(){} */
	if (isCode(goodsCode.value, 10)) {
		//ajax이용
		const jobCode = "SearchGoodsInfo";
		const formData = goodsCode.name + "=" + goodsCode.value;
		//formData.append(goodsCode.name, goodsCode.value);
		sendAjax(jobCode, formData, "setOrderList");
	} else {
		alert("상품코드를 확인해주세요");
	}
}

//Ajax
function sendAjax(jobCode, formData, funcName) {
	const ajax = new XMLHttpRequest();
	ajax.open('POST', jobCode); //default 비동기
	//ajax시작 ajax.send(formData); application/json
	ajax.setRequestHeader("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
	ajax.send(formData);

	ajax.onreadystatechange = function() {
		if (ajax.readyState == ajax.DONE && ajax.status == "200") {
			window[funcName](ajax.responseText);
			//setOrderList(ajax.responseText); //responseText = 서버로부터 넘어온 데이터
		}
	}
}

function setOrderList(data) {
	/* 순번, 코드, 상품명, 가격, 수량, 합계, 상태
	 * 순번, 합계 = 변경이 자주 일어남
	 * 코드, 상품명, 가격 = 서버 데이터
	 * 코드, 수량 = 결제 시 필요한 정보 */
	const search = document.getElementsByName("goodsCode")[0]; //검색창
	let isCheck = false; //새로운 품목: false, 이미 등록된 품목true
	if (data != "none") {
		if (data.length > 0) {
			/* 이미 등록된 상품이면 수량만 증가 */
			if (list.length > 0) {
				const goodsCode = data.substring(0, data.indexOf(":"));
				isCheck = increaseQty(goodsCode);
			}
			if (!isCheck) {
				let record = (data += (":1:0")).split(":");
				list.push(record);
			}
			makeOrder();
		}
	} else {
		alert("상품코드를 확인해주세요.");
		search.value = "";
		search.focus();
	}
}

function increaseQty(goodsCode) {
	let isCheck = false;
	//list[][0]
	for (rec = 0; rec < list.length; rec++) {
		if(list[rec][4]==0){
			if (goodsCode == list[rec][0]) {
				isCheck = true;
				list[rec][3] = Number(list[rec][3]) + 1;
				break;
			}
		}
	}
	return isCheck;
}

function selectIdx(idx) {
	selectedIdx = idx;
}

function increase(num) {
	selectedIdx = (selectedIdx != -1)?selectedIdx:list.length-1;
	if (Number(list[selectedIdx][3]) + num > 0) {
		list[selectedIdx][3] = Number(list[selectedIdx][3]) + num;
	} else {
		return;
	}
	makeOrder();
}

function makeOrder() {
	const search = document.getElementsByName("goodsCode")[0]; //검색창
	const frame = document.getElementById("goodsList");
	let total = document.getElementById("total");

	frame.innerText = "";
	total.value = "";
	totAmount = 0;
	let recordNum=0;
	for (rec = 0; rec < list.length; rec++) { //상품 레코드 하나
		if (Number(list[rec][4]) == 0) {
			recordNum++;
			const goodsInfo = document.createElement("div");
			goodsInfo.setAttribute("class", "goods");
			goodsInfo.setAttribute("onclick", "selectIdx(" + rec + ")");
			goodsInfo.setAttribute("onmouseover", "changeTd(this,'goods tdOver')");
			goodsInfo.setAttribute("onmouseout", "changeTd(this,'goods')");
			let record = [];
			for (idx = 0; idx < 6; idx++) { //상품 정보
				record.push(document.createElement("div"));
				record[idx].setAttribute("class", items[idx]);

				goodsInfo.appendChild(record[idx]);
			}
			//상품정보 set 0순번,1상품코드,2상품명,3가격,4수량,5합계
			record[0].innerText = recordNum;
			record[1].innerText = list[rec][0];
			record[2].innerText = list[rec][1];
			record[3].innerText = Number(list[rec][2]).toLocaleString();
			record[4].innerText = Number(list[rec][3]).toLocaleString();
			record[5].innerText = Number(list[rec][2] * list[rec][3]).toLocaleString();
			//list[rec][3]; 상품상태

			totAmount += (Number(list[rec][2]) * Number(list[rec][3]));
			frame.appendChild(goodsInfo);
			total.value = totAmount.toLocaleString();
		}
	}
	search.value = "";
	search.focus;
}

function delRecord() {
	selectedIdx = (selectedIdx != -1)?selectedIdx:list.length-1;
	list.splice(selectedIdx, 1);
	selectedIdx = -1;
	makeOrder();
}

function modifyOrder() {
	let total = document.getElementById("total");
	let max = findMaxNumber();
	
	for (rec=0;rec<list.length;rec++) {
		if(list[rec][4]==0) {
			list[rec][4] = max;
		}
	}
	makeOrder();
	total.value = "";
}
function modifyOrderList(title) {
	const modal = document.querySelector(".modalBox");
	let modalTitle = document.querySelector(".modalTitle");
	let modalCommand = document.querySelector(".modalCommand");
	
	modalTitle.innerHTML = title + "<div class='closeBox' onclick='modalClose()'>X</div>";
	/* 보류리스트 출력 
		1. 현재 처리되지 않은 주문리스트 여부 확인
		2. 보류된 리스트가 있는지 확인
	*/
	if(list.length > 0){
		if (list[list.length-1][4] != 0) {
			if(findMaxNumber() > 1) { //maxnumber=보류항목번호, maxnumber-1=보류항목 개수
				/*1: a외 1개 품목
				2: w외 1개 품목*/
				let max = findMaxNumber();
				let html = "<table><tr><th>번호</th><th>내용</th></tr>";				
				for(num=1;num<max;num++) {
					let cnt = -1;
					let info;
					for(rec=0;rec<list.length;rec++) {
						if(Number(list[rec][4])==num) {
							cnt++;
							if(cnt==0){
								info = list[rec][1];
							}
						}
					}
					info += ("외 "+cnt+"개의 품목");
					html += ("<tr onclick='moveOrderList("+num+","+cnt+")'><td>"+num+"</td><td>"+info+"</td></tr>");
				}
				html += "</table>";
				modalCommand.innerHTML = html;
			}
		} else {
			modalCommand.innerText = "현재 주문을 완료하신 후 목록을 불러오실 수 있습니다.";
		}
	} else modalCommand.innerText = "보류 목록이 존재하지 않습니다.";
	modal.style.display = "block";
}

function moveOrderList(number,count) {
	/* 1. 주문상태를 number에서 0으로 업데이트 //주문 진행중
	   2. number보다 큰 다른 보류번호에 -1
	   3. 보류번호가 number인 상품레코드를 배열의 맨마지막으로 이동
	   4. makeOrder()
	 */
	for(rec=0;rec<list.length;rec++) { //주문상태 변경
		list[rec][4] = (list[rec][4]==number)?0:
						(list[rec][4]>number)?list[rec][4]-1:list[rec][4];
	}
	let idx = -1;
	for(rec=list.length-1;rec>=0;rec--) { //배열의 맨뒤로 이동
		if(list[rec][4]==0){ //주문상태가 0인 친구
			idx = rec;
			let record = list[rec];
			list.push(record);
		}
	}
	list.splice(idx,count+1); //"품목"외 1개 할때 "품목" 제외하느라 -1했던거 다시 돌려놓기 
	
	makeOrder();
	modalClose();
}

function findMaxNumber() {
	let maxNum=0;
	for (rec=0;rec<list.length;rec++) {
		if (Number(list[rec][4]) > maxNum) {
			maxNum = Number(list[rec][4]);
		}
	}
	return maxNum + 1;
}

function orderComplete(){
	if(Number(document.getElementById("total").value) != ""){
		let obj = document.getElementById("money");
		obj.placeholder = "금액입력";
		obj.focus();	
	}else{
		alert("주문내역이 없습니다.");
	}
}

function setChange(obj, event){
	if(event.key != "Enter") return;
	
	let change = document.getElementById("change");
	change.value = (Number(obj.value) - totAmount).toLocaleString();
}

function member(title) {
	const modal = document.querySelector(".modalBox");
	let modalTitle = document.querySelector(".modalTitle");
	let modalCommand = document.querySelector(".modalCommand");
	
	modalTitle.innerHTML = title + "<div class='closeBox' onclick='modalClose()'>X</div>";
	
	let html = "<div>회원코드를 입력하세요</div>";
	html += "<input type='text' name='memCode' placeholder='회원코드 입력'/>";
	html += "<input type='button' value='확인' onclick='payment()'/>"
	
	modalCommand.innerHTML = html;
	modal.style.display = "block";
}

function payment(){
	const modal = document.querySelector(".modalBox");
	modal.style.display = "none";
	let change = document.getElementById("change").value;
	let memCode = document.getElementsByName("memCode")[0].value;
	if(change != "" && Number(change.replace(",","")) >= 0){
		const jobCode = "Orders";
		let formData = "";
		for(idx=0;idx<list.length;idx++) {
			if(list[idx][4]==0){
				formData += "goodsCode="+list[idx][0]+"&"+"goodsQty="+list[idx][3];
				if(idx!=list.length-1){
					formData+="&";
				}
			}
		}
		if(formData == "") return;
		if(memCode != "") formData+="&memberCode="+memCode;
		sendAjax(jobCode, formData, "completeOrder");
	}else{
		alert("받은 금액 처리를 확인해 주세요");
		let obj = document.getElementById("money");
		obj.placeholder = "금액입력";
		obj.focus();
	}
}

function completeOrder (data) {
	if(data != "none"){
		if(data.split(":")[0] == "주문완료") {
			alert(data);
			for(rec=list.length-1;rec>=0;rec--) {
				if(list[rec][4]==0){
					list.splice(list[rec],1);
				}
			}
			orderInit();
			makeOrder();
		} else {
			alert(data);
			return;
		}
	}
}

function orderInit() {
	document.getElementById("total").innerText = "";
	document.getElementById("money").value = "";
	document.getElementById("change").value = "";
}

function isCode(data, num) {
	return (data.length == num) ? true : false;
}

function changeTd(obj, cname) {
	obj.className = cname;
}