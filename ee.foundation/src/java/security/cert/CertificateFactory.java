/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 The OSGi Alliance
 */

/* $Header$ */

package java.security.cert;
public class CertificateFactory {
    protected CertificateFactory(java.security.cert.CertificateFactorySpi var0, java.security.Provider var1, java.lang.String var2) { }
    public final java.security.cert.Certificate generateCertificate(java.io.InputStream var0) throws java.security.cert.CertificateException { return null; }
    public final java.util.Collection generateCertificates(java.io.InputStream var0) throws java.security.cert.CertificateException { return null; }
    public final java.security.cert.CRL generateCRL(java.io.InputStream var0) throws java.security.cert.CRLException { return null; }
    public final java.util.Collection generateCRLs(java.io.InputStream var0) throws java.security.cert.CRLException { return null; }
    public final static java.security.cert.CertificateFactory getInstance(java.lang.String var0) throws java.security.cert.CertificateException { return null; }
    public final static java.security.cert.CertificateFactory getInstance(java.lang.String var0, java.lang.String var1) throws java.security.cert.CertificateException, java.security.NoSuchProviderException { return null; }
    public final java.security.Provider getProvider() { return null; }
    public final java.lang.String getType() { return null; }
}

