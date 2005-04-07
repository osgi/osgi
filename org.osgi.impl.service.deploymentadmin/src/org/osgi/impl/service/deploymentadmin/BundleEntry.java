package org.osgi.impl.service.deploymentadmin;

import java.io.Serializable;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.WrappedJarInputStream.Entry;

public class BundleEntry implements Serializable {
    
    private String  symbName;
    private String  version;
    private Long    id;
    private Boolean customizer;

    public BundleEntry(String location, 
            String symbName, 
            String version, 
            boolean customizer, 
            long id) 
	{
		this.symbName = symbName;
		this.version = version;
		this.id = new Long(id);
		this.customizer = new Boolean(customizer);
	}

    public BundleEntry(String symbName, String version, boolean customizer) {
        this(null, symbName, version, customizer, -1);
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
        customizer = new Boolean(entry.isCustomizerBundle());
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

    public boolean isCustomizer() {
        return customizer.booleanValue();
    }
    
    public long getId() {
        return null == id ? -1 : id.longValue();
    }

    public void setId(long id) {
        this.id = new Long(id);
    }

    public String getSymbName() {
        return symbName;
    }

    public String getVersion() {
        return version;
    }

    public void setSymbName(String symbName) {
        this.symbName = symbName;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

