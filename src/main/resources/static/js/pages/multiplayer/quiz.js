function create_quiz_success(response) {
	sessionStorage.setItem('create_quiz_success', response.code);
	window.location.replace(`/multiplayer/${response.code}/questions`);
}

function create_quiz_error(response) {
	$.notify({
		icon : "fa fa-times",
		title : "Failed to create quiz!",
		message : "An error occurred!"
	}, {
		type : "danger",
		allow_dismiss : false
	});
}
