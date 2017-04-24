hljs.initHighlightingOnLoad();

var fixAnchors = function() {
	$("a.anchor").each(function(i, d) {
		var j = $(d);
		var href = window.location.href.split('#')[0] + "#" + j.attr("id");
		j.attr("href", href);
		//console.log("Processed: {}", d);
	});
}

document.addEventListener(
	'DOMContentLoaded',
	function() {
		var app = senna.dataAttributeHandler.getApp();

		app.on(
			'endNavigate',
			function(event) {
				var icon = document.getElementById('mobile-menu-icon');
				var sidebar = document.getElementById('sidebar');

				if (icon && sidebar) {
					if (icon.classList.contains('active')) {
						sidebar.classList.remove('active');
					}

					if (sidebar.classList.contains('menu-open')) {
						sidebar.classList.remove('menu-open');
					}

					window.onhashchange = null;
				}

				var contentDiv = document.getElementById('content');

				contentDiv.scrollTop = 0;

				var snippets = document.querySelectorAll('pre code');

				for (var i = 0; i < snippets.length; i++) {
					hljs.highlightBlock(snippets[i]);
				}

				fixAnchors();
			}
		);

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
	}
);