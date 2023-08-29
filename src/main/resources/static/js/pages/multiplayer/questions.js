$('.challenge-checkbox').on('change', function() {
	$(this).closest("form").submit();
});

$('.data-table').on('draw.dt', function() {
	$(".data-table form").unbind("submit");
	$(".data-table form").simplifySubmit();
});

function assign_challenge_success(response) {
	$.notify({
		icon : "fa fa-check",
		title : response.title + " updated!",
		message : "Challenge has been successfully updated."
	}, {
		type : "success",
		allow_dismiss : false
	});
}

function assign_challenge_error(response) {
	$.notify({
		icon : "fa fa-times",
		title : "Failed to assign/unassign challenge!",
		message : "An error occurred!"
	}, {
		type : "danger",
		allow_dismiss : false
	});
}
function enable_quiz_success(response) {
	$.notify({
		icon : "fa fa-check",
		title : "Quiz updated!",
		message : "Quiz Enabled/Disabled."
	}, {
		type : "success",
		allow_dismiss : false
	});
}

function enable_quiz_error(response) {
	$.notify({
		icon : "fa fa-times",
		title : "Failed to enable/disable quiz!",
		message : "An error occurred!"
	}, {
		type : "danger",
		allow_dismiss : false
	});
}

function change_code_success(response) {
	sessionStorage.setItem("change_code", response.code);
	window.location.replace(`/multiplayer/${response.code}/questions`);
}

function change_code_error(response) {
	$.notify({
		icon : "fa fa-times",
		title : "Failed to change quiz code!",
		message : "An error occurred!"
	}, {
		type : "danger",
		allow_dismiss : false
	});
}

function change_year_success(response) {
	$.notify({
		icon : "fa fa-check",
		title : "Quiz year updated!",
		message : `Successfully updated quiz year to ${response.year}.`
	}, {
		type : "success",
		allow_dismiss : false
	});
}

function change_year_error(response) {
	$.notify({
		icon : "fa fa-times",
		title : "Failed to change quiz year!",
		message : "An error occurred!"
	}, {
		type : "danger",
		allow_dismiss : false
	});
}

function change_duration_success(response) {
	$.notify({
		icon : "fa fa-check",
		title : "Quiz duration updated!",
		message : `Successfully updated quiz duration to ${response.duration}.`
	}, {
		type : "success",
		allow_dismiss : false
	});
}

function change_duration_error(response) {
	$.notify({
		icon : "fa fa-times",
		title : "Failed to change quiz duration!",
		message : "An error occurred!"
	}, {
		type : "danger",
		allow_dismiss : false
	});
}

function change_expire_success(response) {
	$.notify({
		icon : "fa fa-check",
		title : "Quiz expire updated!",
		message : `Successfully updated quiz expire to ${response.expire}.`
	}, {
		type : "success",
		allow_dismiss : false
	});
}

function change_expire_error(response) {
	$.notify({
		icon : "fa fa-times",
		title : "Failed to change quiz expire!",
		message : "An error occurred!"
	}, {
		type : "danger",
		allow_dismiss : false
	});
}



$(document).ready(function () {
	if (sessionStorage.getItem("create_quiz_success")) {
		$.notify({
			icon : "fa fa-check",
			title : "Quiz created!",
			message : `Quiz with code ${sessionStorage.getItem("create_quiz_success")} been successfully created.`
		}, {
			type : "success",
			allow_dismiss : false
		});
		sessionStorage.removeItem("create_quiz_success");
	}

	if (sessionStorage.getItem("change_code")){
		$.notify({
			icon : "fa fa-check",
			title : "Quiz code updated!",
			message : `Successfully updated quiz code to ${sessionStorage.getItem("change_code")}.`
		}, {
			type : "success",
			allow_dismiss : false
		});
		sessionStorage.removeItem("change_code");
	}
})