package org.osgi.impl.service.basiccontainer;

import org.osgi.service.application.*;
import java.util.*;
import java.io.*;

public class BasicContainerDescriptor implements ApplicationDescriptor
{
  private String appName;
  private String appCategory;
  private String appVersion;
  private String contID;
  private String id;
  private String mainClass;

  public BasicContainerDescriptor( String name, String category, String version, String cId, 
                                  String appId, String mainCls )
  {
    appName = name;
    appCategory = category;
    appVersion = version;
    contID = cId;
    id = appId;
    mainClass = mainCls;
  }

  public String getCategory()
  {
    return appCategory;
  }
  
  public void setCategory(String cat)
  {
    appCategory = cat;
  }

  public String getContainerID()
  {
    return contID;
  }

  public Map getContainerProperties(String locale)
  {
    /* TODO TODO TODO TODO TODO */ 
    return null;
  }

  public String getName()
  {
    return appName;
  }
  
  public void setName(String name)
  {
    appName = name;
  }

  public Map getProperties(String locale)
  {
    /* TODO TODO TODO TODO TODO */ 
    return null;
  }
  
  public String getUniqueID()
  {
    return contID + "-" + appCategory + "-" + appName + "-" + appVersion;
  }
  
  public String getVersion()
  {
    return appVersion;
  }
  
  public void setVersion(String ver)
  {
    appVersion = ver;
  }
  
  public InputStream getIcon(Locale aLocale, int pixNum)
  {
    /* TODO TODO TODO TODO TODO */ 
    return null;
  }
  
  public String getId()
  {
    return id;
  }
  
  public String getMainClass()
  {
    return mainClass;
  }  
}
