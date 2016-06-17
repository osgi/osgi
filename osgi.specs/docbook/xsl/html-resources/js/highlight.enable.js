hljs.initHighlightingOnLoad();

document.addEventListener('DOMContentLoaded', function() {
	var app = senna.dataAttributeHandler.getApp();

	app.on('endNavigate', function(event) {
		var sidebar = document.getElementById('sidebar');

		if (sidebar.classList.contains('menu-open')) {
			sidebar.classList.remove('menu-open');
			window.onhashchange = null;
		}

		var contentDiv = document.getElementById('content');

		contentDiv.scrollTop = 0;

		var snippets = document.querySelectorAll('pre code');

		for (var i = 0; i < snippets.length; i++) {
			hljs.highlightBlock(snippets[i]);
		}
	});

	var icon = document.getElementById('modile-menu-icon');

	if (icon) {
		icon.addEventListener('click', function (event) {
			var sidebar = document.getElementById('sidebar');

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
		});
	}
});