package org.osgi.impl.service.application;

import org.osgi.service.application.*;
import java.util.Map;

public class ApplicationContextImpl implements ApplicationContext 
{
    private Map args;

    public ApplicationContextImpl( Map args )
    {
      this.args = args;
    }
    
    public Map getLaunchArgs()
    {
      return args;
    }
}
