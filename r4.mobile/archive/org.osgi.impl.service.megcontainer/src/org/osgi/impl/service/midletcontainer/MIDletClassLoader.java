package org.osgi.impl.service.midletcontainer;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.ProtectionDomain;
import org.osgi.framework.Bundle;

class MIDletClassLoader extends ClassLoader {
	private Bundle				bundle;
	private ProtectionDomain	protectionDomain;
	private ClassLoader   parent;
	private String        mainClassLocation;
	private String []     exportPackages;

	public MIDletClassLoader(ClassLoader parent, Bundle bundle,
			ProtectionDomain protectionDomain, String mainClassLocation ) {
		super(parent);
		
		this.bundle = bundle;
		this.protectionDomain = protectionDomain;
		this.parent = parent;
		this.mainClassLocation = mainClassLocation;
		
		String exports = (String)bundle.getHeaders().get( "Export-Package" );
		
		if( exports == null )
			exportPackages = new String [ 0 ];
		else {		
		  String newStr = "";           // deleting the white spaces		
		  for( int i=0; i != exports.length(); i++ ) {
			  char chr = exports.charAt( i );
			  if( chr == '\n' || chr == ' ' || chr == '\t' )
				  continue;
			  newStr += chr;
		  }
		  exports = newStr;
		  exportPackages  = Splitter.split( exports, ',', 0);
		}
	}

	protected Class findClass(String name) throws ClassNotFoundException {
		byte b[] = loadClassData(name);
		return defineClass(name, b, 0, b.length, protectionDomain);
	}
	
	public Class loadClass( String name, boolean resolve ) throws ClassNotFoundException {
		// First, check if the class has already been loaded
		Class c = findLoadedClass(name);
		if (c == null) {
		  try {
		    c = findClass(name);
		  } catch (ClassNotFoundException e) {
		    // If still not found, then invoke findClass in order
		    // to find the class.
			  if (parent != null) {
			    c = parent.loadClass(name);
			  }
		  }
		}
		if (resolve) {
		    resolveClass(c);
		}
		return c;
	}

	private byte[] loadClassData(String name) throws ClassNotFoundException {			
		try {
			byte data[];
			String classFile = name.replace('.', '/') + ".class";
			URL url = bundle.getResource(classFile);
			if (url == null)
				throw new ClassNotFoundException();
			
			if( !url.toString().startsWith( mainClassLocation ) )
				throw new ClassNotFoundException();

			/* the exported packages will not be loaded with the new class loader */
			int pkgNdx = name.lastIndexOf( '.' );
			if( pkgNdx > 0) {
				String pkg = name.substring( 0, pkgNdx );
				
				for( int i=0; i != exportPackages.length; i++ )
					if( exportPackages[i].equals( pkg ) ) {
						throw new ClassNotFoundException();
					}
			}
			
			URLConnection connection = url.openConnection();			
			int length = connection.getContentLength();
			data = new byte[length];
			InputStream input = connection.getInputStream();
			try {
				input.read(data);
			}
			finally {
				input.close();
			}
			return data;
		}
		catch (Exception e) {
			throw new ClassNotFoundException("Cannot load the required class!",
					e);
		}
	}
}