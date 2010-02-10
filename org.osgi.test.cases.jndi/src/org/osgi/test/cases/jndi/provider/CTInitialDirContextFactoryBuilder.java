/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
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


package org.osgi.test.cases.jndi.provider;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

public class CTInitialDirContextFactoryBuilder implements
		InitialContextFactoryBuilder {

	public InitialContextFactory createInitialContextFactory(Hashtable env) throws NamingException {
		String contextFactory =  (String) env.get(Context.INITIAL_CONTEXT_FACTORY);
		if (contextFactory != null) {
			if (contextFactory.equals(CTInitialDirContextFactory.class.getName())) {
				return new CTInitialDirContextFactory();
			} else {
				return null;
			}
		} else {
			return new CTInitialDirContextFactory();
		}
	}
}
