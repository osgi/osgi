package org.osgi.util.tr069;

import info.dmtree.Uri;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/**
 * Utility class for translating between DMT URI and TR-069 Path.
 * 
 * @author Ikuo YAMASAKI, NTT Corporation
 * 
 */
public class TR069URI {

	private TR069URI() {
	};

	/**
	 * Get the URI in DMT corresponding to the specified absolute path in
	 * TR-069.
	 * 
	 * The specified TR-069 path should be valid absolute TR-069 path. If the
	 * specified path ends with period Åg.Åh, it means a Partial path. In that
	 * case, the conversion will be done after the last period is removed.
	 * 
	 * @param tr069Path
	 *            absolute TR-069 path
	 * @return URI in DMT, corresponding to the specified TR-069 path.
	 * @throws IllegalArgumentException
	 *             tr069Path is not either valid or absolute path.
	 */
	public static final String getDmtUri(final String tr069Path)
			throws IllegalArgumentException {
		if (!isValidTR069Path(tr069Path))
			throw new IllegalArgumentException("tr069Path must be valid:"
					+ tr069Path);
		if (!isAbsoluteTR069Path(tr069Path))
			throw new IllegalArgumentException("tr069Path must be absolute:"
					+ tr069Path);

		String result = tr069Path;
		if (result.endsWith(Uri.ROOT_NODE))
			result = result.substring(0, result.length() - 1);
		if (result.length() == 0)
			return Uri.ROOT_NODE;
		return Uri.ROOT_NODE + Uri.PATH_SEPARATOR
				+ result.replace(Uri.ROOT_NODE_CHAR, Uri.PATH_SEPARATOR_CHAR);
	}

	/**
	 * Get the TR069 path corresponding to the specified URI in DMT.
	 * 
	 * The specified URI should be valid absolute URI.
	 * 
	 * @param uri
	 *            URI in DMT.
	 * @return path in TR-069, corresponding to the specified URI.
	 * @throws java.lang.IllegalArgumetException
	 *             if uri is not either valid or absolute uri.
	 */
	public static final String getTR069Path(final String uri) {
		// if (uri == null)
		// throw new IllegalArgumentException("uri must not be null.");
		if (!Uri.isValidUri(uri))
			throw new IllegalArgumentException("uri must be valid:" + uri);
		if (!Uri.isAbsoluteUri(uri))
			throw new IllegalArgumentException("uri must be absolute:" + uri);
		if (uri.equals(Uri.ROOT_NODE))
			return "";
		String ret = uri.substring((Uri.ROOT_NODE + Uri.PATH_SEPARATOR)
				.length());
		return ret.replace(Uri.ROOT_NODE_CHAR, Uri.PATH_SEPARATOR_CHAR);
	}

	/**
	 * Get the TR-069 absolute path corresponding to the specified TR-069 base
	 * path and the specified TR-069 path.
	 * <p>
	 * tr069BasePath must be absolute but tr069Path can be relative or absolute.
	 * If tr069path is absolute, it is returned unaltered. Otherwise an absolute
	 * path is formed using tr069BasePath and returned *
	 * 
	 * @param tr069BasePath
	 *            TR-069 base path. It must be TR-069 absolute path, which means
	 *            it cannot end with Uri.ROOT_NODE.
	 * @param tr069Path
	 * @throws java.lang.IllegalArgumetException
	 *             if either tr069BasePath or tr069Path is any of {@code null},
	 *             empty, invalid TR-069 path, and if tr069BasePath is not
	 *             TR-069 absolute path.
	 * @return absolute path in TR-069.
	 */
	public static final String getTR069AbsolutePath(final String tr069BasePath,
			String tr069Path) {

		if (tr069BasePath == null)
			throw new IllegalArgumentException(
					"tr069BasePath must not be null.");
		if (tr069BasePath.length() == 0)
			throw new IllegalArgumentException(
					"tr069BasePath must not be empty:" + tr069BasePath);
		if (!TR069URI.isValidTR069Path(tr069BasePath))
			throw new IllegalArgumentException(
					"tr069BasePath must be valid TR-069 path:" + tr069BasePath);
		if (tr069BasePath.startsWith(Uri.ROOT_NODE))
			throw new IllegalArgumentException(
					"tr069BasePath must be absolute.:" + tr069BasePath);
		if (tr069Path == null)
			throw new IllegalArgumentException("tr069Path must not be null:"
					+ tr069Path);
		if (tr069Path.length() == 0)
			throw new IllegalArgumentException("tr069Path must not be empty:"
					+ tr069Path);
		if (!TR069URI.isValidTR069Path(tr069Path))
			throw new IllegalArgumentException(
					"tr069Path must be valid TR-069 path:" + tr069Path);
		if (tr069Path.startsWith(Uri.ROOT_NODE))
			return tr069Path;

		return tr069BasePath + Uri.ROOT_NODE + tr069Path;
	}

