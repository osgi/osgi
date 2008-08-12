/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2005, 2006). All Rights Reserved.
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
package org.osgi.service.webapplication;

import java.util.Dictionary;

/**
 * DefaultWebApplication provides a basic, default service component which
 * implements WebApplication.
 * 
 * <p>
 * A web application bundle which wants to implement the WebApplication methods
 * should either subclass this class or create a class which implements
 * WebApplication and then modify the <code>implementation</code> element of the
 * service component descriptor to reference that class.
 * 
 * <p>
 * Here is an example service component description which uses the
 * DefaultWebApplication.
 * 
 * <pre>
 *     &lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
 *     &lt;scr:component name=&quot;com.example.myisv.webapp&quot; xmlns:scr=&quot;http://www.osgi.org/xmlns/scr/v1.0.0&quot; 
 *       &lt;implementation class=&quot;org.osgi.service.webapplication.DefaultWebApplication&quot;/&gt;
 *       &lt;properties entry=&quot;/WEB-INF/wab.properties&quot; /&gt;
 *       &lt;service&gt;
 *         &lt;provide interface=&quot;org.osgi.service.webapplication.WebApplication&quot;/&gt;
 *       &lt;/service&gt;
 *     &lt;/scr:component&gt;
 * </pre>
 * 
 * @version $Revision$
 */
public class DefaultWebApplication implements WebApplication {
	/**
	 * Returns the attributes that will be available to the Web Application
	 * Bundle's servlets as ServletContext attributes.
	 * 
	 * <p>
	 * The default web application provides no ServletContext attibutes.
	 * 
	 * @return <code>null</code>
	 * @see org.osgi.service.webapplication.WebApplication#getServletContextAttributes()
	 */
	public Dictionary getServletContextAttributes() {
		return null;
	}

	/**
	 * This method must be called by the Web Container to report any exception
	 * that occurs during the deployment of this WebApplication service.
	 * 
	 * <p>
	 * The default web application ignores deployment exceptions.
	 * 
	 * @param e The Exception that occurred during deployment.
	 * @see org.osgi.service.webapplication.WebApplication#deploymentException(java.lang.Exception)
	 */
	public void deploymentException(Exception e) {
	}
}
