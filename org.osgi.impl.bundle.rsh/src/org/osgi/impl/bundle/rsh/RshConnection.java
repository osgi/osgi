package org.osgi.impl.bundle.rsh;

import java.io.*;
import java.net.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;

/**
 * URLConnection for the RSH protocol. Most of the work is actually done by
 * HTTP. Everything is redirected to a shadowed HTTP url except for
 * getInputStream which actually does the crypto.
 * 
 * @author breed@almaden.ibm.com
 */
public class RshConnection extends URLConnection {
	URL				httpURL;
	/*
	 * This is not good enough! We need a good random source, but that will be
	 * platform specific.
	 */
	SecureRandom	rand	= new SecureRandom();
	URLConnection	urlConnection;
	/*
	 * These two are left here for when we want to do HMACs using the SHA digest
	 * in JDK 1.1
	 */
	static byte		ipad[]	= new byte[64];
	static byte		opad[]	= new byte[64];
	static {
		for (int i = 0; i < 64; i++) {
			ipad[i] = (byte) 0x36;
			opad[i] = (byte) 0x5c;
		}
	}

	String get6bitEnc(int i) {
		StringBuffer sb = new StringBuffer();
		if (i < 26)
			sb.append((char) ('A' + i));
		else
			if (i < 52)
				sb.append((char) ('a' + i - 26));
			else
				if (i < 62)
					sb.append((char) ('0' + i - 52));
				else
					if (i == 62)
						sb.append("%2b");
					else
						sb.append("%2f");
		return sb.toString();
	}

	String get3byteEnc(int i, int bytes) {
		StringBuffer sb = new StringBuffer();
		sb.append(get6bitEnc((i >> 18) & 0x3f));
		sb.append(get6bitEnc((i >> 12) & 0x3f));
		sb.append(bytes < 2 ? "%3d" : get6bitEnc((i >> 6) & 0x3f));
		sb.append(bytes < 3 ? "%3d" : get6bitEnc(i & 0x3f));
		return sb.toString();
	}

	byte	clientFG[]	= new byte[16];

	String generateClientFG() {
		int i;
		StringBuffer sb = new StringBuffer();
		// We only use 24-bits for convenience...
		for (i = 0; i < 5; i++) {
			int r = rand.nextInt((1 << 24) - 1);
			sb.append(get3byteEnc(r, 3));
			clientFG[i * 3] = (byte) ((r >> 16) & 0xff);
			clientFG[i * 3 + 1] = (byte) ((r >> 8) & 0xff);
			clientFG[i * 3 + 2] = (byte) (r & 0xff);
		}
		int r = rand.nextInt(255);
		clientFG[15] = (byte) r;
		sb.append(get3byteEnc(r << 16, 1));
		return sb.toString();
	}

	byte	rshSecret[];

	/**
	 * Constructor for RshConnection. An HTTP request will be build based on the
	 * passed parameters.
	 */
	public RshConnection(URL url, String spid, byte rshSecret[])
			throws MalformedURLException {
		super(url);
		this.rshSecret = rshSecret;
		String sansRsh = url.toString().substring(3);
		StringBuffer end = new StringBuffer();
		if (sansRsh.indexOf('?') == -1) {
			end.append('?');
		}
		else {
			end.append('&');
		}
		end.append("service_platform_id=");
		try {
			end.append(encode(spid));
		}
		catch (UnsupportedEncodingException e) {
			/* UTF-8 must be in every implementation of Java!!! */
			throw new RuntimeException("UTF-8 encoding not present");
		}
		end.append("&clientfg=");
		end.append(generateClientFG());
		httpURL = new URL("http" + sansRsh + end.toString());
	}

	/**
	 * @see java.net.URLConnection#connect()
	 */
	public void connect() throws IOException {
		urlConnection = httpURL.openConnection();
	}

	/** Constant used to calculate the encryption key */
	static final byte		E[]		= {(byte) 0x05, (byte) 0x36, (byte) 0x54,
			(byte) 0x70, (byte) 0x00};
	/** Constant used to calculate the MAC key */
	static final byte		A[]		= {(byte) 0x00, (byte) 0x4f, (byte) 0x53,
			(byte) 0x47, (byte) 0x49};
	ByteArrayInputStream	bais	= null;

