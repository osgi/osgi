package org.osgi.impl.service.dmt.dispatcher;


import info.dmtree.MountPlugin;
import info.dmtree.MountPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

public class Dispatcher extends ServiceTracker {

	Segment dataPluginRoot = new Segment();
	Segment execPluginRoot = new Segment();
	IDManager idManager;
	ServiceTracker eaTracker;
	

	public Dispatcher(BundleContext context, Filter filter) {
		super(context, filter, null);
		idManager = new IDManager(context);
		eaTracker = new ServiceTracker(context, EventAdmin.class.getName(), null );
		eaTracker.open();
	}


	public Object addingService(ServiceReference ref) {
		try {
			Plugin p = null;
			Collection<String> dataRootURIs = toCollection(ref.getProperty("dataRootURIs"));
			Collection<String> execRootURIs = toCollection(ref.getProperty("execRootURIs"));
			if ( dataRootURIs != null ) {
				p = new Plugin( ref, dataPluginRoot );
				p.init( dataRootURIs, idManager);
			}
			if ( execRootURIs != null ) {
				p = new Plugin( ref, execPluginRoot );
				p.init(execRootURIs, idManager);
			}
			if ( p != null ) {
				// is it a MountPlugin ? then invoke callback
				invokeMountPointsCallback( getMountPlugin(p), p ) ;
			}

			return p;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void removedService(ServiceReference ref, Object service) {
		try {
			((Plugin) service).close();
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

	private MountPlugin getMountPlugin( Plugin p ) {
		MountPlugin mp = null;
		try {
			mp =  (MountPlugin) context.getService(p.reference);
		} catch (ClassCastException e) {}
		
		return mp;
	}
	
	private void invokeMountPointsCallback( MountPlugin mp, Plugin p ) {
		if ( mp == null || p == null ) return;
		List<MountPoint> mps = new ArrayList<MountPoint>();
		for (Iterator i = p.getOwns().iterator(); i.hasNext();) {
			Segment s = (Segment) i.next();
			mps.add( new MountPointImpl(s.getPath(), eaTracker ));
		}
		mp.setMountPoints( mps.toArray( new MountPoint[p.getOwns().size()]) );
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

}
