package org.osgi.impl.service.upnp.cp.basedriver;

import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import org.osgi.service.upnp.*;
import org.osgi.impl.service.upnp.cp.description.*;

public class UPnPIconImpl implements UPnPIcon {
	private Icon		icon;
	private int			width;
	private int			height;
	private int			size;
	private int			depth;
	private String		mimeType;
	private InputStream	ins;
	private URL			iconurl;

	// This constructor creates the UPnPIcon object based on the given Icon
	// object.
	UPnPIconImpl(Icon icon, String baseURL) {
		this.icon = icon;
		mimeType = icon.getMimeType();
		width = icon.getWidth();
		height = icon.getHeight();
		depth = icon.getDepth();
		try {
			URL iconurl = new URL(new URL(baseURL), icon.getURL());
			ins = iconurl.openStream();
			size = ins.available();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// This method returns the mimetype of the icon.
	public String getMimeType() {
		return mimeType;
	}

	// This method returns the width of the icon.
	public int getWidth() {
		return width;
	}

	// This method returns the height of the icon.
	public int getHeight() {
		return height;
	}

	// This method returns the size of the icon.
	public int getSize() {
		return size;
	}

	// This method returns the depth of the icon.
	public int getDepth() {
		return depth;
	}

	// This method returns the input stream of the icon.
	public InputStream getInputStream() throws IOException {
		return ins;
	}
}
