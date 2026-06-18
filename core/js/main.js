document.addEventListener("DOMContentLoaded", function() {
	var anchorElements = document.querySelectorAll("h1.title > a.anchor,h2.title > a.anchor,h3.title > a.anchor,h4.title > a.anchor,h5.title > a.anchor,div.figure > a.anchor,div.table > a.anchor");
	var sidebar = document.getElementById('sidebar');
	var currentUrl = window.location.href.split('#')[0];
	var lastSegment = currentUrl.substr(currentUrl.lastIndexOf('/') + 1);
	var link = document.querySelector("#sidebar a[href='" + lastSegment + "']");

	anchorElements.forEach(function(el) {
		var a = document.createElement('a');
		a.classList.toggle('permalink');
		a.href = currentUrl + "#" + el.id;
		a.innerHTML = "&#182";
		el.parentNode.insertBefore(a, el.nextSibling);
	});

	var chapters = document.querySelectorAll("#sidebar .filetree > li > span.handle");

	chapters.forEach(function(el) {
		el.addEventListener("click", function() {
			var panel = this.nextElementSibling.nextElementSibling;
			if (panel.classList.contains("active")) {
				panel.classList.remove("active");
				this.innerHTML = '+ ';
			} else {
				panel.classList.add("active");
				this.innerHTML = '- ';
				link.scrollIntoView();
			}
		});
	});

	var icon = document.getElementById('mobile-menu-icon');

	if (icon && sidebar) {
		icon.addEventListener('click', function() {
			if (sidebar.style.maxHeight) {
				window.onhashchange = null;
				sidebar.style.maxHeight = null;
			}
			else {
				sidebar.style.maxHeight = sidebar.scrollHeight + "px";
				window.onhashchange = function() {
					sidebar.style.maxHeight = null;
				};
			}
		});
	}

	if (lastSegment && link) {
		link.scrollIntoView();
	}

	hljs.initHighlightingOnLoad();
});

window.addEventListener('popstate', function(event) {
	var id = "scrollable";
	if (window.location.hash) {
		id = window.location.hash.substring(1);
	}
	var el = document.getElementById(id);
	if (el) {
		el.scrollIntoView();
	}
}, false);