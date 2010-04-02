/*
 * Copyright (c) OSGi Alliance (2010). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 
                    Remote Service Admin Compliance Test README
 
 This README describes the compliance tests for Remote Service Admin (RSA) and documents
 additional settings and procedures to follow when using the test suite against
 your own implementation of RSA.
 
 I. General Setup:
  For most cases the tests run as a plugin in a OSGi framework (parent) that is configured via the
  bnd.bnd file of this project. In addition, a second framework instance (child) is created using
  the FrameworkFactory class to host the same set of bundles as the parent plus any other
  configured bundles required for the RI to function.
  The tests typically trigger the export of services in one framework and expect the other framework
  to detect the service and make it available.
  
  The FrameworkFactory class is read from the resource
   /META-INF/services/org.osgi.framework.launch.FrameworkFactory
  inside the test bundle or otherwise read from the system bundle (bundle 0).
  
  This setup requires support from the RI to run multiple times within the same JVM. The current RI
  does support this.
 
 II. Properties to be set in the bnd.bnd file to configure the CT tests:
  - osgi.console=1111 - console port for the OSGi framework. The CT Tests configure the console
                        port for the second framework that is launched to be osgi.console+1
  - rsa.ct.timeout=300000 - in positive test cases there is no good time limit for allowing the
                            registration of a service to happen. It can't be enforced by the CT.
                            However, in order to make the tests useful in an automated build, this
                            timeout can be set (in milliseconds) for the CT tests to wait for
                            completion of the operation.
  - rsa.ct.timeout.factor=3 - this factor is applied to the rsa.ct.timeout timeout value for negative
                              tests, in which a condition is tested that must NOT happen. This can
                              not really be enforced by the CT, so a reasonable factor is given to
                              allow for automated tests to complete in a reasonable time.
  - ${p}.bundles="" - list of bundles that need to be installed in the child framework. This list
                      is specific to the RI.
  - ${p}.serverconfig="service.exported.configs" - RI specific properties that can be read by the CT tests.
  - service.exported.configs="" - list of supported configuration types of the RI. This list is RI specific.
                                  This list is required for cases where the supported list cannot be obtained
                                  programmatically, e.g. in Remote Services (chapter 13)
                                  
                                  