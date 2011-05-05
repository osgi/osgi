package org.osgi.impl.service.dmt.dispatcher;


import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class Dispatcher extends ServiceTracker {

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
			Plugin p = null;
			Collection<String> dataRootURIs = Util.toCollection(ref.getProperty(DataPlugin.DATA_ROOT_URIS));
			Collection<String> execRootURIs = Util.toCollection(ref.getProperty(ExecPlugin.EXEC_ROOT_URIS));
			if ( dataRootURIs != null ) {
				p = new Plugin( ref, dataPluginRoot, eaTracker, context );
				p.init( dataRootURIs, idManager);
//				System.out.println( ">>>>>>>>>> data plugin added: " + p.getOwns());
//				dumpSegments(dataPluginRoot);

				// SD: according to bug-discussion 1898, there must be no mapping of "pending plugins" 
				// if a plugin failed to be mapped at registration time it must be ignored (until it might 
				// be un-registered/registered again)
//				if ( p!= null && p.getOwns() != null )
//					mapPotentialDependingPlugins(ref, true);
			}
			if ( execRootURIs != null ) {
				p = new Plugin( ref, execPluginRoot, eaTracker, context );
				p.init(execRootURIs, idManager);
//				System.out.println( ">>>>>>>>>> exec plugin added: " + p.getOwns());
//				dumpSegments(execPluginRoot);

//				if ( p!= null && p.getOwns() != null )
//					mapPotentialDependingPlugins(ref, false);
			}
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
//	/**
//	 * checks whether there are un-mapped plugins that can be mapped to any of the newly mapped mountPoints
//	 * @param ref
//	 */
//	private synchronized void mapPotentialDependingPlugins( ServiceReference ref, boolean forDataPlugin) throws InvalidSyntaxException {
//		Collection<String> mountPoints = Util.toCollection(ref.getProperty(MountPlugin.MOUNT_POINTS));
//		Collection<String> dataRootUris = Util.toCollection(ref.getProperty(DataPlugin.DATA_ROOT_URIS));
//		Collection<String> execRootUris = Util.toCollection(ref.getProperty(ExecPlugin.EXEC_ROOT_URIS));
//		if ( mountPoints == null || mountPoints.size() == 0 )
//			return;
//
//		List<ServiceReference> mappedRefs = null;
//		String filterPrefix = null;
//		if ( forDataPlugin ) {
//			mappedRefs = getMappedPluginReferences(dataPluginRoot);
//			filterPrefix = DataPlugin.DATA_ROOT_URIS + "=" + dataRootUris.iterator().next() + "/";
//		}
//		else {
//			mappedRefs = getMappedPluginReferences(execPluginRoot);
//			filterPrefix = ExecPlugin.EXEC_ROOT_URIS + "=" + execRootUris.iterator().next() + "/";
//		}
//		
//		for (String mountPoint : mountPoints) {
//			// find plugins that are registered for this dataRootUri
//			String filter = "(" + filterPrefix + mountPoint + ")";
//			ServiceReference[] refs = context.getServiceReferences(DataPlugin.class.getName(), filter);
//
//			for (int i = 0; refs != null && i < refs.length; i++) {
//				// is this plugin mapped already? (should not, because mountpoint just appeared)
//				ServiceReference pluginRef = refs[i];
//				if ( ! mappedRefs.contains( pluginRef )) {
//					// invoke addingService for this ref (starting recursion)
//					addingService(pluginRef);
//				}
//			}
//			
//		}
//	}

	public void removedService(ServiceReference ref, Object service) {
		try {
			// close/unmap this plugin and all dependend plugins, i.e. all plugins which's MountingPlugin was unregistered
			// also invokes the callback on the MountPlugin interface, if possible
			boolean isDataPlugin = ref.getProperty(DataPlugin.DATA_ROOT_URIS) != null ;
			boolean isExecPlugin = ref.getProperty(ExecPlugin.EXEC_ROOT_URIS) != null;

			String type = "dataPlugin";
			if ( isExecPlugin )
				type+= "/execPlugin";
//			System.out.println( ">>>>>>>>>> " + type + " removed: " + ((Plugin)service).getOwns());
//			dumpSegments(root);
			((Plugin) service).close();
			
//			System.out.println( "dump dataPlugins: ");
//			dumpSegments(dataPluginRoot);
//			System.out.println( "dump execPlugins: ");
//			dumpSegments(execPluginRoot);
				
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private List<ServiceReference> getMappedPluginReferences( Segment segment ) {
		List<ServiceReference> mappedRefs = new ArrayList<ServiceReference>();
		if ( segment.getPlugin() != null ) 
			mappedRefs.add( segment.getPlugin().getReference() );
		for (Segment child : segment.getChildren() ) {
			mappedRefs.addAll( getMappedPluginReferences( child ) );
		}
		return mappedRefs;
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
