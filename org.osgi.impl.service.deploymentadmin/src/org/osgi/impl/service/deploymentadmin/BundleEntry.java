package org.osgi.impl.service.deploymentadmin;

import java.io.Serializable;

import org.osgi.framework.Bundle;

public class BundleEntry implements Serializable {
    
    public String location;
    public String symbName;
    public String version;
    public Long   id;
    
    public BundleEntry(String location, String symbName, String version, long id) {
        this.location = location;
        this.symbName = symbName;
        this.version = version;
        this.id = new Long(id);
    }
    
    public BundleEntry(BundleEntry other) {
        this.location = other.location;
        this.symbName = other.symbName;
        this.version = other.version;
        this.id = other.id;
    }
    
    public BundleEntry(Bundle b) {
        this.location = b.getLocation();
        this.symbName = (String) b.getHeaders().get("Bundle-SymbolicName");
        this.version = (String) b.getHeaders().get("Bundle-Version");
        this.id = new Long(b.getBundleId());
    }
    
    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof BundleEntry))
            return false;
        BundleEntry other = (BundleEntry) obj;
        return location.equals(other.location) &&
               symbName.equals(other.symbName) &&
               version.equals(other.version);
        // id is not needed
    }
    
    public int hashCode() {
        return (location + symbName + version).hashCode();
        // id is not needed
    }
    
    public String toString() {
        return "[" + location + " " + symbName + " " + version + " " + id + "]";
    }
    
}

