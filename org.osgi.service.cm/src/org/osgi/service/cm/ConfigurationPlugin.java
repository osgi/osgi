/*
 * $Header$
 *
 * Copyright (c) The Open Services Gateway Initiative (2001, 2002).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the Open Services Gateway Initiative
 * (OSGI) Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of OSGi). OSGi is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and OSGI DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL OSGI BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.service.cm;
import java.util.*;
import org.osgi.framework.*;

/**
 * A service interface for processing configuration dictionary before the update.
 *
 * <p>A bundle registers a <tt>ConfigurationPlugin</tt> object in order to process
 * configuration updates before they reach the Managed Service or Managed Service Factory.
 * The Configuration Admin service will detect registrations
 * of Configuration Plugin services and must call these services every
 * time  before it calls the <tt>ManagedService</tt> or <tt>ManagedServiceFactory</tt>
 * <tt>updated</tt> method.  The
 * Configuration Plugin service thus has the opportunity to view and modify
 * the properties before they are passed to the ManagedS ervice or
 * Managed Service Factory.
 *
 * <p>Configuration Plugin (plugin) services have full read/write access to all
 * configuration information. Therefore, bundles using this facility should
 * be trusted. Access to this facility should be limited with <tt>ServicePermission[REGISTER, ConfigurationPlugin]</tt>.
 * Implementations of a Configuration Plugin service should assure that they
 * only act on appropriate configurations.
 *
 * <p>The <tt>Integer</tt> <tt>service.cmRanking</tt> registration
 * property may be specified.  Not specifying this registration
 * property, or setting it to something other than an <tt>Integer</tt>, is the
 * same as setting it to the <tt>Integer</tt> zero.  The
 * <tt>service.cmRanking</tt> property determines the order in which
 * plugins are invoked.  Lower ranked plugins are called before
 * higher ranked ones.  In the event of more than one plugin having
 * the same value of <tt>service.cmRanking</tt>, then the
 * Configuration Admin service arbitrarily chooses the order in which they are
 * called.
 *
 * <p>By convention, plugins with <tt>service.cmRanking&lt; 0</tt>
 * or <tt>service.cmRanking &gt; 1000</tt> should not make
 * modifications to the properties.
 *
 * <p>The Configuration Admin service has the right to hide properties from
 * plugins, or to ignore some or all the changes that they make. This
 * might be done for security reasons.  Any such behavior is entirely
 * implementation defined.
 *
 * <p>A plugin may optionally specify a <tt>cm.target</tt> registration
 * property whose value is the PID of the Managed Service or
 * Managed Service Factory whose configuration updates the plugin
 * is intended to intercept.  The plugin will then only be called
 * with configuration updates that are targetted at the
 * Managed Service or Managed Service Factory with the specified
 * PID.  Omitting the <tt>cm.target</tt> registration property means
 * that the plugin is called for all configuration updates.
 *
 * @version $Revision$
*/

public interface ConfigurationPlugin {
	/**
	 * A service property to limit the Managed Service or Managed Service Factory
	 * configuration dictionaries a Configuration Plugin service receives.
	 *
	 * This property contains a <tt>String[]</tt> of PIDs. A Configuration
	 * Admin service must call a Configuration Plugin service only
	 * when this property is not set, or the target service's PID
	 * is listed in this property.
	*/
	String  				CM_TARGET   = "cm.target";
	
	/**
	 * A service property to specify the order in which plugins are invoked.
     *
	 * This property contains an <tt>Integer</tt> ranking of the plugin.
     * Not specifying this registration
     * property, or setting it to something other than an <tt>Integer</tt>, is the
     * same as setting it to the <tt>Integer</tt> zero.  This
     * property determines the order in which
     * plugins are invoked.  Lower ranked plugins are called before
     * higher ranked ones.
	 *
     * @since 1.2
	 */
	String  				CM_RANKING   = "service.cmRanking";
	
	/**
	 * View and possibly modify the a set of configuration properties
	 * before they are sent to the Managed Service or the
	 * Managed Service Factory. The Configuration Plugin services are called
	 * in increasing order of their <tt>service.cmRanking</tt> property.
	 * If this property is undefined or is a non-<tt>Integer</tt> type, 0 is used.
	 *
	 * <p>This method should not modify the properties unless the
	 * <tt>service.cmRanking</tt> of this plugin is in the range
	 * <tt>0 &lt;= service.cmRanking &lt;= 1000</tt>.
	 * <p>If this method throws any <tt>Exception</tt>, the Configuration Admin service
	 * must catch it and should log it.
     *
	 * @param reference reference to the Managed Service or Managed Service Factory
	 * @param configuration The configuration properties.
     * This argument must not contain the
     * "service.bundleLocation" property. The value of this property
     * may be obtained from the <tt>Configuration.getBundleLocation</tt> method.
	*/
	void modifyConfiguration( ServiceReference reference, Dictionary properties  );
}


