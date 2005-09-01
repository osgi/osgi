/*
 * Copyright (c) IBM Corporation (2005)
 *
 * These materials have been contributed  to the OSGi Alliance as 
 * "MEMBER LICENSED MATERIALS" as defined in, and subject to the terms of, 
 * the OSGi Member Agreement, specifically including but not limited to, 
 * the license rights and warranty disclaimers as set forth in Sections 3.2 
 * and 12.1 thereof, and the applicable Statement of Work. 
 *
 * All company, brand and product names contained within this document may be 
 * trademarks that are the sole property of the respective owners.
 */

package org.eclipse.osgi.component.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import org.eclipse.osgi.component.model.ComponentDescription;
import org.eclipse.osgi.component.model.PropertyValueDescription;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class PropertyElement extends DefaultHandler {
	protected ParserHandler				root;
	protected ComponentElement			parent;
	protected PropertyValueDescription	property;
	protected List						values;

	public PropertyElement(ParserHandler root, ComponentElement parent,
			Attributes attributes) {
		this.root = root;
		this.parent = parent;
		property = new PropertyValueDescription(parent
				.getComponentDescription());
		values = new ArrayList();

		int size = attributes.getLength();
		for (int i = 0; i < size; i++) {
			String key = attributes.getQName(i);
			String value = attributes.getValue(i);

			if (key.equals(ParserConstants.NAME_ATTRIBUTE)) {
				property.setName(value);
				continue;
			}

			if (key.equals(ParserConstants.VALUE_ATTRIBUTE)) {
				property.setValue(value);
				continue;
			}

			if (key.equals(ParserConstants.TYPE_ATTRIBUTE)) {
				property.setType(value);
				continue;
			}
			root.logError("unrecognized properties element attribute: " + key);
		}

		if (property.getName() == null) {
			root.logError("property name not specified");
		}
	}

	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		root.logError("property does not support nested elements");
	}

	public void characters(char[] ch, int start, int length) {
		int end = start + length;
		int cursor = start;
		while (cursor < end) {
			if (ch[cursor] == '\n') {
				charLine(ch, start, cursor - start);
				start = cursor;
			}
			cursor++;
		}
		charLine(ch, start, cursor - start);
	}

	private void charLine(char[] ch, int start, int length) {
		if (length > 0) {
			String line = new String(ch, start, length).trim();
			if (line.length() > 0) {
				values.add(line);
			}
		}
	}

	public void endElement(String uri, String localName, String qName) {

		int size = values.size();

		// If the value attribute is specified, then body of the property
		// element is ignored.
		if ((property.getValue() != null) && (size > 0)) {
			root
					.logError("If the value attribute is specified, the body of the property element is ignored. key = "
							+ property.getName()
							+ " value = "
							+ property.getValue());

			// if characters were specified ( values are specifed in the body of
			// the property element )
		}
		else
			if (size > 0) {
				// if String then store as String[]
				if (property.getType().equals("String")) {
					String[] result = new String[size];
					values.toArray(result);
					property.setValue(result);
				}
				else
					if (property.getType().equals("Integer")) {
						int[] result = new int[size];
						if (values != null) {
							Iterator it = values.iterator();
							int i = 0;
							while (it.hasNext()) {
								Integer value = new Integer((String) it.next());
								result[i++] = value.intValue();
							}
							property.setValue(result);
						}
					}
					else
						if (property.getType().equals("Long")) {
							long[] result = new long[size];
							if (values != null) {
								Iterator it = values.iterator();
								int i = 0;
								while (it.hasNext()) {
									Long value = new Long((String) it.next());
									result[i++] = value.longValue();
								}
								property.setValue(result);
							}
						}
						else
							if (property.getType().equals("Double")) {
								double[] result = new double[size];
								if (values != null) {
									Iterator it = values.iterator();
									int i = 0;
									while (it.hasNext()) {
										Double value = new Double((String) it
												.next());
										result[i++] = value.doubleValue();
									}
									property.setValue(result);
								}
							}
							else
								if (property.getType().equals("Float")) {
									float[] result = new float[size];
									if (values != null) {
										Iterator it = values.iterator();
										int i = 0;
										while (it.hasNext()) {
											Float value = new Float((String) it
													.next());
											result[i++] = value.floatValue();
										}
										property.setValue(result);
									}
								}
								else
									if (property.getType().equals("Byte")) {
										byte[] result = new byte[size];
										if (values != null) {
											Iterator it = values.iterator();
											int i = 0;
											while (it.hasNext()) {
												Byte value = new Byte(
														(String) it.next());
												result[i++] = value.byteValue();
											}
											property.setValue(result);
										}
									}
									else
										if (property.getType().equals("Char")) {
											char[] result = new char[size];
											if (values != null) {
												Iterator it = values.iterator();
												int i = 0;
												while (it.hasNext()) {
													char[] value = ((String) it
															.next())
															.toCharArray();
													result[i++] = value[0];
												}
												property.setValue(result);
											}
										}
										else
											if (property.getType().equals(
													"Boolean")) {
												boolean[] result = new boolean[size];
												if (values != null) {
													Iterator it = values
															.iterator();
													int i = 0;
													while (it.hasNext()) {
														Boolean value = new Boolean(
																(String) it
																		.next());
														result[i++] = value
																.booleanValue();
													}
													property.setValue(result);
												}
											}
											else
												if (property.getType().equals(
														"Short")) {
													short[] result = new short[size];
													if (values != null) {
														Iterator it = values
																.iterator();
														int i = 0;
														while (it.hasNext()) {
															Short value = new Short(
																	(String) it
																			.next());
															result[i++] = value
																	.shortValue();
														}
														property
																.setValue(result);
													}
												}

			}

		ComponentDescription component = parent.getComponentDescription();
		component.addProperty(property);
		root.setHandler(parent);
	}
}
