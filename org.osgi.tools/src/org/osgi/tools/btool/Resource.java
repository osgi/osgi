package org.osgi.tools.btool;


import java.io.*;
import java.security.*;

public class Resource implements Comparable {
	Source source;
	String path;
	BTool	btool;
	String	sourcePath;
	
	public Resource(BTool btool, Source source, String path) {
		this.path = path;
		this.btool = btool;
		this.source = source;
	}
	
	String getPath() {
		return path;
	}
	public int hashCode() { return path.hashCode(); }
	public boolean equals(Object other) { return path.equals(((PackageResource)other).name); }

	public int compareTo(Object other) {
		return path.compareTo(((Resource)other).path);
	}
	


	Resource(Source source, String path) {
		this.source = source;
		this.path = path;
	}


	boolean isClass() {
		return path.endsWith(".class");
	}

	InputStream getInputStream() throws IOException {
		if ( sourcePath != null )
			return source.getEntry(sourcePath);
		return source.getEntry(path);
	}
	
	
	public String getName() { return getPath(); }
	

	
	
	public String toString() { return "R:" + getPath(); }

	/**
	 * Calculate the checksum
	 */
	String getChecksum() {
		try {
			InputStream in = getInputStream();
			if ( in == null )
				return null;
			
			byte [] data = btool.readAll(getInputStream(),0);
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
		} catch (Exception e) {
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
}