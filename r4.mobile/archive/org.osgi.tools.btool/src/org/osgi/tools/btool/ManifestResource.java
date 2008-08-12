package org.osgi.tools.btool;

import java.io.*;
import java.util.*;

public class ManifestResource extends FileResource {
	boolean	replace;
	boolean	manifestVersionFound;
	Map		headers	= new HashMap();
	String	lastHeader;
	boolean	usedRelease2Headers;
	
	ManifestResource(BTool btool, String manifest, boolean replace) {
		super(btool, "META-INF/MANIFEST.MF", new File(manifest), true);
		this.replace = replace;
	}
	
	void parse( BufferedReader br, PrintWriter pw ) throws IOException {
		parse(br,pw, 76, "\r\n ");
	}

	protected String replace(String key) throws IOException {
		if (!replace)
			return super.replace(key);
		if (key.equals("IMPORT-PACKAGE")) {
			Collection imports = btool.getImports();
			for ( Iterator i = imports.iterator(); i.hasNext(); ) {
				Package p = (Package) i.next();
				try {
					// We remove any micro numbers from the import
					// version so that bug fixes do not force
					// a new version for import.
					Version	v = new Version(p.getVersion());
					v = new Version( v.getMajor(), v.getMinor(), 0, null );
					p.setVersion(v.toString());
				}
				catch( Exception e ) {
					// We ignore this
				}
			}
			Collection exports = btool.getExports();
			Collection combined = new TreeSet();
			combined.addAll(imports);
			if (btool.manifestVersion > 1 ) {
				combined.addAll(exports);
				usedRelease2Headers = true;
			}
			return printImExport("Import-Package", combined);
		}
		if (key.equals("EXPORT-PACKAGE")) {
			Collection exports = btool.getExports();
			usedRelease2Headers = true;
			return printImExport("Export-Package", exports);
		}
		if (key.equals("PRIVATE-PACKAGE")) {
			Collection privates = btool.getPrivates();
			return printImExport("Private-Package", privates);
		}
		if (key.equals("FILE-SECTION")) {
			StringBuffer sb = new StringBuffer();
			
			for (Iterator i = btool.contents.values().iterator(); i.hasNext();) {
				Resource r = (Resource) i.next();
				sb.append("\r\n");
				sb.append("Name: ");
				sb.append(r.getPath());
				if (r instanceof PackageResource) {
					sb.append('/');
				}
				sb.append("\r\n");
				String checksum = r.getChecksum();
				if (checksum != null) {
					sb.append("Digest-Algorithms: MD5\r\n");
					sb.append("MD5-Digest: ");
					sb.append(checksum);
					sb.append("\r\n");
				}
				if (r instanceof PackageResource) {
					PackageResource p = (PackageResource) r;
					if (p.getVersion() != null) {
						sb.append("Specification-Version: ");
						sb.append(p.getVersion());
						sb.append("\r\n");
					}
				}
			}
			return sb.toString();
		}
		return super.replace(key);
	}

	/**
	 * @param string
	 * @param imports
	 * @return
	 */
	private String printImExport(String string, Collection imports) {
		if (imports.isEmpty())
			return "";
		StringBuffer sb = new StringBuffer();
		sb.append(string);
		String del = ": ";
		for (Iterator i = imports.iterator(); i.hasNext();) {
			sb.append(del);
			del = ",\r\n       ";
			Package pr = (Package) i.next();
			sb.append(pr.getName());
			sb.append(" ");
			if (!btool.isIgnoreVersions() && pr.getVersion() != null) {
				sb.append(";specification-version=\"");
				sb.append(pr.getVersion());
				sb.append("\"");
				if ( btool.manifestVersion > 1 ) {
					sb.append(";version=\"");
					sb.append(pr.getVersion());
					sb.append("\"");
				}
			}
			if ( btool.manifestVersion > 1 && pr.uses != null ) {
				sb.append("; uses:=");
				if ( pr.uses.size() > 1 )
					sb.append("\"");
				String usedDel = "";
				for ( Iterator u=pr.uses.iterator(); u.hasNext(); ) {
					String used = (String) u.next();
					sb.append(usedDel);
					sb.append(used.replaceAll("/","."));
					usedDel = ",";
				}
				if ( pr.uses.size() > 1 )
					sb.append("\"");
			}
		}
		return sb.toString();
	}

	/**
	 * This method reads the lines from the input and
	 * calculates the headers from this. It also does
	 * some checking at the end of the main section.
	 * 
	 * @param line
	 * @return
	 * @throws IOException
	 * @see org.osgi.tools.btool.FileResource#process(java.lang.String)
	 */
	String process(String line) throws IOException {
		String normalized = line.toLowerCase();
		if (normalized.length() == 0) {
			// End of Main section
			if (!headers.containsKey("bundle-manifestversion") 
					&& usedRelease2Headers 
					&& btool.manifestVersion > 1 ) {
				headers.put("bundle-manifestversion", "2");
				return "Bundle-ManifestVersion: 2\r\n";
			}
		}
		else {
			// Handled extended lines
			if (normalized.startsWith(" ")) {
				String last = (String) headers.get(lastHeader);
				normalized = last + normalized.substring(1);
			}
			else {
				// Check if it is a header (could be a line starting with a $)
				int n = normalized.indexOf(':');
				if (n > 0) {
					lastHeader = normalized.substring(0, n);
					normalized = normalized.substring(n + 2);
				}
				else
					lastHeader = null;
			}
			if ( lastHeader != null )
				headers.put(lastHeader, normalized);
		}
		return super.process(line);
	}
}
