package org.osgi.tools.btool;

import java.io.*;
import java.security.MessageDigest;

public class Resource implements Comparable {
	Source	source;
	String	path;
	BTool	btool;
	String	sourcePath;
	byte[]	extra;

	public Resource(BTool btool, Source source, String path) {
		this.path = path;
		this.btool = btool;
		this.source = source;
		if (btool == null) {
			System.out.println("No Btool ");
			throw new RuntimeException("No btool");
		}
	}

	public long lastModified() {
		String path = sourcePath;
		if (path == null)
			path = this.path;
		return getSource().lastModified(path);
	}

	public String getPath() {
		return path;
	}

	public int hashCode() {
		return path.hashCode();
	}

	public boolean equals(Object other) {
		return path.equals(((PackageResource) other).name);
	}

	public int compareTo(Object other) {
		return path.compareTo(((Resource) other).path);
	}

	boolean isClass() {
		return path.endsWith(".class");
	}

	InputStream getInputStream() throws IOException {
		Source source = getSource();
		try {
			if (sourcePath != null)
				return source.getEntry(sourcePath);
			return source.getEntry(path);
		}
		catch (FileNotFoundException e) {
			btool.errors.add("Resource not found " + this);
			return null;
		}
	}

	public String getName() {
		return getPath();
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (source != null)
			sb.append(source.getFile().getName());
		else
			sb.append("<>");
		sb.append(":");
		sb.append(getPath());
		if (sourcePath != null) {
			sb.append("[");
			sb.append(sourcePath);
			sb.append("]");
		}
		return sb.toString();
	}

	/**
	 * Calculate the checksum
	 */
	String getChecksum() {
		try {
			InputStream in = getInputStream();
			if (in == null)
				return null;
			if (btool == null)
				return "XXXXXXXXX ";
			byte[] data = btool.readAll(in, 0);
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data);
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				if ((0xFF & digest[i]) < 16)
					sb.append('0');
				sb.append(Integer.toHexString((0xFF & digest[i])));
			}
			return sb.toString();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param sourcePath The sourcePath to set.
	 */
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * @return
	 */
	public byte[] getExtra() {
		return extra;
	}

	public void setExtra(byte[] extra) {
		this.extra = extra;
	}

	/**
	 * @return
	 */
	public Source getSource() {
		return source == null ? btool.project : source;
	}
}