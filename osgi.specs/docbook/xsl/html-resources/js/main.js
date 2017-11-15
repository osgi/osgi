var anchorElements = "h1.title > a.anchor,h2.title > a.anchor,h3.title > a.anchor,h4.title > a.anchor,h5.title > a.anchor,div.figure > a.anchor,div.table > a.anchor";

var fixAnchors = function() {
	var currentUrl = window.location.href.split('#')[0];
	$(anchorElements).after(function() {
		return "<a class='permalink' href='" + currentUrl + "#" + this.id + "'>&#182;</a>";
	});
}
