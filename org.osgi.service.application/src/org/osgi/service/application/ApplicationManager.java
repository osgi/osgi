
package org.osgi.service.application;

import java.io.IOException;
import java.util.*;
import org.osgi.framework.BundleException;

// import org.osgi.service.content.*;


/** @modelguid {B6B44DD8-374F-4796-AC9A-1220A68C585A} */
public interface ApplicationManager {

    /**
     * Get the descriptors of all the installed applications. 
     * @modelguid {8CD89DB5-61CD-4DC3-A779-220571854AD7}
     */
    public ApplicationDescriptor[] getAppDescriptors();
    
    /**
     * Get an application descriptor with the application UID
     *
     * @param appUID Application UID
     * @exception Exception Application with the specified ID cannot be found
     * @modelguid {835B8794-2D98-4308-8201-98B55772B7A9}
     */
    public ApplicationDescriptor getAppDescriptor(String appUID) throws Exception;
    
    
    /**
     * Launches a new instance of an application.
     * <p>
     * The following steps should be performedif the represented application is
     * a Meglet:
     * <p>
     * 1. If the application is a singleton and already has a running instance
     * then throws SingletonException.
     * <p>
     * 2. If the bundle of the application is not ACTIVE then it starts that
     * bundle. If the bundle has dependencies then the underlying bundles should
     * also be started. This step may throw BundleException.
     * <p>
     * 3. Creates a MegletContext, but the MegletContext.getApplicationObject()
     * will return null at this moment
     * <p>
     * 4. Then creates a new instance of the developer implemented application
     * (Meglet) using its parameterless constructor.
     * <p>
     * 5. Calls Meglet.startApplication() and passes the MegletContext and the
     * arguments to it
     * <p>
     * 6. If the startApplication() succeeds then it makes the ApplicationObject
     * available on MegletContext.getApplicationObject() then registeres the
     * ApplicationObject
     * <p>
     * 7. Returns the registered ApplicationObject
     * <p>
     * 
     * @param args Arguments for the newly launched application
     * @return The registered ApplicaitonObject which represents the newly
     * launched applicaiton instance
     * @throws SingletonException if the call attempts to launch a second 
     * instance of a singleton applicaiton
     * @throws BundleException if starting the bundle(s) failed
     * @modelguid {1E455A11-4289-4E0E-A3D3-B65B291E6FE3}
     */
    
    public ApplicationHandle launchApplication(ApplicationDescriptor appDescriptor, Map args) throws SingletonException, Exception;
    

    /**
     * Add an application to be scheduled at a specified time. 
     *
     * @param appUID                             the unique identifier of the application to be launched
     * @param arguments                          the arguments to launch the application
     * @param date                               the time to launch the application
     * @return                                   the id for this scheduled launch or -1 if the request is ignored
     * @exception IOException                    if cannot access scheduled application list
     * @exception ApplicationNotFoundException   if the appUID is not found
     *
     * @modelguid {F91D977E-C639-4802-BA36-2C000BF0DBD9}
     */
    public ScheduledApplication addScheduledApplication(ApplicationDescriptor appDescriptor, Map arguments, Date date);
        

    /**
     * Get a list of scheduled applications.
     *
     * @return                                   an integer array containing the ids of the scheduled launches.
     *                                           Returns an array of size zero if no application is scheduled.
     * @exception IOException                    if cannot access scheduled application list
     *
     * @modelguid {629A6D4A-2288-4BC5-A5C3-4F9F7986D61A}
     */
    public ScheduledApplication[] getScheduledApplications() throws IOException;
    
    /**
     * Sets the lock state of the application or the device.
     * @param appUID UID of the application being locked - if UID is null, entire device can be locked
     * @param lockValue A boolean value true/false indicating whether the 
     * application should be locked or not.
     * @exception ApplicationNotFoundException Application with the specified ID cannot be found
     * @modelguid {A627693E-D67C-45EE-B683-4EA2B93AF982}
     */
    public void lock(ApplicationDescriptor appDescriptor) throws Exception;

    /**
     * Unset the lock of a specified application or all the applications 
     * 
     * @param appUID - UID or null if unlocking all applications on the device
     * @param passcode Passcode (PIN) value that will enable unlocking of the device/application
     * @exception ApplicationNotFoundException Application with the specified ID cannot be found
     * @modelguid {739EAD89-AF58-4C38-9833-B8E86E56C44E}
     */
    public void unLock(ApplicationDescriptor appDescriptor) throws Exception;

    /**
     * Returns a boolean indicating whether this application is locked or not.
     * @param appUID Application UID of the application. 
     * @return A boolean value true/false indicating whether the application is locked or not.
     * @exception ApplicationNotFoundException Application with the specified ID cannot be found
     * @modelguid {54F610F8-B6E8-4B44-A973-A39A177452D4}
     */
    public boolean isLocked(ApplicationDescriptor appDescriptor) throws Exception;


    /**
     * This method gets the content handler service based on
     * file extension (obtained from URL) and calls the service factory to
     * get a ContentHandler object to be used by caller.
     * 
     * @param url URL of the content.
     * @param filter A filter string (OSGi style  filter string)
     * to uniquely identify the ContentHandlerFactory in the service registry.
     * @return ContentHandler object that matches the filter criteria.
     * @modelguid {293A6111-D06F-41F1-B527-9C54397C8EB1}
     */
//     public ContentHandler getContentHandlerByURL(String url, String action);

    /**
     * Get a list of MIME types supported in the system. 
     * @return A list of MIME types supported in the system
     * @modelguid {2583C8CE-854C-451C-BA02-9F37DEAB9A18}
     */
    public String[] getSupportedMimeTypes();
       
}
