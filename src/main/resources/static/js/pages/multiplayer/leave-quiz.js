function leave_quiz_success(response) {
	sessionStorage.setItem('leave_quiz_success', true);
	window.location.replace("/");
}

function leave_quiz_error(response) {
	$.notify({
		icon: "fa fa-times",
		title: "Failed to leave quiz!",
		message: "An error occurred!"
	}, {
		type: "danger",
		allow_dismiss: false
	});
}

$(document).ready(function () {
	if (sessionStorage.getItem("leave_quiz")) {
		let csrfToken = document.getElementsByName('_csrf')[1].value;
		console.log(csrfToken)
		$.ajaxSetup({
			headers: {
				'X-CSRF-TOKEN': csrfToken
			}
		});
		$.ajax({
			url: "http://localhost/multiplayer/quiz/leave",
			type: "PUT",
			contentType: "application/x-www-form-urlencoded",
			success: (data) => {
				sessionStorage.setItem("quiz_expired", true);
				sessionStorage.setItem("leave_quiz_success", true);
				window.location.href = "/dashboard";
			},
			error: (xhr, status, error) => {
				console.log(error);
			}
		})
	}
})