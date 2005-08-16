
package org.osgi.application;

/**
 * Using this class, OSGi-aware applications can obtain their {@link ApplicationContext} 
 *
 */
public final class Framework {

    private Framework() { }
    
    /**
     * This method needs an argument, an object that represents the application instance. 
     * An application consists of a set of object, however there is a single object, which 
     * is used by the corresponding application container to manage the lifecycle on the 
     * application instance. The lifetime of this object equals the lifetime of 
     * the application instance; therefore, it is suitable to represent the instance. 
     * <P>
     * The returned {@link ApplicationContext} object is singleton for the 
     * specified application instance. Subsequent calls to this method with the same 
     * application instance must return the same context object
     * 
     * @param applicationInstance is the representative object of an application instance
     * @throws java.lang.IllegalArgumentException if  called with an object that is not 
     *     the representative object of an application.
     * @return the {@link ApplicationContext} of the specified application instance.
     */
    public static ApplicationContext getMyApplicationContext(Object applicationInstance) {
        return null;        
    }

}
