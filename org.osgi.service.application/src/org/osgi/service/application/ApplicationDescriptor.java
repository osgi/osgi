
package org.osgi.service.application;

import java.util.Map;


/**
 * Descriptor of an application. This is the Application representer in the service registry. 
 * The application descriptor will be passed to an application container for instance creation.
 * @modelguid {8679CDDA-3F17-4CE1-B0B2-2371B5876B1D}
 */

public interface ApplicationDescriptor {

    /**
     * Returns the display name of application
     * @modelguid {ACF2BBC9-FABB-440C-8472-8B9BC7E1C7C0}
     */
    public String getName();

    /**
     * Get the unique identifier of the descriptor. 
     * @modelguid {4A78F411-6CF9-45C6-8F6A-44ED8C8F8B7E}
     */
    public String getUniqueID();
    
    /**
     * Returns the version descriptor of the service.
     * @modelguid {983710D5-35AF-4B8B-BEFC-17A4A69D2853}
     */
    public String getVersion ();
    
    /**
     * Get the application container type. The following container types are supported by default
     * <ul>
     * <li>MEG (?)
     * <li>Midlet
     * <li>Doja
     * </ul>
     * @modelguid {8B64608E-99FD-4751-B66C-24EE76B486C4}
     */
    public String getContainerID();

    /**
     * Return the category of the application. 
     * <p>
     * The following list of application types are predefined:
     * <ul>
     * <li>APPTYPE_GAMES
     * <li>APPTYPE_MESSAGING
     * <li>APPTYPE_OFFICETOOLS
     * </ul>
     * @modelguid {8EFA6D51-5C6C-470D-BA82-6CC0209AC147}
     */
    public String getCategory();
   
	/** @modelguid {DA01FC37-C5F5-45F6-ADDA-AA07AD31CDCA} */
    public Map getProperties (String locale);
    
	/** @modelguid {D35447AE-4B9A-4EC3-B0E7-C001DFDB840E} */
    public Map getContainerProperties (String locale);

  
}
