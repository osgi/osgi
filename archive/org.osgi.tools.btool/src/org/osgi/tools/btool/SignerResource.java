package org.osgi.tools.btool;

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
		
	}
	
	
}
