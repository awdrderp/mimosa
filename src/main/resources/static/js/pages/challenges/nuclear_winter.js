function nuclear_code_success(response) {
	$(".code-info").html(response.data.reply);
	default_challenge_success(response);
}

function nuclear_code_error(response) {
	$(".code-info").html(response.data.reply);
	default_challenge_error(response);
}

function nuclear_code_postfilter(form) {
	console.log('form clear');
	form.reset();
}
