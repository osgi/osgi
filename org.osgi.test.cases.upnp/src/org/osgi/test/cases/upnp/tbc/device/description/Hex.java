package org.osgi.test.cases.upnp.tbc.device.description;

/**
 * 
 * 
 * @author 
 * @version 
 * @since 
 */
import java.util.*;

public final class Hex {
	/**
	 * Encodes <code>byte[]</code> into HEX.
	 * 
	 * @return String which contains encoded in HEX bytes If
	 *         byte[] is with length 0 retunrs an empty
	 *         String
	 */
	public static final String encode(byte[] buff) {
		String enc = "";
		for (int i = 0; i < buff.length; i++) {
			enc = enc + Integer.toHexString((int) buff[i])
					+ (i == buff.length - 1 ? new String() : "-");
		}
		return enc;
	}

	/**
	 * Decodes HEX form a <code>String</code>.
	 * 
	 * @param String to decode
	 * @return byte[] decoded bytes
	 * @exception Exception if the string does not contain HEX
	 *            encoded bytes If there are <code>\n</code> or
	 *            <code>\r</code> line breaks they are ignored
	 */
	public static final byte[] decode(String input) throws Exception {
		Vector out = new Vector();
		int n = input.indexOf('\n');
		int r = input.indexOf('\r');
		while ((input != null) && ((n != -1) || (r != -1))) {
			input = (n != -1 ? input.substring(0, input.indexOf('\n'))
					+ input.substring(input.indexOf('\n') + 1) : input);
			input = (r != -1 ? input.substring(0, input.indexOf('\r'))
					+ input.substring(input.indexOf('\r') + 1) : input);
			n = input.indexOf('\n');
			r = input.indexOf('\r');
		}
		input = '-' + input + '-';
		while ((input.length() > 0) && (!input.equals("--"))
				&& (!input.equals("-"))) {
			String tmp = extract("-", "-", input);
			out.addElement(tmp);
			input = "-" + input.substring(tmp.length() + 2);
		}
		byte buff[] = new byte[out.size()];
		if ((out.size() > 0)) {
			for (int i = 0; i < buff.length; i++) {
				try {
					buff[i] = Byte.parseByte(((String) out.elementAt(i)), 16);
				}
				catch (Exception e) {
					throw new Exception("Usupported format");
				}
			}
		}
		else {
			throw new Exception("Usupported format");
		}
		return buff;
	}

	private static final String extract(String beg, String end, String whole) {
		int start = whole.indexOf(beg) + beg.length();
		int stop = whole.indexOf(end, start + (start < whole.length() ? 1 : 0));
		String substring;
		if ((start > 0) && (stop > start)) {
			substring = whole.substring(start, stop);
		}
		else
			return new String();
		return substring.trim();
	}
}