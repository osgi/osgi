package org.osgi.test.cases.transaction;

import java.util.StringTokenizer;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.test.support.signature.DefaultSignatureTestControl;

public class TransactionSignatureTest extends DefaultSignatureTestControl {

    /**
     * test signature per packages specified in the bundle manifest header 
     * - Signature-Packages.
     * 
     */
    public void testSignature() throws Exception {
        BundleContext bc = super.getContext();
        Bundle b = bc.getBundle();
        String signatures = (String) b.getHeaders().get("Signature-Packages");
        StringTokenizer st = new StringTokenizer(signatures, " ,");
        int n = st.countTokens();
        for (int i=0; i<n; i++ ) {
            String signature = st.nextToken().replace('.', '/');
            super.doPackage(b, signature, this);
            //progress(100 * (i+1)/n);
        }
    }
}
