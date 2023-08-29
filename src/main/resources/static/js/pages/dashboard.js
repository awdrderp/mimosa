function add_notice_success(response) {
	$.notify({
		icon: "fa fa-check",
		title: "Notice added!",
		message: "Your notice has been written!"
	}, {
		type: "success",
		allow_dismiss: false
	});

	$.get(document.URL, function (html) {
		$(".dropdown-alerts").first().html(
			$(html).find(".dropdown-alerts").first().html());
		$(".timeline").first().html($(html).find(".timeline").first().html());
	});
}

function add_notice_error(response) {
	$.notify({
		icon: "fa fa-times",
		title: "Failed to add notice!",
		message: "An error occurred!"
	}, {
		type: "danger",
		allow_dismiss: false
	});
}

function add_notice_validate(data) {
	if (!data.title || !data.title.trim()) {
		$.notify({
			icon: "fa fa-exclamation-triangle",
			title: "Missing title!",
			message: "Please provide a title!"
		}, {
			type: "warning",
			allow_dismiss: false
		});

		return false;
	}
	if (!data.content || !data.content.trim()) {
		$.notify({
			icon: "fa fa-exclamation-triangle",
			title: "Missing content!",
			message: "Your notice does not have any content!"
		}, {
			type: "warning",
			allow_dismiss: false
		});

		return false;
	}

	return true;
}

function add_notice_postfilter(form) {
	form.reset();
}

$(document).ready(function () {
	if (sessionStorage.getItem("quiz_expired")) {
		$.notify({
			icon: "fa fa-times",
			title: "The test is over!",
			message: "The previous attempt was not recorded. Please leave the lobby..."
		}, {
			type: "danger",
			allow_dismiss: false
		});
		sessionStorage.removeItem("quiz_expired");
	}
	if (sessionStorage.getItem("leave_quiz_success")) {
		$.notify({
			icon: "fa fa-check",
			title: "Successfully,",
			message: "left lobby."
		}, {
			type: "success",
			allow_dismiss: false
		});
		sessionStorage.removeItem("leave_quiz_success");
	}
	if (sessionStorage.getItem("join_quiz_success")) {
		$.notify({
			icon: "fa fa-check",
			title: "Lobby joined!",
			message: "Successfully joined lobby with code " + sessionStorage.getItem("join_quiz_success")
		}, {
			type: "success",
			allow_dismiss: false
		});
		sessionStorage.removeItem("join_quiz_success");
	}
})