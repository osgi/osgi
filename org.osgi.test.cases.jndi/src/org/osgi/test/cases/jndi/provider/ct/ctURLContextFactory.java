/*
 * Copyright (c) IBM Corporation (2009). All Rights Reserved.
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


package org.osgi.test.cases.jndi.provider.ct;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

/**
 * @version $Revision$ $Date$
 */
public class ctURLContextFactory implements ObjectFactory {

	public Object getObjectInstance(Object obj, Name name, Context context,
			Hashtable table) throws Exception {
		if (obj == null) {
			return new ctURLContext();
		} else if (obj instanceof java.lang.String) {
			String query = (String) obj;
			if (!query.startsWith("ct://")) {
				return null;
			}
			ctURLContext ctx = new ctURLContext(table);
			return ctx.lookup(query);
		} else if (obj instanceof java.lang.String[]) {
			ctURLContext ctx = new ctURLContext(table);
			NamingException ex = null;
			String[] urls = (String[])obj;
			Object result;
			for (int i = 0; i < urls.length; i++) {
				String query = urls[i];
				if (!query.startsWith("ct://")) {
					break;
				}
				try {
					result = ctx.lookup(query);
					return result;
				} catch (Exception e) {
					//Hold onto the exception while we process all the entries
					ex = (NamingException) e;
				}
			}		
			throw new Exception(ex);
		} else {
			return null;
		}
	}
}
