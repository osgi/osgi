package org.osgi.service.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/** @modelguid {A13BD840-4216-4B8B-AD87-1E30766DB619} */
public interface ApplicationContainer {
	/** @modelguid {75A72984-2C97-4BEE-9A62-E258C9ECE60C} */
	public ApplicationDescriptor[] installApplication(InputStream inputStream)
			throws IOException, Exception;

	/** @modelguid {B63D6934-F9A5-41A4-8A22-67BBB79468AB} */
	public ApplicationDescriptor[] uninstallApplication(
			ApplicationDescriptor appDescriptor, boolean force)
			throws IOException, Exception;

	/** @modelguid {D03FDB94-BEF5-4077-85B1-F70BE344B39F} */
	public ApplicationDescriptor[] upgradeApplication(
			ApplicationDescriptor appDescriptor, InputStream inputStream,
			boolean force) throws IOException, Exception;

	/** @modelguid {8814BCD1-CD4D-4462-BEB2-415B8B367C47} */
	public Application createApplication(Map args, ApplicationHandle appHandle)
			throws Exception;
}
