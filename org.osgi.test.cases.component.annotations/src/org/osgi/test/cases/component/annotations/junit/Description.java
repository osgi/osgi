/*
 * Copyright (c) OSGi Alliance (2012, 2013). All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.osgi.test.cases.component.annotations.junit;

import org.w3c.dom.Element;

/**
 * 
 * 
 * @author $Id$
 */
public class Description {
	private final String	name;
	private final NamespaceContextImpl	context;
	private final Element	component;

	/**
	 * @param name
	 * @param context
	 * @param component
	 */
	public Description(String name, NamespaceContextImpl context,
			Element component) {
		this.name = name;
		this.context = context;
		this.component = component;
	}

	/**
	 * @return result
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return result
	 */
	public Element getComponent() {
		return component;
	}

	/**
	 * @return result
	 */
	public NamespaceContextImpl getNamespaceContext() {
		return context;
	}
}
