package com.nokia.test.exampleresourceprocessor.db.api;

import java.io.File;
import java.util.Set;

import org.osgi.service.deploymentadmin.DeploymentPackage;

public interface DbRpTest {

    File getBundlePrivateArea();
    Set getResources(DeploymentPackage dp, String resName);
    
}