	public static void main(String[] args) {

		// try {
		// TR069ParameterValue.getDmtDataForList("1,,3", "int", null,
		// "./Device/Services", null);
		// } catch (IllegalArgumentException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (TR069MappingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//

		String target = "Device.Services.";
		// System.out.println("target=[" + target + "]");
		// boolean retIsValidTR069Path = isValidTR069Path(target);
		// System.out.println("retIsValidTR069Path=[" + retIsValidTR069Path +
		// "]");
		// boolean retIsAbsTR069Path = isAbsoluteTR069Path(target);
		// System.out.println("retIsAbsTR069Path=[" + retIsAbsTR069Path + "]");
		String retGetDmtUri = getDmtUri(target);
		System.out.println("retGetDmtUri=[" + retGetDmtUri + "]");
		//

		// boolean retIsValidUri = Uri.isValidUri(target);
		// System.out.println("retIsValidUri=[" + retIsValidUri + "]");
		// boolean retIsAbsUri = Uri.isAbsoluteUri(target);
		// System.out.println("retIsAbsUri=[" + retIsAbsUri + "]");
		// String retGetTR069Path = getTR069Path(target);
		// System.out.println("retGetTR069Path=[" + retGetTR069Path + "]");
	}

	/**
	 * Checks whether the specified path is an absolute TR-069 path. An absolute
	 * path contains the complete path, which starts with ".".
	 * 
	 * @param uri
	 *            the TR-069 path to be checked, must contain a valid TR-069
	 *            path
	 * @return whether the specified path is absolute
	 * @throws IllegalArgumentException
	 *             if the specified path is invalid TR-069 path
	 */
	public static boolean isAbsoluteTR069Path(final String tr069Path) {
		if (!isValidTR069Path(tr069Path))
			throw new IllegalArgumentException("tr069Path must be valid: "
					+ tr069Path);
		if (tr069Path.startsWith(Uri.ROOT_NODE))
			return false;
		return true;
	}

	/**
	 * Checks whether the specified path is valid TR-069 path. A path is
	 * considered valid if it meets the following constraints:
	 * <ul>
	 * <li>the path is not {@code null};
	 * <li>the path follows the syntax defined for valid TR-069 path; the path
	 * can be either absolute or relative. The path cannot end with ".".
	 * </ul>
	 * 
	 * @param tr069Path
	 *            the path to be validated
	 * @return whether the specified path is valid
	 */
	public static final boolean isValidTR069Path(final String tr069Path) {

		if (tr069Path == null)
			return false;
		if (tr069Path.length() == 0)
			return true;
		String tmp = tr069Path;

		int position = tmp.indexOf(Uri.ROOT_NODE);
		if (position == 0)
			tmp = tmp.substring(1);

		if (tmp.endsWith(Uri.ROOT_NODE))
			return false;
		if (tmp.indexOf(" ") != -1)
			return false;

		String token = null;
		while (true) {
			position = tmp.indexOf(Uri.ROOT_NODE);
			if (position == -1)
				token = tmp;
			else
				token = tmp.substring(0, position).toLowerCase();
			tmp = tmp.substring(position + 1);
			if (token.length() == 0)
				return false;
			if (isDigits(token.charAt(0))) {
				for (int i = 1; i < token.length(); i++) {
					if (!isDigits(token.charAt(i)))
						return false;
				}
			} else {
				if (!isLettersOrUnderscores(token.charAt(0)))
					return false;
				for (int i = 1; i < token.length(); i++) {
					char ch = token.charAt(i);
					if (isLettersOrUnderscores(ch))
						continue;
					if (isDigits(ch))
						continue;
					if (!isHyphens(ch))
						return false;
				}
			}
			if (position == -1)
				break;

		}
		return true;
	}

