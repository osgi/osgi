/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security.cert;
public abstract class X509CRL extends java.security.cert.CRL implements java.security.cert.X509Extension {
    protected X509CRL() { super(null); }
    public abstract byte[] getEncoded() throws java.security.cert.CRLException;
    public abstract java.security.Principal getIssuerDN();
    public abstract java.util.Date getNextUpdate();
    public abstract java.security.cert.X509CRLEntry getRevokedCertificate(java.math.BigInteger var0);
    public abstract java.util.Set getRevokedCertificates();
    public abstract java.lang.String getSigAlgName();
    public abstract java.lang.String getSigAlgOID();
    public abstract byte[] getSigAlgParams();
    public abstract byte[] getSignature();
    public abstract byte[] getTBSCertList() throws java.security.cert.CRLException;
    public abstract java.util.Date getThisUpdate();
    public abstract int getVersion();
    public abstract void verify(java.security.PublicKey var0) throws java.security.cert.CRLException, java.security.NoSuchAlgorithmException, java.security.InvalidKeyException, java.security.NoSuchProviderException, java.security.SignatureException;
    public abstract void verify(java.security.PublicKey var0, java.lang.String var1) throws java.security.cert.CRLException, java.security.NoSuchAlgorithmException, java.security.InvalidKeyException, java.security.NoSuchProviderException, java.security.SignatureException;
    public boolean equals(java.lang.Object var0) { return false; }
    public int hashCode() { return 0; }
    public abstract boolean hasUnsupportedCriticalExtension();
    public abstract java.util.Set getCriticalExtensionOIDs();
    public abstract java.util.Set getNonCriticalExtensionOIDs();
    public abstract byte[] getExtensionValue(java.lang.String var0);
}

