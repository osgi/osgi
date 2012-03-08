package org.osgi.impl.service.tr069todmt;

import java.util.Arrays;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;

import java.text.ParseException;

import org.osgi.service.dmt.Uri;
import org.osgi.service.dmt.DmtData;
import org.osgi.service.dmt.MetaNode;
import org.osgi.service.dmt.DmtSession;
import org.osgi.service.dmt.DmtException;
import org.osgi.service.dmt.DmtConstants;
import org.osgi.service.log.LogService;

import org.osgi.service.tr069todmt.ParameterInfo;
import org.osgi.service.tr069todmt.ParameterValue;
import org.osgi.service.tr069todmt.TR069Connector;
import org.osgi.service.tr069todmt.TR069Exception;

import org.osgi.impl.service.tr069todmt.encode.Base64;
import org.osgi.impl.service.tr069todmt.encode.HexBinary;

/**
 * 
 *
 */
public class TR069ConnectorImpl implements TR069Connector {
  
  private DmtSession session;
  private TR069ConnectorFactoryImpl factory;
  
  /**
   * @param session
   * @param factory 
   */
  TR069ConnectorImpl(DmtSession session, TR069ConnectorFactoryImpl factory) {
    this.session = session;
    this.factory = factory;
  }

  public void setParameterValue(String parameterPath, String value, int type) throws TR069Exception {
    try {
      checkParameterPath(parameterPath);
      String nodeUri;
      if (parameterPath.endsWith(Utils.ALIAS)) {
        nodeUri = toURI(parameterPath.substring(0, parameterPath.lastIndexOf(Utils.DOT)), true);
        factory.persistenceManager.renameNode(session, nodeUri, escape(value));
      } else {
        nodeUri = toURI(parameterPath, true);
        
        MetaNode metanode = session.getMetaNode(nodeUri);
        if (metanode == null || metanode.can(MetaNode.CMD_REPLACE)) {
          /*If no meta-data is provided for a node, all operations are valid*/
          DmtData dmtValue = convertToDmtData(nodeUri, value, type, session.getMetaNode(nodeUri));
          if (dmtValue != null) {
            factory.persistenceManager.setNodeValue(session, nodeUri, dmtValue);
          }
        } else {
          throw new TR069Exception("Parameter " + parameterPath + " is not writable", TR069Exception.NON_WRITABLE_PARAMETER);
        }
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  private DmtData convertToDmtData(String nodeUri, String value, int tr069Type, MetaNode metanode) throws DmtException {
    DmtData result = null;
    if (metanode == null) {
      switch(tr069Type) {
        case TR069_BOOLEAN: {
          result = convert(value, DmtData.FORMAT_BOOLEAN);
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_BOOLEAN", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        
        case TR069_INT:
        case TR069_UNSIGNED_INT: {
          result = convert(value, new int[] {DmtData.FORMAT_INTEGER, DmtData.FORMAT_LONG, DmtData.FORMAT_FLOAT, DmtData.FORMAT_STRING});
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_INTEGER, FORMAT_LONG, FORMAT_FLOAT and FORMAT_STRING", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        
        case TR069_LONG:
        case TR069_UNSIGNED_LONG: {
          result = convert(value, new int[] {DmtData.FORMAT_LONG, DmtData.FORMAT_FLOAT, DmtData.FORMAT_INTEGER, DmtData.FORMAT_STRING});
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_LONG, FORMAT_FLOAT, FORMAT_INTEGER and FORMAT_STRING", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        
        case TR069_HEXBINARY:
        case TR069_BASE64: {
          result = convert(value, new int[] {DmtData.FORMAT_BASE64, DmtData.FORMAT_BINARY, DmtData.FORMAT_RAW_BINARY});
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_BASE64, FORMAT_BINARY, FORMAT_RAW_BINARY", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        
        case TR069_STRING: {
          /* FORMAT_STRING, FORMAT_BOOLEAN, FORMAT_FLOAT, FORMAT_INTEGER, FORMAT_LONG, FORMAT_XML, LIST */
          result = convert(value, new int[] {DmtData.FORMAT_STRING, DmtData.FORMAT_BOOLEAN, DmtData.FORMAT_FLOAT, DmtData.FORMAT_INTEGER, DmtData.FORMAT_LONG, DmtData.FORMAT_XML});
          if (result == null && value.indexOf(Utils.COMMA) != -1) {
            setChildrenValues(nodeUri, value.split(Utils.COMMA), tr069Type);
            return null;
          }
          if (result == null) {
            throw new TR069Exception(
              "Error converting " + value + " in FORMAT_STRING, FORMAT_BOOLEAN, FORMAT_FLOAT, FORMAT_INTEGER, FORMAT_LONG, FORMAT_XML, LIST", 
              TR069Exception.INVALID_PARAMETER_VALUE
            );
          }
          return result;
        }
          
        case TR069_DATETIME: {
          result = convert(value, new int[] {DmtData.FORMAT_DATE_TIME, DmtData.FORMAT_DATE, DmtData.FORMAT_TIME});
          if (result == null) {
            throw new TR069Exception("Error converting " + value + " in FORMAT_DATE_TIME, FORMAT_DATE and FORMAT_TIME", TR069Exception.INVALID_PARAMETER_VALUE);
          }
          return result;
        }
        default: throw new TR069Exception("Unknown TR069 type: " + tr069Type, TR069Exception.INVALID_PARAMETER_TYPE);
      }
    } else {
      int format = metanode.getFormat();
      //TODO can a node be not only FORMAT_NODE?!?
      if (format == DmtData.FORMAT_NODE) {
        if (value.indexOf(Utils.COMMA) != -1) {
          setChildrenValues(nodeUri, value.split(Utils.COMMA), tr069Type);
          return null;
        } else {
          throw new TR069Exception("Value " + value + " is not a comma-separated list", TR069Exception.INVALID_PARAMETER_VALUE);
        }
      } else {
        int[] formats = getFormats(format, tr069Type);
        result = convert(value, formats);
        if (result == null) {
          return convertToDmtData(nodeUri, value, tr069Type, null);
        }
        return result;
      }
    }
  }
  
  private int[] getFormats(int mask, int tr069Type) {
    int index = 0;
    switch (tr069Type) {
      case TR069_BASE64:
      case TR069_HEXBINARY: {
        /*FORMAT_BASE64, FORMAT_BINARY, FORMAT_RAW_BINARY*/
        int[] result = new int[3];
        index = addFormat(mask, DmtData.FORMAT_BASE64, result, index);
        index = addFormat(mask, DmtData.FORMAT_BINARY, result, index);
        index = addFormat(mask, DmtData.FORMAT_RAW_BINARY, result, index);
        return trim(result, index);
      } case TR069_BOOLEAN: {
        /*FORMAT_BOOLEAN, FORMAT_STRING*/
        int[] result = new int[2];
        index = addFormat(mask, DmtData.FORMAT_BOOLEAN, result, index);
        index = addFormat(mask, DmtData.FORMAT_STRING, result, index);
        return trim(result, index);
      } case TR069_DATETIME: {
        /*FORMAT_DATE_TIME, FORMAT_DATE, FORMAT_TIME*/
        int[] result = new int[3];
        index = addFormat(mask, DmtData.FORMAT_DATE_TIME, result, index);
        index = addFormat(mask, DmtData.FORMAT_DATE, result, index);
        index = addFormat(mask, DmtData.FORMAT_TIME, result, index);
        return trim(result, index);
      } case TR069_INT:
        case TR069_UNSIGNED_INT: {
        /*FORMAT_INTEGER, FORMAT_LONG, FORMAT_FLOAT, FORMAT_STRING*/
        int[] result = new int[4];
        index = addFormat(mask, DmtData.FORMAT_INTEGER, result, index);
        index = addFormat(mask, DmtData.FORMAT_LONG, result, index);
        index = addFormat(mask, DmtData.FORMAT_FLOAT, result, index);
        index = addFormat(mask, DmtData.FORMAT_STRING, result, index);
        return trim(result, index);
      } case TR069_LONG:
        case TR069_UNSIGNED_LONG: {
        /*FORMAT_LONG, FORMAT_FLOAT, FORMAT_INTEGER, FORMAT_STRING*/
        int[] result = new int[4];
        index = addFormat(mask, DmtData.FORMAT_LONG, result, index);
        index = addFormat(mask, DmtData.FORMAT_FLOAT, result, index);
        index = addFormat(mask, DmtData.FORMAT_INTEGER, result, index);
        index = addFormat(mask, DmtData.FORMAT_STRING, result, index);
        return trim(result, index);
      } case TR069_STRING: {
        /*FORMAT_STRING, FORMAT_BOOLEAN, FORMAT_INTEGER, FORMAT_LONG, FORMAT_FLOAT, FORMAT_RAW_STRING, FORMAT_XML*/
        int[] result = new int[7];
        index = addFormat(mask, DmtData.FORMAT_STRING, result, index);
        index = addFormat(mask, DmtData.FORMAT_BOOLEAN, result, index);
        index = addFormat(mask, DmtData.FORMAT_INTEGER, result, index);
        index = addFormat(mask, DmtData.FORMAT_LONG, result, index);
        index = addFormat(mask, DmtData.FORMAT_FLOAT, result, index);
        index = addFormat(mask, DmtData.FORMAT_RAW_STRING, result, index);
        index = addFormat(mask, DmtData.FORMAT_XML, result, index);
        return trim(result, index);
      } default: return new int[0];
    }
  }
  
  private int addFormat(int mask, int format, int[] result, int index) { 
    if ((mask & format) > 0) {
      result[index++] = format;
    }
    return index;
  }
  
  private int[] trim(int[] arrayToTrim, int index) {
    if (arrayToTrim == null || arrayToTrim.length <= index) {
      return arrayToTrim;
    }
    int[] result = new int[index];
    System.arraycopy(arrayToTrim, 0, result, 0, index);
    return result;
  }
  
  
  private DmtData convert(String value, int[] formats) {
    DmtData result = null;
    for (int i = 0; i < formats.length; i++) {
      result = convert(value, formats[i]);
      if (result != null) {
        return result;
      }
    }
    return null;
  }

  private DmtData convert(String value, int format) {
    switch (format) {
      case DmtData.FORMAT_BASE64: {
        if (Utils.BASE64_PATTERN.matcher(value).matches()) {
          try {
            return new DmtData(Base64.decode(value.getBytes()), DmtData.FORMAT_BASE64);
          } catch (Exception e) {
            factory.log(LogService.LOG_WARNING, "Error converting " + value + " in FORMAT_BASE64", e);
            return null;
          }
        }
        return null;
      }
      
      case DmtData.FORMAT_BINARY: {
        if (Utils.HEXBINARY_PATTERN.matcher(value).matches()) {
          return new DmtData(new HexBinary(value).binaryValue(), DmtData.FORMAT_BINARY);
        }
        return null;
      }
      
      case DmtData.FORMAT_RAW_BINARY: {
      //TODO What format should I put here?
        return new DmtData("raw_binary", value.getBytes());
      }
      
      case DmtData.FORMAT_BOOLEAN: {
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")) {
          return new DmtData(true);
        }
        if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0")) {
          return new DmtData(false);
        }
        return null;
      }

      case DmtData.FORMAT_INTEGER: {
        try {
          return new DmtData(Integer.parseInt(value));
        } catch (IllegalArgumentException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
          return null;
        }
      }

      case DmtData.FORMAT_LONG: {
        try {
          return new DmtData(Long.parseLong(value));
        } catch (IllegalArgumentException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
          return null;
        }
      }

      case DmtData.FORMAT_FLOAT: {
        try {
          return new DmtData(Float.parseFloat(value));
        } catch (IllegalArgumentException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
          return null;
        }
      }

      case DmtData.FORMAT_RAW_STRING: {
        //TODO What format should I put here?
        return new DmtData("raw_string", value);
      }
      case DmtData.FORMAT_STRING:
      case DmtData.FORMAT_XML: {
        return new DmtData(value, format);
      }

      case DmtData.FORMAT_DATE:
      case DmtData.FORMAT_TIME: {
        Date date = null;
        try {
          date = (format == DmtData.FORMAT_DATE ? Utils.DMT_DATE_FORMAT : Utils.DMT_LOCAL_TIME_FORMAT).parse(value);
        } catch (ParseException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
          if (format == DmtData.FORMAT_TIME) {
            //try UTC time
            try {
              date = Utils.DMT_UTC_TIME_FORMAT.parse(value);
            } catch (ParseException e1) {
              factory.log(LogService.LOG_WARNING, e.toString(), null);
            }
          }
        }
        return date != null ? new DmtData(value, format) : null;
      }
      case DmtData.FORMAT_DATE_TIME: {
        if (Utils.TR069_UNKNOWN_TIME.equals(value)) {
          return DmtData.NULL_VALUE;
        }
        try {
          return new DmtData(Utils.TR069_DATE_ENCODING_FORMAT.parse(value));
        } catch (ParseException e) {
          factory.log(LogService.LOG_WARNING, e.toString(), null);
        }
        return null;
      }
    }
    throw new TR069Exception("Unknown format " + format + " for " + value, TR069Exception.INVALID_PARAMETER_TYPE, null);
  }
  
  private void setChildrenValues(String parentUri, String[] values, int tr069Type) throws DmtException {
    /*remove all children*/
    String[] children = factory.persistenceManager.getChildNodeNames(session, parentUri, true);
    for (int i = 0; i < children.length; i++) {
      factory.persistenceManager.deleteNode(session, parentUri + Uri.PATH_SEPARATOR + children[i]);
    }

    for (int i = 0; i < values.length; i++) {
      int instanceId = factory.persistenceManager.generateInstanceId(session, parentUri);
      String childUri = parentUri + Uri.PATH_SEPARATOR + String.valueOf(instanceId);
      createNode(childUri, instanceId);
      factory.persistenceManager.setNodeValue(session, childUri, convertToDmtData(childUri, values[i].trim(), tr069Type, null));
    }
  }
  
  
  public ParameterValue getParameterValue(final String parameterPath) throws TR069Exception {
    checkParameterPath(parameterPath);
    if (parameterPath.endsWith(Utils.NUMBER_OF_ENTRIES)) {
      final int dotIndex = parameterPath.lastIndexOf(Utils.DOT);
      return new ParameterValue() {
        
        private String uri = toURI(dotIndex == -1 ? "" : parameterPath.substring(0, dotIndex), true);
        
        public String getValue() {
          try {
            String[] children = factory.persistenceManager.getChildNodeNames(session, uri, true);
            if (children == null) {
              return "0";
            }
            return String.valueOf(children.length);
          } catch (DmtException e) {
            throw new TR069Exception(e);
          }
        }
        
        public int getType() {
          return TR069_UNSIGNED_INT;
        }
        
        public String getPath() {
          return parameterPath;
        }
      };
    } else if (parameterPath.endsWith(Utils.ALIAS)) {
      String uri = toURI(parameterPath.substring(0, parameterPath.indexOf(Utils.ALIAS) - 1), true);
      String[] nodePath = Uri.toPath(uri);
      final String name = nodePath[nodePath.length - 1];
      final String alias = factory.persistenceManager.getAlias(session, uri);
      
      return new ParameterValue() {
        
        public String getValue() {
          return alias != null ? alias : name;
        }
        
        public int getType() {
          return TR069_STRING;
        }
        
        public String getPath() {
          //TODO parameterPath or parent path should be returned here?!?
          return parameterPath;
        }
      };
    } else {
      return new ParameterValueImpl(parameterPath, new Node(toURI(parameterPath, true), session));
    }
  }

  public Collection<ParameterInfo> getParameterNames(String objectOrTablePath, boolean nextLevel) throws TR069Exception {
    checkPath(objectOrTablePath);
    ArrayList<ParameterInfo> result = new ArrayList<ParameterInfo>();
    String aliasedParentUri = toURI(objectOrTablePath, true);
    if (objectOrTablePath.length() == 0 || objectOrTablePath.endsWith(Utils.DOT)) {
      addChildren(aliasedParentUri, result, nextLevel);
      if (!nextLevel) {
        result.add(new ParameterInfoImpl(this, objectOrTablePath, new Node(aliasedParentUri, session)));
      }
    } else {
      Node node = new Node(aliasedParentUri, session);
      if (!nextLevel && node.isLeaf()) {
        result.add(new ParameterInfoImpl(this, objectOrTablePath, node));
      } else {
        throw new TR069Exception("Invalid object ot table path: " + objectOrTablePath, TR069Exception.INVALID_ARGUMENTS);
      }
    }
    return result;
  }
  
  private void addChildren(String aliasedParentUri, ArrayList<ParameterInfo> names, boolean nextLevel) {
    try {
      /* Any MAP and LIST node must include a ParameterInfo for the corresponding NumberOfEntries parameter */
      /* If the parent node is a MAP, then the synthetic Alias parameter must be included */
      String parentPath = toPath(aliasedParentUri);
      if (DmtConstants.DDF_MAP.equals(factory.persistenceManager.getNodeType(session, aliasedParentUri)) || 
          DmtConstants.DDF_LIST.equals(factory.persistenceManager.getNodeType(session, aliasedParentUri))
      ) {
        String numberOfEntriesName = escape(parentPath.substring(parentPath.lastIndexOf(Utils.DOT) + 1)) + Utils.NUMBER_OF_ENTRIES;
        names.add(new ParameterInfoImpl(this, parentPath + Utils.DOT + numberOfEntriesName, new Node(aliasedParentUri + Uri.PATH_SEPARATOR + numberOfEntriesName, session)));
      }
      
      String[] children = factory.persistenceManager.getChildNodeNames(session, aliasedParentUri, true);
      if (children == null || children.length == 0) {
        return;
      }
      boolean isParentMap = DmtConstants.DDF_MAP.equals(factory.persistenceManager.getNodeType(session, aliasedParentUri));
      for (int i = 0; i < children.length; i++) {
        /* If the child nodes have an InstanceId node then the returned names must include the InstanceId values instead of the node names */
        String nodeUri = (aliasedParentUri.length() > 0 ? aliasedParentUri + Uri.PATH_SEPARATOR : aliasedParentUri) + children[i];
        String instanceIDUri = nodeUri + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
        String childUri;
        if (session.isNodeUri(instanceIDUri)) {
          childUri = (aliasedParentUri.length() > 0 ? aliasedParentUri + Uri.PATH_SEPARATOR : aliasedParentUri) + Utils.getDmtValueAsString(new Node(instanceIDUri, session));
        } else {
          childUri = nodeUri;
        }
        String childPath = toPath(childUri);
        if (Node.isMultiInstanceNode(session, childUri)) {
          childPath = childPath.concat(Utils.DOT);
        }
        names.add(new ParameterInfoImpl(this, childPath, new Node(childUri, session)));
        if (isParentMap) {
          names.add(new ParameterInfoImpl(this, 
            childPath + Utils.ALIAS, new Node(childUri + Uri.PATH_SEPARATOR + Utils.ALIAS, session))
          );
        }

        if (!nextLevel) {
          addChildren(nodeUri, names, nextLevel);
        }
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }

  public String addObject(String path) throws TR069Exception {
    try {
      checkPath(path);
      if (path.endsWith(Utils.ALIAS)) {
        /* If the path ends in an alias, then the node name must be the alias */
        String nodePath = path.substring(0, path.indexOf(Utils.ALIAS) - 1);
        String parentUri = toURI(nodePath.substring(0, nodePath.lastIndexOf(Utils.DOT)), true);

        checkListOrMapUri(parentUri);
        String nodeName = nodePath.substring(nodePath.lastIndexOf(Utils.DOT) + 1);
        int instanceNumber = factory.persistenceManager.getInstanceNumber(session, toURI(nodePath, true));
        if (instanceNumber < 0) {
          if (Utils.INSTANCE_ID_PATTERN.matcher(nodeName).matches()) {
            instanceNumber = Integer.parseInt(nodeName);
          } else {
            instanceNumber = factory.persistenceManager.generateInstanceId(session, parentUri);
          }
        }
        return String.valueOf(instanceNumber);
      }
      
      if (path.endsWith(Utils.DOT)) {
        path = path.substring(0, path.length() - 1);
      }
      String nodeName = path.substring(path.lastIndexOf(Utils.DOT) + 1);
      if (Utils.ALIAS_PATTERN.matcher(nodeName).matches()) {
        String parentUri = toURI(path.substring(0, path.length() - nodeName.length()), true);
        checkListOrMapUri(parentUri);
        nodeName = nodeName.substring(1, nodeName.length() - 1);
        String nodeUri = parentUri + Uri.PATH_SEPARATOR + nodeName;
        int instanceNumber = factory.persistenceManager.getInstanceNumber(session, nodeUri);
        if (instanceNumber < 0) {
          instanceNumber = factory.persistenceManager.generateInstanceId(session, parentUri);
          createNode(nodeUri, instanceNumber);
          return nodeName;
        }
        throw new TR069Exception("A node with '" + nodeName + "' alias already exists!", TR069Exception.INVALID_PARAMETER_NAME);
      }
      String parentUri = toURI(path, true);
      checkListOrMapUri(parentUri);
      int instanceNumber = factory.persistenceManager.generateInstanceId(session, parentUri);
      createNode(parentUri + Uri.PATH_SEPARATOR + instanceNumber, instanceNumber);
      return String.valueOf(instanceNumber);
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }

  public void deleteObject(String objectPath) throws TR069Exception {
    try {
      checkPath(objectPath);
      String nodeUri;
      try {
        nodeUri = toURI(objectPath, false);
      } catch (TR069Exception e) {
        if (e.getFaultCode() == TR069Exception.INVALID_PARAMETER_NAME) {
          /* A missing node must be ignored (toURI method throws INVALID_PARAMETER_NAME when create is false)*/
          return;
        } else {
          throw e;
        }
      }
      if (Node.isMultiInstanceNode(session, nodeUri)) {
        factory.persistenceManager.deleteNode(session, nodeUri);
      } else {
        throw new TR069Exception("Node " + objectPath + " is not a multi-instance node", TR069Exception.INVALID_PARAMETER_NAME);
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }

  public String toPath(String uri) throws TR069Exception {
    if (Uri.isValidUri(uri)) {
      StringBuffer path = new StringBuffer();
      toPath(uri, path);
      return path.toString();
    }
    throw new TR069Exception("Invalid uri: " + uri, TR069Exception.INVALID_ARGUMENTS);
  }
  
  private void toPath(String uri, StringBuffer path) {
    if (uri == null || uri.length() == 0) {
      if (path.length() > 0) {
        path.deleteCharAt(0);
      }
      return;
    }
    
    String[] uriToPath = Uri.toPath(uri);
    if (uriToPath.length == 1) {
      uri = escape(uri);
      path.insert(0, uri);
      return;
    }
    try {
      String segment = uriToPath[uriToPath.length - 1];
      String parentUri = uri.substring(0, uri.length() - segment.length() - 1);
      if (Utils.ALIAS.equals(segment) || Utils.INSTANCE_ID.equals(segment)) {
        toPath(parentUri, path);
      } else {
        String nodeType = factory.persistenceManager.getNodeType(session, parentUri);
        if (DmtConstants.DDF_MAP.equals(nodeType) || DmtConstants.DDF_LIST.equals(nodeType)) {
          int instanceNumber = factory.persistenceManager.getInstanceNumber(session, uri);
          if (instanceNumber < 0) {
            if (Utils.INSTANCE_ID_PATTERN.matcher(segment).matches()) {
              instanceNumber = Integer.parseInt(segment);
            } else {
              //TODO should I generate it here ?!?!
              instanceNumber = factory.persistenceManager.generateInstanceId(session, parentUri);
            }
          }
          path.insert(0, instanceNumber).insert(0, Utils.DOT);
          toPath(parentUri, path);
        } else {
          path.insert(0, escape(segment)).insert(0, Utils.DOT);
          toPath(parentUri, path);
        }
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }

  public String toURI(String path, boolean create) throws TR069Exception {
    checkPath(path);
    StringBuffer uri = new StringBuffer();
    try {
      toUri(path, create, uri);
    } catch (IllegalArgumentException e) {
      throw new TR069Exception("Invalid parameter name: " + path, TR069Exception.INVALID_PARAMETER_NAME);
    }
    return uri.toString();
  }
  
  private void toUri(String path, boolean create, StringBuffer uri) {
    if (path == null || path.length() == 0) {
      return;
    }
    try {
      String currentNode;
      String segment;
      String remainder;
      int dotIndex = path.indexOf(Utils.DOT);
      if (dotIndex == -1) {
        segment = path;
        remainder = null;
      } else {
        segment = path.substring(0, dotIndex);
        remainder = path.substring(dotIndex + 1);
      }
      if (segment.trim().length() == 0) {
        throw new IllegalArgumentException();
      }
      
      segment = unescape(checkForAliasPattern(segment));
      currentNode = uri.toString();
      
      /*check parent node*/
      if (!factory.persistenceManager.isNodeUri(session, currentNode)) {
        if (create) {
          createNode(currentNode, -1);
        } else {
          throw new TR069Exception("Node " + currentNode + " does not exist!", TR069Exception.INVALID_PARAMETER_NAME);
        }
      }
      String nodeType = factory.persistenceManager.getNodeType(session, currentNode);
      if ((DmtConstants.DDF_MAP.equals(nodeType) || DmtConstants.DDF_LIST.equals(nodeType)) && Utils.INSTANCE_ID_PATTERN.matcher(segment).matches()) {
        long instanceID = Long.parseLong(segment);
        String[] children = factory.persistenceManager.getChildNodeNames(session, currentNode, true);
        for (int i = 0; i < children.length; i++) {
          String child = currentNode + Uri.PATH_SEPARATOR_CHAR + children[i];
          String instanceIDUri = child + Uri.PATH_SEPARATOR_CHAR + Utils.INSTANCE_ID;
          if (session.isNodeUri(instanceIDUri)) {
            if (session.getNodeValue(instanceIDUri).getLong() == instanceID) {
              if (uri.length() > 0) {
                uri.append(Uri.PATH_SEPARATOR_CHAR);
              }
              uri.append(children[i]);
              toUri(remainder, create, uri);
              return;
            }
          }
        }
      }
      if (uri.length() > 0) {
        uri.append(Uri.PATH_SEPARATOR_CHAR);
      }
      uri.append(segment);
      currentNode = uri.toString();
      if (!factory.persistenceManager.isNodeUri(session, currentNode)) {
        if (create) {
          createNode(currentNode, -1);
          toUri(remainder, create, uri);
        } else {
          throw new TR069Exception("Node " + currentNode + "does not exist!", TR069Exception.INVALID_PARAMETER_NAME);
        }
      } else {
        toUri(remainder, create, uri);
      }
    } catch (DmtException e) {
      throw new TR069Exception(e);
    }
  }
  
  private String checkForAliasPattern(String segment) {
    if (Utils.ALIAS_PATTERN.matcher(segment).matches()) {
      segment = segment.substring(1, segment.length() - 1);
    }
    return segment;
  }

  private void createNode(String nodeUri, int instanceNumber) throws DmtException {
    if (factory.persistenceManager.isNodeUri(session, nodeUri)) {
      return;
    }
    MetaNode metanode = session.getMetaNode(Node.getParentUri(nodeUri));
    String[] mimeTypes = metanode == null ? null : metanode.getMimeTypes();
    factory.persistenceManager.createInteriorNode(
      session, nodeUri, instanceNumber, mimeTypes == null ? false : Arrays.asList(mimeTypes).contains(TR069Connector.TR069_MIME_EAGER)
    );
  }
  
  private String unescape(String s) {
    StringBuffer sb = new StringBuffer(s);
    Matcher matcher = Utils.THORN_ESCAPE.matcher(sb);
    int rover = 0;
    while (matcher.find(rover)) {
      int unicode = Integer.parseInt(matcher.group(1), 16);
      sb.delete(matcher.start(), matcher.end());
      sb.insert(matcher.start(), (char) unicode);
      rover = matcher.start() + 1;
    }
    return Uri.encode(sb.toString());
  }
  
  /*
    NAME ::= (Letter | ’_’ )( Letter | Digit | ’-’ | ’_’ | CombiningChar | Extender)*
  
    TR-106: Parameter names MUST be treated as case sensitive. The name of each node in the hierarchy MUST start with a letter or underscore, 
    and subsequent characters MUST be letters, digits, underscores or hyphens.
    
    CombiningChar, Extender and first non-letter and non-underscore symbols MUST be escaped
   */
  private String escape(String s) {
    StringBuffer sb = new StringBuffer(Uri.decode(s));
    char c;
    for (int i = 0; i < sb.length();) {
      c = sb.charAt(i);
      if (i == 0 && !(Character.isLetter(c) || (c == Utils.UNDERSCORE_CODE))) {
        i = thornEscape(sb, i, c);
      } else if (Character.isWhitespace(c) || Utils.CHARS_TO_ESCAPE_PATTERN.matcher(String.valueOf(c)).matches()) {
        i = thornEscape(sb, i, c);
      } else {
        i++;
      }
    }
    return sb.toString();
  }
 
  private int thornEscape(StringBuffer sb, int index, char c) {
    sb.insert(index++, Utils.THORN);
    String hex = Integer.toHexString(c).toUpperCase();
    for(int i = hex.length(); i < 4; i++) {
      sb.insert(index++, '0');
    }
    sb.insert(index++, hex);
    sb.deleteCharAt(++index);
    return index;
  }
  
  /* The TR069 Connector only accepts escaped paths and returns escaped paths */
  private void checkPath(String path) {
    if (path == null) {
      throw new TR069Exception("Path cannot be null!", TR069Exception.INVALID_PARAMETER_NAME);
    }
    char[] chars = path.toCharArray();
    for (int i = 0; i < chars.length; i++) {
      if (chars[i] == Utils.THORN) {
        if ((i + 5 > chars.length) || 
            !Utils.THORN_ESCAPE.matcher(new String(new char[] {Utils.THORN, chars[i + 1], chars[i + 2], chars[i + 3], chars[i + 4]})).matches()
        ) {
          throw new TR069Exception("The TR069 Connector accepts only escaped paths. The path is not as expected: " + path, TR069Exception.INVALID_PARAMETER_NAME);
        } else {
          continue;
        }
      }
      if ((i == 0 && !(Character.isLetter(chars[i]) || (chars[i] == Utils.UNDERSCORE_CODE))) ||
          (Character.isWhitespace(chars[i]) || Utils.CHARS_TO_ESCAPE_PATTERN.matcher(String.valueOf(chars[i])).matches())) {
        throw new TR069Exception("The TR069 Connector accepts only escaped paths. The path is not as expected: " + path, TR069Exception.INVALID_PARAMETER_NAME);
      }
    }
  }
  
  private void checkParameterPath(String path) {
    checkPath(path);
    if (path.endsWith(Utils.DOT)) {
      throw new TR069Exception("Invalid Parameter Path " + path, TR069Exception.INVALID_PARAMETER_NAME);
    }
  }
                                                
  
  private void checkListOrMapUri(String uri) {
    if (factory.persistenceManager.isNodeUri(session, uri)) {
      if (!Node.isMultiInstanceParent(session, uri)) {
        throw new TR069Exception("The " + uri + " is not a valid Map/List uri!", TR069Exception.INVALID_PARAMETER_NAME);
      }
    } else {
      throw new TR069Exception("The " + uri + " is not a valid node!", TR069Exception.INVALID_PARAMETER_NAME);
    }
  }

  public void close() {
    /* Closing the connector must not close the corresponding DmtSession */
  }
}
