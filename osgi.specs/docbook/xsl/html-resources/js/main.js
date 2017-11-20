var anchorElements = "h1.title > a.anchor,h2.title > a.anchor,h3.title > a.anchor,h4.title > a.anchor,h5.title > a.anchor,div.figure > a.anchor,div.table > a.anchor";

var fixAnchors = function() {
	var currentUrl = window.location.href.split('#')[0];
	$(anchorElements).after(function() {
		return "<a class='permalink' href='" + currentUrl + "#" + this.id + "'>&#182;</a>";
	});
}

$(function() {
	var icon = document.getElementById('mobile-menu-icon');

	if (icon) {
		icon.addEventListener(
			'click',
			function(event) {
				var sidebar = document.getElementById('sidebar');

				if (icon && sidebar) {
					if (icon.classList.contains('active')) {
						icon.classList.remove('active');
					}
					else {
						icon.classList.add('active');
					}

					if (sidebar.classList.contains('menu-open')) {
						sidebar.classList.remove('menu-open');

						window.onhashchange = null;
					}
					else {
						sidebar.classList.add('menu-open');

						window.onhashchange = function() {
							sidebar.classList.remove('menu-open');
						};
					}
				}
			}
		);
	}
});
