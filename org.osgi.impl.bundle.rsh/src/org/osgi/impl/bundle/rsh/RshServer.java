package org.osgi.impl.bundle.rsh;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.util.Hashtable;
import java.security.SecureRandom;
import java.net.Socket;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * A simple server that responds to RSH clients. It has a hard coded shared
 * secret. It will listen to requests on port 1111.
 * 
 * @author breed@almaden.ibm.com
 */
public class RshServer extends Thread {
	/**
	 * Constructor for RshServer.
	 */
	public RshServer() {
		start();
	}

	/** Returns the request string */
	String getRequest(InputStream is) throws IOException {
		int c;
		do {
			c = is.read();
		} while (c != -1 && c != ' ');
		StringBuffer sb = new StringBuffer();
		c = is.read();
		do {
			sb.append((char) c);
			c = is.read();
		} while (c != -1 && c != ' ');
		return sb.toString();
	}

	/** Reads the request headers and throws them away. */
	void eatHeaders(InputStream is) throws IOException {
		int c;
		boolean readNl = false;
		do {
			c = is.read();
			switch (c) {
				case '\n' :
					if (readNl)
						return;
					readNl = true;
				case '\r' :
					break;
				default :
					readNl = false;
			}
		} while (c != -1);
	}

	/** Constant used in calculating the encryption key */
	static final byte	E[]	= {(byte) 0x05, (byte) 0x36, (byte) 0x54,
			(byte) 0x70, (byte) 0x00};
	/** Constant used in calculating the MAC key */
	static final byte	A[]	= {(byte) 0x00, (byte) 0x4f, (byte) 0x53,
			(byte) 0x47, (byte) 0x49};

