
package org.osgi.impl.service.tr069todmt;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import org.osgi.impl.service.tr069todmt.encode.Base64;
import org.osgi.impl.service.tr069todmt.encode.HexBinary;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.Uri;
import org.osgi.service.log.LogService;
import org.osgi.service.tr069todmt.ParameterInfo;
import org.osgi.service.tr069todmt.ParameterValue;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 * 
 *
 */
public class TR069ConnectorImpl implements TR069Connector {

	private static final Map<Integer, String>	DMT_FORMAT_NAMES	= new HashMap<Integer, String>();
	static {
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_BASE64), "base64");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_BINARY), "binary");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_BOOLEAN), "boolean");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_DATE), "date");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_FLOAT), "float");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_INTEGER), "integer");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_NODE), "NODE");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_NULL), "null");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_STRING), "string");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_TIME), "time");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_XML), "xml");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_LONG), "long");
		DMT_FORMAT_NAMES.put(Integer.valueOf(DmtData.FORMAT_DATE_TIME), "dateTime");
	}

	private DmtSession							session;
	private TR069ConnectorFactoryImpl			factory;

	/**
	 * @param session
	 * @param factory
	 */
	TR069ConnectorImpl(DmtSession session, TR069ConnectorFactoryImpl factory) {
		this.session = session;
		this.factory = factory;
	}

	public void setParameterValue(String parameterPath, String value, int type) throws TR069Exception {
		try {
			checkParameterPath(parameterPath);
			String nodeUri;
			if (parameterPath.endsWith(Utils.ALIAS)) {
				nodeUri = toURI(parameterPath.substring(0, parameterPath.lastIndexOf(Utils.DOT)), true);
				factory.persistenceManager.renameNode(session, nodeUri, escape(value));
			} else {
				nodeUri = toURI(parameterPath, true);

				MetaNode metanode = session.getMetaNode(nodeUri);
				if (metanode == null || metanode.can(MetaNode.CMD_REPLACE)) {
					/*
					 * If no meta-data is provided for a node, all operations
					 * are valid
					 */
					DmtData dmtValue = convertToDmtData(nodeUri, value, type, session.getMetaNode(nodeUri));
					if (dmtValue != null) {
						factory.persistenceManager.setNodeValue(session, nodeUri, dmtValue);
					}
				} else {
					throw new TR069Exception("Parameter " + parameterPath + " is not writable", TR069Exception.NON_WRITABLE_PARAMETER);
				}
			}
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	private DmtData convertToDmtData(String nodeUri, String value, int tr069Type, MetaNode metanode) throws DmtException {
		DmtData result = null;
		if (metanode == null) {
			switch (tr069Type) {
				case TR069_BOOLEAN : {
					result = convert(value, DmtData.FORMAT_BOOLEAN);
					if (result == null) {
						throw new TR069Exception("Error converting " + value + " in FORMAT_BOOLEAN", TR069Exception.INVALID_PARAMETER_VALUE);
					}
					return result;
				}

				case TR069_INT :
				case TR069_UNSIGNED_INT : {
					result = convert(value, new int[] {DmtData.FORMAT_INTEGER, DmtData.FORMAT_LONG, DmtData.FORMAT_FLOAT, DmtData.FORMAT_STRING});
					if (result == null) {
						throw new TR069Exception("Error converting " + value + " in FORMAT_INTEGER, FORMAT_LONG, FORMAT_FLOAT and FORMAT_STRING", TR069Exception.INVALID_PARAMETER_VALUE);
					}
					return result;
				}

				case TR069_LONG :
				case TR069_UNSIGNED_LONG : {
					result = convert(value, new int[] {DmtData.FORMAT_LONG, DmtData.FORMAT_FLOAT, DmtData.FORMAT_INTEGER, DmtData.FORMAT_STRING});
					if (result == null) {
						throw new TR069Exception("Error converting " + value + " in FORMAT_LONG, FORMAT_FLOAT, FORMAT_INTEGER and FORMAT_STRING", TR069Exception.INVALID_PARAMETER_VALUE);
					}
					return result;
				}

				case TR069_HEXBINARY :
				case TR069_BASE64 : {
					result = convert(value, new int[] {DmtData.FORMAT_BASE64, DmtData.FORMAT_BINARY, DmtData.FORMAT_RAW_BINARY});
					if (result == null) {
						throw new TR069Exception("Error converting " + value + " in FORMAT_BASE64, FORMAT_BINARY, FORMAT_RAW_BINARY", TR069Exception.INVALID_PARAMETER_VALUE);
					}
					return result;
				}

				case TR069_STRING : {
					/*
					 * FORMAT_STRING, FORMAT_BOOLEAN, FORMAT_FLOAT,
					 * FORMAT_INTEGER, FORMAT_LONG, FORMAT_XML, LIST
					 */
					result = convert(value, new int[] {DmtData.FORMAT_STRING, DmtData.FORMAT_BOOLEAN, DmtData.FORMAT_FLOAT, DmtData.FORMAT_INTEGER, DmtData.FORMAT_LONG, DmtData.FORMAT_XML});
					if (result == null && value.indexOf(Utils.COMMA) != -1) {
						setChildrenValues(nodeUri, value.split(Utils.COMMA), tr069Type);
						return null;
					}
					if (result == null) {
						throw new TR069Exception(
								"Error converting " + value + " in FORMAT_STRING, FORMAT_BOOLEAN, FORMAT_FLOAT, FORMAT_INTEGER, FORMAT_LONG, FORMAT_XML, LIST",
								TR069Exception.INVALID_PARAMETER_VALUE);
					}
					return result;
				}

				case TR069_DATETIME : {
					result = convert(value, new int[] {DmtData.FORMAT_DATE_TIME, DmtData.FORMAT_DATE, DmtData.FORMAT_TIME});
					if (result == null) {
						throw new TR069Exception("Error converting " + value + " in FORMAT_DATE_TIME, FORMAT_DATE and FORMAT_TIME", TR069Exception.INVALID_PARAMETER_VALUE);
					}
					return result;
				}
				default :
					throw new TR069Exception("Unknown TR069 type: " + tr069Type, TR069Exception.INVALID_PARAMETER_TYPE);
			}
		} else {
			int format = metanode.getFormat();
			// TODO can a node be not only FORMAT_NODE?!?
			if (format == DmtData.FORMAT_NODE) {
				if (value.indexOf(Utils.COMMA) != -1) {
					setChildrenValues(nodeUri, value.split(Utils.COMMA), tr069Type);
					return null;
				} else {
					throw new TR069Exception("Value " + value + " is not a comma-separated list", TR069Exception.INVALID_PARAMETER_VALUE);
				}
			} else {
				int[] formats = getFormats(format, tr069Type);
				if (TR069_BOOLEAN == tr069Type) {
					value = value.equals("0") ? "false" : (value.equals("1") ? "true" : value.toLowerCase());
					if (!("true".equals(value) || "false".equals(value))) {
						throw new TR069Exception("Error converting " + value + " as Boolean", TR069Exception.INVALID_PARAMETER_VALUE);
					}
				}
				result = convert(value, formats);
				if (result == null) {
					StringBuffer error = new StringBuffer("Error converting ");
					error.append(value).append(" in ");
					for (int i = 0; i < formats.length; i++) {
						error.append(DMT_FORMAT_NAMES.get(formats[i]));
						if (i < formats.length - 1) {
							error.append(", ");
						}
					}
					throw new TR069Exception(error.toString(), TR069Exception.INVALID_PARAMETER_VALUE);
					/* return convertToDmtData(nodeUri, value, tr069Type, null); */
				}
				return result;
			}
		}
	}

	private int[] getFormats(int mask, int tr069Type) {
		int index = 0;
		switch (tr069Type) {
			case TR069_BASE64 :
			case TR069_HEXBINARY : {
				/* FORMAT_BASE64, FORMAT_BINARY, FORMAT_RAW_BINARY */
				int[] result = new int[3];
				index = addFormat(mask, DmtData.FORMAT_BASE64, result, index);
				index = addFormat(mask, DmtData.FORMAT_BINARY, result, index);
				index = addFormat(mask, DmtData.FORMAT_RAW_BINARY, result, index);
				return trim(result, index);
			}
			case TR069_BOOLEAN : {
				/* FORMAT_BOOLEAN, FORMAT_STRING */
				int[] result = new int[2];
				index = addFormat(mask, DmtData.FORMAT_BOOLEAN, result, index);
				index = addFormat(mask, DmtData.FORMAT_STRING, result, index);
				return trim(result, index);
			}
			case TR069_DATETIME : {
				/* FORMAT_DATE_TIME, FORMAT_DATE, FORMAT_TIME */
				int[] result = new int[3];
				index = addFormat(mask, DmtData.FORMAT_DATE_TIME, result, index);
				index = addFormat(mask, DmtData.FORMAT_DATE, result, index);
				index = addFormat(mask, DmtData.FORMAT_TIME, result, index);
				return trim(result, index);
			}
			case TR069_INT :
			case TR069_UNSIGNED_INT : {
				/* FORMAT_INTEGER, FORMAT_LONG, FORMAT_FLOAT, FORMAT_STRING */
				int[] result = new int[4];
				index = addFormat(mask, DmtData.FORMAT_INTEGER, result, index);
				index = addFormat(mask, DmtData.FORMAT_LONG, result, index);
				index = addFormat(mask, DmtData.FORMAT_FLOAT, result, index);
				index = addFormat(mask, DmtData.FORMAT_STRING, result, index);
				return trim(result, index);
			}
			case TR069_LONG :
			case TR069_UNSIGNED_LONG : {
				/* FORMAT_LONG, FORMAT_FLOAT, FORMAT_INTEGER, FORMAT_STRING */
				int[] result = new int[4];
				index = addFormat(mask, DmtData.FORMAT_LONG, result, index);
				index = addFormat(mask, DmtData.FORMAT_FLOAT, result, index);
				index = addFormat(mask, DmtData.FORMAT_INTEGER, result, index);
				index = addFormat(mask, DmtData.FORMAT_STRING, result, index);
				return trim(result, index);
			}
			case TR069_STRING : {
				/*
				 * FORMAT_STRING, FORMAT_BOOLEAN, FORMAT_INTEGER, FORMAT_LONG,
				 * FORMAT_FLOAT, FORMAT_RAW_STRING, FORMAT_XML
				 */
				int[] result = new int[7];
				index = addFormat(mask, DmtData.FORMAT_STRING, result, index);
				index = addFormat(mask, DmtData.FORMAT_BOOLEAN, result, index);
				index = addFormat(mask, DmtData.FORMAT_INTEGER, result, index);
				index = addFormat(mask, DmtData.FORMAT_LONG, result, index);
				index = addFormat(mask, DmtData.FORMAT_FLOAT, result, index);
				index = addFormat(mask, DmtData.FORMAT_RAW_STRING, result, index);
				index = addFormat(mask, DmtData.FORMAT_XML, result, index);
				return trim(result, index);
			}
			default :
				return new int[0];
		}
	}

	private int addFormat(int mask, int format, int[] result, int index) {
		if ((mask & format) > 0) {
			result[index++] = format;
		}
		return index;
	}

	private int[] trim(int[] arrayToTrim, int index) {
		if (arrayToTrim == null || arrayToTrim.length <= index) {
			return arrayToTrim;
		}
		int[] result = new int[index];
		System.arraycopy(arrayToTrim, 0, result, 0, index);
		return result;
	}

	private DmtData convert(String value, int[] formats) {
		DmtData result = null;
		for (int i = 0; i < formats.length; i++) {
			result = convert(value, formats[i]);
			if (result != null) {
				return result;
			}
		}
		return null;
	}

	private DmtData convert(String value, int format) {
		switch (format) {
			case DmtData.FORMAT_BASE64 : {
				if (Utils.BASE64_PATTERN.matcher(value).matches()) {
					try {
						return new DmtData(Base64.decode(value.getBytes()), DmtData.FORMAT_BASE64);
					} catch (Exception e) {
						factory.log(LogService.LOG_WARNING, "Error converting " + value + " in FORMAT_BASE64", e);
						return null;
					}
				}
				return null;
			}

			case DmtData.FORMAT_BINARY : {
				if (Utils.HEXBINARY_PATTERN.matcher(value).matches()) {
					return new DmtData(new HexBinary(value).binaryValue(), DmtData.FORMAT_BINARY);
				}
				return null;
			}

			case DmtData.FORMAT_RAW_BINARY : {
				// TODO What format should I put here?
				return new DmtData("raw_binary", value.getBytes());
			}

			case DmtData.FORMAT_BOOLEAN : {
				if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")) {
					return new DmtData(true);
				}
				if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0")) {
					return new DmtData(false);
				}
				return null;
			}

			case DmtData.FORMAT_INTEGER : {
				try {
					return new DmtData(Integer.parseInt(value));
				} catch (IllegalArgumentException e) {
					factory.log(LogService.LOG_WARNING, e.toString(), null);
					return null;
				}
			}

			case DmtData.FORMAT_LONG : {
				try {
					return new DmtData(Long.parseLong(value));
				} catch (IllegalArgumentException e) {
					factory.log(LogService.LOG_WARNING, e.toString(), null);
					return null;
				}
			}

			case DmtData.FORMAT_FLOAT : {
				try {
					return new DmtData(Float.parseFloat(value));
				} catch (IllegalArgumentException e) {
					factory.log(LogService.LOG_WARNING, e.toString(), null);
					return null;
				}
			}

			case DmtData.FORMAT_RAW_STRING : {
				// TODO What format should I put here?
				return new DmtData("raw_string", value);
			}
			case DmtData.FORMAT_STRING :
			case DmtData.FORMAT_XML : {
				return new DmtData(value, format);
			}

			case DmtData.FORMAT_DATE :
			case DmtData.FORMAT_TIME : {
				Date date = null;
				try {
					date = (format == DmtData.FORMAT_DATE ? Utils.DMT_DATE_FORMAT : Utils.DMT_LOCAL_TIME_FORMAT).parse(value);
				} catch (ParseException e) {
					factory.log(LogService.LOG_WARNING, e.toString(), null);
					if (format == DmtData.FORMAT_TIME) {
						// try UTC time
						try {
							date = Utils.DMT_UTC_TIME_FORMAT.parse(value);
						} catch (ParseException e1) {
							factory.log(LogService.LOG_WARNING, e.toString(), null);
						}
					}
				}
				return date != null ? new DmtData(value, format) : null;
			}
			case DmtData.FORMAT_DATE_TIME : {
				if (Utils.TR069_UNKNOWN_TIME.equals(value)) {
					return DmtData.NULL_VALUE;
				}
				try {
					return new DmtData(Utils.TR069_DATE_ENCODING_FORMAT.parse(value));
				} catch (ParseException e) {
					factory.log(LogService.LOG_WARNING, e.toString(), null);
				}
				return null;
			}
		}
		throw new TR069Exception("Unknown format " + format + " for " + value, TR069Exception.INVALID_PARAMETER_TYPE, null);
	}

	private void setChildrenValues(String parentUri, String[] values, int tr069Type) throws DmtException {
		/* remove all children */
		String[] children = factory.persistenceManager.getChildNodeNames(session, parentUri, true);
		for (int i = 0; i < children.length; i++) {
			factory.persistenceManager.deleteNode(session, parentUri + Uri.PATH_SEPARATOR + children[i]);
		}

		for (int i = 0; i < values.length; i++) {
			int instanceId = factory.persistenceManager.generateInstanceId(session, parentUri);
			String childUri = parentUri + Uri.PATH_SEPARATOR + String.valueOf(instanceId);
			// createNode(childUri, instanceId, true);
			factory.persistenceManager.setNodeValue(session, childUri, convertToDmtData(childUri, values[i].trim(), tr069Type, null));
		}
	}

	public ParameterValue getParameterValue(final String parameterPath) throws TR069Exception {
		checkParameterPath(parameterPath);
		if (parameterPath.endsWith(Utils.NUMBER_OF_ENTRIES)) {
			final int dotIndex = parameterPath.lastIndexOf(Utils.DOT);
			return new ParameterValue() {

				private String	uri	= toURI(dotIndex == -1 ? "" : parameterPath.substring(0, dotIndex), true);

				public String getValue() {
					try {
						String[] children = factory.persistenceManager.getChildNodeNames(session, uri, true);
						if (children == null) {
							return "0";
						}
						return String.valueOf(children.length);
					} catch (DmtException e) {
						throw new TR069Exception(e);
					}
				}

				public int getType() {
					return TR069_UNSIGNED_INT;
				}

				public String getPath() {
					return parameterPath;
				}
			};
		} else if (parameterPath.endsWith(Utils.ALIAS)) {
			String uri = toURI(parameterPath.substring(0, parameterPath.indexOf(Utils.ALIAS) - 1), true);
			String[] nodePath = Uri.toPath(uri);
			final String name = nodePath[nodePath.length - 1];
			final String alias = factory.persistenceManager.getAlias(session, uri);

			return new ParameterValue() {

				public String getValue() {
					return alias != null ? alias : name;
				}

				public int getType() {
					return TR069_STRING;
				}

				public String getPath() {
					// TODO parameterPath or parent path should be returned
					// here?!?
					return parameterPath;
				}
			};
		} else {
			return new ParameterValueImpl(parameterPath, new Node(toURI(parameterPath, true), session));
		}
	}

	public Collection<ParameterInfo> getParameterNames(String objectOrTablePath, boolean nextLevel) throws TR069Exception {
		checkPath(objectOrTablePath);
		ArrayList<ParameterInfo> result = new ArrayList<ParameterInfo>();
		String aliasedParentUri = toURI(objectOrTablePath, true);
		if (objectOrTablePath.length() == 0 || objectOrTablePath.endsWith(Utils.DOT)) {
			addChildren(aliasedParentUri, result, nextLevel);
			if (!nextLevel) {
				result.add(new ParameterInfoImpl(this, objectOrTablePath, new Node(aliasedParentUri, session)));
			}
		} else {
			Node node = new Node(aliasedParentUri, session);
			if (!nextLevel && node.isLeaf()) {
				result.add(new ParameterInfoImpl(this, objectOrTablePath, node));
			} else {
				throw new TR069Exception("Invalid object or table path: " + objectOrTablePath, TR069Exception.INVALID_ARGUMENTS);
			}
		}
		return result;
	}

	private void addChildren(String aliasedParentUri, ArrayList<ParameterInfo> names, boolean nextLevel) {
		try {
			/*
			 * If the parent node is a MAP, then the synthetic Alias parameter
			 * must be included
			 */
			String[] children = factory.persistenceManager.getChildNodeNames(session, aliasedParentUri, true);
			if (children == null || children.length == 0) {
				return;
			}
			boolean addAliasChild = DmtConstants.DDF_MAP.equals(factory.persistenceManager.getNodeType(
					session, Utils.getParentPath(aliasedParentUri)));
			String parentPath = toPath(aliasedParentUri);
			if (addAliasChild) {
				names.add(new ParameterInfoImpl(this,
						parentPath + Utils.ALIAS, new Node(parentPath + Utils.ALIAS, session)));
			}
			for (int i = 0; i < children.length; i++) {
				if (Utils.INSTANCE_ID.equals(children[i])) {
					continue;
				}
				/*
				 * If the child nodes have an InstanceId node then the returned
				 * names must include the InstanceId values instead of the node
				 * names
				 */
				String childUri = (aliasedParentUri.length() > 0 ? aliasedParentUri + Uri.PATH_SEPARATOR : aliasedParentUri) + children[i];
				String childPath = toPath(childUri);

				names.add(new ParameterInfoImpl(this, childPath, new Node(childUri, session)));

				// If the child is MAP or LIST, add NumberOfEntries node.
				String childType = factory.persistenceManager.getNodeType(session, childUri);
				if ((DmtConstants.DDF_MAP.equals(childType)) ||
						(DmtConstants.DDF_LIST.equals(childType))) {
					int childPathLength = childPath.length();
					String numberOfEntriesName = escape(childPath.substring(
							childPath.lastIndexOf(Utils.DOT, childPathLength - 2) + 1,
							childPathLength - 1)) + Utils.NUMBER_OF_ENTRIES;
					String numberOfEntriesUri = (aliasedParentUri.length() > 0 ?
							aliasedParentUri + Uri.PATH_SEPARATOR : aliasedParentUri)
							+ numberOfEntriesName;
					names.add(new ParameterInfoImpl(this, parentPath + numberOfEntriesName,
							new Node(numberOfEntriesUri, session)));
				}

				if (!nextLevel) {
					addChildren(childUri, names, nextLevel);
				}
			}
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	public String addObject(String path) throws TR069Exception {
		try {
			checkPath(path);
			factory.persistenceManager.checkSessionLock(session);
			if (path.endsWith(Utils.ALIAS)) {
				/*
				 * If the path ends in an alias, then the node name must be the
				 * alias
				 */
				String nodePath = path.substring(0, path.indexOf(Utils.ALIAS) - 1);
				String parentUri = toURI(nodePath.substring(0, nodePath.lastIndexOf(Utils.DOT)), true);

				factory.persistenceManager.checkListOrMapUri(session, parentUri);
				String nodeName = nodePath.substring(nodePath.lastIndexOf(Utils.DOT) + 1);
				int instanceNumber = factory.persistenceManager.getInstanceNumber(session, toURI(nodePath, true));
				if (instanceNumber < 0) {
					if (Utils.INSTANCE_ID_PATTERN.matcher(nodeName).matches()) {
						instanceNumber = Integer.parseInt(nodeName);
					} else {
						instanceNumber = factory.persistenceManager.generateInstanceId(session, parentUri);
					}
				}
				return String.valueOf(instanceNumber);
			}

			if (path.endsWith(Utils.DOT)) {
				path = path.substring(0, path.length() - 1);
			}
			String nodeName = path.substring(path.lastIndexOf(Utils.DOT) + 1);
			if (Utils.ALIAS_PATTERN.matcher(nodeName).matches()) {
				String parentUri = toURI(path.substring(0, path.length() - nodeName.length()), true);
				factory.persistenceManager.checkListOrMapUri(session, parentUri);
				nodeName = nodeName.substring(1, nodeName.length() - 1);
				String nodeUri = parentUri + Uri.PATH_SEPARATOR + nodeName;
				int instanceNumber = factory.persistenceManager.getInstanceNumber(session, nodeUri);
				if (instanceNumber < 0) {
					instanceNumber = factory.persistenceManager.generateInstanceId(session, parentUri);
					createInteriorNode(nodeUri, instanceNumber, false);
					return nodeName;
				}
				throw new TR069Exception("A node with '" + nodeName + "' alias already exists!", TR069Exception.INVALID_PARAMETER_NAME);
			}
			String parentUri = toURI(path, true);
			factory.persistenceManager.checkListOrMapUri(session, parentUri);
			int instanceNumber = factory.persistenceManager.generateInstanceId(session, parentUri);
			createInteriorNode(parentUri + Uri.PATH_SEPARATOR + instanceNumber, instanceNumber, false);
			return String.valueOf(instanceNumber);
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	public void deleteObject(String objectPath) throws TR069Exception {
		try {
			checkPath(objectPath);
			String nodeUri;
			try {
				nodeUri = toURI(objectPath, false);
			} catch (TR069Exception e) {
				if (e.getFaultCode() == TR069Exception.INVALID_PARAMETER_NAME) {
					/*
					 * A missing node must be ignored (toURI method throws
					 * INVALID_PARAMETER_NAME when create is false)
					 */
					return;
				} else {
					throw e;
				}
			}
			if (Node.isMultiInstanceNode(session, nodeUri)) {
				factory.persistenceManager.deleteNode(session, nodeUri);
			} else {
				throw new TR069Exception("Node " + objectPath + " is not a multi-instance node", TR069Exception.INVALID_PARAMETER_NAME);
			}
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	public String toPath(String uri) throws TR069Exception {
		if (Uri.isValidUri(uri)) {
			StringBuffer path = new StringBuffer();
			toPath(uri, path);
			try {
				if ((uri.length() > 0) && !session.isLeafNode(uri)) {
					path.append(Utils.DOT);
				}
			} catch (DmtException e) {
				throw new TR069Exception(e);
			}
			return path.toString();
		}
		throw new TR069Exception("Invalid uri: " + uri, TR069Exception.INVALID_ARGUMENTS);
	}

	private void toPath(String uri, StringBuffer path) {
		if (uri == null || uri.length() == 0) {
			if (path.length() > 0) {
				path.deleteCharAt(0);
			}
			return;
		}

		String[] uriToPath = Uri.toPath(uri);
		if (uriToPath.length == 1) {
			uri = escape(uri);
			path.insert(0, uri);
			return;
		}

		try {
			String segment = uriToPath[uriToPath.length - 1];
			String parentUri = uri.substring(0, uri.length() - segment.length() - 1);
			if (Utils.ALIAS.equals(segment) || Utils.INSTANCE_ID.equals(segment)) {
				toPath(parentUri, path);
			} else {
				String nodeType = factory.persistenceManager.getNodeType(session, parentUri);
				if (DmtConstants.DDF_MAP.equals(nodeType) || DmtConstants.DDF_LIST.equals(nodeType)) {
					int instanceNumber = factory.persistenceManager.getInstanceNumber(session, uri);
					if (instanceNumber < 0) {
						if (Utils.INSTANCE_ID_PATTERN.matcher(segment).matches()) {
							instanceNumber = Integer.parseInt(segment);
						} else {
							// TODO should I generate it here ?!?!
							instanceNumber = factory.persistenceManager.generateInstanceId(session, parentUri);
						}
					}
					path.insert(0, instanceNumber).insert(0, Utils.DOT);
					toPath(parentUri, path);
				} else {
					path.insert(0, escape(segment)).insert(0, Utils.DOT);
					toPath(parentUri, path);
				}
			}
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	public String toURI(String path, boolean create) throws TR069Exception {
		checkPath(path);
		StringBuffer uri = new StringBuffer();
		try {
			if (path.endsWith(Utils.NUMBER_OF_ENTRIES) || path.endsWith(Utils.ALIAS)) {
				throw new TR069Exception("NumberOfEntries and Alias parameter cannot be mapped!", TR069Exception.INVALID_PARAMETER_NAME);
			}
			toUri(path, create, uri);
			try {
				String nodeUri = uri.toString();
				String name = checkNode(nodeUri, create, path.endsWith(Utils.DOT));
				return nodeUri.endsWith(name) ? nodeUri : factory.persistenceManager.getRenamedUri(nodeUri, name);
			} catch (DmtException e) {
				throw new TR069Exception(e);
			}
		} catch (IllegalArgumentException e) {
			throw new TR069Exception("Invalid parameter name: " + path, TR069Exception.INVALID_PARAMETER_NAME);
		}
	}

	private void toUri(String path, boolean create, StringBuffer uri) {
		if (path == null || path.length() == 0) {
			return;
		}
		try {
			String currentNode;
			String segment;
			String remainder;
			int dotIndex = path.indexOf(Utils.DOT);
			if (dotIndex == -1) {
				segment = path;
				remainder = null;
			} else {
				segment = path.substring(0, dotIndex);
				remainder = path.substring(dotIndex + 1);
			}
			if (segment.trim().length() == 0) {
				throw new IllegalArgumentException();
			}

			segment = unescape(checkForAliasPattern(segment));

			/* check parent node */
			currentNode = uri.toString();
			checkNode(currentNode, create, true);
			String nodeType = factory.persistenceManager.getNodeType(session, currentNode);
			if ((DmtConstants.DDF_MAP.equals(nodeType) || DmtConstants.DDF_LIST.equals(nodeType)) && Utils.INSTANCE_ID_PATTERN.matcher(segment).matches()) {
				long instanceID = Long.parseLong(segment);
				String[] children = session.getChildNodeNames(currentNode);
				for (int i = 0; i < children.length; i++) {
					String child = currentNode + Uri.PATH_SEPARATOR_CHAR + children[i];
					checkNode(child, create, true);
					String instanceIDUri = child + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
					if (session.isNodeUri(instanceIDUri)) {
						if (session.getNodeValue(instanceIDUri).getLong() == instanceID) {
							appendtoUri(uri, children[i]);
							toUri(remainder, create, uri);
							return;
						}
					}
				}
				segment = checkNode(currentNode + Uri.PATH_SEPARATOR_CHAR + segment, create, true);
			}
			appendtoUri(uri, segment);
			currentNode = uri.toString();
			MetaNode metanode = session.getMetaNode(currentNode);
			// TODO if metanode is null how to decide if it is leaf or interior
			// node?!?
			if (metanode != null) {
				checkNode(currentNode, create, !metanode.isLeaf());
			} else {
				if (!(factory.persistenceManager.isNodeUri(session, currentNode) || create)) {
					throw new TR069Exception("Node " + uri + " does not exist!", TR069Exception.INVALID_PARAMETER_NAME);
				}
			}
			toUri(remainder, create, uri);
		} catch (DmtException e) {
			throw new TR069Exception(e);
		}
	}

	private void appendtoUri(StringBuffer uri, String segment) {
		if (uri.length() > 0) {
			uri.append(Uri.PATH_SEPARATOR_CHAR);
		}
		uri.append(segment);
	}

	private String checkNode(String uri, boolean create, boolean isInterior) throws DmtException {
		if (!session.isNodeUri(uri)) {
			if (create) {
				if (isInterior) {
					String newNode = createInteriorNode(uri, -1, true);
					if (newNode != null) {
						return newNode;
					}
				} else {
					factory.persistenceManager.createLeafNode(session, uri, false, null);
				}
			} else {
				throw new TR069Exception("Node " + uri + " does not exist!", TR069Exception.INVALID_PARAMETER_NAME);
			}
		}
		return Node.getNodeName(uri);
	}

	private String checkForAliasPattern(String segment) {
		if (Utils.ALIAS_PATTERN.matcher(segment).matches()) {
			segment = segment.substring(1, segment.length() - 1);
		}
		return segment;
	}

	private String createInteriorNode(String nodeUri, int instanceNumber, boolean eager) throws DmtException {
		if (session.isNodeUri(nodeUri)) {
			return null;
		}
		MetaNode metanode = session.getMetaNode(Node.getParentUri(nodeUri));
		String[] mimeTypes = metanode == null ? null : metanode.getMimeTypes();
		return factory.persistenceManager.createInteriorNode(
				session, nodeUri, instanceNumber,
				eager ? true : (mimeTypes == null ? false : Arrays.asList(mimeTypes).contains(TR069Connector.TR069_MIME_EAGER))
				);
	}

	private String unescape(String s) {
		StringBuffer sb = new StringBuffer(s);
		Matcher matcher = Utils.THORN_ESCAPE.matcher(sb);
		int rover = 0;
		while (matcher.find(rover)) {
			int unicode = Integer.parseInt(matcher.group(1), 16);
			sb.delete(matcher.start(), matcher.end());
			sb.insert(matcher.start(), (char) unicode);
			rover = matcher.start() + 1;
		}
		return Uri.encode(sb.toString());
	}

	/*
	 * NAME ::= (Letter | ’_’ )( Letter | Digit | ’-’ | ’_’ | CombiningChar |
	 * Extender)*
	 * 
	 * TR-106: Parameter names MUST be treated as case sensitive. The name of
	 * each node in the hierarchy MUST start with a letter or underscore, and
	 * subsequent characters MUST be letters, digits, underscores or hyphens.
	 * 
	 * CombiningChar, Extender and first non-letter and non-underscore symbols
	 * MUST be escaped
	 */
	private String escape(String s) {
		StringBuffer sb = new StringBuffer(Uri.decode(s));
		char c;
		for (int i = 0; i < sb.length();) {
			c = sb.charAt(i);
			if (i == 0 && !(Character.isLetter(c) || (c == Utils.UNDERSCORE_CODE))) {
				i = thornEscape(sb, i, c);
			} else if (Character.isWhitespace(c) || Utils.CHARS_TO_ESCAPE_PATTERN.matcher(String.valueOf(c)).matches()) {
				i = thornEscape(sb, i, c);
			} else {
				i++;
			}
		}
		return sb.toString();
	}

	private int thornEscape(StringBuffer sb, int index, char c) {
		sb.insert(index++, Utils.THORN);
		String hex = Integer.toHexString(c).toUpperCase();
		for (int i = hex.length(); i < 4; i++) {
			sb.insert(index++, '0');
		}
		sb.insert(index++, hex);
		sb.deleteCharAt(++index);
		return index;
	}

	/* The TR069 Connector only accepts escaped paths and returns escaped paths */
	private void checkPath(String path) {
		if (path == null) {
			throw new TR069Exception("Path cannot be null!", TR069Exception.INVALID_PARAMETER_NAME);
		}
		char[] chars = path.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == Utils.THORN) {
				if ((i + 5 > chars.length) ||
						!Utils.THORN_ESCAPE.matcher(new String(new char[] {Utils.THORN, chars[i + 1], chars[i + 2], chars[i + 3], chars[i + 4]})).matches()) {
					throw new TR069Exception("The TR069 Connector accepts only escaped paths. The path is not as expected: " + path, TR069Exception.INVALID_PARAMETER_NAME);
				} else {
					continue;
				}
			}
			if ((i == 0 && !(Character.isLetter(chars[i]) || (chars[i] == Utils.UNDERSCORE_CODE))) ||
					(Character.isWhitespace(chars[i]) || Utils.CHARS_TO_ESCAPE_PATTERN.matcher(String.valueOf(chars[i])).matches())) {
				throw new TR069Exception("The TR069 Connector accepts only escaped paths. The path is not as expected: " + path, TR069Exception.INVALID_PARAMETER_NAME);
			}
		}
	}

	private void checkParameterPath(String path) {
		checkPath(path);
		if (path.endsWith(Utils.DOT)) {
			throw new TR069Exception("Invalid Parameter Path " + path, TR069Exception.INVALID_PARAMETER_NAME);
		}
	}

	public void close() {
		/* Closing the connector must not close the corresponding DmtSession */
	}
}
