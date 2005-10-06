package org.osgi.impl.bundle.midletcontainer;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;

import javax.microedition.midlet.MIDlet;

import org.osgi.framework.Bundle;
import org.osgi.service.application.*;

class MIDletProtectionDomain extends ProtectionDomain {
	MIDletProtectionDomain(ProtectionDomain oldDomain) {
		super( oldDomain.getCodeSource(), oldDomain.getPermissions() );
	}

  public boolean implies(Permission permission) {
  	Permission newPermission = permission;
  	
  	if( permission instanceof ApplicationAdminPermission ) {
  		newPermission = ((ApplicationAdminPermission)permission)
				.setCurrentApplicationId( MidletContainer.getSelfID() );
  	}
  	return super.implies( newPermission );
  }  
}

class MIDletClassLoader extends ClassLoader {
	private Bundle									bundle;
	private MIDletProtectionDomain	protectionDomain;
	private ClassLoader   					parent;
	private String        					mainClassLocation;
	private MIDlet        					correspondingMidlet = null;
	private String        					correspondingMidletID = null;
  private static char[] hex = "0123456789ABCDEF".toCharArray();
  private static final boolean DEBUG = false;

	public MIDletClassLoader(ClassLoader parent, Bundle bundle,
			ProtectionDomain protectionDomain, String mainClassLocation ) {
		super(parent);
		
		this.bundle = bundle;
		this.protectionDomain = new MIDletProtectionDomain( protectionDomain );
		this.parent = parent;
		this.mainClassLocation = mainClassLocation;		
	}

	protected Class findClass(String name) throws ClassNotFoundException {
		byte b[] = loadClassData(name);
        if(DEBUG)
            System.out.println("name:" + name + "\nbytes:" + getHexDump(b));
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

			URLConnection connection = url.openConnection();			
			int length = connection.getContentLength();
			data = new byte[length];
			InputStream input = connection.getInputStream();
			try {
                int offset = 0;
                while(offset < length) {
                    int res = input.read(data, offset, length-offset);
                    if(res < 0)
                        break;
                    offset += res;
                }
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
	
	MIDlet getCorrespondingMIDlet() {
		return correspondingMidlet;
	}

	String getCorrespondingMIDletID() {
		return correspondingMidletID;
	}
	
	void setCorrespondingMIDlet( MIDlet midlet, String Id ) {
		correspondingMidlet = midlet;
		correspondingMidletID = Id;
	}

    // generates a hexadecimal dump of the given binary data
    private static String getHexDump(byte[] bytes) {
        if(bytes.length == 0)
            return "";
        
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            if((i % 16) == 0)
                buf.append('\n');
            byte b = bytes[i];
            buf.append(hex[(b & 0xF0) >> 4]).append(hex[b & 0x0F]).append(' ');
        }
        
        return buf.toString();
    }
}