	private static final int[] baseCharRange = new int[] { 0x0041, 0x005A,
			0x0061, 0x007A, 0x00C0, 0x00D6, 0x00D8, 0x00F6, 0x00F8, 0x00FF,
			0x0100, 0x0131, 0x0134, 0x013E, 0x0141, 0x0148, 0x014A, 0x017E,
			0x0180, 0x01C3, 0x01CD, 0x01F0, 0x01F4, 0x01F5, 0x01FA, 0x0217,
			0x0250, 0x02A8, 0x02BB, 0x02C1, 0x0388, 0x038A, 0x038E, 0x03A1,
			0x03A3, 0x03CE, 0x03D0, 0x03D6, 0x03E2, 0x03F3, 0x0401, 0x040C,
			0x040E, 0x044F, 0x0451, 0x045C, 0x045E, 0x0481, 0x0490, 0x04C4,
			0x04C7, 0x04C8, 0x04CB, 0x04CC, 0x04D0, 0x04EB, 0x04EE, 0x04F5,
			0x04F8, 0x04F9, 0x0531, 0x0556, 0x0561, 0x0586, 0x05D0, 0x05EA,
			0x05F0, 0x05F2, 0x0621, 0x063A, 0x0641, 0x064A, 0x0671, 0x06B7,
			0x06BA, 0x06BE, 0x06C0, 0x06CE, 0x06D0, 0x06D3, 0x06E5, 0x06E6,
			0x0905, 0x0939, 0x0958, 0x0961, 0x0985, 0x098C, 0x098F, 0x0990,
			0x0993, 0x09A8, 0x09AA, 0x09B0, 0x09B6, 0x09B9, 0x09DC, 0x09DD,
			0x09DF, 0x09E1, 0x09F0, 0x09F1, 0x0A05, 0x0A0A, 0x0A0F, 0x0A10,
			0x0A13, 0x0A28, 0x0A2A, 0x0A30, 0x0A32, 0x0A33, 0x0A35, 0x0A36,
			0x0A38, 0x0A39, 0x0A59, 0x0A5C, 0x0A72, 0x0A74, 0x0A85, 0x0A8B,
			0x0A8F, 0x0A91, 0x0A93, 0x0AA8, 0x0AAA, 0x0AB0, 0x0AB2, 0x0AB3,
			0x0AB5, 0x0AB9, 0x0B05, 0x0B0C, 0x0B0F, 0x0B10, 0x0B13, 0x0B28,
			0x0B2A, 0x0B30, 0x0B32, 0x0B33, 0x0B36, 0x0B39, 0x0B5C, 0x0B5D,
			0x0B5F, 0x0B61, 0x0B85, 0x0B8A, 0x0B8E, 0x0B90, 0x0B92, 0x0B95,
			0x0B99, 0x0B9A, 0x0B9E, 0x0B9F, 0x0BA3, 0x0BA4, 0x0BA8, 0x0BAA,
			0x0BAE, 0x0BB5, 0x0BB7, 0x0BB9, 0x0C05, 0x0C0C, 0x0C0E, 0x0C10,
			0x0C12, 0x0C28, 0x0C2A, 0x0C33, 0x0C35, 0x0C39, 0x0C60, 0x0C61,
			0x0C85, 0x0C8C, 0x0C8E, 0x0C90, 0x0C92, 0x0CA8, 0x0CAA, 0x0CB3,
			0x0CB5, 0x0CB9, 0x0CE0, 0x0CE1, 0x0D05, 0x0D0C, 0x0D0E, 0x0D10,
			0x0D12, 0x0D28, 0x0D2A, 0x0D39, 0x0D60, 0x0D61, 0x0E01, 0x0E2E,
			0x0E32, 0x0E33, 0x0E40, 0x0E45, 0x0E81, 0x0E82, 0x0E87, 0x0E88,
			0x0E94, 0x0E97, 0x0E99, 0x0E9F, 0x0EA1, 0x0EA3, 0x0EAA, 0x0EAB,
			0x0EAD, 0x0EAE, 0x0EB2, 0x0EB3, 0x0EC0, 0x0EC4, 0x0F40, 0x0F47,
			0x0F49, 0x0F69, 0x10A0, 0x10C5, 0x10D0, 0x10F6, 0x1102, 0x1103,
			0x1105, 0x1107, 0x110B, 0x110C, 0x110E, 0x1112, 0x1154, 0x1155,
			0x115F, 0x1161, 0x116D, 0x116E, 0x1172, 0x1173, 0x11AE, 0x11AF,
			0x11B7, 0x11B8, 0x11BC, 0x11C2, 0x1E00, 0x1E9B, 0x1EA0, 0x1EF9,
			0x1F00, 0x1F15, 0x1F18, 0x1F1D, 0x1F20, 0x1F45, 0x1F48, 0x1F4D,
			0x1F50, 0x1F57, 0x1F5F, 0x1F7D, 0x1F80, 0x1FB4, 0x1FB6, 0x1FBC,
			0x1FC2, 0x1FC4, 0x1FC6, 0x1FCC, 0x1FD0, 0x1FD3, 0x1FD6, 0x1FDB,
			0x1FE0, 0x1FEC, 0x1FF2, 0x1FF4, 0x1FF6, 0x1FFC, 0x212A, 0x212B,
			0x2180, 0x2182, 0x3041, 0x3094, 0x30A1, 0x30FA, 0x3105, 0x312C,
			0xAC00, 0xD7A3 };
	private static final int[] baseCharNoRange = new int[] { 0x0386, 0x038C,
			0x03DA, 0x03DC, 0x03DE,
			0x03E0,
			0x06D5,
			0x09B2,
			0x0559,
			// 0x093D | 0x0A8D, 0x0ABD, 0x0AE0, 0x0B3D, 0x0A5E | 0x0CDE, 0x0E30,
			0x093D, 0x0A8D, 0x0ABD, 0x0AE0, 0x0B3D, 0x0A5E, 0x0CDE, 0x0E30,
			0x0EB0, 0x0E8A, 0x0E8D, 0x0B9C, 0x0EA5, 0x0EA7, 0x0E84, 0x0EBD,
			0x1100, 0x1109, 0x113C, 0x113E, 0x1140, 0x114C, 0x114E, 0x1150,
			0x1159, 0x1163, 0x1165, 0x1167, 0x1169, 0x1175, 0x119E, 0x11A8,
			0x11AB, 0x11BA, 0x11EB, 0x11F0, 0x11F9, 0x1F59, 0x1F5B, 0x1F5D,
			0x1FBE, 0x2126, 0x212E };
	private static final int[] ideographicRange = new int[] { 0x4E00, 0x9FA5,
			0x3021, 0x3029 };
	private static final int[] ideographicNoRange = new int[] { 0x3007 };
	private static final int[] digitRange = new int[] { 0x0030, 0x0039, 0x0660,
			0x0669, 0x06F0, 0x06F9, 0x0966, 0x096F, 0x09E6, 0x09EF, 0x0A66,
			0x0A6F, 0x0AE6, 0x0AEF, 0x0B66, 0x0B6F, 0x0BE7, 0x0BEF, 0x0C66,
			0x0C6F, 0x0CE6, 0x0CEF, 0x0D66, 0x0D6F, 0x0E50, 0x0E59, 0x0ED0,
			0x0ED9, 0x0F20, 0x0F29 };

