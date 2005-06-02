package org.osgi.impl.service.deploymentadmin;

import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.jar.Attributes;

import org.osgi.framework.Bundle;
import org.osgi.impl.service.deploymentadmin.DeploymentPackageJarInputStream.Entry;

public class BundleEntry implements Serializable {
    
    private String             name;
    private String             symbName;
    private String             version;
    private Long               id;
    private Boolean            customizer;
    private Boolean            missing;
    private CaseInsensitiveMap attrs = new CaseInsensitiveMap();

    public BundleEntry(String name,
            String location, 
            String symbName, 
            String version, 
            boolean customizer, 
            long id,
            boolean missing,
            Attributes attrs) 
	{
        this.name = name;
		this.symbName = symbName;
		this.version = version;
		this.id = new Long(id);
		this.customizer = new Boolean(customizer);
		this.missing = new Boolean(missing);
		extractAttrs(attrs);
	}

    public BundleEntry(String name,
            		   String symbName, 
                       String version, 
                       boolean customizer, 
                       boolean missing,
                       Attributes attrs) 
    {
        this(name, null, symbName, version, customizer, -1, missing, attrs);
    }
    
    public BundleEntry(BundleEntry other) {
        this.name = other.getName();
        this.symbName = other.symbName;
        this.version = other.version;
        this.id = other.id;
        this.missing = new Boolean(other.isMissing());
        this.attrs = new CaseInsensitiveMap(other.attrs);
    }
    
    public BundleEntry(final Bundle b) {
        Object[] triple = (Object[]) AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
                Object[] triple = new Object[3];
                triple[0] = (String) b.getHeaders().get(DAConstants.BUNDLE_SYMBOLIC_NAME);
                triple[1] = (String) b.getHeaders().get(DAConstants.BUNDLE_VERSION);
                triple[2] = new Long(b.getBundleId());
                return triple;
            }
        });
        this.symbName = (String) triple[0];
        this.version = (String) triple[1];
        this.id = (Long) triple[2];
        missing = new Boolean(false);
    }
    
    public BundleEntry(Entry entry) {
        name = entry.getName();
        symbName = entry.getAttributes().getValue(DAConstants.BUNDLE_SYMBOLIC_NAME);
        version = entry.getAttributes().getValue(DAConstants.BUNDLE_VERSION);
        customizer = new Boolean(entry.isCustomizerBundle());
        missing = new Boolean(entry.isMissing());
        extractAttrs(entry.getAttributes());
    }

    private void extractAttrs(Attributes as) {
        for (Iterator iter = as.keySet().iterator(); iter.hasNext();) {
            Attributes.Name key = (Attributes.Name) iter.next();
            Object value = as.getValue(key);
            attrs.put(key.toString(), value);
        }
    }

    public boolean equals(Object obj) {
        if (null == obj)
            return false;
        if (!(obj instanceof BundleEntry))
            return false;
        BundleEntry other = (BundleEntry) obj;
        return ((null == symbName && null == other.symbName) || symbName.equals(other.symbName)) &&
               ((null == version && null == other.version) || version.equals(other.version));
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

    public boolean isMissing() {
        return missing.booleanValue();
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

    public String getName() {
        return name;
    }
    
    CaseInsensitiveMap getAttrs() {
        return attrs;
    }

    public void setSymbName(String symbName) {
        this.symbName = symbName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getValue(String header) {
        return (String) attrs.get(header);
    }

}

