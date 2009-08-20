
package org.osgi.test.cases.util;

import java.io.*;
import java.net.*;
import java.util.*;
import org.osgi.framework.*;

/**
 * The Registry class is a convenience class that handles things as
 * installing/uninstalling bundles, registering/unregistering services
 * and getting/ungetting bundles, makes it easier by keeping track of 
 * most of the handles and it also have the ability to clean everything 
 * up easily.
 */
public class Registry {

    Vector          bundles = new Vector();
    Hashtable       registeredServices = new Hashtable();
    Hashtable       fetchedServices = new Hashtable();
    String          webserver;
    BundleContext   context;
    Logger          logger;

    public Registry(BundleContext context, String webserver, Logger logger) {
        this.context = context;
        this.webserver = webserver;
        this.logger = logger;
    }
    
    public BundleContext getContext() {
        return context;
    }
    

    /*** Bundle methods ***/

    public Bundle installBundle(String bundleName) throws Exception {
    	return installBundle(bundleName, true);
    }
    
    public Bundle installBundle(String bundleName, boolean start) throws Exception {
        try {
            URL    url = new URL(webserver + bundleName);
            InputStream in = url.openStream();
 
            Bundle        b = context.installBundle(webserver + bundleName, in);

            bundles.addElement(b);
            if (start) {
            	b.start();
            }
            return b;
        }
        catch(BundleException e) {
            System.out.println("Not able to install testbundle " + bundleName);
            System.out.println("Nested " + e.getNestedException());
            e.printStackTrace();
            throw e;
        }
        catch(Exception e) {
            System.out.println("Not able to install testbundle " + bundleName);
            e.printStackTrace();
            throw e;
        }
    }

    public boolean isBundleInstalled(String bundleName) {
        String fullName = webserver + bundleName;
        boolean found = false;
        int index = 0;
        
        while(!found && (index < bundles.size())) {
            Bundle bundle = (Bundle) bundles.elementAt(index);
            if(bundle.getLocation().equals(fullName)) {
                found = true;
            }
            index++;
        }
        
        return found;
    }
    
        
        

    public void uninstallBundle(Bundle b) throws Exception {
        try {
			if ( b.getState() != Bundle.UNINSTALLED )
	            b.uninstall();
            bundles.removeElement(b);
        }
        catch(BundleException e) {
            System.out.println("Not able to uninstall testbundle " + b.getLocation());
            System.out.println("Nested " + e.getNestedException());
            e.printStackTrace();
            throw (Exception) e.getNestedException();
        }
        catch(Exception e) {
            System.out.println("Not able to uninstall testbundle " + b.getLocation());
            e.printStackTrace();
            throw e;
        }
    }

    public void uninstallAllBundles() throws Exception {
    	Vector bundlesClone = (Vector) bundles.clone();
        Enumeration bundleElements = bundlesClone.elements();
        while(bundleElements.hasMoreElements()) {
            Bundle b = (Bundle) bundleElements.nextElement();
            uninstallBundle(b);
        }
    }


    /*** Service methods ***/

    /**
     * Convenience method for getting services from the framework in cases
     * when you don't want to keep track of the ServiceReference.
     * <p> 
     * To unregister the service, use the ungetService(Object service) method.
     *
     * @param clazz the Class object of the service you with to retreive
     * @return a service object
     * @throws NullPointerException if the service couldn't be retreived
     *         from the framework.
     */
    public Object getService(Class clazz)  {
        Object service = null;
        try {
            service = getService(clazz, null);
        }
        catch(InvalidSyntaxException e) {
            /* null can not give an exception of this type */
        }
        return service;
    }

    /**
     */
    public Object getService(Class clazz, String filter) throws InvalidSyntaxException {
        ServiceReference[] refs = getContext().getServiceReferences(clazz.getName(), filter);

        if(refs == null) {
            throw new NullPointerException(
                    "Can't get service reference for " + clazz.getName());
        }

        ServiceReference chosenRef = pickServiceReference(refs);

        Object service = getContext().getService(chosenRef);

        if(service == null) {
            throw new NullPointerException(
                    "Can't get service for " + clazz.getName());
        }

        /* Save the service and its reference */
        fetchedServices.put(service, chosenRef);

        return service;
    }
    


