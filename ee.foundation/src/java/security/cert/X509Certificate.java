/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 OSGi Alliance
 */

/* $Header$ */

package java.security.cert;
public abstract class X509Certificate extends java.security.cert.Certificate implements java.security.cert.X509Extension {
    protected X509Certificate() { super(null); }
    public abstract void checkValidity() throws java.security.cert.CertificateExpiredException, java.security.cert.CertificateNotYetValidException;
    public abstract void checkValidity(java.util.Date var0) throws java.security.cert.CertificateExpiredException, java.security.cert.CertificateNotYetValidException;
    public abstract int getBasicConstraints();
    public abstract java.security.Principal getIssuerDN();
    public abstract boolean[] getIssuerUniqueID();
    public abstract boolean[] getKeyUsage();
    public abstract java.util.Date getNotAfter();
    public abstract java.util.Date getNotBefore();
    public abstract java.math.BigInteger getSerialNumber();
    public abstract java.lang.String getSigAlgName();
    public abstract java.lang.String getSigAlgOID();
    public abstract byte[] getSigAlgParams();
    public abstract byte[] getSignature();
    public abstract java.security.Principal getSubjectDN();
    public abstract boolean[] getSubjectUniqueID();
    public abstract byte[] getTBSCertificate() throws java.security.cert.CertificateEncodingException;
    public abstract int getVersion();
    public abstract boolean hasUnsupportedCriticalExtension();
    public abstract java.util.Set getCriticalExtensionOIDs();
    public abstract java.util.Set getNonCriticalExtensionOIDs();
    public abstract byte[] getExtensionValue(java.lang.String var0);
}

