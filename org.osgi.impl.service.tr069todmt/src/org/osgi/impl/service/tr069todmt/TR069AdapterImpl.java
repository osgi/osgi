package org.osgi.impl.service.tr069todmt;

import org.osgi.service.dmt.*;

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
public class TR069AdapterImpl implements TR069Connector {
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

	DmtSession session;
	int alias = 1;

	/**
	 * 
	 */

	Pattern ALIAS_HANDLING = Pattern.compile("(.*)\\.([^\\.]+)\\.Alias");

	@Override
	public void setParameterValue(String fullParameterName, String value,
			int type) throws TR069Exception {
		try {

			Matcher m = ALIAS_HANDLING.matcher(fullParameterName);
			if (m.matches()) {
				String oldNameEscaped = unescapeTR(m.group(2));
				String oldName = Uri.encode(oldNameEscaped);
				String newName = Uri.encode(value);

				String mapURI;
				mapURI = toURI(m.group(1), false);

				session.renameNode(mapURI + "/" + oldName, mapURI + "/"
						+ newName);
				return;
			}

			String uri = toURI(fullParameterName, false);

			int format = DmtData.FORMAT_STRING;

			MetaNode node = session.getMetaNode(uri);
			if (node != null)
				format = node.getFormat();

			DmtData data = convert(value, format);
			session.setNodeValue(uri, data);
		} catch (DmtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	DmtData convert(String value, int format) {
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
						TR069Exception.INVALID_PARAMETER_TYPE, null);

		case DmtData.FORMAT_BOOLEAN:
			if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1"))
				return new DmtData(true);
			if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0"))
				return new DmtData(false);
			throw new TR069Exception(
					"Target is boolean but source is not true,false,0, or 1, it is: "
							+ value, TR069Exception.INVALID_PARAMETER_TYPE,
					null);

		case DmtData.FORMAT_DATE_TIME:
			Date date = getDate(value);
			if (date != null)
				return new DmtData(date);
			throw new TR069Exception(
					"Target is dateTime but source is not unknown time (0001-01-01T00:00:00Z), not a proper date format, nor a relative time "
							+ value, TR069Exception.INVALID_PARAMETER_TYPE,
					null);

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
					"Target is date but format is not yyyyMMdd, it is " + value,
					TR069Exception.INVALID_PARAMETER_TYPE, null);
		}

		case DmtData.FORMAT_TIME: {
			Date d = getDate(value);
			if (d != null) {
				DateFormat df = new SimpleDateFormat("HHmmss");
				return new DmtData(df.format(d), DmtData.FORMAT_TIME);
			}
			throw new TR069Exception(
					"Target is date but format is not HHmmss, it is " + value,
					TR069Exception.INVALID_PARAMETER_TYPE, null);
		}
		}
		throw new TR069Exception("Unknown format type " + format + " for "
				+ value, TR069Exception.INVALID_PARAMETER_TYPE, null);
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
	public ParameterValue getParameterValue(String fullParameterName)
			throws TR069Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<ParameterInfo> getParameterNames(String fullParameterName)
			throws TR069Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addObject(String objectName) throws TR069Exception {
		try {

			String uri = toURI(objectName, false);
			if (isMap(uri)) {

				String newObjectId = findUnique(uri);
				session.createInteriorNode(newObjectId);
				if (session.isNodeUri(newObjectId + "/InstanceId")) {
					DmtData data = session.getNodeValue(newObjectId
							+ "/InstanceId");
					String s = data.toString();
					session.renameNode(uri, newObjectId);
				}

			} else if (isList(uri)) {

			}

			return null;
		} catch (DmtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new TR069Exception(e);
		}
	}

	private boolean isList(String uri) {
		// TODO Auto-generated method stub
		return false;
	}

	private String findUnique(String uri) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean isMap(String uri) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void deleteObject(String objectName) throws TR069Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String toName(String uri) throws TR069Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toURI(String name, boolean create) throws TR069Exception {
		try {
			List<String> segments = Arrays.asList(name.split("."));
			List<String> out = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();

			traverse(segments, sb);
			return null;
		} catch (DmtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new TR069Exception(e);
		}

	}

	static Pattern NUMBER = Pattern.compile("[1-9][0-9]*");

	private boolean traverse(List<String> segments, StringBuilder uri)
			throws DmtException {
		String escaped = segments.remove(0);
		String unescaped = unescapeTR(escaped);
		String encoded = Uri.encode(unescaped);
		
		StringBuilder sb = new StringBuilder(encoded);
		
		uri.append('/');
		int rover = uri.length();
		uri.append(sb);
		String u = uri.toString();
		String type = session.getNodeType(u);

		if (!session.isNodeUri(u))
			throw new DmtException(u, DmtException.INVALID_URI,
					"URI does not exist");

		if (segments.size() == 0)
			return true;

		if (isMapOrList(type) && segments.size() > 0
				&& isNumber(segments.get(0)))
			doInstanceId(segments, uri);

		return traverse(segments, uri);
	}

	private boolean isNumber(String string) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isMapOrList(String type) {
		// TODO Auto-generated method stub
		return false;
	}

	private void doInstanceId(List<String> segments, StringBuilder uri)
			throws DmtException {
		String s = segments.get(0);
		String u = uri.toString();
		long instanceId = Long.parseLong(s);
		uri.append('/');
		int rover = uri.length();
		String[] children = session.getChildNodeNames(u);
		if (children == null || children.length == 0)
			throw new DmtException(u, DmtException.INVALID_URI,
					"URI does not exist");

		for (String child : children) {
			uri.append(child);
			uri.append('/');
			uri.append("InstanceId");
			String uu = uri.toString();
			if (!session.isNodeUri(uu))
				return;

			DmtData data = session.getNodeValue(uu);
			long id = data.getLong();
			if (id == instanceId) {
				uri.delete(rover, uri.length());
				uri.append(child);
				return;
			}
		}
		throw new DmtException(u, DmtException.INVALID_URI,
				"URI does not exist");
	}

	static Pattern THORN_ESCAPE = Pattern.compile("þ([0-9A-Z]{4})");

	private String unescapeTR(CharSequence s) {
		StringBuilder sb = new StringBuilder(s);
		Matcher matcher = THORN_ESCAPE.matcher(sb);
		int rover = 0;
		while (matcher.find(rover)) {
			int unicode = Integer.parseInt(matcher.group(1), 16);
			sb.delete(matcher.start(), matcher.end());
			sb.insert(matcher.start(), (char) unicode);
			rover = matcher.start() + 1;
		}
		return sb.toString();
	}

	String convert(DmtData value, int type) {
		return null;
	}

	public void close() {

	}

}
