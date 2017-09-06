
package org.osgi.impl.service.tr069todmt;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.osgi.impl.service.tr069todmt.encode.Base64;
import org.osgi.impl.service.tr069todmt.encode.HexBinary;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.Uri;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 *
 */
public class Utils {

	static final Pattern			HEXBINARY_PATTERN			= Pattern.compile("([0-9A-Za-z][0-9A-Za-z])+");
	static final Pattern			BASE64_PATTERN				= Pattern.compile("([A-Za-z0-9+/]+={0,3})");
	static final Pattern			ALIAS_PATTERN				= Pattern.compile("\\[([^\\\\.]+)\\]");
	static final Pattern			INSTANCE_ID_PATTERN			= Pattern.compile("([0-9]+)");
	static final Pattern			THORN_ESCAPE				= Pattern.compile("þ([0-9A-Z]{4})");

	static final char				THORN						= 'þ';
	static final int				UNDERSCORE_CODE				= '_';
	static final String				EXTENDERS					= "\u00B7\u02D0\u02D1\u0387\u0640\u0E46\u0EC6\u3005[\u3031-\u3035][\u309D-\u309E][\u30FC-\u30FE]";
	static final String				COMBINING_CHAR				= "[\u0300-\u0345][\u0360-\u0361][\u0483-\u0486][\u0591-\u05A1][\u05A3-\u05B9][\u05BB-\u05BD]\u05BF" +
																		"[\u05C1-\u05C2]\u05C4[\u064B-\u0652]\u0670[\u06D6-\u06DC][\u06DD-\u06DF][\u06E0-\u06E4][\u06E7-\u06E8]" +
																		"[\u06EA-\u06ED][\u0901-\u0903]\u093C[\u093E-\u094C]\u094D[\u0951-\u0954][\u0962-\u0963][\u0981-\u0983]" +
																		"\u09BC\u09BE\u09BF[\u09C0-\u09C4][\u09C7-\u09C8][\u09CB-\u09CD]\u09D7[\u09E2-\u09E3]\u0A02\u0A3C\u0A3E\u0A3F" +
																		"[\u0A40-\u0A42][\u0A47-\u0A48][\u0A4B-\u0A4D][\u0A70-\u0A71][\u0A81-\u0A83]\u0ABC[\u0ABE-\u0AC5][\u0AC7-\u0AC9]" +
																		"[\u0ACB-\u0ACD][\u0B01-\u0B03]\u0B3C[\u0B3E-\u0B43][\u0B47-\u0B48][\u0B4B-\u0B4D][\u0B56-\u0B57][\u0B82-\u0B83]" +
																		"[\u0BBE-\u0BC2][\u0BC6-\u0BC8][\u0BCA-\u0BCD]\u0BD7[\u0C01-\u0C03][\u0C3E-\u0C44][\u0C46-\u0C48][\u0C4A-\u0C4D]" +
																		"[\u0C55-\u0C56][\u0C82-\u0C83][\u0CBE-\u0CC4][\u0CC6-\u0CC8][\u0CCA-\u0CCD][\u0CD5-\u0CD6][\u0D02-\u0D03]" +
																		"[\u0D3E-\u0D43][\u0D46-\u0D48][\u0D4A-\u0D4D]\u0D57\u0E31[\u0E34-\u0E3A][\u0E47-\u0E4E]\u0EB1[\u0EB4-\u0EB9]" +
																		"[\u0EBB-\u0EBC][\u0EC8-\u0ECD][\u0F18-\u0F19]\u0F35\u0F37\u0F39\u0F3E\u0F3F[\u0F71-\u0F84][\u0F86-\u0F8B][\u0F90-\u0F95]" +
																		"\u0F97[\u0F99-\u0FAD][\u0FB1-\u0FB7]\u0FB9[\u20D0-\u20DC]\u20E1[\u302A-\u302F]\u3099\u309A";

	static final Pattern			CHARS_TO_ESCAPE_PATTERN		= Pattern.compile("[" + EXTENDERS + COMBINING_CHAR + THORN + "\u002F]*");

	static final String				NUMBER_OF_ENTRIES			= "NumberOfEntries";
	static final String				INSTANCE_ID					= "InstanceId";
	static final String				ALIAS						= "Alias";

	static final String				DOT							= ".";
	static final String				COMMA						= ",";
	static final String				SPACE						= " ";
	static final String				PERCENT_ENCODED_COMMA		= "%2C";
	static final String				PERCENT_ENCODED_SPACE		= "%20";

