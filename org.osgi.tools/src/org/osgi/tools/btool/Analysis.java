/*
 * Created on Jul 5, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.osgi.tools.btool;

import java.io.*;
import java.util.zip.*;

/**
 * @author Peter Kriens
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class Analysis {
    BTool    btool;
    String   zipfile;
    Manifest manifest;
    private Object dependencies;

    Analysis(BTool btool, Dependencies dependencies, String zipfile) {
        this.btool = btool;
        this.zipfile = zipfile;
    }

    /**
     *  
     */
    public void execute() throws Exception {
        
        ZipFile zip = new ZipFile(new File(zipfile));
        ZipEntry entry = zip.getEntry("META-INF/MANIFEST.MF");
        manifest = new Manifest(zip.getInputStream(entry));
        checkActivator();
    }

    private void checkActivator() {
        String activator = manifest.getValue("Bundle-Activator");
        if ( activator == null )
            return;
        
        activator = activator.replace('.','/') + ".class";
        Resource activatorResource = (Resource) btool.contents.get(activator);
        if ( activatorResource == null ) {
            btool.errors.add("Activator not found " + activator );
        }
    }

    
}