package org.osgi.impl.service.dmt.dispatcher;



import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.spi.DataPlugin;
import org.osgi.service.dmt.spi.ExecPlugin;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * This class tracks registration changes (reg/unreg/mod) of 
 * DataPlugins and ExecPlugins and takes care of their mapping. 
 * @author steffen
 *
 */
public class Dispatcher extends ServiceTracker {

	private static final int DATA_PLUGINS = 0;
	private static final int EXEC_PLUGINS = 1;
	
	private static ServiceTracker eaTracker;

	Segment dataPluginRoot = new Segment();
	Segment execPluginRoot = new Segment();
	IDManager idManager;
	
	private Set<MappingListener> mappingListeners;
	

	/**
	 * creates a new Dispatcher
	 * @param context ... the BundleContext
	 * @param filter ... the filter to be used for the plugin tracker
	 */
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

			// add plugin to mapping
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
		if (rootUris.contains(".") && rootSegment.getPlugin() != null )
			return null;
		Collection<String> mps = Util.toCollection(ref.getProperty(DataPlugin.MOUNT_POINTS));
		if ( mps != null ) {
			if (rootUris.size() > 1) {
				System.err.println("mountPoints are not allowed for plugins with more than one dataRootURI "	+ this);
				return null;
			} 
			for (String mp : mps)
				if ( Uri.isAbsoluteUri(mp) || ! Uri.isValidUri(mp))
					return null;
			
			if ( overlapDetected(new Vector<String>(mps)) )
				return null;
		}
	
		Plugin p = findMappedPlugin(rootSegment, ref);
		// try to map, if at least one rootUri of the plugin is still unmapped
		if ( p == null || (p.getOwns().size() < rootUris.size()) ) {
			
			if ( p == null )
				// can fail
				p = new Plugin( ref, rootSegment, eaTracker, context, rootUris );
		
			if ( p != null ) {
				// map each uri individually
				for (String rootUri : rootUris) {
					String mappedUri = p.mapUri(rootUri,idManager);
					if ( mappedUri != null ) {
						// necessary to immediately invalidate sessions
						notifyMappingListeners(mappedUri, ref);
						// check if potential parent plugins wait for a mapping that 
						// can now be satisfied, because MP constraints are fulfilled by this plugin
						checkAndMapMountingPlugin(mappedUri, pluginType);
					}
				}
				// map potential child plugins, if this plugin has mountPoints
				if (p.getMountPoints().size() > 0 )
					for ( String mpUri : p.getMountPoints() )
						checkAndMapMountedPlugin(mpUri, pluginType);
			}
		}
		
		System.out.println( ">>>>>>>>>> plugin added: " + p.getOwns());
		dumpSegments(rootSegment);

