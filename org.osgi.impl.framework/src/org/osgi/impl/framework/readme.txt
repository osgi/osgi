*******************************************************************
**    How to start and test the framework ref. implementation    **
**                                                               **
**    2001-06-13                                                 **
**                                                               **
***   Copyright 1999 - 2001 Gatespace AB. All rights reserved.   **
*******************************************************************


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


Note on permissions:

 To enable permission check you must start with the sercurity manager, do:

 1. Create a policy file containing (Needed to bootstrap policy):

    ============= Start policyfile =================
    // Framework must have all permissions to start
    
    grant codeBase "file:framework.jar" {
      permission java.security.AllPermission;
    };
    ================================================

 2. Start framework (extension directory contains JSDK servlet.jar):
     java -Dorg.osgi.framework.system.packages=javax.servlet,javax.servlet.http \
          -Djava.security.manager \
	  -Djava.security.policy=policyfile \
          -jar framework.jar \
          -init -launch -istart log.jar -istart http.jar

 The PermissionAdmin service should now be available for use.

Please mail questions or comments to

 support@gatespace.com


------------------------------------------------------------------
Copyright 1999 - 2001 Gatespace AB. All rights reserved.
