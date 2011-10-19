package org.osgi.impl.service.dmt.dispatcher;


import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class Dispatcher extends ServiceTracker {

	private static final int DATA_PLUGINS = 0;
	private static final int EXEC_PLUGINS = 1;
	
	private static ServiceTracker eaTracker;

	Segment dataPluginRoot = new Segment();
	Segment execPluginRoot = new Segment();
	IDManager idManager;
	

	public Dispatcher(BundleContext context, Filter filter) {
		super(context, filter, null);
		idManager = new IDManager(context);
		eaTracker = new ServiceTracker(context, EventAdmin.class.getName(), null );
		eaTracker.open();
	}

	public Object addingService(ServiceReference ref) {
		try {
			mapPlugin(ref, dataPluginRoot, Util.toCollection(ref.getProperty(DataPlugin.DATA_ROOT_URIS)), DATA_PLUGINS);
			mapPlugin(ref, execPluginRoot, Util.toCollection(ref.getProperty(ExecPlugin.EXEC_ROOT_URIS)), EXEC_PLUGINS);
			
			// can't return Plugin, because there could be two instances (exec and data plugin)
			return context.getService(ref);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * handles the unregistration of a plugin
	 * The handler closes the mapping of the unregistered plugin and all dependend plugins, that are currently mapped to 
	 * one of its mountpoints.
	 */
	public void removedService(ServiceReference ref, Object service) {
		try {
			unmapPlugin(ref, dataPluginRoot, ref.getProperty(DataPlugin.DATA_ROOT_URIS), DATA_PLUGINS);
			unmapPlugin(ref, execPluginRoot, ref.getProperty(ExecPlugin.EXEC_ROOT_URIS), EXEC_PLUGINS);
				
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		context.ungetService(ref);
	}

	/**
	 * handles modification of a plugins registration properties
	 */
	public void modifiedService(ServiceReference ref, Object service) {
		try {
			// remove plugin from mapping 
			unmapPlugin(ref, dataPluginRoot, ref.getProperty(DataPlugin.DATA_ROOT_URIS), DATA_PLUGINS);
			unmapPlugin(ref, execPluginRoot, ref.getProperty(ExecPlugin.EXEC_ROOT_URIS), EXEC_PLUGINS);

			// add plugin from mapping
			mapPlugin(ref, dataPluginRoot, Util.toCollection(ref.getProperty(DataPlugin.DATA_ROOT_URIS)), DATA_PLUGINS);
			mapPlugin(ref, execPluginRoot, Util.toCollection(ref.getProperty(ExecPlugin.EXEC_ROOT_URIS)), EXEC_PLUGINS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.ungetService(ref);
	}

	
	private Plugin mapPlugin(ServiceReference ref, Segment rootSegment,
			Collection<String> rootUris, int pluginType) throws Exception {
		if ( rootUris == null )
			return null;
		Plugin p = new Plugin( ref, rootSegment, eaTracker, context );
		if ( p.init(rootUris)) {
	
			// map each uri individually
			for (String rootUri : rootUris)
				p.mapUri(rootUri,idManager);
	
			// map potential child plugins, if this plugin has mountPoints
			if (p.getMountPoints().size() > 0 )
				for ( String mpUri : p.getMountPoints() )
					mapPendingPlugin(mpUri, pluginType);
		}
		
		System.out.println( ">>>>>>>>>> plugin added: " + p.getOwns());
		dumpSegments(rootSegment);

		return p;
	}
	
	
	/**
	 * Finds and maps registered plugins that are waiting for the given uri to become free.
	 * If more than one plugin is waiting for this uri then the one with highest service ranking is mapped.
	 * 
	 * @param freeUri
	 * @param pluginType ... either DATA_PLUGIN or EXEC_PLUGIN
	 */
	private void mapPendingPlugin( String freeUri, int pluginType ) {

		// find plugin registrations for the given uri (highest ranking wins)
		String uriType = (pluginType == DATA_PLUGINS) ? DataPlugin.DATA_ROOT_URIS : ExecPlugin.EXEC_ROOT_URIS;
		String filter = "(" + uriType + "=" + freeUri + ")";
		
		Segment rootSegment = (pluginType == DATA_PLUGINS) ? dataPluginRoot : execPluginRoot;
		String clazz = (pluginType == DATA_PLUGINS) ? DataPlugin.class.getName() : ExecPlugin.class.getName();
		try {
			ServiceReference[] refs = context.getServiceReferences(clazz, filter);
			if ( refs == null)
				return;
			
			Plugin p = findMappedPlugin(rootSegment, refs[0]);
			if ( p == null ) {
				// initialize new plugin
				p = new Plugin( refs[0], rootSegment, eaTracker, context );
				p.init( Util.toCollection(refs[0].getProperty(uriType)));
			} 
			// add this uri to the mapping of the plugin
			p.mapUri(freeUri, idManager);
//			System.out.println( "mapped pending plugin " + p + " to uri: " + freeUri);

		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private synchronized void unmapPlugin( ServiceReference ref, Segment segmentRoot, Object uriProperty, int pluginType ) throws InterruptedException {
		Collection<String> uris = Util.toCollection(uriProperty);
		if ( uris == null )
			return;
		
		Plugin p = findMappedPlugin(segmentRoot, ref);
		System.out.println( ">>>>>>>>>> Plugin removed: " + p.getOwns());
		p.close();
		// map pending plugins to the freed uris
		for (String uri: uris)
			mapPendingPlugin(uri, pluginType);
		dumpSegments(segmentRoot);
	}
	
	
	private Plugin findMappedPlugin( Segment startSegment, ServiceReference ref ) {
		if ( startSegment.getPlugin() != null && ref.equals(startSegment.getPlugin().getReference() ))
			return startSegment.getPlugin();
		Plugin p = null;
		for (Segment child : startSegment.getChildren() ) {
			p = findMappedPlugin( child, ref );
			if ( p!= null )
				return p;
		}
		return p;
	}
	
	public Segment findSegment( String[] path ) {
		return dataPluginRoot.getSegmentFor(path, 1, false);
	}

	public Plugin getDataPluginFor( String[] path ) {
		return dataPluginRoot.getPluginFor(path, 1);
	}
	
	public Plugin getExecPluginFor( String[] path ) {
		return execPluginRoot.getPluginFor(path, 1);
	}
	
	private void dumpSegments( Segment start ) {
		System.out.println( start.getUri() );
		for (Segment child : start.getChildren()) {
			dumpSegments(child);
		}
	}

}
