package org.osgi.test.cases.rmt;

import org.osgi.service.dmt.spi.*;
import org.osgi.test.cases.rmt.util.*;

import aQute.bnd.annotation.component.*;

@Component(properties = DataPlugin.DATA_ROOT_URIS + "=./OSGi/Framework", provide = {
		FrameworkPlugin.class, DataPlugin.class})
public class FrameworkPlugin extends GenericDataPlugin<FrameworkHandler>
		implements DataPlugin {
	
	final FrameworkHandler	root	= new FrameworkHandler();

	public FrameworkPlugin() {
		super(new String[] {".", "OSGi", "Framework"});
	}

	protected FrameworkHandler getRoot(boolean writeable) {
		return root;
	}
}
