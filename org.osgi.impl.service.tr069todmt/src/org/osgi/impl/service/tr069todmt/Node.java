package org.osgi.impl.service.tr069todmt;

import java.util.*;
import java.util.regex.*;

import org.osgi.service.dmt.*;
import org.osgi.service.tr069todmt.*;

/**
 *
 */
public class Node implements ParameterInfo, ParameterValue {
	static Pattern THORN_ESCAPE = Pattern.compile("þ([0-9A-Z]{4})");
	Pattern ALIAS = Pattern.compile("\\[([^\\.]+)\\]");
	Pattern INSTANCEID = Pattern.compile("([0-9]+)");
	Pattern PATH = Pattern.compile("([^\\.]+)(\\.(.*))?");

	enum Type {
		UNKNOWN, PRIMITIVE, SCAFFOLD, MAP, LIST, NODE
	}

	final DmtSession session;
	final String segment;
	final Node parent;
	Type type;
	MetaNode metanode;

	/**
	 * @param session
	 */
	public Node(DmtSession session) {
		this(null, session, null);
	}

	/**
	 * @param parent
	 * @param session
	 * @param segment
	 */
	public Node(Node parent, DmtSession session, String segment) {
		this.session = session;
		this.parent = parent;
		this.segment = segment;
	}

	DmtData getDmtValue() throws DmtException {
		return session.getNodeValue(getUri());
	}

	void setValue(DmtData data) throws DmtException {
		session.setNodeValue(getUri(), data);
	}

	Type getNodeType() throws DmtException {
		if (type == null) {
			MetaNode metanode = getMetaNode();
			if (metanode == null)
				return type = Type.UNKNOWN;
			if (metanode.isLeaf())
				return type = Type.PRIMITIVE;

			String ddf = session.getNodeType(getUri());
			if (ddf == null)
				return type = Type.NODE;

			if (DmtConstants.DDF_LIST.equals(ddf))
				return type = Type.LIST;
			if (DmtConstants.DDF_MAP.equals(ddf))
				return type = Type.MAP;
			if (DmtConstants.DDF_SCAFFOLD.equals(ddf))
				return type = Type.SCAFFOLD;

			return type = Type.UNKNOWN;
		}
		return type;
	}

	MetaNode getMetaNode() throws DmtException {
		if (metanode == null)
			metanode = session.getMetaNode(getUri());

		return metanode;
	}

	/**
	 * This is extremely inefficient but it is easy to understand for now.
	 * 
	 * @return the URI
	 */
	public String getUri() {
		if (parent == null)
			return null;

		String previous = parent.getUri();
		String encoded = Uri.encode(segment);

		if (previous == null)
			return encoded;
		else
			return previous + "/" + encoded;
	}


	/**
	 * This is extremely inefficient but it is easy to understand for now.
	 * 
	 * @return the Path
	 */
	public String getPath() {
		if (parent == null)
			return null;

		String previous = parent.getUri();
		String encoded = Uri.encode(segment);

		if (previous == null)
			return encoded;
		else
			return previous + "/" + encoded;
	}


	/**
	 * @param path
	 * @param create
	 * @return
	 * @throws DmtException
	 */
	public Node getDescendantFromPath(String path, boolean create) throws DmtException {
		if (path.isEmpty())
			return this;

		Matcher matcher = PATH.matcher(path);
		if (matcher.matches())
			throw new TR069Exception("Invalid path " + path,
					TR069Exception.INVALID_PARAMETER_NAME);

		String S = matcher.group(1);
		String R = matcher.group(3);

		Type type = getNodeType();
		Node N = null;

		matcher = ALIAS.matcher(S);
		if (matcher.matches())
			S = matcher.group(1);

		S = unescape(S);

		if (type == Type.LIST || type == Type.MAP) {
			matcher = INSTANCEID.matcher(S);
			if (matcher.matches()) {
				long instanceId = Long.parseLong(matcher.group(1));
				Collection<Node> children = getChildren();
				for (Node c : children) {
					Node ii = c.getDescendantFromPath("InstanceId", false);
					if (ii == null)
						break;
					else if (instanceId == ii.getDmtValue().getLong()) {
						N = c;
						break;
					}
				}
			}
		}
		if (N == null)
			N = new Node(this, session, S);

		if (create && !N.exists())
			N.create();

		return N.getDescendantFromPath(R, create);
	}

	private void create() {
		// TODO Auto-generated method stub

	}

	boolean exists() {
		// TODO Auto-generated method stub
		return false;
	}

	Collection<Node> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	private String unescape(CharSequence s) {
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

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isWriteable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isParameter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ParameterValue getParameterValue() {
		return this;
	}

	@Override
	public String getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getType() {
		return 0;
	}

	int getNumberOfEntries() {
		return getChildren().size();
	}
}
