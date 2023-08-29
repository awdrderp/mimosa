function join_quiz_success(response) {
	let quizCode = document.getElementById('quizCode').value;
	sessionStorage.setItem('join_quiz_success', quizCode);
	// window.location.reload();
	window.location.replace("/");
}

function join_quiz_error(response) {
	$.notify({
		icon : "fa fa-times",
		title : "Failed to join quiz!",
		message : "Unable to join quiz... If you should be able to, please ask your lecturer."
	}, {
		type : "danger",
		allow_dismiss : false
	});
}

$(document).ready(function () {
	
})