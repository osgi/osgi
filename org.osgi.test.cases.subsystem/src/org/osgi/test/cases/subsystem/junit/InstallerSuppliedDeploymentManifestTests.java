package org.osgi.test.cases.subsystem.junit;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.Constants;
import org.osgi.service.subsystem.Subsystem;
import org.osgi.service.subsystem.SubsystemConstants;
import org.osgi.service.subsystem.SubsystemException;
import org.osgi.test.cases.subsystem.util.ManifestBuilder;
import org.osgi.test.cases.subsystem.util.SubsystemBuilder;

public class InstallerSuppliedDeploymentManifestTests extends SubsystemTest {
	public void testUnspecifiedContentAndDeploymentManifest() throws Exception {
		String subsystemSymbolicName = getName();
		Subsystem root = getRootSubsystem();
		File file = createEsaFileForInstallingByLocation(
				subsystemSymbolicName, 
				new SubsystemBuilder(getContext())
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, subsystemSymbolicName)
						.build());
		Subsystem subsystem = null;
		try {
			subsystem = root.install(file.toURI().toURL().toString(), null,
					null);
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed as if Subsystem.install(String) had been called");
		}
		finally {
			if (subsystem != null) {
				doSubsystemOperation(subsystemSymbolicName, subsystem, Operation.UNINSTALL, false);
			}
		}
	}
	
	public void testSpecifiedContentUnspecifiedDeploymentManifest() throws Exception {
		String subsystemSymbolicName = getName();
		Subsystem root = getRootSubsystem();
		Subsystem subsystem = null;
		try {
			subsystem = root.install(
					subsystemSymbolicName,
					new SubsystemBuilder(getContext())
							.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, subsystemSymbolicName)
							.build(), null);
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed as if Subsystem.install(String, InputStream) had been called");
		}
		finally {
			if (subsystem != null) {
				doSubsystemOperation(subsystemSymbolicName, subsystem, Operation.UNINSTALL, false);
			}
		}
	}
	
	public void testUnspecifiedContentSpecifiedDeploymentManifest() throws Exception {
		String bundleSymbolicName = "deployment.manifest.a";
		String subsystemSymbolicName = getName();
		Subsystem root = getRootSubsystem();
		File file = createEsaFileForInstallingByLocation(
				subsystemSymbolicName,
				new SubsystemBuilder(getContext())
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, subsystemSymbolicName)
						.header(SubsystemConstants.SUBSYSTEM_CONTENT, bundleSymbolicName)
						.bundle(bundleSymbolicName + ".jar")
						.build());
		Subsystem subsystem = null;
		try {
			subsystem = root.install(
					file.toURI().toURL().toString(),
					null,
					new ByteArrayInputStream(new ManifestBuilder().header(Constants.IMPORT_PACKAGE, "org.osgi.framework").build()));
		} catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have installed because supplied deployment manifest met the needs of all content");
		} finally {
			if (subsystem != null) {
				doSubsystemOperation(subsystemSymbolicName, subsystem, Operation.UNINSTALL, false);
			}
		}
	}

	public void testSpecifiedContentSpecifiedDeploymentManifest() throws Exception {
		String bundleSymbolicName = "deployment.manifest.a";
		String subsystemSymbolicName = getName();
		Subsystem root = getRootSubsystem();
		Subsystem subsystem =  root.install(
				subsystemSymbolicName,
				new SubsystemBuilder(getContext())
						.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, subsystemSymbolicName)
						.header(SubsystemConstants.SUBSYSTEM_CONTENT, bundleSymbolicName)
						.deploymentHeader(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, subsystemSymbolicName)
						.deploymentHeader(SubsystemConstants.DEPLOYED_CONTENT, bundleSymbolicName + ";deployed-version=0.0")
						.deploymentHeader(Constants.IMPORT_PACKAGE, "org.osgi.framework,org.osgi.framework.wiring")
						.bundle(bundleSymbolicName + ".jar")
						.build(), 
				new ByteArrayInputStream(
						new ManifestBuilder()
								.header(SubsystemConstants.SUBSYSTEM_SYMBOLICNAME, subsystemSymbolicName)
								.header(SubsystemConstants.DEPLOYED_CONTENT, bundleSymbolicName + ";deployed-version=0.0")
								.header(Constants.IMPORT_PACKAGE, "org.osgi.framework")
								.build()));
		try {
			subsystem.start();
		}
		catch (SubsystemException e) {
			e.printStackTrace();
			fail("Subsystem should have started because package org.osgi.framework.wiring should not have been visible to content bundle");
		}
		finally {
			if (subsystem != null) {
				doSubsystemOperation(subsystemSymbolicName, subsystem, Operation.UNINSTALL, false);
			}
		}
	}

	private File createEsaFileForInstallingByLocation(String name, InputStream subsystem) throws IOException {
		File result = new File(getContext().getDataFile("testSubsystems"), name + ".esa");
		try {
			FileOutputStream fos = new FileOutputStream(result);
			try {
				byte[] bytes = new byte[1024];
				int read;
				while ((read = subsystem.read(bytes)) != -1) {
					fos.write(bytes, 0, read);
				}
			}
			finally {
				fos.close();
			}
		}
		finally {
			subsystem.close();
		}
		return result;
	}
}
