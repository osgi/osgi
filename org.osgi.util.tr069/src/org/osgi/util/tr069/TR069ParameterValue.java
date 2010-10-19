package org.osgi.util.tr069;

import info.dmtree.DmtData;
import info.dmtree.MetaNode;
import info.dmtree.Uri;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

/******************************/
/**
 * Class which contains value and data type for TR-069, and static methods of
 * utilities.
 * 
 */
public class TR069ParameterValue {
	public static String TR069_TYPE_INT = "int";

	public static String TR069_TYPE_UNSIGNED_INT = "unsignedInt";

	public static String TR069_TYPE_LONG = "long";

	public static String TR069_TYPE_UNSIGNED_LONG = "unsignedLong";

	public static String TR069_TYPE_STRING = "string";

	public static String TR069_TYPE_BOOLEAN = "boolean";

	public static String TR069_TYPE_BASE64 = "base64";

	public static String TR069_TYPE_HEXBINARY = "hexBinary";

	public static String TR069_TYPE_DATETIME = "dateTime";

	/**
	 * This Constants will be defined in DMT package.
	 */
	
	private static boolean debug = true;
	private final String value;
	private final String type;

	/**
	 * Constructor of TR-069 Parameter Value.
	 * 
	 * @param value
	 *            value to be used.
	 * @param tr069DataType
	 *            data type defined in TR-069.
	 */
	public TR069ParameterValue(String value, String type) {
		this.value = value;
		this.type = type;
	}

	public String getValue() {
		return this.value;
	}

	public String getType() {
		return this.type;
	}

	/**
	 * Get DmtData to be used for DmtSession#setNodeValue() against a leaf node.
	 * 
	 * @param value
	 *            value to be set to the specified node uri. It must not be
	 *            null.
	 * @param tr069Type
	 *            TR069 data type specified by SetPamaremeterValues RPC. It must
	 *            not be null or empty.
	 * @param valueCharsetName
	 *            character set to be used for the value. If null, a platform
	 *            default character set will be used. The character set is used
	 *            for getting byte array from the specified value.
	 * @param nodeUri
	 *            URI of the leaf node in DMT. It must be valid absolute DMT
	 *            URI.
	 * @param metaNode
	 *            Meta node of the specified node uri. It must not be null.
	 * @return DmtData to be used for DmtSession#setNodeValue().
	 * @throws TR069MappingException
	 *             if creating DmtData is failed for some reasons.
	 * @throws UnsupportedEncodingException
	 *             if the specified character set name is not supported.
	 * @throws IllegalArgumetException
	 *             if value is null, if tr069Type is either null or empty, if
	 *             nodeUri is invalid absolute DMT URI, or if metaNode is null.
	 */

