var anchorElements = "h1.title > a.anchor,h2.title > a.anchor,h3.title > a.anchor,h4.title > a.anchor,h5.title > a.anchor,div.figure > a.anchor,div.table > a.anchor";

var fixAnchors = function() {
	var currentUrl = window.location.href.split('#')[0];
	$(anchorElements).after(function() {
		return "<a class='permalink' href='" + currentUrl + "#" + this.id + "'>&#182;</a>";
	});
};

$(function() {
	var link;
	var lastSegment = document.location.href.substr(document.location.href.lastIndexOf('/') + 1);
	if (lastSegment) {
		link = document.querySelector("#sidebar a[href='" + lastSegment.split("#")[0] + "']");
	}

	var chapter = document.querySelectorAll("#sidebar .filetree > li > span.handle");
	var i;

	for (i = 0; i < chapter.length; i++) {
		chapter[i].addEventListener("click", function() {
			this.classList.toggle("active");
			var panel = this.nextElementSibling.nextElementSibling;
			if (panel.style.maxHeight){
				panel.style.maxHeight = null;
			} else {
				panel.style.maxHeight = panel.scrollHeight + "px";
			}
		});
	}

	var icon = document.getElementById('mobile-menu-icon');
	var sidebar = document.getElementById('sidebar');

	if (icon && sidebar) {
		icon.addEventListener('click', function() {
				icon.classList.toggle('active')

				if (sidebar.classList.contains('menu-open')) {
					sidebar.classList.remove('menu-open');

					window.onhashchange = null;
				}
				else {
					sidebar.classList.add('menu-open');
					link.scrollIntoView(true);
					var panel = link.parentElement.nextElementSibling;
					if (panel.style.maxHeight === '0px' || panel.style.maxHeight === null){
						panel.style.maxHeight = panel.scrollHeight + "px";
					}
					window.onhashchange = function() {
						sidebar.classList.remove('menu-open');
					};
				}
			}
		);
	}

	if (link) {
		link.scrollIntoView(true);
		link.parentElement.previousElementSibling.click();
	}
});
