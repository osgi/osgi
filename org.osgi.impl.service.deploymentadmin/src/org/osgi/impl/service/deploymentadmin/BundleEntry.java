package org.osgi.impl.service.deploymentadmin;

import java.io.Serializable;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.WrappedJarInputStream.Entry;

public class BundleEntry implements Serializable {
    
    public String symbName;
    public String version;
    public Long   id;
    
    public BundleEntry(String symbName, String version) {
        this(null, symbName, version, -1);
    }
    
    public BundleEntry(String location, String symbName, String version, long id) {
        this.symbName = symbName;
        this.version = version;
        this.id = new Long(id);
    }
    
    public BundleEntry(BundleEntry other) {
        this.symbName = other.symbName;
        this.version = other.version;
        this.id = other.id;
    }
    
    public BundleEntry(Bundle b) {
        this.symbName = (String) b.getHeaders().get("Bundle-SymbolicName");
        this.version = (String) b.getHeaders().get("Bundle-Version");
        this.id = new Long(b.getBundleId());
    }
    
    public BundleEntry(Entry entry) {
        symbName = entry.getAttributes().getValue(DAConstants.BUNDLE_SYMBOLIC_NAME);
        version = entry.getAttributes().getValue(DAConstants.BUNDLE_VERSION);
    }

    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof BundleEntry))
            return false;
        BundleEntry other = (BundleEntry) obj;
        return symbName.equals(other.symbName) &&
               version.equals(other.version);
    }
    
    public int hashCode() {
        return (symbName + version).hashCode();
    }
    
    public String toString() {
        return "[" + symbName + " " + version + "]";
    }
    
}

