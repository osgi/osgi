package org.osgi.impl.service.discovery.equinox;


public class DiscoveryCommandProvider /*
										 * implements
										 * org.eclipse.osgi.framework.console.CommandProvider
										 */{
	// private Discovery discovery;
	// private BundleContext context;
	//
	// private HashMap publishedServices = new HashMap();
	//
	// public DiscoveryCommandProvider(final BundleContext context) {
	// this.context = context;
	//
	// ServiceTracker tracker = new ServiceTracker(context, Discovery.class
	// .getName(), new ServiceTrackerCustomizer() {
	//
	// public Object addingService(ServiceReference reference) {
	// discovery = (Discovery) context.getService(reference);
	// return null;
	// }
	//
	// public void modifiedService(ServiceReference reference,
	// Object service) {
	// discovery = (Discovery) context.getService(reference);
	// }
	//
	// public void removedService(ServiceReference reference,
	// Object service) {
	// discovery = null;
	// }
	//
	// });
	// tracker.open();
	// }
	//
	// public String getHelp() {
	// return "\n---Discovery---\n\tlookup [<interface>] -- list
	// interfaces\n\tpublish <bundle id> -- publish all interfaces of the given
	// bundle\n\tunpublish <bundle id> -- unpublish all interfaces of the given
	// bundle\n";
	// }
	//
	// public void _lookup(CommandInterpreter ci) {
	// if (discovery != null) {
	// String arg = ci.nextArgument();
	// ci.println("argument is " + arg);
	// Iterator services = discovery.findService(arg, null).iterator();
	//
	// StringBuffer buf = new StringBuffer();
	// buf.append("services found: ");
	//
	// while (services.hasNext()) {
	// ci.println(((ServiceEndpointDescription) services.next())
	// .getProperty(Constants.OBJECTCLASS));
	// }
	// ci.println("services found:");
	// } else {
	// ci.println("Discovery service not set");
	// }
	// }
	//
	// public void _publish(CommandInterpreter ci) {
	// if (discovery != null) {
	// String idstr = ci.nextArgument();
	// if (idstr != null) {
	// try {
	// Bundle bundle = context.getBundle(Long.parseLong(idstr));
	// ServiceReference[] refs = bundle.getRegisteredServices();
	// if (refs == null) {
	// ci.println("the bundle " + bundle.getSymbolicName()
	// + " has not registered any service");
	// return;
	// }
	// for (int i = 0; i < refs.length; i++) {
	// Map interfaceAndFilter = new HashMap();
	// Object objclass = refs[i]
	// .getProperty(Constants.OBJECTCLASS);
	// interfaceAndFilter
	// .put(
	// (objclass instanceof String) ? (String) objclass
	// : ((String[]) objclass)[0],
	// "0.0.0");
	// Map properties = new HashMap();
	// properties.put("myKey", "myValue");
	// ServiceEndpointDescription descr = discovery
	// .publishService(interfaceAndFilter, null,
	// properties);
	// if (descr != null) {
	// publishedServices.put(refs[i], descr);
	// ci.println("Successfully published");
	// } else {
	// ci.println("Failed to publish");
	// }
	// }
	// } catch (Exception ex) {
	// ci.printStackTrace(ex);
	// }
	// } else {
	// ci.println("Must specify valid bundle id!");
	// }
	// } else {
	// ci.println("Discovery service not set");
	// }
	// }
	//
	// public void _unpublish(CommandInterpreter ci) {
	// if (discovery != null) {
	// String idstr = ci.nextArgument();
	// if (idstr != null) {
	// try {
	// Bundle bundle = context.getBundle(Long.parseLong(idstr));
	// ServiceReference[] refs = bundle.getRegisteredServices();
	// if (refs == null) {
	// ci.println("the bundle " + bundle.getSymbolicName()
	// + " has not registered any service");
	// }
	// for (int i = 0; (refs != null) && (i < refs.length); i++) {
	// ServiceEndpointDescription serviceDescription =
	// (ServiceEndpointDescription) publishedServices
	// .get(refs[i]);
	// discovery.unpublishService(serviceDescription);
	// }
	// } catch (Exception ex) {
	// ci.printStackTrace(ex);
	// }
	// } else {
	// ci.println("Must specify valid bundle id!");
	// }
	// } else {
	// ci.println("Discovery service not set");
	// }
	// }
}
