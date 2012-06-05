package org.osgi.impl.service.residentialmanagement.plugins;

public class RMTConstants {
	
	protected static final String RMT_ROOT = System.getProperty("org.osgi.dmt.residential");
	
	//Framework
	protected static final String FRAMEWORKSTARTLEVEL = "StartLevel";
	protected static final String INITIALBUNDLESTARTLEVEL = "InitialBundleStartLevel";
	protected static final String PROPERTY = "Property";

	protected static final String BUNDLE = "Bundle";
	protected static final String URL = "URL";
	protected static final String AUTOSTART = "AutoStart";
	protected static final String FAULTTYPE = "FaultType";
	protected static final String FAULTMESSAGE = "FaultMessage";
	protected static final String BUNDLEID = "BundleId";
	protected static final String SYMBOLICNAME = "SymbolicName";
	protected static final String VERSION = "Version";
	protected static final String BUNDLETYPE = "BundleType";
	protected static final String HEADERS = "Headers";
	protected static final String LOCATION = "Location";
	protected static final String STATE = "State";
	protected static final String REQUESTEDSTATE = "RequestedState";
	protected static final String BUNDLESTARTLEVEL = "StartLevel";
	protected static final String LASTMODIFIED = "LastModified";
	protected static final String BUNDLEINSTANCEID = "InstanceId";

	protected static final String WIRES = "Wires";
	protected static final String NAMESPACE = "Namespace";
	protected static final String REQUIRER = "Requirer";
	protected static final String PROVIDER = "Provider";
	protected static final String WIRESINSTANCEID = "InstanceId";

	protected static final String REQUIREMENT = "Requirement";
	protected static final String REQUIREMENTDIRECTIVE = "Directive";
	protected static final String REQUIREMENTATTRIBUTE = "Attribute";

	protected static final String CAPABILITY = "Capability";
	protected static final String CAPABILITYDIRECTIVE = "Directive";
	protected static final String CAPABILITYATTRIBUTE = "Attribute";

	protected static final String SIGNERS = "Signers";
	protected static final String ISTRUSTED = "IsTrusted";
	protected static final String CERTIFICATECHAIN = "CertificateChain";
	protected static final String SIGNERSINSTANCEID = "InstanceId";

	protected static final String ENTRIES = "Entries";
	protected static final String PATH = "Path";
	protected static final String CONTENT = "Content";
	protected static final String ENTRIESINSTANCEID = "InstanceId";

	protected static final String FRAGMENT = "FRAGMENT";
	protected static final String INSTALLED = "INSTALLED";
	protected static final String RESOLVED = "RESOLVED";
	protected static final String STARTING = "STARTING";
	protected static final String ACTIVE = "ACTIVE";
	protected static final String STOPPING = "STOPPING";
	protected static final String UNINSTALLED = "UNINSTALLED";
	protected static final String FRAMEWORK_NODE_TYPE = "org.osgi/1.0/FrameworkManagementObject";
	protected static final String SERVICE_NAMESPACE = "osgi.wiring.rmt.service";
	protected static final String KEY_OF_RMT_ROOT_URI = "org.osgi.dmt.residential";
	protected static final String WAIT_TIME_FOR_FRAMEWORK_UPDATE = "framework.update.wait.time";
	protected static final String WAIT_TIME_FOR_BUNDLE_REFRESH = "bundle.refresh.wait.time";
	protected static final String WAIT_TIME_FOR_SETSTARTLEVEL = "bundle.setStartLevel.wait.time";
	
	protected static final String[] LAUNCHING_PROPERTIES = new String[] {
			"org.osgi.framework.bootdelegation",
			"org.osgi.framework.bsnversion",
			"org.osgi.framework.bundle.parent",
			"org.osgi.framework.command.execpermission",
			"org.osgi.framework.language",
			"org.osgi.framework.library.extensions",
			"org.osgi.framework.os.name", "org.osgi.framework.os.version",
			"org.osgi.framework.processor", "org.osgi.framework.security",
			"org.osgi.framework.startlevel.beginning",
			"org.osgi.framework.storage", "org.osgi.framework.storage.clean",
			"org.osgi.framework.system.packages",
			"org.osgi.framework.system.packages.extra",
			"org.osgi.framework.system.capabilities",
			"org.osgi.framework.system.capabilities.extra",
			"org.osgi.framework.trust.repositories",
			"org.osgi.framework.windowsystem", "org.osgi.dmt.residential" };
	
	//Log
	protected static final String LOG = "Log";
	protected static final String LOGENTRIES = "LogEntries";
	protected static final String TIME = "Time";
	protected static final String LEVEL = "Level";
	protected static final String MESSAGE = "Message";
	protected static final String EXCEPTION = "Exception";
	protected static final String LOG_NODE_TYPE = "org.osgi/1.0/LogManagementObject";
	
	//Filter
	protected static final String FILTER = "Filter";
	protected static final String TARGET = "Target";
	protected static final String LIMIT = "Limit";
	protected static final String RESULTURILIST = "ResultUriList";
	protected static final String RESULT = "Result";
	protected static final String INSTANCEID = "InstanceId";
	protected static final String FILTER_NODE_TYPE = "org.osgi/1.0/FiltersManagementObject";
}
