package org.osgi.impl.service.dmt.dispatcher;


import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;
import info.dmtree.spi.MountPlugin;

import java.security.Provider.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
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
			Collection<String> dataRootURIs = toCollection(ref.getProperty(DataPlugin.DATA_ROOT_URIS));
			Collection<String> execRootURIs = toCollection(ref.getProperty(ExecPlugin.EXEC_ROOT_URIS));
			if ( dataRootURIs != null ) {
				p = new Plugin( ref, dataPluginRoot, eaTracker, context );
				p.init( dataRootURIs, idManager);
				System.out.println( ">>>>>>>>>> plugin added: " + p.getOwns());
				dumpSegments(dataPluginRoot);
			}
			if ( execRootURIs != null ) {
				p = new Plugin( ref, execPluginRoot, eaTracker, context );
				p.init(execRootURIs, idManager);
			}
			if ( p!= null && p.getOwns() != null )
				mapPotentialDependingDataPlugins(ref);
			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * checks whether there are un-mapped plugins that can be mapped to any of the newly mapped mountPoints
	 * @param ref
	 */
	private synchronized void mapPotentialDependingDataPlugins( ServiceReference ref ) throws InvalidSyntaxException {
		Collection<String> mountPoints = toCollection(ref.getProperty(MountPlugin.MOUNT_POINTS));
		if ( mountPoints == null || mountPoints.size() == 0 )
			return;
		List<ServiceReference> mappedRefs = getMappedPluginReferences(dataPluginRoot);
		
		String dataRootUri = ((String[]) ref.getProperty(DataPlugin.DATA_ROOT_URIS))[0];
		for (String mountPoint : mountPoints) {
			// find plugins that are registered for this dataRootUri
			String filter = "(" + DataPlugin.DATA_ROOT_URIS + "=" + dataRootUri + "/" + mountPoint + ")";
			ServiceReference[] refs = context.getServiceReferences(DataPlugin.class.getName(), filter);

			for (int i = 0; refs != null && i < refs.length; i++) {
				// is this plugin mapped already? (should not, because mountpoint just appeared)
				ServiceReference pluginRef = refs[i];
				if ( ! mappedRefs.contains( pluginRef )) {
					// invoke addingService for this ref (starting recursion)
					addingService(pluginRef);
				}
			}
			
		}
	}

	public void removedService(ServiceReference ref, Object service) {
		try {
			// close/unmap this plugin and all dependend plugins, i.e. all plugins which's MountingPlugin was unregistered
			// also invokes the callback on the MountPlugin interface, if possible
			System.out.println( ">>>>>>>>>> plugin removed: " + ((Plugin)service).getOwns());
			((Plugin) service).close();
			dumpSegments(dataPluginRoot);
				
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private Collection<String> toCollection(Object property) {
		if (property instanceof String)
			return Arrays.asList((String) property);

		if (property instanceof String[])
			return Arrays.asList((String[]) property);

		return null;
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
