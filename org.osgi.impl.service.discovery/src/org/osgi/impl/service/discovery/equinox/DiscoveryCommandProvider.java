package org.osgi.impl.service.discovery.equinox;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.discovery.Discovery;
import org.osgi.service.discovery.ServiceDescription;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class DiscoveryCommandProvider implements org.eclipse.osgi.framework.console.CommandProvider {
	Discovery discovery;
	private BundleContext context;
	
	public DiscoveryCommandProvider(final BundleContext context) {
		this.context = context;
		
		ServiceTracker tracker = new ServiceTracker(context, Discovery.class.getName(), new ServiceTrackerCustomizer() {

			public Object addingService(ServiceReference reference) {
				discovery = (Discovery) context.getService(reference);
				return null;
			}

			public void modifiedService(ServiceReference reference,
					Object service) {
				discovery = (Discovery) context.getService(reference);
			}

			public void removedService(ServiceReference reference,
					Object service) {
				discovery = null;
			}
			
		});
		tracker.open();
	}

	public String getHelp() {
		return "\n---Discovery---\n\tlookup [<interface>] -- list interfaces\n\tpublish <bundle id> -- publish all interfaces of the given bundle\n";
	}

	public void _lookup(CommandInterpreter ci) {
		if (discovery != null) {
			ServiceDescription serviceDescription = new ServiceDescriptionAdapter(ci.nextArgument());
			
			Collection services = discovery.findService(serviceDescription);
			
			ci.println("services found:");
			for (Iterator i = services.iterator(); i.hasNext();) {
				ci.println((ServiceDescription) i.next());
			}
		} else {
			ci.println("Discovery service not set");
		}
	}
	
	public void _publish(CommandInterpreter ci) {
		if (discovery != null) {
			String idstr = ci.nextArgument();
			
			if (idstr != null) {
				try {
					
					Bundle bundle = context.getBundle(Long.parseLong(idstr));
					ServiceReference[] refs = bundle.getRegisteredServices();
					
					
					boolean result = false;
					ServiceDescription serviceDescription;
					
					for (int i=0; i<refs.length; i++) {
						final ServiceReference sr = refs[i];
						
						serviceDescription = new ServiceDescription() {

							public String getInterfaceName() {
								return ((String[])sr.getProperty(Constants.OBJECTCLASS))[0];
							}

							public Map getProperties() {
								HashMap map = new HashMap();
								
								String[] keys = sr.getPropertyKeys();
								for (int i=0; i<keys.length; i++) {
									map.put(keys[i], sr.getProperty(keys[i]));
								}
								return map;
							}

							public Object getProperty(String key) {
								return sr.getProperty(key);
							}

							public Collection keys() {
								return Arrays.asList(sr.getPropertyKeys());
							}

							public int compare(Object obj, Object obj1) {
								return 0;
							}
							
						};
						
						result = discovery.publish(serviceDescription);
						
						if (result) {
							ci.println("Successfully published " + serviceDescription.getInterfaceName());
						} else {
							ci.println("Failed to publish " + serviceDescription.getInterfaceName());
						}
					}
					
				} catch (Exception ex) {
					ci.printStackTrace(ex);
				}
			} else {
				ci.println("Must specify valid bundle id!");
			}
		} else {
			ci.println("Discovery service not set");
		}
	}
	
	private class ServiceDescriptionAdapter implements ServiceDescription {
		private String intf;
		
		ServiceDescriptionAdapter(String interf) {
			intf = interf;
		}

		public String getInterfaceName() {
			return intf;
		}

		public Map getProperties() {
			// TODO Auto-generated method stub
			return null;
		}

		public Object getProperty(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		public Collection keys() {
			// TODO Auto-generated method stub
			return null;
		}

		public int compare(Object arg0, Object arg1) {
			// TODO Auto-generated method stub
			return 0;
		}
		
		public String toString() {
			return "ServiceDescription: interface=" + intf;
		}
	}
}
