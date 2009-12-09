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


package org.osgi.test.cases.jndi.provider;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;

public class CTReferenceable implements Referenceable {

	public Reference getReference() throws NamingException {
		Reference ref = new CTReference(String.class.getName(), CTObjectFactory.class.getName());
		return ref;
	}

}