	static final String				TR069_UNKNOWN_TIME			= "0001-01-01T00:00:00Z";
	static final SimpleDateFormat	TR069_DATE_ENCODING_FORMAT	= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	/*
	 * FORMAT_DATE - value must be parsable to an ISO 8601 calendar date in
	 * complete representation, basic format (pattern {@code CCYYMMDD}) TODO -
	 * CC stands for?!? May be Century?
	 */
	static final SimpleDateFormat	DMT_DATE_FORMAT				= new SimpleDateFormat("yyyyMMdd");
	static final SimpleDateFormat	DMT_LOCAL_TIME_FORMAT		= new SimpleDateFormat("HHmmss");
	static final SimpleDateFormat	DMT_UTC_TIME_FORMAT			= new SimpleDateFormat("HHmmss'Z'");

	static {
		TimeZone utc = TimeZone.getTimeZone("UTC");
		TR069_DATE_ENCODING_FORMAT.setTimeZone(utc);
		DMT_DATE_FORMAT.setTimeZone(utc);
	}

	static final BigInteger			MAX_UNSIGNED_LONG			= BigInteger.valueOf(2).pow(64).subtract(BigInteger.valueOf(1));

	@SuppressWarnings("deprecation")
	static String getDmtValueAsString(Node node) throws TR069Exception {
		DmtData dmtValue;
		try {
			dmtValue = node.getDmtValue();
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
		if (dmtValue == null) {
			return null;
		}
		String[] mimeTypes = node.getMimeTypes();
		switch (dmtValue.getFormat()) {
			case DmtData.FORMAT_INTEGER : {
				return encode(Integer.valueOf(dmtValue.getInt()), getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_LONG : {
				return encode(Long.valueOf(dmtValue.getLong()), getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_FLOAT : {
				return encode(Float.valueOf(dmtValue.getFloat()), getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_STRING : {
				return encode(dmtValue.getString(), getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_BOOLEAN : {
				return encode(dmtValue.getBoolean() ? Boolean.TRUE : Boolean.FALSE, getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_BINARY : {
				return encode(dmtValue.getBinary(), getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_BASE64 : {
				return encode(dmtValue.getBase64(), getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_XML : {
				return encode(dmtValue.getXml(), getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_NULL : {
				switch (getTR069Type(dmtValue, mimeTypes)) {
					case TR069Connector.TR069_BOOLEAN : {
						return "false";
					}

					case TR069Connector.TR069_DATETIME : {
						return Utils.TR069_UNKNOWN_TIME;
					}
					case TR069Connector.TR069_INT :
					case TR069Connector.TR069_LONG :
					case TR069Connector.TR069_UNSIGNED_INT :
					case TR069Connector.TR069_UNSIGNED_LONG : {
						return "0";
					}
					case TR069Connector.TR069_STRING : {
						return "null";
					}
				}
			}
			case DmtData.FORMAT_NODE : {
				return encode(node, getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_RAW_STRING : {
				return encode(dmtValue.getRawString(), getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_RAW_BINARY : {
				return encode(dmtValue.getRawBinary(), getTR069Type(dmtValue, mimeTypes));
			}
			case DmtData.FORMAT_DATE : {
				try {
					return Utils.TR069_DATE_ENCODING_FORMAT.format(Utils.DMT_DATE_FORMAT.parse(dmtValue.getDate()));
				} catch (ParseException e) {
					throw new TR069Exception(e.toString(), TR069Exception.INVALID_PARAMETER_TYPE);
				}
			}
			case DmtData.FORMAT_TIME : {
				Date time;
				String value = dmtValue.getTime();
				try {
					// try Local Time Format
					time = Utils.DMT_LOCAL_TIME_FORMAT.parse(value);
				} catch (ParseException e) {
					// try UTC Time Format
					try {
						time = Utils.DMT_UTC_TIME_FORMAT.parse(value);
					} catch (ParseException e1) {
						throw new TR069Exception(e.toString(), TR069Exception.INVALID_PARAMETER_TYPE);
					}
				}
				/*
				 * The FORMAT_TIME must be treated as a relative time for
				 * TR-069.
				 * 
				 * If absolute time is not available to the CPE, it SHOULD
				 * instead indicate the relative time since boot, where the boot
				 * time is assumed to be the beginning of the first day of
				 * January of year 1, or 0001 01 01T00:00:00. Relative time
				 * since boot MUST be expressed using an untimezoned
				 * representation.
				 */
				time.setYear(-1900);/* to represent the year 1 */
				return Utils.TR069_DATE_ENCODING_FORMAT.format(time);
			}
			case DmtData.FORMAT_DATE_TIME : {
				return Utils.TR069_DATE_ENCODING_FORMAT.format(dmtValue.getDateTime());
			}
			default : {
				throw new TR069Exception("Unsupported DMT value type: " + dmtValue.getFormatName(), TR069Exception.INVALID_PARAMETER_TYPE);
			}
		}
	}

	static int getTR069Type(DmtData dmtValue, String[] mimeTypes) {
		int dmtType = dmtValue.getFormat();
		int mimeTypeDefinedType = getMimeTypeDefinedType(mimeTypes);

		switch (dmtType) {
			case DmtData.FORMAT_BASE64 :
			case DmtData.FORMAT_BINARY :
			case DmtData.FORMAT_RAW_BINARY : {
				/* base64 (default), hexBinary */
				if (mimeTypeDefinedType == TR069Connector.TR069_DEFAULT ||
						mimeTypeDefinedType == TR069Connector.TR069_BASE64 ||
						mimeTypeDefinedType == TR069Connector.TR069_HEXBINARY) {
					return mimeTypeDefinedType == TR069Connector.TR069_DEFAULT ? TR069Connector.TR069_BASE64 : mimeTypeDefinedType;
				} else {
					throwException(dmtValue, mimeTypeDefinedType);
				}
			}

			case DmtData.FORMAT_BOOLEAN : {
				/* boolean (default), string */
				if (mimeTypeDefinedType == TR069Connector.TR069_DEFAULT ||
						mimeTypeDefinedType == TR069Connector.TR069_BOOLEAN ||
						mimeTypeDefinedType == TR069Connector.TR069_STRING) {
					return mimeTypeDefinedType == TR069Connector.TR069_DEFAULT ? TR069Connector.TR069_BOOLEAN : mimeTypeDefinedType;
				} else {
					throwException(dmtValue, mimeTypeDefinedType);
				}
			}

			case DmtData.FORMAT_DATE :
			case DmtData.FORMAT_TIME :
			case DmtData.FORMAT_DATE_TIME : {
				/*
				 * datetime (default for FORMAT_DATE and FORMAT_DATE_TIME),
				 * string (default for FORMAT_TIME)
				 */
				if (mimeTypeDefinedType == TR069Connector.TR069_DEFAULT ||
						mimeTypeDefinedType == TR069Connector.TR069_DATETIME ||
						mimeTypeDefinedType == TR069Connector.TR069_STRING) {
					if (mimeTypeDefinedType == TR069Connector.TR069_DEFAULT) {
						if (dmtType == DmtData.FORMAT_TIME) {
							return TR069Connector.TR069_STRING;
						} else {
							return TR069Connector.TR069_DATETIME;
						}
					}
					return mimeTypeDefinedType;
				} else {
					throwException(dmtValue, mimeTypeDefinedType);
				}
			}

			case DmtData.FORMAT_FLOAT :
			case DmtData.FORMAT_INTEGER :
			case DmtData.FORMAT_LONG : {
				/*
				 * int (default for FORMAT_INTEGER), long (default for
				 * FORMAT_LONG and FORMAT_FLOAT), string, unsignedInt,
				 * unsignedLong
				 */
				if (mimeTypeDefinedType == TR069Connector.TR069_DEFAULT ||
						mimeTypeDefinedType == TR069Connector.TR069_INT ||
						mimeTypeDefinedType == TR069Connector.TR069_LONG ||
						mimeTypeDefinedType == TR069Connector.TR069_UNSIGNED_INT ||
						mimeTypeDefinedType == TR069Connector.TR069_UNSIGNED_LONG ||
						mimeTypeDefinedType == TR069Connector.TR069_STRING) {
					if (mimeTypeDefinedType == TR069Connector.TR069_DEFAULT) {
						if (dmtType == DmtData.FORMAT_INTEGER) {
							return TR069Connector.TR069_INT;
						} else {
							return TR069Connector.TR069_LONG;
						}
					}
					return mimeTypeDefinedType;
				} else {
					throwException(dmtValue, mimeTypeDefinedType);
				}
			}

			case DmtData.FORMAT_NULL : {
				/*
				 * boolean, datetime, int, long, string (default), unsignedInt,
				 * unsignedLong
				 */
				if (mimeTypeDefinedType == TR069Connector.TR069_DEFAULT ||
						mimeTypeDefinedType == TR069Connector.TR069_BOOLEAN ||
						mimeTypeDefinedType == TR069Connector.TR069_DATETIME ||
						mimeTypeDefinedType == TR069Connector.TR069_INT ||
						mimeTypeDefinedType == TR069Connector.TR069_LONG ||
						mimeTypeDefinedType == TR069Connector.TR069_STRING ||
						mimeTypeDefinedType == TR069Connector.TR069_UNSIGNED_INT ||
						mimeTypeDefinedType == TR069Connector.TR069_UNSIGNED_LONG) {
					return mimeTypeDefinedType == TR069Connector.TR069_DEFAULT ? TR069Connector.TR069_STRING : mimeTypeDefinedType;
				} else {
					throwException(dmtValue, mimeTypeDefinedType);
				}
			}

			case DmtData.FORMAT_NODE :
			case DmtData.FORMAT_RAW_STRING :
			case DmtData.FORMAT_STRING :
			case DmtData.FORMAT_XML : {
				/* string */
				if (mimeTypeDefinedType == TR069Connector.TR069_DEFAULT || mimeTypeDefinedType == TR069Connector.TR069_STRING) {
					return TR069Connector.TR069_STRING;
				} else {
					throwException(dmtValue, mimeTypeDefinedType);
				}
			}

			default : {
				throw new TR069Exception("Unsupported DMT type: " + dmtValue.getFormatName(), TR069Exception.INVALID_PARAMETER_TYPE);
			}
		}
	}

	private static void throwException(DmtData dmtValue, int mimeTypeDefinedType) {
		throw new TR069Exception(
				"Impossible DMT to TR069 type conversion - DMT Type: " + dmtValue.getFormatName() +
						"; TR069 Type: " + Utils.getTR069TypeName(mimeTypeDefinedType), TR069Exception.INVALID_ARGUMENTS);
	}

	private static String encode(Object object, int tr069Type) throws TR069Exception {
		if (object == null) {
			throw new NullPointerException("Object should be non-null!");
		} else if (object instanceof byte[]) {
			if (((byte[]) object).length == 0) {
				return "";
			} else {
				switch (tr069Type) {
					case TR069Connector.TR069_BASE64 : {
						return new String(Base64.encode((byte[]) object));
					}
					default : {// hexBinary
						return new HexBinary((byte[]) object).getEncoded();
					}
				}
			}
		} else if (object instanceof Date) {
			return Utils.TR069_DATE_ENCODING_FORMAT.format((Date) object);
		} else if (object instanceof Integer ||
				object instanceof Long ||
				object instanceof Float) {
			switch (tr069Type) {
				case TR069Connector.TR069_INT : {
					return String.valueOf(((Number) object).intValue());
				}

				case TR069Connector.TR069_LONG : {
					return String.valueOf(((Number) object).longValue());
				}

				case TR069Connector.TR069_UNSIGNED_INT :
				case TR069Connector.TR069_UNSIGNED_LONG : {
					String stringValue = object instanceof Float ? String.valueOf(((Float) object).intValue()) : object.toString();
					checkUnsignedNumber(stringValue);
					if (tr069Type == TR069Connector.TR069_UNSIGNED_INT) {
						/* check unsignedInt */
						long l = (Long.valueOf(stringValue)).longValue();
						long l1 = 0xfffffffeL;
						if (l < 0L || l > l1) {
							throw new IllegalArgumentException("The unsigned int " + stringValue + " is out of range.");
						}
						return String.valueOf(l);
					} else { /* TR069Connector.TR069_UNSIGNED_LONG */
						/* check unsignedLong */
						BigInteger biginteger = new BigInteger(stringValue);
						if (biginteger.compareTo(BigInteger.valueOf(0)) < 0 || biginteger.compareTo(Utils.MAX_UNSIGNED_LONG) > 0) {
							throw new IllegalArgumentException("The unsigned long " + stringValue + " is out of range.");
						}
						return biginteger.toString();
					}
				}
				default : {
					return object.toString();
				}
			}
		} else if (object instanceof Boolean) {
			return object.toString();
		} else if (object instanceof Node) {
			Node parent = (Node) object;
			String[] children = null;
			try {
				children = parent.getChildrenNames();
			} catch (DmtException e) {
				throw new TR069Exception(e);
			}
			if (children == null) {
				return "";
			} else {
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < children.length; i++) {
					sb.append(Utils.getDmtValueAsString(parent.getChildNode(children[i]))
							.replaceAll(Utils.SPACE, Utils.PERCENT_ENCODED_SPACE)
							.replaceAll(Utils.COMMA, Utils.PERCENT_ENCODED_COMMA))
							.append(Utils.COMMA);
				}
				sb.deleteCharAt(sb.length() - 1);
				return new String(sb);
			}
		} else if (object instanceof String) {
			return (String) object;
		} else {
			throw new IllegalArgumentException("Unsupported type " + object.getClass().getName() + '!');
		}
	}

	private static void checkUnsignedNumber(String s) {
		if (s == null) {
			throw new IllegalArgumentException("The unsigned number string is null.");
		}
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isDigit(s.charAt(i))) {
				throw new IllegalArgumentException("The unsigned number string contains invalid characters: " + s);
			}
		}
	}

	private static int getMimeTypeDefinedType(String[] mimeTypes) {
		if (mimeTypes == null || mimeTypes.length == 0) {
			return TR069Connector.TR069_DEFAULT;
		}
		for (int i = 0; i < mimeTypes.length; i++) {
			if (mimeTypes[i].startsWith(TR069Connector.PREFIX)) {
				if (TR069Connector.TR069_MIME_DEFAULT.equals(mimeTypes[i])) {
					return TR069Connector.TR069_DEFAULT;
				} else if (TR069Connector.TR069_MIME_INT.equals(mimeTypes[i])) {
					return TR069Connector.TR069_INT;
				} else if (TR069Connector.TR069_MIME_UNSIGNED_INT.equals(mimeTypes[i])) {
					return TR069Connector.TR069_UNSIGNED_INT;
				} else if (TR069Connector.TR069_MIME_LONG.equals(mimeTypes[i])) {
					return TR069Connector.TR069_LONG;
				} else if (TR069Connector.TR069_MIME_UNSIGNED_LONG.equals(mimeTypes[i])) {
					return TR069Connector.TR069_UNSIGNED_LONG;
				} else if (TR069Connector.TR069_MIME_STRING.equals(mimeTypes[i])) {
					return TR069Connector.TR069_STRING;
				} else if (TR069Connector.TR069_MIME_STRING_LIST.equals(mimeTypes[i])) {
					return TR069Connector.TR069_STRING;
				} else if (TR069Connector.TR069_MIME_BOOLEAN.equals(mimeTypes[i])) {
					return TR069Connector.TR069_BOOLEAN;
				} else if (TR069Connector.TR069_MIME_BASE64.equals(mimeTypes[i])) {
					return TR069Connector.TR069_BASE64;
				} else if (TR069Connector.TR069_MIME_HEXBINARY.equals(mimeTypes[i])) {
					return TR069Connector.TR069_HEXBINARY;
				} else if (TR069Connector.TR069_MIME_DATETIME.equals(mimeTypes[i])) {
					return TR069Connector.TR069_DATETIME;
				}
			}
		}
		return TR069Connector.TR069_DEFAULT;
	}

	static String getTR069TypeName(int type) {
		switch (type) {
			case TR069Connector.TR069_INT :
				return "int";
			case TR069Connector.TR069_UNSIGNED_INT :
				return "unisgnedInt";
			case TR069Connector.TR069_LONG :
				return "long";
			case TR069Connector.TR069_UNSIGNED_LONG :
				return "unsignedLong";
			case TR069Connector.TR069_STRING :
				return "string";
			case TR069Connector.TR069_BOOLEAN :
				return "boolean";
			case TR069Connector.TR069_BASE64 :
				return "base64";
			case TR069Connector.TR069_HEXBINARY :
				return "hexBinary";
			case TR069Connector.TR069_DATETIME :
				return "dateTime";
			default :
				return "Unknown";
		}
	}

	static String getParentPath(String uri) {
		String[] path = Uri.toPath(uri);
		if (path.length == 0) {
			return "";
		}
		String[] parentPath = new String[path.length - 1];
		System.arraycopy(path, 0, parentPath, 0, parentPath.length);
		return Uri.toUri(parentPath);
	}
}
