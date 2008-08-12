package nursery.obr.bindex;

import java.io.*;
import java.net.URI;
import java.util.*;
import java.util.zip.*;

import nursery.obr.resource.ResourceImpl;


import aQute.lib.tag.Tag;

/**
 * Iterate over a set of given bundles and convert them
 * to resources. After this, convert an local urls (file systems, JAR file)
 * to relative URLs and create a ZIP file with the complete content.
 * This ZIP file can be used in an OSGi Framework to map to an
 * http service or it can be expanded on the web server's file
 * system.
 *
 * @version $Revision$
 */
public class Index {
	static String	output		= "repository.zip";
	static String	repository	= "repository.xml";
	static String	license		= null;
	static boolean	show		= false;
	static String	name		= "Untitled";

	/**
	 * Main entry. See -help for options.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		System.err.println("Bundle Indexer | v2.0");
		System.err.println("(c) 2005 OSGi, All Rights Reserved");
		Set resources = new HashSet();

		for (int i = 0; i < args.length; i++)
			try {
				if (args[i].startsWith("-o"))
					output = args[++i];
				if (args[i].startsWith("-n"))
					name = args[++i];
				else if (args[i].startsWith("-r"))
					repository = args[++i];
				else if (args[i].startsWith("-s"))
					show = true;
				else if (args[i].startsWith("-help")) {
					System.err
							.println("bindex [-o repository.zip] [ -r repository.xml ] [-help] [-l file:license.html ] <jar file>*");
				}
				else {
					BundleInfo info = new BundleInfo(args[i]);
					ResourceImpl resource = info.build();
					resources.add(resource);
				}
			}
			catch (Exception e) {
				System.err.println("Error " + e.getMessage());
				e.printStackTrace();
			}
		ZipOutputStream collected = new ZipOutputStream(new FileOutputStream(
				output));

		translate(resources, collected);
		doIndex(resources, collected);
		collected.close();
	}

	/**
	 * Create the repository index
	 * 
	 * @param resources Set of resources
	 * @param collected The output zip file
	 * @throws IOException
	 */
	private static void doIndex(Set resources,
			ZipOutputStream collected) throws IOException {
		Tag repository = new Tag("repository");
		repository.addAttribute("time", new Date());
		repository.addAttribute("name", name);
		for ( Iterator i= resources.iterator(); i.hasNext(); ) {
			ResourceImpl resource = (ResourceImpl) i.next();
			repository.addContent(resource.toXML());
		}

		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(bytes));
		repository.print(0, pw);
		pw.close();
		addToZip(collected, "repository.xml", new ByteArrayInputStream(bytes
				.toByteArray()));

		if (show) {
			pw = new PrintWriter(new OutputStreamWriter(System.out));
			repository.print(0, pw);
			pw.close();
		}
	}

	/**
	 * Find out if there are any URIs used that point to the
	 * local file system. These resources are read and stored in the
	 * ZIP file. URIs are then converted to be relative.
	 * 
	 * @param resources Set of resources
	 * @param collected ZIP file with resources and index
	 */
	private static void translate(Set resources,
			ZipOutputStream collected) {
		for ( Iterator i= resources.iterator(); i.hasNext(); ) {
			ResourceImpl resource = (ResourceImpl) i.next();
			try {
				String base = resource.getName() + "-" + resource.getVersion();
				String path = base + ".jar";
				path = path.replaceAll("[^\\w0-9_.-]","_");
				addToZip(collected, path, resource.getURI().toURL()
						.openStream());
				resource.setURI(new URI(path));

				resource.setDocumentation(doURI(resource.getDocumentation(),
						collected, base + "-doc"));
				if (resource.getLicense() != null)
					resource.setLicense(doURI(resource.getLicense(), collected,
							base + "-lic"));
				resource.setSource(doURI(resource.getSource(), collected, base
						+ "-src"));
			}
			catch (Exception e) {
				System.err.println("Error " + e.getMessage());
			}
		}
	}

	/**
	 * Convert the URI and pack the content if necessary.
	 * 
	 * @param uri	The given URI
	 * @param collected The output ZIP file
	 * @param base The base name of the resource
	 * @return The original or modified URI
	 * @throws Exception
	 */
	private static URI doURI(URI uri, ZipOutputStream collected, String base)
			throws Exception {
		if (uri == null)
			return null;

		String scheme = uri.getScheme().toLowerCase();
		if (scheme.matches("jar|file")) {
			String extension = "";
			String parts = uri.toString();
			int n = parts.lastIndexOf('.');
			if (n > 0)
				extension = parts.substring(n);
			try {
				addToZip(collected, base + extension, uri.toURL().openStream());
				return new URI(base + extension);
			}
			catch (Exception e) {
				System.err.println("URI cannot be read " + uri);
			}
		}
		return uri;
	}

	/**
	 * Add the resource to the ZIP file, calculating the CRC etc.
	 * 
	 * @param zip The output ZIP file
	 * @param name The name of the resource
	 * @param actual The contents stream
	 * @throws IOException
	 */
	static void addToZip(ZipOutputStream zip, String name, InputStream actual)
			throws IOException {
		byte buffer[];
		buffer = readAll(actual, 0);
		actual.close();
		CRC32 checksum = new CRC32();
		checksum.update(buffer);
		ZipEntry ze = new ZipEntry(name);
		ze.setSize(buffer.length);
		ze.setCrc(checksum.getValue());
		zip.putNextEntry(ze);
		zip.write(buffer, 0, buffer.length);
		zip.closeEntry();
	}

	/**
	 * Read a complete stream till EOF. This method will parse the input stream
	 * until a -1 is discovered.
	 * 
	 * The method is recursive. It keeps on calling a higher level routine until
	 * EOF. Only then is the result buffer calculated.
	 */
	static byte[] readAll(InputStream in, int offset) throws IOException {
		byte temp[] = new byte[4096];
		byte result[];
		int size = in.read(temp, 0, temp.length);
		if (size <= 0)
			return new byte[offset];
		//
		// We have a positive result, copy it
		// to the right offset.
		//
		result = readAll(in, offset + size);
		System.arraycopy(temp, 0, result, offset, size);
		return result;
	}

}