	synchronized public InputStream getInputStream() throws IOException {
		if (bais != null)
			return bais;
		if (urlConnection == null)
			urlConnection = httpURL.openConnection();
		InputStream is = urlConnection.getInputStream();
		DataInputStream dis = new DataInputStream(is);
		dis.readInt();
		dis.readShort();
		byte serverFG[] = new byte[16];
		dis.read(serverFG);
		int maclen = dis.readInt();
		if (maclen > 16)
			throw new IOException("MAC is too large " + maclen);
		byte macSent[] = new byte[maclen];
		byte m1[], m2[], ka[];
		dis.read(macSent);
		int len = dis.readInt();
		if (len > 1 << 20)
			throw new IOException("Sanity check failed (larger than 1M) ");
		try {
			MessageDigest M1 = MessageDigest.getInstance("SHA");
			MessageDigest M2 = MessageDigest.getInstance("SHA");
			MessageDigest Ka = MessageDigest.getInstance("SHA");
			M1.update(rshSecret);
			M1.update(clientFG);
			M1.update(serverFG);
			m1 = M1.digest(E);
			M2.update(rshSecret);
			M2.update(m1);
			M2.update(clientFG);
			M2.update(serverFG);
			m2 = M2.digest(E);
			Ka.update(rshSecret);
			Ka.update(clientFG);
			Ka.update(serverFG);
			ka = Ka.digest(A);
			Cipher c = Cipher.getInstance("DESede/CBC/PKCS5Padding");
			byte deskey[] = new byte[24];
			System.arraycopy(m1, 0, deskey, 0, 20);
			System.arraycopy(m2, 0, deskey, 20, 4);
			DESedeKeySpec desKeySpec = new DESedeKeySpec(deskey);
			SecretKeyFactory keyFactory = SecretKeyFactory
					.getInstance("DESede");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(m2, 4, 8);
			c.init(Cipher.DECRYPT_MODE, secretKey, iv);
			byte cipherText[] = new byte[len];
			dis.readFully(cipherText);
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec macKey = new SecretKeySpec(ka, "HmacSHA1");
			mac.init(macKey);
			byte macCalc[] = mac.doFinal(cipherText);
			for (int i = 0; i < 16; i++) {
				if (macSent[i] != macCalc[i])
					throw new IOException("Invalid MAC");
			}
			byte response[] = c.doFinal(cipherText);
			bais = new ByteArrayInputStream(response);
			return bais;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new IOException("Security Exception:  " + e);
		}
	}

	/** Proxied to the shadowed HTTP url connection */
	public java.lang.String getHeaderField(java.lang.String s) {
		return urlConnection.getHeaderField(s);
	}

	/** Proxied to the shadowed HTTP url connection */
	public java.lang.String getHeaderFieldKey(int i) {
		return urlConnection.getHeaderFieldKey(i);
	}

	/** Proxied to the shadowed HTTP url connection */
	public java.lang.String getHeaderField(int i) {
		return urlConnection.getHeaderField(i);
	}

	/** Proxied to the shadowed HTTP url connection */
	public java.security.Permission getPermission() throws java.io.IOException {
		return urlConnection.getPermission();
	}

	/** Proxied to the shadowed HTTP url connection */
	public void setUseCaches(boolean b) {
		urlConnection.setUseCaches(b);
	}

	/** Proxied to the shadowed HTTP url connection */
	public boolean getUseCaches() {
		return urlConnection.getUseCaches();
	}

	/** Proxied to the shadowed HTTP url connection */
	public void setIfModifiedSince(long l) {
		urlConnection.setIfModifiedSince(l);
	}

	/** Proxied to the shadowed HTTP url connection */
	public long getIfModifiedSince() {
		return urlConnection.getIfModifiedSince();
	}

	/** Proxied to the shadowed HTTP url connection */
	public boolean getDefaultUseCaches() {
		return urlConnection.getDefaultUseCaches();
	}

	/** Proxied to the shadowed HTTP url connection */
	public void setDefaultUseCaches(boolean b) {
		urlConnection.setDefaultUseCaches(b);
	}

	/** Proxied to the shadowed HTTP url connection */
	public void setRequestProperty(java.lang.String p, java.lang.String v) {
		if (urlConnection != null)
			urlConnection.setRequestProperty(p, v);
	}

	/** Proxied to the shadowed HTTP url connection */
	public java.lang.String getRequestProperty(java.lang.String p) {
		return urlConnection.getRequestProperty(p);
	}

	String encode(String spid) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		byte[] buffer = spid.getBytes("UTF-8");
		for (int i = 0; i < buffer.length; i++) {
			int c = buffer[i] & 0xFF;
			if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')
					|| (c >= '0' && c <= '9') || c == '.' || c == '-'
					|| c == '*' || c == '_')
				sb.append((char) c);
			else
				if (c == ' ')
					sb.append('+');
				else {
					sb.append('%');
					int l = (0xF0 & c) / 16;
					sb.append((char) (l >= 10 ? 'A' + (l - 10) : '0' + l));
					l = c & 0xF;
					sb.append((char) (l >= 10 ? 'A' + (l - 10) : '0' + l));
				}
		}
		return sb.toString();
	}
}
