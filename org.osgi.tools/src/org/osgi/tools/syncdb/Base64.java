package org.osgi.tools.syncdb;

public class Base64 {
	byte[]				data;

	static final String	alphabet	= "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	public Base64(byte data[]) {
		this.data = data;
	}
	
	public String toString() {
		return toBase64(data);
	}
		
	public static String toBase64(byte [] data) { 
		StringBuffer sb = new StringBuffer();
		int buf = 0;
		int bits = 0;
		int n = 0;

		while (true) {
			if (bits >= 6) {
				bits -= 6;
				int v = 0x3F & (buf >> bits);
				sb.append(alphabet.charAt(v));
			}
			else {
				if (n >= data.length)
					break;

				buf <<= 8;
				buf |= 0xFF & data[n++];
				bits += 8;
			}
		}
		if (bits != 0) // must be less than 7
			sb.append(alphabet.charAt(0x3F & (buf << (6 - bits))));

		int mod = 4 - (sb.length() % 4);
		if (mod != 4) {
			for (int i = 0; i < mod; i++)
				sb.append('=');
		}
		return sb.toString();
	}
}
