package org.osgi.impl.service.dmt;

class Utils {
	static boolean isValidNodeName(String nodeName) {
		return nodeName != null && !nodeName.equals("..")
				&& nodeName.indexOf('/') == -1;
		// TODO further syntax checks based on URI RFC 2396
	}

	static boolean isValidUri(String uri) {
		if (uri == null)
			return false;
		String[] nodeNames = Splitter.split(uri, '/', -1);
		for (int i = 0; i < nodeNames.length; i++)
			if (!isValidNodeName(nodeNames[i])
					|| (i != 0 && nodeNames[i].equals(".")))
				return false;
		return true;
	}

	static boolean isAbsoluteUri(String uri) {
		return uri.equals(".") || uri.startsWith("./");
	}

	// precondition: uri != null
	static String normalizeUri(String uri) {
		if (uri.charAt(uri.length() - 1) == '/')
			uri = uri.substring(0, uri.length() - 1);
		return uri;
	}

	static String normalizeAbsoluteUri(String uri) {
		if (uri == null) // not much point as long as isValidUri does not accept
						 // null
			return ".";
		uri = normalizeUri(uri);
		if (isAbsoluteUri(uri))
			return uri;
		return "./" + uri;
	}

	static String parentUri(String uri) {
		int sep = uri.lastIndexOf('/');
		if (sep == -1)
			return null;
		return uri.substring(0, sep);
	}

	static String[] normalizeAbsoluteUris(String[] uris) {
		if (uris == null)
			return null;
		String[] newUris = new String[uris.length];
		for (int i = 0; i < uris.length; i++)
			newUris[i] = normalizeAbsoluteUri(uris[i]);
		return newUris;
	}

	static boolean isAncestor(String ancestor, String node) {
		if (node.equals(ancestor))
			return true;
		return node.startsWith(ancestor + "/");
	}

	static String relativeUri(String ancestor, String node) {
		if (!isAncestor(ancestor, node))
			return null;
		if (node.length() == ancestor.length())
			return "";
		return node.substring(ancestor.length() + 1);
	}

	static String createAbsoluteUri(String ancestor, String subNode) {
		return subNode.equals("") ? ancestor : ancestor + '/' + subNode;
	}

	static String firstSegment(String uri) {
		int sep = uri.indexOf('/');
		if (sep == -1)
			return uri;
		return uri.substring(0, sep);
	}

	static String lastSegments(String uri) {
		int sep = uri.indexOf('/');
		if (sep == -1)
			return "";
		return uri.substring(sep + 1);
	}
}
