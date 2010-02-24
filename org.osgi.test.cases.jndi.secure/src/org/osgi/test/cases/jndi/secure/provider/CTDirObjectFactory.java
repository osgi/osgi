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

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.OperationNotSupportedException;
import javax.naming.RefAddr;
import javax.naming.directory.Attributes;
import javax.naming.spi.DirObjectFactory;

/**
 * @version $Revision$ $Date$
 */
public class CTDirObjectFactory implements DirObjectFactory {

	
	private Hashtable env = new Hashtable();
	
	public CTDirObjectFactory() {
		
	}
	
	public CTDirObjectFactory(Hashtable env) {
		this.env = env;
	}
	

	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment) throws Exception {
		return getObjectInstance(obj, name, nameCtx, environment, null);
	}
	
	public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable environment, Attributes attrs) throws Exception {
		if (obj instanceof CTReference) {
			RefAddr value = ((CTReference) obj).get("value"); 
			if (value != null) {
				return new CTTestObject(value.getContent().toString(), CTDirObjectFactory.class.getName(), attrs);
			} else {
				return new CTTestObject(null, CTDirObjectFactory.class.getName(), attrs);
			}
		} else if (obj instanceof String) {
			if (((String)obj).equals(CTTestObject.class.getName())) {
				return new CTTestObject(null, CTDirObjectFactory.class.getName(), attrs);
			}
		}
		return null;

	}

	public Hashtable getEnvironment() {
		return env;
	}

}