    public void ungetService(Object service) {
        ServiceReference ref = (ServiceReference) fetchedServices.get(service);

        getContext().ungetService(ref);
        fetchedServices.remove(service);
    }
    
    public void ungetAllServices() {
        Enumeration e = fetchedServices.keys();

        while(e.hasMoreElements()) {
            Object service = e.nextElement();
            ungetService(service);
        }
    }

    /*** Available methods ***/
    
    /**
     * Silently returns <code>true</code> if there is a service registered
     * under the supplied classname, logs and returns <code>false</code> 
     * otherwise.
     */
    public boolean serviceAvailable(Class clazz) {
        boolean result = false;
        
        try {
            Object service = getService(clazz);
            ungetService(service);
            result = true;
        }
        catch(NullPointerException e) {
            logger.log(clazz.getName() + " not available");
            result = false;
        }
        
        return result;
    }

    /**
     * Checks if the need and the availability of a security manager 
     * matches. Returns <code>true</code> if the need an availability is 
     * a match; returns <code>false</code> and logs otherwise.
     */
    public boolean securityNeeded(boolean needed) {
        boolean result = false;
        boolean available = (System.getSecurityManager() != null);
        
        if(needed == available) {
            result = true;
        }
        else {
            logger.log("Needed security manager: " + needed 
                       + " , got security manager: " + available);
            result = false;
        }
        
        return result;
    }


    /*** Service registration methods ***/

    public void registerService(String clazz, Object service,
            Dictionary properties) throws Exception {
        registerService(new String[] { clazz }, service, properties);
    }

    public void registerService(String clazz, Object service,
            Dictionary properties, BundleContext bc) throws Exception {
        registerService(new String[] { clazz }, service, properties, bc);
    }

    public void registerService(String[] clazz, Object service,
            Dictionary properties) throws Exception {
        registerService(clazz, service, properties, getContext());
    }

    public void registerService(String[] clazz, Object service,
        Dictionary properties, BundleContext bc) throws Exception {

        ServiceRegistration sr = bc.registerService(
                clazz, service, properties);

        registeredServices.put(service, sr);            
    }
        

    public void unregisterService(Object service) {
        ServiceRegistration sr = 
            (ServiceRegistration) registeredServices.get(service);

        sr.unregister();
        registeredServices.remove(service);
    }

    public void unregisterAllServices() {
        Enumeration e = registeredServices.keys();

        while(e.hasMoreElements()) {
            Object service = e.nextElement();
            unregisterService(service);
        }
    }
    
    public void cleanAll() throws Exception {
        /* Clean up all install bundles, gotten services and
           registered services. */
        ungetAllServices();
        unregisterAllServices();
        uninstallAllBundles();
    }
    

    
    /**
     * Picks a service from the given array of references. The rules
     * the service is picked by are the same as the rules defined in
     * BundleContext.getServiceReference() (highest ranking, lowest
     * service ID if the ranking is a tie)
     */
    private ServiceReference pickServiceReference(ServiceReference[] refs) {
        ServiceReference highest = refs[0];
        
        for(int i = 1; i < refs.length; i++) {
            ServiceReference challenger = refs[i];
            
            if(ranking(highest) < ranking(challenger)) {
                highest = challenger;
            }
            else if(ranking(highest) == ranking(challenger)) {
                if(serviceid(highest) > serviceid(challenger)) {
                    highest = challenger;
                }
            }
        }
        
        return highest;
    }
    
    /**
     * Get service ranking from a service reference.
     *
     * @param s The service reference
     * @return Ranking value of service, default value is zero
     */
    private int ranking(ServiceReference s) {
	    Object v = s.getProperty(Constants.SERVICE_RANKING);
	    
	    if (v != null && v instanceof Integer) {
	        return ((Integer)v).intValue();
	    } else {
	        return 0;
	    }
    }
    
    private long serviceid(ServiceReference s) {
	    Long sid = (Long) s.getProperty(Constants.SERVICE_RANKING);

	    return sid.longValue();
    }
    
}
