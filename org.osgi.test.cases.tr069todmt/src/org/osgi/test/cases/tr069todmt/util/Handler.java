package org.osgi.test.cases.tr069todmt.util;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.*;

public class Handler<T> {
	private static final String[]	EMPTY_STRING	= {};
	Access							access;

	public Handler(Access access) {
		this.access = access;
	}

	public String[] getChildren(T o) {
		throw new UnsupportedOperationException();
	}

	public Object getChild(T o, String name) {
		throw new UnsupportedOperationException();
	}

	public void setChild(T o, String name, Object v) throws Exception {
		throw new UnsupportedOperationException();
	}

	static class Primitive extends Handler<Object> {
		public Primitive(Access access) {
			super(access);
		}
	}

	static class NodeHandler extends Handler<AsNode> {
		public NodeHandler(Access access) {
			super(access);
		}

		public String[] getChildren(AsNode o) {
			return o.getChildren();
		}

		public Object getChild(AsNode o, String name) {
			return o.getChild(name);
		}
	}

	static class Structured<T> extends Handler<T> {
		static class Child {
			Method	set;
			Method	get;
			Field	field;
		}

		final Class<T>						clazz;
		final java.util.Map<String, Child>	members	= new HashMap<String, Child>();
		final String[]						children;


		Structured(Access access, Class<T> cx) {
			super(access);
			this.clazz = cx;
			for (Method m : cx.getMethods()) {
				if (Modifier.isPublic(m.getModifiers())) {
					NodeName nodeName = m.getAnnotation(NodeName.class);
					if (nodeName == null)
						continue;
					
					String name = nodeName.value();
					boolean s = m.getParameterTypes().length == 1;
					boolean g = m.getParameterTypes().length == 0;

					if (s || g) {
						Child child = getChild(name);
						if (s)
							child.set = m;
						else
							child.get = m;
					}
				}
			}

			Class< ? > x = cx;
			do {
				for (Field f : x.getDeclaredFields()) {
					if (!Modifier.isPrivate(f.getModifiers())) {
						String name = f.getName();
						name = Character.toUpperCase(name.charAt(0))
								+ name.substring(1);
						getChild(name).field = f;
						f.setAccessible(true);
					}
				}
				x = x.getSuperclass();
			} while (x != null);
			children = members.keySet().toArray(EMPTY_STRING);
		}

		private Child getChild(String name) {
			Child child = members.get(name);
			if (child == null) {
				child = new Child();
				members.put(name, child);
			}
			return child;
		}

		public String[] getChildren(Object o) {
			return children;
		}

		public Object getChild(Object o, String name) {
			try {
				Child c = members.get(name);
				if (c.get != null)
					return c.get.invoke(o);
				else
					if (c.field != null)
						return c.field.get(o);
				return null;
			}
			catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
	}

	static class Map extends Handler<java.util.Map<String, ? >> {
		public Map(Access access) {
			super(access);
		}

		public String[] getChildren(java.util.Map<String, ? > o) {
			return ((java.util.Map<String, ? >) o).keySet().toArray(
					EMPTY_STRING);
		}

		public Object getChild(java.util.Map<String, ? > o, String name) {
			return ((java.util.Map<String, ? >) o).get(name);
		}
	}

	static class List extends Handler<java.util.List< ? >> {
		public List(Access access) {
			super(access);
		}

		public String[] getChildren(java.util.List< ? > list) {
			String[] names = new String[list.size()];
			for (int i = 0; i < names.length; i++)
				names[i] = "" + i;
			return names;
		}

		public Object getChild(java.util.List< ? > list, String name) {
			int n = Integer.parseInt(name);
			return list.get(n);
		}

	}

}
