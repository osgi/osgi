/*
 * $Header$
 *
 * (C) Copyright 2001 Sun Microsystems, Inc.
 * Copyright (c) OSGi Alliance (2001, 2005). All Rights Reserved.
 */

package java.security.cert;

import java.math.BigInteger;
import java.security.Principal;
import java.util.*;

import javax.security.auth.x500.X500Principal;

public abstract class X509Certificate extends Certificate implements
		X509Extension {

	protected X509Certificate() {
		super("");
	}

	public abstract void checkValidity() throws CertificateExpiredException,
			CertificateNotYetValidException;

	public abstract void checkValidity(Date date)
			throws CertificateExpiredException, CertificateNotYetValidException;

	public abstract int getVersion();

	public abstract BigInteger getSerialNumber();

	public abstract Principal getIssuerDN();

	public X500Principal getIssuerX500Principal() {
		return null;
	}

	public abstract Principal getSubjectDN();

	public X500Principal getSubjectX500Principal() {
		return null;
	}

	public abstract Date getNotBefore();

	public abstract Date getNotAfter();

	public abstract byte[] getTBSCertificate()
			throws CertificateEncodingException;

	public abstract byte[] getSignature();

	public abstract String getSigAlgName();

	public abstract String getSigAlgOID();

	public abstract byte[] getSigAlgParams();

	public abstract boolean[] getIssuerUniqueID();

	public abstract boolean[] getSubjectUniqueID();

	public abstract boolean[] getKeyUsage();

	public List getExtendedKeyUsage() throws CertificateParsingException {
		return null;
	}

	public abstract int getBasicConstraints();

	public Collection getSubjectAlternativeNames()
			throws CertificateParsingException {
		return null;
	}

	public Collection getIssuerAlternativeNames()
			throws CertificateParsingException {
		return null;
	}
}