	/**
	 * Encode the plainText returning the cipher text. Note that the macVal will
	 * get updated with the calculated MAC.
	 */
	byte[] encodeStream(byte plainText[], byte rshSecret[], byte clientFG[],
			byte serverFG[], byte macVal[]) {
		byte m1[], m2[], ka[];
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
			c.init(Cipher.ENCRYPT_MODE, secretKey, iv);
			byte cipherText[] = c.doFinal(plainText);
			Mac mac = Mac.getInstance("HmacSHA1");
			SecretKeySpec macKey = new SecretKeySpec(ka, "HmacSHA1");
			mac.init(macKey);
			byte macCalc[] = mac.doFinal(cipherText);
			System.arraycopy(macCalc, 0, macVal, 0, macVal.length);
			return cipherText;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.toString());
		}
	}

	SecureRandom	rand	= new SecureRandom();

	byte[] generateServerFG() {
		int i;
		byte serverFG[] = new byte[16];
		for (i = 0; i < 4; i++) {
			int r = rand.nextInt();
			serverFG[i * 4] = (byte) (r >> 24);
			serverFG[i * 4 + 1] = (byte) ((r >> 16) & 0xff);
			serverFG[i * 4 + 2] = (byte) ((r >> 8) & 0xff);
			serverFG[i * 4 + 3] = (byte) ((r) & 0xff);
		}
		return serverFG;
	}

	/** The simple hardcoded shared secret */
	byte	rshSecret[]	= "This is my bogus RSH secret".getBytes();

	/**
	 * The thread that accepts connections. Before processing a connection, it
	 * will start up another RshServer to processes the next connection.
	 */
	public void run() {
		OutputStream os = null;
		Socket s = null;
		try {
			s = ss.accept();
			new RshServer();
			InputStream is = s.getInputStream();
			os = s.getOutputStream();
			String req = getRequest(is);
			eatHeaders(is);
			int qIndex = req.indexOf('?');
			if (qIndex == -1) {
				doError(os, "Required parameters not present");
				return;
			}
			String file = req.substring(0, qIndex);
			Hashtable args = parseArgs(req.substring(qIndex + 1));
			String base64ClientFG = (String) args.get("clientfg");
			if (base64ClientFG == null) {
				doError(os, "Missing clientFG");
				return;
			}
			byte clientFG[] = base64Decode(base64ClientFG.getBytes());
			while (file.startsWith("/"))
				file = file.substring(1);
			FileInputStream fis = new FileInputStream(file);
			byte plainText[] = new byte[fis.available()];
			new DataInputStream(fis).readFully(plainText);
			fis.close();
			byte mac[] = new byte[16];
			byte serverFG[] = generateServerFG();
			byte cipherText[] = encodeStream(plainText, rshSecret, clientFG,
					serverFG, mac);
			os
					.write("HTTP/1.0 200 OK\r\nContent-Type: application/x-rsh\r\n\r\n"
							.getBytes());
			DataOutputStream dos = new DataOutputStream(os);
			dos.writeInt(0x2e);
			dos.writeShort(0x0100);
			dos.write(serverFG);
			dos.writeInt(0x10);
			dos.write(mac);
			dos.writeInt(cipherText.length);
			dos.write(cipherText);
			dos.close();
		}
		catch (Exception e) {
			if (os != null)
				try {
					doError(os, e.toString());
				}
				catch (IOException ee) {
				}
			e.printStackTrace();
		}
		finally {
			try {
				if (s != null)
					s.close();
			}
			catch (Exception e) {
			}
		}
	}

	int fromHex(char c) {
		if (c >= '0' && c <= '9')
			c -= '0';
		else
			if (c >= 'a' && c <= 'f')
				c -= 'a' - 0xa;
			else
				if (c >= 'A' && c <= 'F')
					c -= 'A' - 0xa;
				else
					throw new RuntimeException(c + " is not a hex char");
		return c;
	}

	/**
	 * This decodes the URLencoded request. It is here since URLDecoder didn't
	 * appear until JDK 1.2
	 */
	String urlDecode(String string) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			if (c == '%') {
				sb
						.append((char) ((fromHex(string.charAt(++i)) << 4) + fromHex(string
								.charAt(++i))));
			}
			else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * Method parseArgs.
	 * 
	 * @param string the query string of a request.
	 * @return Hashtable the table of key value pairs from the query string.
	 */
	Hashtable parseArgs(String string) {
		int andIndex;
		int start = 0;
		Hashtable table = new Hashtable();
		do {
			String pair;
			andIndex = string.indexOf('&', start);
			if (andIndex != -1)
				pair = string.substring(start, andIndex);
			else
				pair = string.substring(start);
			int equalIndex = pair.indexOf('=');
			if (equalIndex != -1) {
				table.put(urlDecode(pair.substring(0, equalIndex)),
						urlDecode(pair.substring(equalIndex + 1)));
			}
			start = andIndex + 1;
		} while (andIndex != -1);
		return table;
	}

	int get6BitDecode(byte b) {
		if (b >= 'A' && b <= 'Z')
			return b - 'A';
		if (b >= 'a' && b <= 'z')
			return b - 'a' + 26;
		if (b >= '0' && b <= '9')
			return b - '0' + 52;
		if (b == '+')
			return 62;
		if (b == '/')
			return 63;
		return 0;
	}

	/**
	 * Method base64Decode.
	 * 
	 * @param string the base64 encoded string.
	 */
	byte[] base64Decode(byte string[]) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int i = 0; i < string.length; i += 4) {
			int t = 0;
			t += get6BitDecode(string[i]) << 18;
			t += get6BitDecode(string[i + 1]) << 12;
			t += get6BitDecode(string[i + 2]) << 6;
			t += get6BitDecode(string[i + 3]);
			baos.write((byte) (t >> 16));
			if (string[i + 2] != '=')
				baos.write((byte) ((t >> 8) & 0xff));
			if (string[i + 3] != '=')
				baos.write((byte) ((t) & 0xff));
		}
		return baos.toByteArray();
	}

	/**
	 * Method doError.
	 * 
	 * @param os destination of the error return code
	 * @param string error message
	 */
	void doError(OutputStream os, String string) throws IOException {
		os.write("HTTP/1.0 400 Error\r\nContent-Type: text/html\r\n\r"
				.getBytes());
		os.write("<h1>".getBytes());
		os.write(string.getBytes());
		os.write("</h1>".getBytes());
	}

	static ServerSocket	ss;

	public static void main(String[] args) {
		try {
			ss = new ServerSocket(1111);
			new RshServer();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
