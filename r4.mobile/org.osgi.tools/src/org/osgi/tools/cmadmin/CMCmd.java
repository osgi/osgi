package org.osgi.tools.cmadmin;

import java.util.*;

import org.osgi.framework.*;
import org.osgi.service.cm.*;
import org.osgi.tools.command.*;
import org.osgi.util.tracker.ServiceTracker;


public class CMCmd implements BundleActivator, CommandProvider {
    BundleContext       	context;
    ServiceRegistration 	registration;          // Ourselves
	ServiceTracker			tracker;
	/*
	 * @see BundleActivator#start(BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		this.context = context;
		tracker = new ServiceTracker(context, ConfigurationAdmin.class.getName(), null );
		tracker.open();
		registration = context.registerService( CommandProvider.class.getName(), this, null );

	}

	/*
	 * @see BundleActivator#stop(BundleContext)
	 */
	public void stop(BundleContext arg0) throws Exception {
	}



    /**
	Return a list with our commands.
	 */
    public String getHelp() {
        return
                "CM\r\n"
        + 		"list configs [ <filter> ]\r\n"
        + 		"list factories\r\n"
        + 		"create factory <factory pid> [<loc>]\r\n"
        + 		"get config <pid> [ <loc> ]\r\n"
        + 		"update config <pid> ( <key>=<value> )*\r\n"
        + 		"delete config <pid>\r\n";
    }


	/**
	List all the configuration objects.
	 */
	public Object _listconfigs( CommandInterpreter intp ) throws Exception {
		Object returnValue = null;
		
		Vector result = new Vector();
		Configuration[] configs = getConfigurations(intp);
		
		if(configs != null) {
			for(int i = 0; i < configs.length; i++) {
				result.addElement(configToString(configs[i]));
			}
			returnValue = result;
		}
		else {
			returnValue = "No configurations";
		}
		
		return returnValue;
	}
	
	/**
	 * List all the factories.
	 */
	public Object _listfactories( CommandInterpreter intp ) throws Exception {
		ServiceReference refs[] = context.getServiceReferences(
			ManagedServiceFactory.class.getName(), null );
		Vector v = new Vector();
		for ( int i=0; refs != null && i <refs.length; i++ ) {
			v.addElement( refs[i].getProperty(Constants.SERVICE_PID ));
		}
		return v;
	}
	
	/**
	Create a new configuration object.
	 */
    public Object _getconfig( CommandInterpreter intp ) throws Exception {
    	String pid = intp.nextArgument();
    	String loc = intp.nextArgument();
		return getCM().getConfiguration(pid,loc);
   }

    /**
	Create a new configuration object.
	 */
    public Object _createfactory( CommandInterpreter intp ) throws Exception {
        Configuration config = null;

    	String pid = intp.nextArgument();
    	String loc = intp.nextArgument();
    	if ( loc != null )
	        config = getCM().createFactoryConfiguration(pid,loc);
	    else
	        config = getCM().createFactoryConfiguration(pid);
	        
        return config.getPid();	        
    }

    /**
	Update a configuration object.
	 */
    public Object _updateconfig( CommandInterpreter intp ) throws Exception {
        String pid = intp.nextArgument();
        
        Configuration configuration = getCM().getConfiguration(pid,null);
        Hashtable values = getHashtable(intp);
		configuration.update(values);
        return configToString(configuration);
        // return configuration.getPid() + " : " + configuration.getProperties();
    }

    /**
	Delete a configuration object.
	 */
    public Object _deleteconfig( CommandInterpreter intp ) throws Exception {
		String pid = intp.nextArgument();
		try {

	        Configuration configuration = getCM().getConfiguration(pid);
	        if ( configuration != null )
	        	configuration.delete();
	        else
	        	throw new RuntimeException( "No such configuration " + pid );
		}
		catch( Exception e ) {
			e.printStackTrace();
			return pid;
		}
		return null;
    }


    /**
	Get a list of configurations, optionally a filter.
	 */
    Configuration [] getConfigurations( CommandInterpreter intp ) throws Exception {
        return (Configuration[]) getCM().listConfigurations( intp.nextArgument() );
    }

     /**
		Help method to collect a hashtable from the console.
		 */
    Hashtable getHashtable( CommandInterpreter intp ) {
        String key;
        Hashtable ht = new Hashtable();
        while ( (key=intp.nextArgument()) != null )
        {
            String value = intp.nextArgument();
            if ( value != null
            && "=".equals( value ) )
                value = intp.nextArgument();

            if ( value != null )
            {
                if ( value.equals( "[" ) )
                {
                    Vector  values = new Vector();
                    value = intp.nextArgument();
                    while ( value != null && ! value.equals( "]" ) )
                    {
                        values.addElement( value );
                        value = intp.nextArgument();
                        if ( value.equals( "," ) )
                            value = intp.nextArgument();
                    }
                    String [] result = new String[ values.size() ];
                    values.copyInto( result );
                    ht.put( key, values );
                }
                else
                    ht.put( key, value );
            }
            else
                System.out.println( "Orphan " + key );
        }
        return ht;
    }

    public String configToString(Configuration config) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(config.getPid() + "\r\n");
        Dictionary props = config.getProperties();
        
        Enumeration keys = props.keys();
        while(keys.hasMoreElements()) {
            Object key = keys.nextElement();
            buffer.append("    " + key + "=" + props.get(key) + "\r\n");
        }
        
        return buffer.toString();  
    }
    


    public Object toString( Object o ) {
    	if ( o instanceof Configuration ) {
    		Configuration c = (Configuration) o;
    		return c.getPid();
    	}
        return o;
    }

    ConfigurationAdmin getCM() {
    	ConfigurationAdmin admin = (ConfigurationAdmin) tracker.getService();
    	if ( admin == null )
    		throw new RuntimeException( "No cm available at the moment" );

    	return admin;
    }

}

