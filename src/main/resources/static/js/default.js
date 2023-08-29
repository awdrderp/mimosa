function default_challenge_success(response) {
	$("#output").html("");

	if (response.status === "Perfect") {
		$.notify({
			icon : "fa fa-gem",
			title : "Perfect challenge completion!",
			message : "You have attained " + response.points
					+ " points! Incredible!"
		}, {
			type : "info",
			allow_dismiss : false
		});
	} else if (response.status === "Completed") {
		$.notify({
			icon : "fa fa-check",
			title : "Challenge complete!",
			message : "You attained " + response.points + " points!"
		}, {
			type : "success",
			allow_dismiss : false
		});
	} else if (response.status === "Fail") {
		$.notify({
			icon : "fa fa-times",
			title : "Challenge failed!",
			message : "You have attained " + response.points
					+ " points! Try Harder!"
		}, {
			type : "danger",
			allow_dismiss : false
		});
	}

	$.get(document.URL, function(html) {
		$("#side-menu").metisMenu("dispose");
		$(".challenge-status").first().html(
				$(html).find(".challenge-status").first().html());
		$("#side-menu").html($(html).find("#side-menu").html());

		$('ul.nav a').filter(function() {
			return this.href == window.location;
		}).addClass('active').parents('#side-menu li').addClass('active');
		$("#side-menu").metisMenu();
	});
}

function default_challenge_error(response) {
	if (response.type === "Redirect"){
		sessionStorage.setItem("lobby_expired", true)
		window.location.replace("/");
	}
	else if (response.type === "Compilation") {
		$("#output").html(response.exception.message);

		$.notify({
			icon : "fa fa-times",
			title : "Error!",
			message : "Your code is erroneous!"
		}, {
			type : "danger",
			allow_dismiss : false
		});
	} else {
		$.notify({
			icon : "fa fa-times",
			title : "Error!",
			message : "An error occurred!"
		}, {
			type : "danger",
			allow_dismiss : false
		});
	}
}

function default_challenge_postfilter(form) {
	form.reset();
}

function default_hint_success(response) {
	$.notify({
		icon : "fa fa-star",
		title : "New Hint!",
		message : "A new hint has been added!"
	}, {
		type : "info",
		allow_dismiss : false
	});

	$.get(document.URL, function(html) {
		$(".challenge-hints").first().html(
				$(html).find(".challenge-hints").first().html());
		$(".hint-form").simplifySubmit();
	});
}

function default_hint_error(response) {
	$.notify({
		icon : "fa fa-times",
		title : "Error!",
		message : "Invalid request! An error has occurred."
	}, {
		type : "danger",
		allow_dismiss : false
	});
}
