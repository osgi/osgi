package org.osgi.impl.service.tr069todmt;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 */
public class MappingTable extends Hashtable<String, Long> {

  private static final long serialVersionUID = 1L;
  
  @Override
  public synchronized Long remove(Object key) {
    /*remove the whole subtree*/
    String nodeToRemove = (String)key;
    Enumeration<String> nodes = keys();
    while (nodes.hasMoreElements()) {
      String node = nodes.nextElement();
      if (node.startsWith(nodeToRemove)) {
        super.remove(node);
      }
      
    }
    return super.remove(key);
  }
  
  void rename(String oldKey, String newKey) {
    Enumeration<String> nodes = keys();
    while (nodes.hasMoreElements()) {
      String node = nodes.nextElement();
      if (node.startsWith(oldKey)) {
        super.put(node.replace(oldKey, newKey), super.remove(node));
      }
    }
  }
}
