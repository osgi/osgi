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


package org.osgi.test.cases.jndi.provider.ct;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

/**
 * @author $Id$
 */
public class ctURLContextFactory implements ObjectFactory {

	@Override
	public Object getObjectInstance(Object obj, Name name, Context context,
			Hashtable< ? , ? > table) throws Exception {
		if (obj == null) {
			return new ctURLContext();
		} else if (obj instanceof java.lang.String) {
			String query = (String) obj;
			if (!query.startsWith("ct://")) {
				return null;
			}
			@SuppressWarnings("unchecked")
			ctURLContext ctx = new ctURLContext((Map<String,Object>) table);
			return ctx.lookup(query);
		} else {
			return null;
		}
	}
}
