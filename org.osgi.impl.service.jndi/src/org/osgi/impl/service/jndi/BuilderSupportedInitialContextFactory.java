/*
 * Copyright 2010 Oracle Corporation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


package org.osgi.impl.service.jndi;

import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;

public interface BuilderSupportedInitialContextFactory extends InitialContextFactory {
	
	/**
	 * Returns the InitialContextFactoryBuilder service used to 
	 * create this InitialContextFactory. 
	 * 
	 * @return the InitialContextFactoryBuilder that was used to 
	 * create this InitialContextFactory.  
	 */
	public InitialContextFactoryBuilder getBuilder();
}
