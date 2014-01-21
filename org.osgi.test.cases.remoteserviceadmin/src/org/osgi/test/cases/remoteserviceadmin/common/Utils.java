package org.osgi.test.cases.remoteserviceadmin.common;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.osgi.service.remoteserviceadmin.EndpointDescription;

public class Utils {
	/**
	 * @param scopeobj
	 * @param description
	 * @return
	 * 
	 * @author <a href="mailto:tdiekman@tibco.com">Tim Diekmann</a>
	 */
	public static String isInterested(Object scopeobj,
			EndpointDescription description) {
		if (scopeobj instanceof List<?>) {
			List<String> scope = (List<String>) scopeobj;
			for (Iterator<String> it = scope.iterator(); it.hasNext();) {
				String filter = it.next();

				if (description.matches(filter)) {
					return filter;
				}
			}
		} else if (scopeobj instanceof String[]) {
			String[] scope = (String[]) scopeobj;
			for (String filter : scope) {
				if (description.matches(filter)) {
					return filter;
				}
			}
		} else if (scopeobj instanceof String) {
			StringTokenizer st = new StringTokenizer((String) scopeobj, " ");
			for (; st.hasMoreTokens();) {
				String filter = st.nextToken();
				if (description.matches(filter)) {
					return filter;
				}
			}
		}
		return null;
	}
}
