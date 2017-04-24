function checkContactCookie() {
	var contactCookie = Cookies.get('osgi_contact');

	if (contactCookie == 'true') {
		displayModal();
	} else if (contactCookie == 'false') {
		return;
	} else {
		setCookie('true');
	}
}

function dismissForm() {
	$.modal.close();

	setCookie('false');
}

function displayModal() {
	$('#ninja_forms_form_13_all_fields_wrap').modal();
}

function setCookie(value) {
	Cookies.set('osgi_contact', value, {expires: 365, path: '/' });
}