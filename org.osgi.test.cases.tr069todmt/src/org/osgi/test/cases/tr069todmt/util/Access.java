package org.osgi.test.cases.tr069todmt.util;

import java.util.*;

public class Access {
	final java.util.Map<Object, Node< ? >>			nodes				= new IdentityHashMap<Object, Node< ? >>();
	final java.util.Map<Class< ? >, Handler< ? >>	members				= new HashMap<Class< ? >, Handler< ? >>();
	final Handler<java.util.Map<String, ? >>		mapHandler			= new Handler.Map(
																				this);
	final Handler<java.util.List< ? >>				listHandler			= new Handler.List(
																				this);
	final Handler< ? >								primitiveHandler	= new Handler.Primitive(
																				this);
	final Handler< ? >								nodeHandler			= new Handler.NodeHandler(
																				this);

	@SuppressWarnings("unchecked")
	<T> Handler<T> getHandler(Class<T> c) {
		Handler< ? > h = (Handler<T>) members.get(c);
		if (h == null) {

			if (AsNode.class.isAssignableFrom(c))
				h = nodeHandler;
			else
				if (java.util.Map.class.isAssignableFrom(c))
					h = mapHandler;
				else
					if (java.util.List.class.isAssignableFrom(c))
						h = listHandler;
					else
						if (isPrimitive(c))
							h = primitiveHandler;
						else
							h = new Handler.Structured<T>(this, c);
			members.put(c, h);
		}
		return (Handler<T>) h;
	}

	private boolean isPrimitive(Class< ? > c) {
		if (c.isPrimitive())
			return true;
		if (String.class.isAssignableFrom(c))
			return true;
		if (Number.class.isAssignableFrom(c))
			return true;

		if (Boolean.class.isAssignableFrom(c))
			return true;

		if (Character.class.isAssignableFrom(c))
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> Handler<T> getHandler(T node) {
		return (Handler<T>) getHandler(node.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T> Node<T> getNode(T o) {
		if (nodes.containsKey(o))
			return (Node<T>) nodes.get(o);

		Node<T> node = new Node<T>();
		node.object = o;
		node.handler = getHandler(o);
		nodes.put(o, node);
		return node;
	}
}
