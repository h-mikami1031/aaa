//パスワードのマスク処理
function pushHideButton(passwordId, buttonId) {
	var txtPass = document.getElementById(passwordId);
	var btnEye = document.getElementById(buttonId);
	if (txtPass.type === "text") {
		txtPass.type = "password";
		btnEye.className = "fa fa-eye";
	} else {
		txtPass.type = "text";
		btnEye.className = "fa fa-eye-slash";
	}
}