******************************************************************
**    How to start and test the GS framework implementation     **
**                                                              **
**    2000-04-07                                                **
**                                                              **
***   Copyright 1999, 2000 Gatespace AB. All rights reserved.   **
******************************************************************


The class java org.osgi.impl.framework.Main in framework.jar should
be used to start the framework.

Usage:  java [properties] -jar framework.jar [-init] [options]

Options:
  -exit         Exit the JVM process
  -help         Print this text
  -init         Start an empty platform
  -install URL  Install a bundle
  -istart URL   Install and start bundle
  -launch       Launch framework (Default)
  -sleep SEC    Sleep a while before next command
  -shutdown     Shutdown framework
  -start ID     Start bundle
  -stop ID      Stop bundle
  -uninstall ID Uninstall a bundle

Properties:
  org.osgi.framework.dir -
    Where we store persistent data
    (Default: ./fwdir)
  org.osgi.framework.system.packages -
    List of packages exported from system classloader,
    all none java.* and org.osgi.framework in classpath
  org.osgi.framework.install.base -
    Base URL for relative install commands
    (Default: file:./)
  org.osgi.framework.permissions -
    Should we check framework permissions
    (Default: false)


Options are processed in command line argument order.


Example:

  Install log, http and device, launch framework and 
  start installed services. Copy jsdk/servlet.jar to javas
  extension directory.

  java -Dorg.osgi.framework.system.packages=javax.servlet,javax.servlet.http \
     -jar framework.jar \
     -init \
     -install log.jar \
     -install http.jar \
     -install device.jar \
     -launch \
     -start 1 -start 2 -start 3


Note on the gatespace HTTP server port:

 The port number defaults to 80 (and tries 8080 if that fails). If any
 other port is more suitable, set the property

   org.osgi.service.http.port

 to an integer number. (e.g: -Dorg.osgi.service.http.port=8081)


Note on the gatespace BasicDriverLocator bundle:

 The database file defaults to a embedded file "driverDB.props" inside
 the bundle's jar file. If you need to test with an external database,
 set the property

   org.osgi.service.basicdriverlocator.dburl

 to a suitable URL.
  (e.g -Dorg.osgi.service.basicdriverlocator.dburl=file:/tmp/test.props)


Note on permissions:

 To start the platform with permissions, do:

 1. Create a policy file containing:

    ============= Start policyfile =================
    // Framework code gets all permissions
    // And it must be all permissions (AdminPermission, PackagePermission
    // and ServicePermission) for framework to work.
    
    grant codeBase "file:framework.jar" {
      permission org.osgi.framework.AdminPermission;
      permission org.osgi.framework.PackagePermission "*", "export,import";
      permission org.osgi.framework.ServicePermission "*", "get,register";
      permission java.io.FilePermission "fwdir/-", "read,write,execute,delete";
    };
    
    // Bundle permissions
    
    grant codeBase "file:log.jar" {
      permission org.osgi.framework.PackagePermission "*", "import";
      permission org.osgi.framework.PackagePermission "org.osgi.service.log", "export";
      permission org.osgi.framework.ServicePermission "org.osgi.service.log.*", "register";
      permission java.io.FilePermission "fwdir/-", "read,write,execute,delete";
    };
    
    grant codeBase "file:http.jar" {
      permission org.osgi.framework.AdminPermission;
      permission org.osgi.framework.PackagePermission "*", "export,import";
      permission org.osgi.framework.ServicePermission "*", "get,register";
      permission java.io.FilePermission "fwdir/-", "read,write,execute,delete";
    };
    ================================================

 2. Start framework (extension directory contains JSDK servlet.jar):
     java -Dorg.osgi.framework.system.packages=javax.servlet,javax.servlet.http \
          -Djava.security.policy=policyfile \
          -Dorg.osgi.framework.permissions=true \
          -jar framework.jar \
          -init -launch -istart log.jar -istart http.jar

 Permissions is only added for testing purposes in this implementation. We need to define how
 to adminstrate permissions for a useable implementation.

Please mail questions or comments to

 support@gatespace.com


------------------------------------------------------------------
Copyright 1999, 2000 Gatespace AB. All rights reserved.
