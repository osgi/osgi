package org.osgi.impl.service.dmt.dispatcher;

public interface Node {

	public boolean isAbsolute();

	public String getUri();

	public boolean isAncestorOf(Node node);

	public String[] getPath();

	public boolean isOnSameBranch(Node rootNode);

	public Node getParent();

	public boolean isRoot();

	public Node appendSegment(String string);

	public boolean isAncestorOf(Node root, boolean b);

	public String getLastSegment();

	public Node appendRelativeNode(Node node);

	public boolean isEmpty();

	public Node getRelativeNode(Node key);

}