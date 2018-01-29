document.addEventListener("DOMContentLoaded", function() {
	var anchorElements = document.querySelectorAll("h1.title > a.anchor,h2.title > a.anchor,h3.title > a.anchor,h4.title > a.anchor,h5.title > a.anchor,div.figure > a.anchor,div.table > a.anchor");
	var currentUrl = window.location.href.split('#')[0];

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
			this.classList.toggle("active");
			var panel = this.nextElementSibling.nextElementSibling;
			if (panel.style.maxHeight){
				this.innerHTML = '+ ';
				panel.style.maxHeight = null;
			} else {
				this.innerHTML = '- ';
				panel.style.maxHeight = panel.scrollHeight + "px";
			}
		});
	});

	var lastSegment = currentUrl.substr(currentUrl.lastIndexOf('/') + 1);
	var link = document.querySelector("#sidebar a[href='" + lastSegment + "']");

	if (lastSegment && link) {
		link.parentElement.previousElementSibling.click();
		link.scrollIntoView();
	}

	var icon = document.getElementById('mobile-menu-icon');
	var sidebar = document.getElementById('sidebar');

	if (icon && sidebar) {
		icon.addEventListener('click', function() {
			if (sidebar.style.maxHeight) {
				window.onhashchange = null;
				sidebar.style.maxHeight = null;
			}
			else {
				sidebar.style.maxHeight = sidebar.scrollHeight + "px";
				link.scrollIntoView();
				window.onhashchange = function() {
					sidebar.style.maxHeight = null;
				};
				var panel = link.parentElement.nextElementSibling;
				if (panel.style.maxHeight === '0px' || panel.style.maxHeight === null){
					link.parentElement.previousElementSibling.innerHTML = '- ';
					panel.style.maxHeight = panel.scrollHeight + "px";
				}
			}
		});
	}

	hljs.initHighlightingOnLoad();
});
