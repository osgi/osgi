package org.osgi.util.tr069;

class HexBinary {
	static byte[] hexBinaryToByteArray(String value) {
		// Peter implemented it at BundleFest, June 2010.
		byte[] bytes = new byte[value.length() / 2];
		if (bytes.length * 2 != value.length())
			throw new IllegalArgumentException(
					"Odd length of string not acceptable for hex conversion.");
		int n = 0;
		for (int i = 0; i < value.length(); i += 2) {
			char high = value.charAt(i);
			char low = value.charAt(i + 1);
			//XXX:Evgeni: the hex dump can contain space separator
			// see info.dmtree.DmtData.getHexDump(byte[])
			//XXX:Ikuo: Do we need to change the code in this method ?
			bytes[n++] = (byte) ((nibble(high) << 4) + nibble(low));
		}
		return null;
	}

	private static int nibble(char low) {
		// Peter implemented it at BundleFest, June 2010.
		low = Character.toUpperCase(low);
		int v = low - '0';
		if (v < 0)
			throw new IllegalArgumentException(
					"Odd char, not acceptable for hex conversion.");
		if (v > 9)
			//XXX:Evgeni: the hex dump can contain space separator
			//XXX:Ikuo: thanks. Please check. If not please correct the code directly.
			return low - 'A' + 10; //[A-F] i.e. [10-15]
			//return v + '0' + 10 - 'A';
		else
			return v;
	}

	static String byteArrayToHexBinary(byte[] bytes) {
		// XXX please implement it !
		//XXX:Evgeni: It is implemented in info.dmtree.DmtData.getHexDump(byte[])
		// XXX:Ikuo: thanks.
		return getHexDump(bytes);
	}
	
	// character array of hexadecimal digits, used for printing binary data
	private static char[] hex = "0123456789ABCDEF".toCharArray();

	// Copied from info.dmtree.DmtData .
	// generates a hexadecimal dump of the given binary data
	private static String getHexDump(byte[] bytes) {
		if (bytes.length == 0)
			return "";

		StringBuffer buf = new StringBuffer();
		appendHexByte(buf, bytes[0]);
		for (int i = 1; i < bytes.length; i++)
			appendHexByte(buf.append(' '), bytes[i]);

		return buf.toString();
	}
	
	// Copied from info.dmtree.DmtData .
	private static void appendHexByte(StringBuffer buf, byte b) {
		buf.append(hex[(b & 0xF0) >> 4]).append(hex[b & 0x0F]);
	}


}
