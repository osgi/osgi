package org.osgi.impl.service.dmt.dispatcher;

import info.dmtree.spi.ExecPlugin;

public interface Dispatcher {

	public Plugin getDataPlugin(Node node);

	public ExecPlugin getExecPlugin(Node node);

	public boolean handledBySameDataPlugin(Node node, Node newNode);

}