		return p;
	}
	
	private boolean overlapDetected(Vector<String> mps) {
		String first = null;
		for (String mp : mps) {
			if ( first != null ) {
				if ( first.startsWith(mp) || mp.startsWith(first))
					return true;
			}
			else
				first = mp;
		}
		if ( ! mps.isEmpty() )
			mps.remove(0);
		if ( mps.size() > 1 )
			return overlapDetected(mps);
		return false;
	}
	
	
	/**
	 * Finds and maps registered plugins that where waiting for the given MountPoint to become valid.
	 * The method checks for registered plugins that have been registered with a rootUri pointing to 
	 * exactly the given mountPoint-uri. 
	 * If more than one plugin is waiting for this uri then the one with highest service ranking is mapped.
	 * 
	 * @param mountPoint
	 * @param pluginType ... either DATA_PLUGIN or EXEC_PLUGIN
	 */
	private void checkAndMapMountedPlugin( String mountPoint, int pluginType ) {

		// find plugin registrations for the given uri (highest ranking wins)
		String uriType = (pluginType == DATA_PLUGINS) ? DataPlugin.DATA_ROOT_URIS : ExecPlugin.EXEC_ROOT_URIS;
		String filter = "(" + uriType + "=" + mountPoint + ")";
		
		Segment rootSegment = (pluginType == DATA_PLUGINS) ? dataPluginRoot : execPluginRoot;
		String clazz = (pluginType == DATA_PLUGINS) ? DataPlugin.class.getName() : ExecPlugin.class.getName();
		try {
			ServiceReference[] refs = context.getServiceReferences(clazz, filter);
			if ( refs == null)
				return;
		
			mapPlugin(refs[0], rootSegment, Util.toCollection(refs[0].getProperty(uriType)), pluginType);
			
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Finds and maps registered plugins that where waiting for the given MountPoint to become valid.
	 * The method checks for registered plugins that have been registered with a rootUri/MountPoint that 
	 * fits the given uri. 
	 * 
	 * @param mountedUri
	 * @param pluginType ... either DATA_PLUGIN or EXEC_PLUGIN
	 */
	private void checkAndMapMountingPlugin( String mountedUri, int pluginType ) {

		// find plugin registrations for the given uri (highest ranking wins)
		String uriType = (pluginType == DATA_PLUGINS) ? DataPlugin.DATA_ROOT_URIS : ExecPlugin.EXEC_ROOT_URIS;
		Segment rootSegment = (pluginType == DATA_PLUGINS) ? dataPluginRoot : execPluginRoot;
		
		// check all possible combinations of rootUri + MountPoint
		Segment s = rootSegment.getSegmentFor(Uri.toPath(mountedUri), 1, false);
		while (s.parent != null && !s.parent.equals(rootSegment)) {
			String rootUri = s.parent.getUri().toString();
			String filterUri = "(" + uriType + "=" + rootUri + ")";
			String filterMP = "(" + DataPlugin.MOUNT_POINTS + "=*)";
			String filter = "(&" + filterUri + filterMP + ")";
			
			String clazz = (pluginType == DATA_PLUGINS) ? DataPlugin.class.getName() : ExecPlugin.class.getName();
			try {
				ServiceReference[] refs = context.getServiceReferences(clazz, filter);
				if (refs != null) { 
					for (ServiceReference ref : refs) {
						Collection<String> mps = Util.toCollection(ref.getProperty(DataPlugin.MOUNT_POINTS));
						for (String mp : mps) {
							// check if rootUri + MP equals given mountPoint
							if ( mountedUri.equals(rootUri + "/" + mp) )
								mapPlugin(ref, rootSegment, Util.toCollection(ref.getProperty(uriType)), pluginType);
						}
					}
				}
			} catch (InvalidSyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			s = s.parent;
		}
	}
	
	private synchronized void unmapPlugin( ServiceReference ref, Segment segmentRoot, Object uriProperty, int pluginType ) throws InterruptedException {
		Collection<String> uris = Util.toCollection(uriProperty);
		if ( uris == null )
			return;
		
		Plugin p = findMappedPlugin(segmentRoot, ref);
		if ( p != null ) {
			// close this plugin and all dependent plugins  
			p.close();
			for (String uri: uris)
				notifyMappingListeners(uri, ref);
//			// map pending plugins to the freed uris
//			for (String uri: uris)
//				checkAndMapBlockedPlugins(uri, pluginType);
		}
		checkAndMapBlockedPlugins(pluginType);
		System.out.println( "unmapped plugin: " + uriProperty );
		dumpSegments(segmentRoot);
	}
	
	/**
	 * Finds and maps registered plugins that have been blocked by the new unmapped plugin.
	 * The method simply checks for all registered plugins that still have unmapped uris. 
	 * 
	 * @param pluginType ... either DATA_PLUGIN or EXEC_PLUGIN
	 */
	private void checkAndMapBlockedPlugins( int pluginType ) {

		// find plugin registrations for the given uri (highest ranking wins)
		String uriType = (pluginType == DATA_PLUGINS) ? DataPlugin.DATA_ROOT_URIS : ExecPlugin.EXEC_ROOT_URIS;
		Segment rootSegment = (pluginType == DATA_PLUGINS) ? dataPluginRoot : execPluginRoot;
		
		// check all possible combinations of rootUri + MountPoint
		String clazz = (pluginType == DATA_PLUGINS) ? DataPlugin.class.getName() : ExecPlugin.class.getName();
		String filter = "(" + uriType + "=*)";
		try {
			ServiceReference[] refs = context.getServiceReferences(clazz, filter);
			if (refs != null) { 
				for (ServiceReference ref : refs) {
					// just try to (re-)map the plugin
					mapPlugin(ref, rootSegment, Util.toCollection(ref.getProperty(uriType)), pluginType);
				}
			}
		} catch (InvalidSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	
	private Set<MappingListener> getMappingListeners() {
		if (mappingListeners == null )
			mappingListeners = new HashSet<MappingListener>();
		return mappingListeners;
	}
	
	public void addMappingListener( MappingListener listener ) {
		getMappingListeners().add(listener);
	}
	
	public void removeMappingListener( MappingListener listener ) {
		getMappingListeners().remove(mappingListeners);
	}
	
	private void notifyMappingListeners( String pluginRoot, ServiceReference ref ) {
		for (MappingListener listener : getMappingListeners()) {
			listener.pluginMappingChanged(pluginRoot, ref);
		}
	}
	
}
