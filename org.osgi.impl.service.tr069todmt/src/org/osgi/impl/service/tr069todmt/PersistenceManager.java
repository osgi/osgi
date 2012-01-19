package org.osgi.impl.service.tr069todmt;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.Uri;
import org.osgi.service.log.LogService;
import org.osgi.service.tr069todmt.TR069Exception;

/**
 *
 */
public class PersistenceManager {
  
  private static final String TEMP_TREE_FILE = "tree.dat";

  private TR069ConnectorFactoryImpl factory;
  private MappingTable mappingTable;
  private MappingTable aliases;
  private SortedSet<String> tree;
  
  /**
   * @param factory 
   */
  public PersistenceManager(TR069ConnectorFactoryImpl factory) {
    this.factory = factory;
    mappingTable = new MappingTable();
    aliases = new MappingTable();
    tree = Collections.synchronizedSortedSet(new TreeSet<String>());
    try {
      load();
    } catch (Exception e) {
      factory.log(LogService.LOG_WARNING, "PersistenceManager cannot load mappings", e);
    }
  }
  
  boolean isNodeUri(DmtSession session, String nodeUri) {
    if (tree.contains(nodeUri)) {
      return true;
    }
    return session.isNodeUri(nodeUri);
  }
  
  void addMapping(String nodeUri, long instanceNumber) {
    mappingTable.put(nodeUri, new Long(instanceNumber));
    String[] nodePath = Uri.toPath(nodeUri);
    String alias = nodePath[nodePath.length - 1];
    nodePath[nodePath.length - 1] = String.valueOf(instanceNumber);
    aliases.put(Uri.toUri(nodePath), alias);
  }
  
  private String getRenamedUri(String oldUri, String newName) {
    String[] nodePath = Uri.toPath(oldUri);
    nodePath[nodePath.length - 1] = newName;
    return Uri.toUri(nodePath);
  }
  
  private void removeMapping(String nodeUri) {
    Object instanceNumber = mappingTable.remove(nodeUri);
    if (instanceNumber != null) {
      aliases.remove(getRenamedUri(nodeUri, instanceNumber.toString()));
    }
  }

  void createInteriorNode(DmtSession session, String nodeUri, int instanceNumber, boolean eager) throws DmtException {
    checkSessionLock(session);
    if (eager) {
      session.createInteriorNode(nodeUri);
      if (tree.contains(nodeUri)) {
        tree.remove(nodeUri);
      }
    } else {
      Long mapping = (Long)mappingTable.get(nodeUri);
      if (mapping != null) {
        nodeUri = nodeUri.substring(0, nodeUri.lastIndexOf(Uri.PATH_SEPARATOR) + 1) + mapping;
      } else if (instanceNumber > -1){
        addMapping(nodeUri, instanceNumber);
      }
      if (!tree.contains(nodeUri)) {
        tree.add(nodeUri);
      }
    }
  }

  String getNodeType(DmtSession session, String nodeUri) throws DmtException {
    if (tree.contains(nodeUri)) { 
      return null;
    }
    return session.getNodeType(nodeUri);
  }
  
