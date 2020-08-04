
package org.osgi.impl.service.tr069todmt;

import java.util.Enumeration;
import java.util.Hashtable;
import org.osgi.service.dmt.Uri;

/**
 *
 */
public class MappingTable extends Hashtable<String, Object> {

	private static final long	serialVersionUID	= 1L;

	@Override
	public synchronized Object remove(Object key) {
		/* remove the whole subtree */
		String nodeToRemove = ((String) key).concat(Uri.PATH_SEPARATOR);
		Enumeration<String> nodes = keys();
		while (nodes.hasMoreElements()) {
			String node = nodes.nextElement();
			if (node.startsWith(nodeToRemove) || node.equals(key)) {
				super.remove(node);
			}

		}
		return super.remove(key);
	}

	void rename(String oldKey, String newKey) {
		Enumeration<String> nodes = keys();
		String prefix = oldKey.concat(Uri.PATH_SEPARATOR);
		while (nodes.hasMoreElements()) {
			String node = nodes.nextElement();
			if (node.startsWith(prefix) || node.equals(oldKey)) {
				super.put(node.replaceAll(oldKey, newKey), super.remove(node));
			}
		}
	}
}
