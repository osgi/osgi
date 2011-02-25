package org.osgi.impl.service.dmt.dispatcher.nxt;

import info.dmtree.spi.DataPlugin;
import info.dmtree.spi.ExecPlugin;

import org.osgi.impl.service.dmt.dispatcher.Node;
import org.osgi.impl.service.dmt.dispatcher.Plugin;

public class PluginInfo implements Plugin {

	
	
	public DataPlugin getDataPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	public Node[] getDataRoots() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExecPlugin getExecPlugin() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean handlesData(Node subtreeUri) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean handlesExec(Node subtreeUri) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRegistered() {
		// TODO Auto-generated method stub
		return false;
	}

}
