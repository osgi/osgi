/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). The OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION
 * HEREIN WILL NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE
 * OSGI ALLIANCE BE LIABLE FOR ANY LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF
 * USE OF DATA, INTERRUPTION OF BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR
 * EXEMPLARY, INCIDENTIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN
 * CONNECTION WITH THIS DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH LOSS OR DAMAGE.
 * 
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
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