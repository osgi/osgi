package org.osgi.tools.btool;


import java.io.*;
import java.util.*;

public class ManifestResource extends FileResource {
	boolean replace;
	
	ManifestResource(BTool btool, String manifest, boolean replace) {
		super(btool, "META-INF/MANIFEST.MF", new File(manifest), true );
		this.replace = replace;
	}
	
	protected String replace(String key) throws IOException {
		if ( !replace )
			return null;
		
		if ( key.equals("IMPORT-PACKAGE") ) {
			Collection imports = btool.getImports();
			return printImExport("Import-Package", imports );
		}
		if ( key.equals("EXPORT-PACKAGE")) {
			Collection exports = btool.getExports();
			return printImExport("Export-Package", exports );
		}
		if ( key.equals("FILE-SECTION")) {
			StringBuffer sb = new StringBuffer();
			for (Iterator i=btool.contents.values().iterator(); i.hasNext(); ) {
				Resource r = (Resource) i.next();
				sb.append("\r\n");
				sb.append("Name: ");
				sb.append(r.getPath());
				if ( r instanceof PackageResource ) {
					sb.append('/');
				}
				sb.append("\r\n");
				String checksum = r.getChecksum();
				if ( checksum != null ) {
					sb.append("Digest-Algorithms: MD5\r\n");
					sb.append("MD5-Digest: ");
					sb.append( checksum  );
				}
				if ( r instanceof PackageResource ) {
					PackageResource p = (PackageResource) r;
					if (p.getVersion()!=null) {
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
		if ( imports.isEmpty())
			return "";
		
		StringBuffer	sb = new StringBuffer();
		sb.append(string);
		String del = ": ";
		for (Iterator i=imports.iterator(); i.hasNext(); ) {
			sb.append(del);
			del=",\r\n       ";
			Package pr = (Package) i.next();
			sb.append(pr.getName());
			if ( ! btool.isIgnoreVersions() && pr.getVersion() != null ) {
				sb.append(";specification-version=\"");
				sb.append(pr.getVersion());
				sb.append("\"");
			}
		}
		return sb.toString();
	}


}
