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
	$('#ninja_forms_form_13_all_fields_wrap').modal(
		{
			clickClose: false,
			escapeClose: false,
			showClose: false
		}
	);
}

function setCookie(value) {
	Cookies.set('osgi_contact', value, {expires: 365, path: '/' });
}

var anchorElements = "h1.title > a.anchor,h2.title > a.anchor,h3.title > a.anchor,h4.title > a.anchor,h5.title > a.anchor,div.figure > a.anchor,div.table > a.anchor";

var fixAnchors = function() {
	var currentUrl = window.location.href.split('#')[0];
	$(anchorElements).after(function() {
		return "<a class='permalink' href='" + currentUrl + "#" + this.id + "'>&#182;</a>";
	});
}
