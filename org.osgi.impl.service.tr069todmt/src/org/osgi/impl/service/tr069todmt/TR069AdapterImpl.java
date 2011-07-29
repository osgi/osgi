package org.osgi.impl.service.tr069todmt;

import info.dmtree.*;

import java.text.*;
import java.util.*;
import java.util.regex.*;

import org.osgi.service.tr069todmt.*;

import aQute.bnd.annotation.component.*;

/**
 * @author aqute
 *
 */
@Component
public class TR069AdapterImpl implements TR069Adapter {
	static final Pattern FULL_PARAMETER_NAME = Pattern
			.compile("([\\p{L}_][-\\p{L}\\p{Digit}\\p{CombiningDiacriticalMarks}_]*(\\.\\d+)?)*");
	static final Pattern HEXBINARY = Pattern
			.compile("([0-9A-Za-Z][0-9A-Za-Z])+");
	static final Pattern BASE64 = Pattern.compile("([A-Za-z0-9+/]+={0,3}");
	static final Pattern RELATIVE_TIME = Pattern
			.compile("(\\d\\d\\d\\d)-(\\d\\d)-(\\d\\d)T(\\d\\d):(\\d\\d):(\\d\\d)");
	// CCYYMMDD
	static final Pattern DATE = Pattern
			.compile("(\\d\\d\\d\\d)(\\d\\d)(\\d\\d)");

	// hhmmss or hhmmssZ
	static final Pattern TIME = Pattern.compile("(\\d\\d)(\\d\\d)(\\d\\d)Z?");
	private static final Date UNKNOWN_DATE = new Date(0);

	/**
	 * 
	 */
	@Override
	public void setParameterValue(DmtSession session, String fullParameterName,
			String value, int type) throws TR069Exception, DmtException {
		String uri = toURI(session, fullParameterName);

		int format = DmtData.FORMAT_STRING;

		MetaNode node = session.getMetaNode(uri);
		if (node != null)
			format = node.getFormat();

		DmtData data = convert(value, format);
		session.setNodeValue(uri, data);
	}

	DmtData convert(String value, int format)  {
		switch (format) {
		case DmtData.FORMAT_RAW_BINARY:
		case DmtData.FORMAT_BASE64:
		case DmtData.FORMAT_BINARY:
			if (HEXBINARY.matcher(value).matches()) {
				return new DmtData(decodeHexBinary(value),
						format == DmtData.FORMAT_BASE64);
			} else if (BASE64.matcher(value).matches()) {
				return new DmtData(decodeBase64(value),
						format == DmtData.FORMAT_BASE64);
			} else
				throw new TR069Exception(
						"Target is binary but source is not encoded in base64 or hex",
						TR069Exception.INVALID_PARAMETER_TYPE);

		case DmtData.FORMAT_BOOLEAN:
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1"))
				return new DmtData(true);
			if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0"))
				return new DmtData(false);
			throw new TR069Exception(
					"Target is boolean but source is not true,false,0, or 1, it is: "
							+ value, TR069Exception.INVALID_PARAMETER_TYPE);

		case DmtData.FORMAT_DATE_TIME:
			Date date = getDate(value);
			if (date != null)
				return new DmtData(date);
			throw new TR069Exception(
					"Target is dateTime but source is not unknown time (0001-01-01T00:00:00Z), not a proper date format, nor a relative time "
							+ value, TR069Exception.INVALID_PARAMETER_TYPE);

		case DmtData.FORMAT_FLOAT:
			return new DmtData(Float.parseFloat(value));

		case DmtData.FORMAT_INTEGER:
			return new DmtData(Integer.parseInt(value));

		case DmtData.FORMAT_RAW_STRING:
		case DmtData.FORMAT_STRING:
		case DmtData.FORMAT_XML:
			return new DmtData(value, format);

		case DmtData.FORMAT_DATE: {
			Date d = getDate(value);
			if (d != null) {
				DateFormat df = new SimpleDateFormat("yyyyMMdd");
				return new DmtData(df.format(d), DmtData.FORMAT_DATE);
			}
			throw new TR069Exception(
					"Target is date but format is not yyyyMMdd, it is " + value, TR069Exception.INVALID_PARAMETER_TYPE);			
		}
			
		case DmtData.FORMAT_TIME: {
			Date d = getDate(value);
			if (d != null) {
				DateFormat df = new SimpleDateFormat("HHmmss");
				return new DmtData(df.format(d), DmtData.FORMAT_TIME);
			}
			throw new TR069Exception(
					"Target is date but format is not HHmmss, it is " + value, TR069Exception.INVALID_PARAMETER_TYPE);			
		}
		}
		throw new TR069Exception(
				"Unknown format type "+format+" for " + value, TR069Exception.INVALID_PARAMETER_TYPE);			
	}

	private Date getDate(String value) {
		if (value.equals("0001-01-01T00:00:00Z"))
			return UNKNOWN_DATE;

		if (value.endsWith("Z")) {
			try {
				// Absolute time
				SimpleDateFormat FORMAT = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ss'Z'");
				return FORMAT.parse(value);
			} catch (ParseException e) {
				return null;
			}
		}
		// Relative time
		Matcher matcher = RELATIVE_TIME.matcher(value);
		if (matcher.matches()) {
			int year = Integer.parseInt(matcher.group(1));
			int month = Integer.parseInt(matcher.group(2));
			int day = Integer.parseInt(matcher.group(3));
			int hour = Integer.parseInt(matcher.group(4));
			int minute = Integer.parseInt(matcher.group(5));
			int second = Integer.parseInt(matcher.group(6));

			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, year + 1970);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.DAY_OF_MONTH, day);
			c.set(Calendar.HOUR, hour);
			c.set(Calendar.MINUTE, minute);
			c.set(Calendar.SECOND, second);
			Date d = c.getTime();
			long ms = d.getTime();
			return new Date(-ms);
		}
		return null;
	}

	private byte[] decodeBase64(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	private byte[] decodeHexBinary(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 */
	@Override
	public ParameterValue getParameterValue(DmtSession session,
			String fullParameterName) throws TR069Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ParameterInfo getParameterNames(DmtSession session,
			String fullParameterName) throws TR069Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addObject(DmtSession session, String objectName)
			throws TR069Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(DmtSession session, String objectName)
			throws TR069Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String toName(DmtSession session, String uri) throws TR069Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toURI(DmtSession session, String name) throws TR069Exception {
		// TODO Auto-generated method stub
		return null;
	}


	String convert(DmtData value, int type) {
		return null;
	}

}
