
package test;

import java.lang.reflect.*;
import java.net.*;
import java.util.*;
import org.osgi.dmt.ddf.*;
import org.osgi.dmt.ddf.Scope.SCOPE;
import org.osgi.dmt.residential.*;

public class Test {

	static class XNode {
		final XNode	parent;
		String		name;
		List<XNode>	children	= new ArrayList<XNode>();
		boolean		optional	= false;
		boolean		multiple	= false;
		boolean		add			= false;
		boolean		delete		= false;
		boolean		get			= false;
		boolean		replace		= false;
		boolean		recursive	= false;
		String		dmtType;
		Type		type;
		SCOPE		scope		= SCOPE.P;
		String		cardinality	= "1";
		String		mime		= "";
		boolean		primitive	= true;

		XNode(XNode parent, String name, Type type) {
			this.type = type;
			this.parent = parent;
			this.name = name;
			if (parent != null && parent.isRecursive(type)) {
				recursive = true;
				get = true;
				dmtType = "Node";
				return;
			}

			if (instanceOf(type, Opt.class)) {
				ParameterizedType p = (ParameterizedType) type;
				optional = true;
				type = p.getActualTypeArguments()[0];
				cardinality = "0,1";
			}
			if (instanceOf(type, LIST.class)) {
				boolean add = false;
				boolean delete = false;
				if (instanceOf(type, MutableLIST.class)) {
					add = true;
					delete = true;
				}

				multiple = true;
				ParameterizedType p = (ParameterizedType) type;
				type = p.getActualTypeArguments()[0];
				get = true;
				XNode child = new XNode(this, "[list]", type);
				child.scope = SCOPE.D;
				child.add = add;
				child.delete = delete;
				child.cardinality = "0..*";
				children.add(child);
				dmtType = "LIST";
				mime = "org.osgi/1.0/LIST";
				return;
			} else
				if (instanceOf(type, MAP.class)) {
					boolean add = false;
					boolean delete = false;
					if (instanceOf(type, MutableMAP.class)) {
						add = true;
						delete = true;
					} else
						if (instanceOf(type, AddableMAP.class)) {
							add = true;
						}

					multiple = true;
					ParameterizedType p = (ParameterizedType) type;
					Class<?> key = (Class<?>) p.getActualTypeArguments()[0];
					String keyTypeName = key.getName().substring(key.getName().lastIndexOf('.') + 1).toLowerCase();
					type = p.getActualTypeArguments()[1];
					get = true;
					XNode child = new XNode(this, "[" + keyTypeName + "]", type);
					child.scope = SCOPE.D;
					child.add = add;
					child.delete = delete;
					child.cardinality = "0..*";
					children.add(child);
					dmtType = "MAP ";
					mime = "org.osgi/1.0/MAP";
					return;
				} else {

					if (instanceOf(type, Mutable.class)) {
						get = true;
						replace = true;
						ParameterizedType p = (ParameterizedType) type;
						type = p.getActualTypeArguments()[0];
					} else
						// not one of ours
						get = true;
				}

			Class<?> c;
			if (type instanceof GenericArrayType)
				c = byte[].class;
			else
				if (type instanceof WildcardType)
					c = NODE.class;
				else
					if (type instanceof ParameterizedType) {
						c = (Class<?>) ((ParameterizedType) type).getRawType();
					} else {
						try {
							c = (Class<?>) type;
						} catch (Exception e) {
							System.out.println(type);
							return;
						}
					}

			primitive = true;
			if (c == int.class || c == Integer.class) {
				dmtType = "integer";
			} else
				if (c == boolean.class || c == Boolean.class) {
					dmtType = "boolean";
				} else
					if (c == long.class || c == Long.class) {
						dmtType = "long";
					} else
						if (c == float.class || c == Float.class) {
							dmtType = "float";
						} else
							if (c == Date.class) {
								dmtType = "dateTime";
							} else
								if (c == URI.class) {
									dmtType = "node_uri";
								} else
									if (c == String.class) {
										dmtType = "string";
									} else
										if (c == base64.class) {
											dmtType = "base64";
										} else
											if (c == byte[].class) {
												dmtType = "binary";
											} else {
												primitive = false;
												dmtType = "Node";
												if (c != NODE.class) {
													dmtType = c.getName().substring(c.getName().lastIndexOf('.') + 1);
													int n = dmtType.lastIndexOf('$');
													if (n > 0)
														dmtType = dmtType.substring(n + 1);
													dmtType = "NODE"; // "{" +
																		// dmtType
																		// +
																		// "}";
													Method ms[] = c.getDeclaredMethods();
													for (Method m : ms) {

														XNode child = new XNode(this, m.getName(), m.getGenericReturnType());
														Scope s = m.getAnnotation(Scope.class);
														if (s != null)
															child.scope = s.value();
														NodeType t = m.getAnnotation(NodeType.class);
														if (t != null)
															child.mime = t.value();
														children.add(child);
													}
												}
											}
		}

		private boolean isPrimitive() {
			return primitive;
		}

		private boolean isRecursive(Type type) {
			return this.type == type || (parent != null && parent.isRecursive(type));
		}

		private boolean instanceOf(Type type, Class<?> class1) {
			if (type instanceof ParameterizedType) {
				return class1.isAssignableFrom((Class<?>) ((ParameterizedType) type).getRawType());
			}
			if (type instanceof TypeVariable) {
				return instanceOf(((TypeVariable) type).getBounds()[0], class1);
			}
			if (type instanceof GenericArrayType) {
				return class1 == byte[].class;
			}
			if (type instanceof WildcardType) {
				return false;
			}
			return class1.isAssignableFrom((Class<?>) type);
		}

		String getPath() {
			String upper = parent == null ? "" : parent.getPath();
			return upper + "/" + name;
		}

		public void print(String indent) {
			String actions = "" + (add ? "A" : "_") + (get ? "G" : "_") + (replace ? "R" : "_") + (delete ? "D" : "_");

			if (mime != null && !mime.isEmpty()) {
				System.out.printf("\n%80s\n", mime);
			}
			System.out.printf("%-36s %-5s %-4s  %-20s  %5s   %s\n", indent + name, (recursive ? " ..." : ""), actions, dmtType, cardinality, scope);

			for (XNode node : children) {
				node.print(indent + "  ");
			}
		}
	}

	static $	x;

	public static void main(String args[]) {
		XNode n = new XNode(null, "$", $.class);
		n.print("");
	}
}