	// The letter check should be done according:
	// http://www.w3.org/TR/REC-xml, Appendix B, [88] Digit

	private static boolean isHyphens(char chr) {
		if (chr == '-')
			return true;
		return false;
	}

	private static boolean isDigits(char chr) {
		for (int i = 0; i < digitRange.length; i = i + 2) {
			if (chr >= digitRange[i] && chr <= digitRange[i + 1])
				return true;
		}
		return false;
	}

	// The letter check should be done according:
	// http://www.w3.org/TR/REC-xml, Appendix B, [84] Letter
	private static boolean isLettersOrUnderscores(char chr) {
		if (chr == '_')
			return true;
		for (int i = 0; i < baseCharNoRange.length; i++) {
			if (chr == baseCharNoRange[i])
				return true;
		}
		for (int i = 0; i < baseCharRange.length; i = i + 2) {
			if (chr >= baseCharRange[i] && chr <= baseCharRange[i + 1])
				return true;
		}
		for (int i = 0; i < ideographicNoRange.length; i++) {
			if (chr == ideographicNoRange[i])
				return true;
		}
		for (int i = 0; i < ideographicRange.length; i = i + 2) {
			if (chr >= ideographicRange[i] && chr <= ideographicRange[i + 1])
				return true;
		}
		return false;
	}
}
