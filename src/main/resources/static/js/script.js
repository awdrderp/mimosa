$(document).ready(function () {
	$("#side-menu").metisMenu();
	$(".data-table").DataTable({
		"pageLength": 25
	});

	$('ul.nav a').filter(function () {
		return this.href == window.location;
	}).addClass('active').parents('#side-menu li').addClass('active');

	$("form").simplifySubmit();
});

function lobbyTimer(playerJoinDatetime, duration, enable, expire) {
	let expireDate = new Date(expire);
	if (!enable || expireDate < new Date()) {
		document.getElementById("timer").innerHTML = "Quiz Disabled/Expired...";
		sessionStorage.setItem("leave_quiz", true);
		if (window.location != "http://localhost/multiplayer/leave")
			window.location.replace("/multiplayer/leave");

		$.notify({
			icon: "fa fa-check",
			title: "Error!",
			message: "Quiz exipred.. Please leave Quiz to continue using MIMOSA."
		}, {
			type: "danger",
			allow_dismiss: false
		});

		// document.getElementById("leave_lobby_form").submit()

		return
	}

	// Taken from https://www.w3schools.com/howto/howto_js_countdown.asp
	// Set the date we're counting down to
	var date = new Date(new Date(playerJoinDatetime).getTime() + duration * 1000);
	var countDownDate = date.getTime();

	// Update the count down every 1 second
	var x = setInterval(function () {

		// Get today's date and time
		var now = new Date().getTime();

		// Find the distance between now and the count down date
		var distance = countDownDate - now;

		// Time calculations for days, hours, minutes and seconds
		var hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
		var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
		var seconds = Math.floor((distance % (1000 * 60)) / 1000);

		// Display the result in the element with id="timer"
		document.getElementById("timer").innerHTML = hours + "h "
			+ minutes + "m " + seconds + "s ";

		// If the count down is finished, write some text
		if (distance < 0) {
			clearInterval(x);
			document.getElementById("timer").innerHTML = "TIME LIMIT REACHED";
			sessionStorage.setItem("leave_quiz", true);
			if (window.location != "http://localhost/multiplayer/leave")
				window.location.replace("/multiplayer/leave");
		}
	}, 1000);
}