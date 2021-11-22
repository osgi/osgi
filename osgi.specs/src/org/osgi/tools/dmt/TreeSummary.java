/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/

package org.osgi.tools.dmt;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.osgi.dmt.ddf.AddableMAP;
import org.osgi.dmt.ddf.LIST;
import org.osgi.dmt.ddf.MAP;
import org.osgi.dmt.ddf.Mutable;
import org.osgi.dmt.ddf.MutableLIST;
import org.osgi.dmt.ddf.MutableMAP;
import org.osgi.dmt.ddf.NODE;
import org.osgi.dmt.ddf.NodeType;
import org.osgi.dmt.ddf.Opt;
import org.osgi.dmt.ddf.Scope;
import org.osgi.dmt.ddf.Scope.SCOPE;
import org.osgi.dmt.ddf.base64;

/**
 * 
 * Generate DMT Tree Summary for {@literal <xi:include>} into spec.
 * 
 * @author $Id$
 */
public class TreeSummary {

	static class XNode {
		final XNode		parent;
		final String	name;
		final Type		type;

		List<XNode>		children	= new ArrayList<XNode>();
		boolean			optional	= false;
		boolean			multiple	= false;
		boolean			add			= false;
		boolean			delete		= false;
		boolean			get			= false;
		boolean			replace		= false;
		boolean			recursive	= false;
		String			dmtType;
		SCOPE			scope		= SCOPE.P;
		String			cardinality	= "1";
		String			mime		= "";
		boolean			primitive	= true;

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
				@SuppressWarnings("hiding")
				boolean add = false;
				@SuppressWarnings("hiding")
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
					@SuppressWarnings("hiding")
					boolean add = false;
					@SuppressWarnings("hiding")
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
								dmtType = "date_time";
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
													List<Method> ms = Arrays
															.stream(c
																	.getDeclaredMethods())
															.sorted(Comparator
																	.comparing(
																			Method::getName))
															.collect(toList());
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

		private boolean isRecursive(Type t) {
			return this.type == t || (parent != null && parent.isRecursive(t));
		}

		private boolean instanceOf(Type t, Class<?> class1) {
			if (t instanceof ParameterizedType) {
				return class1.isAssignableFrom((Class<?>) ((ParameterizedType) t).getRawType());
			}
			if (t instanceof TypeVariable) {
				return instanceOf(((TypeVariable<?>) t).getBounds()[0], class1);
			}
			if (t instanceof GenericArrayType) {
				return class1 == byte[].class;
			}
			if (t instanceof WildcardType) {
				return false;
			}
			return class1.isAssignableFrom((Class<?>) t);
		}

		/**
		 * Generate the docbook xml for the Tree Summary.
		 * 
		 * @param indent Indent prefix to use.
		 * @param section true is a docbook section should wrap the output.
		 */
		public void print(final String indent, final boolean section) {
			final boolean root = parent == null;
			if (section) {
				System.out.printf("<section%s>\n  <title>%s</title>\n  <programlisting>", root ? " xmlns=\"http://docbook.org/ns/docbook\" version=\"5\"" : "", root ? "Tree Summary" : name);
			}

			if (mime != null && !mime.isEmpty()) {
				System.out.printf("\n%80s\n", mime);
			}
			String actions = (add ? "A" : "_") + (get ? "G" : "_") + (replace ? "R" : "_") + (delete ? "D" : "_");
			System.out.printf("%-36s %-5s %-4s  %-20s  %5s   %1s\n", indent + name, (recursive ? " ..." : ""), actions, dmtType, cardinality, scope);

			if (section) {
				if (root) {
					System.out.printf("</programlisting>\n");
				}
			}
			for (XNode node : children) {
				node.print(indent + "  ", root);
			}
			if (section) {
				if (!root) {
					System.out.printf("</programlisting>\n");
				}
				System.out.printf("</section>\n");
			}
		}
	}

	/**
	 * Generate the Tree Summary for the DMT.
	 * 
	 * @param args Unused.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Class<?> root = Class.forName(args[0]);
		XNode n = new XNode(null, root.getSimpleName(), root);
		n.print("", true);
	}
}
