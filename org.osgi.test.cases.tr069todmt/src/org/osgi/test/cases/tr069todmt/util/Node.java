package org.osgi.test.cases.tr069todmt.util;

public class Node<T> {
	Handler<T> handler;
	T object;
	
	
	public Node<?> find(String[] nodePath, int n)  {
		if ( n == nodePath.length)
			return this;
		
		String name = nodePath[ n ];
		Object child = handler.getChild(object, name);
		if ( child != null ) {
			Node<?> cnode = handler.access.getNode(child);
			return cnode.find(nodePath, n+1);
		}
		return null;
	}


	public String[] getChildNodeNames() {
		return handler.getChildren(object);
	}
}
