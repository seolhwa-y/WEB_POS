function accessOut(){
  let form = document.getElementsByName("serverCall")[0];
  form.setAttribute("action", "AccessOut");
  form.submit();
}
function changeBtnCss(obj, cname) { //이벤트가 발생한 요소를넘겨줌
	obj.className = cname;
}
function modalClose() {
	const modal = document.querySelector(".modalBox");
	let modalTitle = document.querySelector(".modalTitle");
	let modalCommand = document.querySelector(".modalCommand");

	modalTitle.innerText = "";
	modalCommand.innerText = "";
	
	modal.style.display = "none";
}
function createInput(type, name, className, value, placeholder, isRead) {
	//type, name, className, value, placeholder, isRead
	let obj = document.createElement("input");
	obj.setAttribute("type", type);
	if (name != null) obj.setAttribute("name", name);
	if (className != null) obj.setAttribute("class", className);
	if (value != null) obj.setAttribute("value", value);
	if (placeholder != null) obj.setAttribute("palceholder", placeholder);
	if (isRead != null) obj.setAttribute("readOnly", isRead);

	return obj;
}