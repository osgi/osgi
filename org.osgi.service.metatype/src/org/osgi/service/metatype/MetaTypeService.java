/*
 * $Header$
 * 
 * Copyright (c) OSGi Alliance (2004). All Rights Reserved.
 * 
 * Implementation of certain elements of the OSGi Specification may be subject
 * to third party intellectual property rights, including without limitation,
 * patent rights (such a third party may or may not be a member of the OSGi
 * Alliance). OSGi Alliance is not responsible and shall not be held
 * responsible in any manner for identifying or failing to identify any or all
 * such third party intellectual property rights.
 * 
 * This document and the information contained herein are provided on an "AS IS"
 * basis and OSGi Alliance DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED,
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
package org.osgi.service.metatype;

import org.osgi.framework.Bundle;

/**
 * The MetaType Service can be used to obtain meta type information for a
 * bundle. The MetaType Service will examine the specified bundle for meta type
 * documents to create the returned <code>MetaTypeInformation</code> object.
 * 
 * <p>
 * If the specified bundle does not contain any meta type documents, then a
 * <code>MetaTypeInformation</code> object will be returned that wrappers any
 * <code>ManagedService</code> or <code>ManagedServiceFactory</code>
 * services registered by the specified bundle that implement
 * <code>MetaTypeProvider</code>. Thus the MetaType Service can be used to
 * retrieve meta type information for bundles which contain a meta type
 * documents or which provide their own <code>MetaTypeProvider</code> objects.
 * 
 * @version $Revision$
 * @since 1.1
 */
public interface MetaTypeService {
	/**
	 * Return the MetaType information for the specified bundle.
	 * 
	 * @param bundle The bundle for which meta type information is requested.
	 * @return A MetaTypeInformation object for the specified bundle.
	 */
	public MetaTypeInformation getMetaTypeInformation(Bundle bundle);

	/**
	 * Location of meta type documents. The MetaType Service will process each
	 * entry in the meta type documents directory.
	 */
	public final static String	METATYPE_DOCUMENTS_LOCATION	= "OSGI-INF/metatype";
}