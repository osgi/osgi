package org.osgi.impl.service.upnp.cp.basedriver;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import org.osgi.service.upnp.*;
import org.osgi.impl.service.upnp.cp.description.*;

import sun.awt.image.URLImageSource;

public class UPnPIconImpl implements UPnPIcon {
	private Icon		icon;
	private String		baseURL;

	// This constructor creates the UPnPIcon object based on the given Icon
	// object.
	UPnPIconImpl(Icon icon, String baseURL) {
		this.icon = icon;
		this.baseURL = baseURL;
	}

	// This method returns the mimetype of the icon.
	public String getMimeType() {
		return icon.getMimeType();
	}

	// This method returns the width of the icon.
	public int getWidth() {
		return icon.getWidth();
	}

	// This method returns the height of the icon.
	public int getHeight() {
		return icon.getHeight();
	}

	// This method returns the size of the icon.
	public int getSize() {
		return -1;
	}

	// This method returns the depth of the icon.
	public int getDepth() {
		return icon.getDepth();
	}

	// This method returns the input stream of the icon.
	public InputStream getInputStream() throws IOException {
		URL		url = new URL( new URL(baseURL), icon.getURL());
		return url.openStream();
	}
}
