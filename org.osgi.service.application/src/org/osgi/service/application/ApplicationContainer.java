/*
 * Copyright (c) The Open Services Gateway Initiative (2004).
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

package org.osgi.service.application;

import java.io.InputStream;
import java.io.IOException;


/** @modelguid {A13BD840-4216-4B8B-AD87-1E30766DB619} */
public interface ApplicationContainer {

	/** @modelguid {75A72984-2C97-4BEE-9A62-E258C9ECE60C} */
	public ApplicationDescriptor[] installApplication( InputStream inputStream) throws IOException, Exception;

	/** @modelguid {B63D6934-F9A5-41A4-8A22-67BBB79468AB} */
	public ApplicationDescriptor[] uninstallApplication(ApplicationDescriptor appDescriptor, boolean force) throws IOException, Exception;

	/** @modelguid {D03FDB94-BEF5-4077-85B1-F70BE344B39F} */
	public ApplicationDescriptor[] upgradeApplication(ApplicationDescriptor appDescriptor, InputStream inputStream, boolean force) throws IOException, Exception;

	/** @modelguid {8814BCD1-CD4D-4462-BEB2-415B8B367C47} */
	public Application createApplication(ApplicationContext appContext, ApplicationHandle appHandle) throws Exception;
	
}
