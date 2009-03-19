package org.osgi.test.cases.blueprint.framework;

import java.util.Dictionary;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.test.cases.blueprint.services.TestUtil;

public class ServiceExistValidator extends MetadataValidator {

    // service Interface Name
    private String serviceInterfaceName;
    
    // the expected Service Properties
    private Dictionary expectedServiceProperties;
    
    public ServiceExistValidator(String serviceInterfaceName, Dictionary expectedServiceProperties) {
        this.serviceInterfaceName = serviceInterfaceName;
        this.expectedServiceProperties = expectedServiceProperties;
    }
    

    
    public void validate(BundleContext testcaseBundleContext) throws Exception {
        // ensure we have everything initialized
        super.validate(testcaseBundleContext);
        
        // the value group must belong to one of the components

        ServiceReference[] refs = testcaseBundleContext.getServiceReferences(this.serviceInterfaceName, null);
        
        assertNotNull("No target service found for:" + this.serviceInterfaceName, refs);
        
        for (int i=0; i<refs.length; i++){
            if (TestUtil.containsAll(this.expectedServiceProperties, refs[i])){
                return;
            }
        }
        
        fail("No service advertised the expected service properties");
               
       
    }
    

    
}
