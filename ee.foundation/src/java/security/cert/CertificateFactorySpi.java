/* (C) Copyright 2001 Sun Microsystems, Inc. 
 * (C) Copyright 2001 Open Services Gateway Initiative, Inc. (the OSGi alliance)
 */

/* $Header$ */

package java.security.cert;
public abstract class CertificateFactorySpi {
    public CertificateFactorySpi() { }
    public abstract java.security.cert.Certificate engineGenerateCertificate(java.io.InputStream var0) throws java.security.cert.CertificateException;
    public abstract java.util.Collection engineGenerateCertificates(java.io.InputStream var0) throws java.security.cert.CertificateException;
    public abstract java.security.cert.CRL engineGenerateCRL(java.io.InputStream var0) throws java.security.cert.CRLException;
    public abstract java.util.Collection engineGenerateCRLs(java.io.InputStream var0) throws java.security.cert.CRLException;
}