	public static DmtData getDmtData(String value, String tr069Type,
			String valueCharsetName, String nodeUri, MetaNode metaNode)
			throws TR069MappingException, IllegalArgumentException,
			UnsupportedEncodingException {

		checkArguments(value, tr069Type, nodeUri, metaNode);

		final int format = metaNode.getFormat();
		DmtData data = null;
		if (tr069Type.equals(TR069_TYPE_BASE64)) {
			if ((format & DmtData.FORMAT_BASE64) != 0) {
				byte[] bytes = Base64.base64ToByteArray(value);
				data = new DmtData(bytes, true);
				// XXX:Evgeni:20100929: The correct check is: ((format &
				// DmtData.FORMAT_RAW_BINARY) != 0)
				// XXX:Ikuo:20101001: You are right. I fixed it at all places.
			} else if ((format & DmtData.FORMAT_RAW_BINARY) != 0) {
				String[] formatNames = metaNode.getRawFormatNames();
				if (formatNames.length > 0) {
					// XXX I'm not sure if it is proper operation. How to get
					// byte array ? Please check it.
					// XXX:Evgeni:SOAP request encoding should be used here. One
					// method parameter more is needed?
					// XXX:Ikuo:20100928: Could you provide the concrete code ?
					// XXX:Evgeni:20100929: byte[] bytes =
					// value.getBytes(valueCharsetName);
					// getBytes() - uses CPE default charset name
					// XXX:Ikuo:20101001: I got it.
					byte[] bytes = value.getBytes(valueCharsetName);
					// byte[] bytes = value.getBytes();
					// byte[] bytes = Base64.base64ToByteArray(value);
					data = new DmtData(formatNames[0], bytes);
				} else {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type
							+ " and FORMAT_RAW_BINARY, no RawFormatNames.");
				}
			} else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type
						+ ", FORMAT is neither FORMAT_BASE64 nor FORMAT_RAW_BINARY.");
		} else if (tr069Type.equals(TR069_TYPE_BOOLEAN)) {
			if ((format & DmtData.FORMAT_BOOLEAN) != 0) {
				if ("0".equals(value))
					data = new DmtData(Boolean.FALSE.booleanValue());
				else if ("1".equals(value))
					data = new DmtData(Boolean.TRUE.booleanValue());
				else
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type + "and FORMAT_BOOLEAN, value(=" + value
							+ ") is neither \"0\" nor \"1\".");
			} else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type + ", FORMAT is not FORMAT_BOOLEAN.");
		} else if (tr069Type.equals(TR069_TYPE_INT)) {
			if ((format & DmtData.FORMAT_INTEGER) != 0) {
				try {
					int valueInt = Integer.parseInt(value);
					data = new DmtData(valueInt);
				} catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type + "and FORMAT_INTEGER, value(=" + value
							+ ") cannot be parsed. " + e);
				}
			} else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type + ", FORMAT is not FORMAT_INTEGER.");
		} else if (tr069Type.equals(TR069_TYPE_STRING)) {
			StringBuffer sb = new StringBuffer();
			// if ((value == null || value.equals(""))
			if ((value == null || value.length() == 0)
					&& (format & DmtData.FORMAT_NULL) != 0) {
				data = DmtData.NULL_VALUE;
			}
			if (data == null && (format & DmtData.FORMAT_FLOAT) != 0) {
				try {
					float valueFloat = Float.parseFloat(value);
					data = new DmtData(valueFloat);
				} catch (Exception e) {
					sb.append("\n");
					sb
							.append("Although the format of the node supports FORMAT_FLOAT, Fail to construct DmtData. "
									+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_DATE) != 0) {
				try {
					data = new DmtData(value, DmtData.FORMAT_DATE);
				} catch (Exception e) {
					sb.append("\n");
					sb
							.append("Although the format of the node supports FORMAT_DATE, Fail to construct DmtData. "
									+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_TIME) != 0) {
				try {
					data = new DmtData(value, DmtData.FORMAT_TIME);
				} catch (Exception e) {
					sb.append("\n");
					sb
							.append("Although the format of the node supports FORMAT_TIME, Fail to construct DmtData. "
									+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_NODE_URI) != 0) {
				String valueUri = value;
				int position = nodeUri.lastIndexOf("/");
				if (value.startsWith("./")) {
					valueUri = nodeUri.substring(0, position)
							+ value.substring("./".length());
				}
				try {
					data = new DmtData(valueUri, DmtData.FORMAT_NODE_URI);
				} catch (Exception e) {
					sb.append("\n");
					sb
							.append("Although the format of the node supports FORMAT_NODE_URI, Fail to construct DmtData. "
									+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_RAW_STRING) != 0) {
				try {
					String[] names = metaNode.getRawFormatNames();
					if (names.length > 0 && names[0] != null
					// && (!names[0].equals(""))) {
							&& (names[0].length() != 0)) {
						data = new DmtData(names[0], value);
					}
				} catch (Exception e) {
					sb.append("\n");
					sb
							.append("Although the format of the node supports FORMAT_RAW_STRING, Fail to construct DmtData. "
									+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_STRING) != 0) {
				data = new DmtData(value);
			}
			if (data == null && (format & DmtData.FORMAT_XML) != 0) {
				data = new DmtData(value, DmtData.FORMAT_XML);
			}
			if (data == null)
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type
						+ ", proper FORMAT is not supported by the node. "
						+ sb.toString());
		} else if (tr069Type.equals(TR069_TYPE_UNSIGNED_INT)) {
			if ((format & DmtData.FORMAT_UNSIGNED_INTEGER) != 0) {
				try {
					data = new DmtData(value, DmtData.FORMAT_UNSIGNED_INTEGER);
				} catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type
							+ "and FORMAT_UNSIGNED_INTEGER, value(=" + value
							+ ") is not in appropriate format." + e);
				}
			} else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type
						+ ", FORMAT is not FORMAT_UNSIGNED_INTEGER.");
		} else if (tr069Type.equals(TR069_TYPE_LONG)) {
			if ((format & DmtData.FORMAT_LONG) != 0) {
				try {
					long valueLong = Long.parseLong(value);
					data = new DmtData(valueLong);
				} catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type + "and FORMAT_LONG, value(=" + value
							+ ") cannot be parsed. " + e);
				}
			} else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type + ", FORMAT is not FORMAT_LONG.");
		} else if (tr069Type.equals(TR069_TYPE_UNSIGNED_LONG)) {
			if ((format & DmtData.FORMAT_UNSIGNED_LONG) != 0) {
				try {
					data = new DmtData(value, DmtData.FORMAT_UNSIGNED_LONG);
				} catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type + "and FORMAT_UNSIGNED_LONG, value(="
							+ value + ") is not in appropriate format." + e);
				}
			} else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type
						+ ", FORMAT is not FORMAT_UNSIGNED_INTEGER.");
		} else if (tr069Type.equals(TR069_TYPE_DATETIME)) {
			if ((format & DmtData.FORMAT_DATETIME) != 0) {
				try {
					data = new DmtData(value, DmtData.FORMAT_DATETIME);
				} catch (Exception e) {
					errorInSetParameterValues("Despite of TR069 dataType="
							+ tr069Type + "and FORMAT_DATETIME, value(="
							+ value + ") is not in appropriate format." + e);
				}
			} else
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type + ", FORMAT is not FORMAT_DATETIME.");
		} else if (tr069Type.equals(TR069_TYPE_HEXBINARY)) {
			StringBuffer sb = new StringBuffer();
			if (data == null && (format & DmtData.FORMAT_HEXBINARY) != 0) {
				try {
					// XXX I'm not sure if it is proper operation. How to get
					// byte array ? Please check it.
					// XXX:Evgeni: See my comments in hexBinaryToByteArray
					// method.
					// XXX:Ikuo:20100928: The code here is not needed to be
					// changed, isn't its?
					// XXX:Evgeni:20100929: Looks ok for me.
					byte[] bytes = HexBinary.hexBinaryToByteArray(value);
					data = new DmtData(bytes, DmtData.FORMAT_HEXBINARY);
				} catch (Exception e) {
					sb.append("\n");
					sb
							.append("Although the format of the node supports FORMAT_HEXBINARY, Fail to construct DmtData. "
									+ e.toString());
				}
			}
			if (data == null && (format & DmtData.FORMAT_BINARY) != 0) {
				try {
					// XXX I'm not sure if it is proper operation. How to get
					// byte array ? Please check it.
					// XXX:Evgeni:SOAP request encoding should be used here. One
					// method parameter more is needed?
					// XXX:Ikuo:20100928: Could you provide the concrete code ?
					// XXX:Evgeni:20100929: byte[] bytes =
					// value.getBytes(valueCharsetName);
					// getBytes() - uses CPE default charset name
					// XXX:Ikuo:20101001: I got it.
					byte[] bytes = value.getBytes(valueCharsetName);
					// byte[] bytes = value.getBytes();
					// byte[] bytes = toHexBinaryBytes(value);
					data = new DmtData(bytes, DmtData.FORMAT_BINARY);
				} catch (Exception e) {
					sb.append("\n");
					sb
							.append("Although the format of the node supports FORMAT_HEXBINARY, Fail to construct DmtData. "
									+ e.toString());
				}
			}
			if (data == null)
				errorInSetParameterValues("Despite of TR069 dataType="
						+ tr069Type
						+ ", proper FORMAT is not supported by the node." + sb);
		}

		return data;
	}

	private static void checkArguments(String value, String tr069Type,
			String nodeUri, MetaNode metaNode) {
		String errmsg = null;
		if (value == null)
			errmsg = "value must not be null.";
		else if (tr069Type == null)
			errmsg = "tr069Type must not be null.";
		// XXX:Evgeni:tr069Type.length() == 0
		// XXX:Ikuo:20100928: (tr069Type.equals("")) is equivalent to
		// (tr069Type.length() == 0), isn't it ?
		// XXX:Evgeni:20100929: (tr069Type.length() == 0) is optimized, just a
		// note
		// XXX:Ikuo:20101001: I got it what you mean.
		// else if (tr069Type.equals(""))
		else if (tr069Type.length() == 0)
			errmsg = "tr069Type must not be empty.";
		else if (!Uri.isValidUri(nodeUri))
			errmsg = "nodeUri must be valid URI.";
		else if (!Uri.isAbsoluteUri(nodeUri))
			errmsg = "nodeUri must be absolute URI.";
		else if (metaNode == null)
			errmsg = "metaNode must not be null.";
		if (errmsg != null)
			throw new IllegalArgumentException(errmsg);
	}

	private static void errorInSetParameterValues(String string)
			throws TR069MappingException {
		throw new TR069MappingException(string);

	}

	/**
	 * Get DmtData array to be used for DmtSession#setNodeValue() against the
	 * child nodes of the specified node uri.
	 * 
	 * The node specified by the node uri must be interior node which has the
	 * node type of {@value info.dmtree.spi.DMTConstants#DDF_LIST_SUBTREE}.
	 * 
	 * @param value
	 *            value to be set to the specified interior node. It must not be
	 *            null.
	 * @param tr069Type
	 *            TR069 data type specified by SetPamaremeterValues RPC. It must
	 *            not be null or empty.
	 * @param valueCharsetName
	 *            character set to be used for the value. If null, a platform
	 *            default character set will be used. The character set is used
	 *            for getting byte array from the specified value.
	 * @param nodeUri
	 *            URI of the interior node in DMT. It must be valid absolute DMT
	 *            URI.
	 * @param metaNode
	 *            Meta node of the child leaf node of the specified node uri. It
	 *            must not be null.
	 * @return array of DmtData to be used for DmtSession#setNodeValue() against
	 *         the child nodes of the specified node uri.
	 * @throws TR069MappingException
	 *             if creating DmtData is failed for some reasons.
	 * @throws UnsupportedEncodingException
	 *             if the specified character set name is not supported.
	 * @throws IllegalArgumetException
	 *             if value is null, if tr069Type is either null or empty, if
	 *             nodeUri is invalid absolute DMT URI, or if metaNode is null.
	 */

	// XXX:Ikuo:20101001: valueCharsetName is added.
	public static DmtData[] getDmtDataForList(String value, String tr069Type,
			String valueCharsetName, String nodeUri, MetaNode metaNode)
			throws TR069MappingException, IllegalArgumentException,
			UnsupportedEncodingException {

		checkArguments(value, tr069Type, nodeUri, metaNode);

		StringTokenizer tokenizer = new StringTokenizer(value, ",", false);
		DmtData[] datas = new DmtData[tokenizer.countTokens()];
		for (int i = 0; i < tokenizer.countTokens(); i++) {
			String uri = nodeUri + "/" + (i + 1);
			// XXX:Ikuo:20100928: trim() is added.
			String valueString = escapeDecode(tokenizer.nextToken().trim());
			// XXX:Ikuo:20101001: valueCharsetName is added.
			datas[i] = TR069ParameterValue.getDmtData(valueString, tr069Type,
					valueCharsetName, uri, metaNode);
			// datas[i] = TR069ParameterValue.getDmtData(valueString, tr069Type,
			// uri, metaNode);
		}
		return datas;
	}

	/**
	 * Get the TR069ParameterValue be used for GetParameterValuesResponse RPC
	 * 
	 * @param data
	 *            DmtData retrieved from DMT.
	 * @return TR069ParameterValue to be used for GetParameterValuesResponse
	 *         RPC.
	 */
	public static TR069ParameterValue getTR069ParameterValueForList(
			DmtData[] data) {
		StringBuffer sb = null;
		for (int i = 0; i < data.length; i++) {
			if (sb == null)
				sb = new StringBuffer();

			if (i != 0)
				sb.append(",");
			sb.append(escapeEncode(data[i].toString()));
		}

		return new TR069ParameterValue((sb == null) ? "" : sb.toString(),
				TR069_TYPE_STRING);
	}

	/**
	 * Get the TR069ParameterValue be used for GetParameterValuesResponse RPC
	 * 
	 * @param data
	 *            DmtData retrieved from DMT.
	 * @return TR069ParameterValue to be used for GetParameterValuesResponse
	 *         RPC.
	 */
	public static TR069ParameterValue getTR069ParameterValue(DmtData data) {
		int format = data.getFormat();
		String value = null;
		switch (format) {
		case DmtData.FORMAT_BASE64:
			value = Base64.byteArrayToBase64(data.getBase64());
			return new TR069ParameterValue(value, TR069_TYPE_BASE64);
		case DmtData.FORMAT_BINARY:
			// XXX I'm not sure if it is proper operation. How to get the
			// byte array ? Please check it.
			// value = byteArrayToBinString(data.getBinary());
			// ???? should convert hexBinary ????
			// XXX:Evgeni:return new TR069ParameterValue(data.toString(),
			// TR069_TYPE_HEXBINARY);
			// XXX:Ikuo:20100928: Fixed.
			value = data.toString();
			return new TR069ParameterValue(value, TR069_TYPE_HEXBINARY);
		case DmtData.FORMAT_BOOLEAN:
			value = (data.getBoolean() ? "1" : "0");
			return new TR069ParameterValue(value, TR069_TYPE_BOOLEAN);
		case DmtData.FORMAT_DATE:
		case DmtData.FORMAT_FLOAT:
		case DmtData.FORMAT_NULL:
		case DmtData.FORMAT_RAW_STRING:
		case DmtData.FORMAT_STRING:
		case DmtData.FORMAT_TIME:
		case DmtData.FORMAT_XML:
			return new TR069ParameterValue(data.toString(),
					TR069_TYPE_STRING);
		case DmtData.FORMAT_INTEGER:
			return new TR069ParameterValue(data.toString(), TR069_TYPE_INT);
		case DmtData.FORMAT_NODE:
			throw new IllegalArgumentException("The format is FORMAT_NODE.");
		case DmtData.FORMAT_RAW_BINARY:
			// XXX I'm not sure if it is proper operation. How to get value
			// string ? Please check it.
			value = Base64.byteArrayToBase64(data.getRawBinary());
			return new TR069ParameterValue(value, TR069_TYPE_BASE64);
		case DmtData.FORMAT_UNSIGNED_INTEGER:
			return new TR069ParameterValue(data.toString(),
					TR069_TYPE_UNSIGNED_INT);
		case DmtData.FORMAT_LONG:
			return new TR069ParameterValue(data.toString(), TR069_TYPE_LONG);
		case DmtData.FORMAT_UNSIGNED_LONG:
			return new TR069ParameterValue(data.toString(),
					TR069_TYPE_UNSIGNED_LONG);
		case DmtData.FORMAT_HEXBINARY:
			// XXX Please implement HexBinary.byteArrayToHexBinary() !
			// XXX:Evgeni: See the comment in byteArrayToHexBinary. We can reuse
			// DmtData.toString()
			// XXX:Ikuo:20100928: Fixed.
			value = data.toString();
			// value = HexBinary.byteArrayToHexBinary(data.getHexBinary());
			return new TR069ParameterValue(value, TR069_TYPE_HEXBINARY);
		case DmtData.FORMAT_NODE_URI:
			value = TR069URI.getTR069Path(data.toString());
			return new TR069ParameterValue(value, TR069_TYPE_STRING);
		default:
			break;
		}

		return null;
	}

	// /**
	// * Get bin string from byte array.
	// *
	// * @param bytes
	// * byte array to be converted.
	// * @return bin format string.
	// */
	// private static String byteArrayToBinString(byte[] bytes) {
	// // XXX is it correct operation ?
	// // XXX:Evgeni: Encoding method must be used here. In this
	// // representation, the decoder cannot separate the bytes.
	// // Actually, the method is redundant and can be removed.
	// StringBuffer sb = new StringBuffer(bytes.length);
	// for (int i = 0; i < bytes.length; i++) {
	// sb.append(Byte.toString(bytes[i]));
	// }
	// return sb.toString();
	// }

	private static String escapeEncode(String target) {
		String encoded1 = replaceString(target, '%', "%25");
		// System.out.println("encoded1\t:" + encoded1);
		String encoded2 = replaceString(encoded1, ',', "%2C");
		return encoded2;
	}

	/**
	 * Replace all specified char into the specified String in the specified
	 * target.
	 * 
	 * @param msg
	 *            original message string
	 * @param ch
	 *            character, which is replaced by the specified replacement.
	 * @param replacement
	 *            String, which will replace the specified target character.
	 * @return resulting String
	 */
	// private static String replaceString(String msg, char ch, String
	// replacement) {
	// if (debug)
	// System.out.println("target\t:" + msg);
	// List list = new ArrayList(10);
	// for (int i = 0; i < msg.length(); i++) {
	// char chr = msg.charAt(i);
	// if (chr == ch)
	// list.add(Integer.valueOf(i));
	// }
	// char[] result = new char[msg.length() + (replacement.length() - 1)
	// * list.size()];
	// int originalHead = 0;
	// int currentPosition = 0;
	// for (Iterator places = list.iterator(); places.hasNext();) {
	// int position = ((Integer) places.next()).intValue();
	// for (int i = 0; i < position - originalHead; i++) {
	// result[currentPosition + i] = msg.charAt(i + originalHead);
	// }
	// for (int i = 0; i < replacement.length(); i++) {
	// result[currentPosition + (position - originalHead) + i] = replacement
	// .charAt(i);
	// }
	// currentPosition = currentPosition + (position - originalHead)
	// + replacement.length();
	// originalHead = position + 1;
	// }
	//
	// for (int i = 0; i < msg.length() - originalHead; i++) {
	// result[currentPosition + i] = msg.charAt(i + originalHead);
	// }
	// if (debug)
	// System.out.println("result\t:" + String.valueOf(result));
	// return String.valueOf(result);
	// }
	// XXX:Evgeni:20100929:
	private static String replaceString(String message, char ch,
			String replacement) {
		if (null == message) {
			return null;
		}
		char[] messageChars = message.toCharArray();
		StringBuffer result = new StringBuffer();
		int nextToInsert = 0;
		for (int i = 0; i < messageChars.length; i++) {
			if (ch == messageChars[i]) {
				result.append(messageChars, nextToInsert, i - nextToInsert);
				nextToInsert = i + 1;
				result.append(replacement);
			}
		}
		if (nextToInsert < messageChars.length) {
			result.append(messageChars, nextToInsert, messageChars.length
					- nextToInsert);
		}
		return result.toString();
	}

	private static String escapeDecode(String target) {
		// XXX:Ikuo:20100928: I had implemented original code. Please check
		// it.
		// XXX:Evgeni:20100929: I prefer to decrease the number of loops, see
		// the commented method above.
		// XXX:Ikuo:20101001: Fixed it.
		String encoded1 = replaceString(target, "%2C", ',');
		String encoded2 = replaceString(encoded1, "%25", '%');
		return encoded2;
		// try {
		// // XXX Should we implement by ourself as escapeEncode(String) ???;
		// // XXX:Evgeni:We need to read "UTF-8" from the SOAP request.
		// // We need only a subset of that decoder, it is specified in TR-106
		// return URLDecoder.decode(target, "UTF-8");
		// } catch (UnsupportedEncodingException e) {
		// // XXX:Evgeni:That constructor has been added since 1.5, we cannot
		// // use it.
		// throw new IllegalStateException("\"UTF-8\" must be supported.", e);
		// }
	}

	/**
	 * Replace all specified String into the specified char in the specified
	 * target.
	 * 
	 * @param msg
	 *            original message string
	 * @param target
	 *            string, which is replaced by the specified replacement.
	 * @param replacement
	 *            character, which will replace the specified target string.
	 * @return resulting String
	 */
	private static String replaceString(String message, String target,
			char replacement) {
		if (debug)
			System.out.println("message:" + message + "\t,target:" + target
					+ "\t,replacement=" + replacement);
		if (null == message) {
			return null;
		}
		char[] messageChars = message.toCharArray();
		char[] targetChars = target.toCharArray();
		StringBuffer result = new StringBuffer();
		int nextToInsert = 0;
		for (int i = 0; i < messageChars.length; i++) {
			boolean match = true;
			for (int j = 0; j < targetChars.length; j++) {
				if (messageChars[i + j] != targetChars[j]) {
					match = false;
					break;
				}
			}
			if (match) {
				result.append(messageChars, nextToInsert, i - nextToInsert);
				nextToInsert = i + targetChars.length;
				result.append(replacement);
			}
		}
		if (nextToInsert < messageChars.length) {
			result.append(messageChars, nextToInsert, messageChars.length
					- nextToInsert);
		}
		if (debug)
			System.out.println("result\t:" + result.toString());
		return result.toString();
	}

	// private static String replaceString(String msg, String target,
	// char replacement) {
	// if (debug)
	// System.out.println("target\t:" + msg);
	//
	// List list = new ArrayList(10);
	// int fromIndex = 0;
	// while (true) {
	// int index = msg.indexOf(target, fromIndex);
	// if (index < 0)
	// break;
	// list.add(Integer.valueOf(index));
	// fromIndex = index + target.length();
	// }
	// StringBuffer sb = new StringBuffer(msg.length() - (target.length() - 1)
	// * list.size());
	// int currentPosition = 0;
	// final String chString = String.valueOf(replacement);
	// for (Iterator places = list.iterator(); places.hasNext();) {
	// int position = ((Integer) places.next()).intValue();
	// sb.append(msg.substring(currentPosition, position));
	// sb.append(chString);
	// currentPosition = position + target.length();
	// }
	// if (currentPosition < msg.length())
	// sb.append(msg.substring(currentPosition));
	//
	// if (debug)
	// System.out.println("result\t:" + sb.toString());
	// return String.valueOf(sb.toString());
	// }
//	public static void main(String[] args) {
//		String target = "test%%2C2chogehoge%2%2C4444";
//		replaceString(target, "%2C", ',');
//
//	}
}