package org.osgi.service.event;

import java.util.*;
import org.osgi.framework.Filter;

public class ChannelEvent 
{
  private String topic;
  private Hashtable properties;
  
  public ChannelEvent(String topic, Map properties) 
  {    
    this.topic = topic;
    
    this.properties = new Hashtable();
    this.properties.put( "topic", topic );

    if( properties != null )
      this.properties.putAll( properties );
  }

  public boolean equals(java.lang.Object obj) 
  {
    if( obj instanceof ChannelEvent )
    {
      ChannelEvent evt = (ChannelEvent)obj;      
      return topic.equals( evt.topic ) && properties.equals( evt.properties );
    }
    return false;
  }

  public final Object getProperty(java.lang.String name) 
  {
    return properties.get( name );
  }

  public final String[] getPropertyNames() 
  {
    String []names = new String [ properties.size() ];
    
    int i=0;
    Enumeration keys = properties.keys();
    
    for( i=0; keys.hasMoreElements(); i++ )
      names[i] = keys.nextElement().toString();
    
    return names;
  }

  public final String getTopic() 
  {
    return topic;
  }

  public int hashCode() 
  {
    return topic.hashCode() ^ properties.hashCode();
  }

  public final boolean matches(Filter filter) 
  {
    return filter.match( properties ); 
  }

  public String toString() 
  {
    return topic.toString();
  }
}
