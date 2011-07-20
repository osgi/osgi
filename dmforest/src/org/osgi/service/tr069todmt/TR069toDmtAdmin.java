package org.osgi.service.tr069todmt;

import info.dmtree.*;

import java.util.*;

public interface TR069toDmtAdmin {
	Map<String,Object> getParameterValues(DmtSession session, Collection<String> parameterNames);
	void setParameterValues(DmtSession session, Map<String,Object> values);
	Collection<ParameterInfo> getParameterNames(DmtSession session, String path, boolean nextLevel);
	long addObject(DmtSession session, String path);
	void deleteObject(DmtSession session, String path);
}
