/*
 * Copyright (c) IBM Corporation (2010). All Rights Reserved.
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


package org.osgi.test.cases.jndi.secure.provider;

import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;
import javax.naming.spi.ObjectFactoryBuilder;

/**
 * @version $Revision$ $Date$
 */
public class CTDirObjectFactoryBuilder implements ObjectFactoryBuilder {
	
	public ObjectFactory createObjectFactory(Object obj, Hashtable environment)
			throws NamingException {
		if (obj instanceof CTReference || obj instanceof String) {
			if (environment != null) {
				return new CTDirObjectFactory(environment);
			} else {
				return new CTDirObjectFactory();
			}
		} else { 
			return null;
		}
	}
}