  void renameNode(DmtSession session, String nodeUri, String newName) throws DmtException {
    checkSessionLock(session);
    session.renameNode(nodeUri, newName);
    
    String[] nodePath = Uri.toPath(nodeUri);
    nodePath[nodePath.length - 1] = newName;

    String newUri = getRenamedUri(nodeUri, newName);
    
    Object mapping = mappingTable.get(nodeUri);
    if (mapping != null) {
      nodePath[nodePath.length - 1] = mapping.toString();
      String instanceUri = Uri.toUri(nodePath);
      aliases.rename(instanceUri, newUri);
      aliases.put(instanceUri, newName);
    }
    mappingTable.rename(nodeUri, newUri);
    
    String[] nodes = tree.toArray(new String[tree.size()]);
    String oldPath = nodeUri.concat(Uri.PATH_SEPARATOR);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].startsWith(oldPath) || nodes[i].equals(nodeUri)) {
        tree.remove(nodes[i]);
        tree.add(nodes[i].replace(nodeUri, newUri));
      }
    }
  }
  
  void setNodeValue(DmtSession session, String nodeUri, DmtData value) throws DmtException {
    checkSessionLock(session);
    if (!createNode(session, nodeUri, value)) {
      session.setNodeValue(nodeUri, value);
    }
  }

  private boolean createNode(DmtSession session, String nodeUri, DmtData value) throws DmtException {
    if (session.isLeafNode(nodeUri)) {
      return false;
    }
    session.createLeafNode(nodeUri, value);
    tree.remove(nodeUri);
    return true;
  }
  
  void deleteNode(DmtSession session, String nodeUri) throws DmtException {
    checkSessionLock(session);
    if (session.isNodeUri(nodeUri)) {
      session.deleteNode(nodeUri);
    }
    
    /*remove the whole subtree*/
    String[] nodes = tree.toArray(new String[tree.size()]);
    String prefix = nodeUri.concat(Uri.PATH_SEPARATOR);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].startsWith(prefix) || nodes[i].equals(nodeUri)) {
        tree.remove(nodes[i]);
      }
    }
    
    removeMapping(nodeUri);
  }
  
  String getAlias(DmtSession session, String nodeUri) {
    String alias = (String)aliases.get(nodeUri);
    if (alias == null) {
      String aliasUri = nodeUri + Uri.PATH_SEPARATOR_CHAR + Utils.ALIAS;
      if (isNodeUri(session, aliasUri)) {
        try {
          alias = session.getNodeValue(aliasUri).getString();
          aliases.put(nodeUri, alias);
        } catch (DmtException e) {
          factory.log(LogService.LOG_WARNING, null, e);
          return null;
        }
      } else {
        return null;
      }
    }
    return alias;
  }
  
  int getInstanceNumber(DmtSession session, String nodeUri) throws DmtException {
    Long mapping = (Long)mappingTable.get(nodeUri);
    if (mapping == null) {
      String instanceIDUri = nodeUri + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
      if (isNodeUri(session, instanceIDUri)) {
        mapping = session.getNodeValue(instanceIDUri).getLong();
        addMapping(nodeUri, mapping);
      } else {
        return -1;
      }
    }
    return mapping.intValue();
  }
  
  int generateInstanceId(DmtSession session, String parentUri) throws DmtException {
    /*The Connector must ensure that any id chosen is not actually already in use or has been handed out recently*/
    String[] children = getChildNodeNames(session, parentUri, false);
    if (children == null || children.length == 0) {
      return 1;
    }
    Comparator<String> comparator = new Comparator<String>() {

      public int compare(String s1, String s2) {
        try {
          int i1 = Integer.parseInt(s1);
          int i2 = Integer.parseInt(s2);
          if (i1 == i2) {
            return 0;
          }
          return i1 > i2 ? 1 : -1;
        } catch (NumberFormatException e) {
          return -s1.compareTo(s2);
        }
      }
    };
    Arrays.sort(children, 0, children.length, comparator);
    String last = children[children.length - 1];
    int res;
    try {
      res = Integer.parseInt(last);
    } catch (NumberFormatException e) {
      return 1;
    }
    
    if (res == Integer.MAX_VALUE) {
      for (int i = 1, insert; i < Integer.MAX_VALUE; i++) {
        insert = Arrays.binarySearch(children, String.valueOf(i), comparator);
        if (insert < 0) {
          try {
            return Integer.parseInt(children[-insert - 2]) + 1; 
          } catch (NumberFormatException e) {
            return 1;
          }
        }
      }
      throw new TR069Exception("The maximum number of " + parentUri + " children is reached!", TR069Exception.RESOURCES_EXCEEDED);
    } else {
      return ++res;
    }
  }
  
  String[] getChildNodeNames(DmtSession session, String parentUri, boolean aliasedNames) throws DmtException {
    String[] nodes = tree.toArray(new String[tree.size()]);
    ArrayList<String> children = new ArrayList<String>();
    if (session.isNodeUri(parentUri)) {
      children.addAll(Arrays.asList(session.getChildNodeNames(parentUri)));
    }

    /* If an aliased URI is given */
    Object instanceNumber = mappingTable.get(parentUri);
    if (instanceNumber != null) {
      parentUri = getRenamedUri(parentUri, instanceNumber.toString());
    }
    String nodeUriPrefix = parentUri.concat(Uri.PATH_SEPARATOR);
    for (int i = 0; i < nodes.length; i++) {
      if (nodes[i].startsWith(nodeUriPrefix)) {
        String name = nodes[i].substring(nodeUriPrefix.length());
        if (!name.contains(Uri.PATH_SEPARATOR)) {
          if (aliasedNames) {
            String alias = (String)aliases.get(nodes[i]);
            children.add(alias == null ? name : alias);
          } else {
            /*in trees paths are kept with instanceNumbers*/
            children.add(name);
          }
        }
      }
    }
    //TODO to check if nodes duplication is possible
    return children.toArray(new String[children.size()]); 
  }
  
  private void checkSessionLock(DmtSession session) throws TR069Exception {
    /*
     * If a non-atomic session is used then the TR069 Connector must not attempt to lazily create objects and reject any
     * addObject(String) and deleteObject(String) methods
     */

    if (session.getLockType() != DmtSession.LOCK_TYPE_ATOMIC) {
      throw new TR069Exception("Cannot add/delete objects and set parameter values in a non-atomic session");
    }
  }
  
  private void load() throws Exception {
    ObjectInputStream in = new ObjectInputStream(new FileInputStream(
      factory.context.getDataFile(TEMP_TREE_FILE)
    ));
    tree = (TreeSet<String>)in.readObject();
    mappingTable = (MappingTable)in.readObject();
    aliases = (MappingTable)in.readObject();
    in.close();
  }
  
  
  void close() throws Exception {
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
      factory.context.getDataFile(TEMP_TREE_FILE)
    ));
    out.writeObject(tree);
    out.writeObject(mappingTable);
    out.writeObject(aliases);
    out.close();
  }

}
