package org.osgi.dmt.residential2;

import java.util.*;

import org.osgi.dmt.ddf.*;
import org.osgi.dmt.service.log.*;

public interface OSGi {
	Opt<Log>	Log();
	
	
	MAP<Long,DeploymentUnit> DeploymentUnit();
	MAP<Long,ExecutionUnit> ExecutionUnit();
	MAP<Long,BundleState>	BundleState();
	
	MAP<String,>
	
}
