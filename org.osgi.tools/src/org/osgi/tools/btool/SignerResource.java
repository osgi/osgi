package org.osgi.tools.btool;

import java.io.File;

import javax.security.cert.X509Certificate;

/**
 *
 * TODO Add Javadoc comment for this type.
 * 
 * @version $Revision$
 */
public class SignerResource extends Resource {
	X509Certificate			cert;
	
	public SignerResource(BTool btool, String certificate) {
		super(btool, null, "" );
		File		file = new File( btool.project.getFile(), certificate );
		
	}
	
	
}
