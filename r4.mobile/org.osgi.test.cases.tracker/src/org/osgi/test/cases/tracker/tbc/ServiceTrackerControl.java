/*
 * $Header$
 *
 * Copyright (c) OSGi Alliance (2000-2001).
 * All Rights Reserved.
 *
 * Implementation of certain elements of the OSGi
 * Specification may be subject to third party intellectual property
 * rights, including without limitation, patent rights (such a third party may
 * or may not be a member of the OSGi Alliance). The OSGi Alliance is not responsible and shall not be
 * held responsible in any manner for identifying or failing to identify any or
 * all such third party intellectual property rights.
 *
 * This document and the information contained herein are provided on an "AS
 * IS" basis and THE OSGI ALLIANCE DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO ANY WARRANTY THAT THE USE OF THE INFORMATION HEREIN WILL
 * NOT INFRINGE ANY RIGHTS AND ANY IMPLIED WARRANTIES OF MERCHANTABILITY OR
 * FITNESS FOR A PARTICULAR PURPOSE. IN NO EVENT WILL THE OSGI ALLIANCE BE LIABLE FOR ANY
 * LOSS OF PROFITS, LOSS OF BUSINESS, LOSS OF USE OF DATA, INTERRUPTION OF
 * BUSINESS, OR FOR DIRECT, INDIRECT, SPECIAL OR EXEMPLARY, INCIDENTIAL,
 * PUNITIVE OR CONSEQUENTIAL DAMAGES OF ANY KIND IN CONNECTION WITH THIS
 * DOCUMENT OR THE INFORMATION CONTAINED HEREIN, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH LOSS OR DAMAGE.
 *
 * All Company, brand and product names may be trademarks that are the sole
 * property of their respective owners. All rights reserved.
 */

package org.osgi.test.cases.tracker.tbc;

import java.util.*;

import org.osgi.framework.*;
import org.osgi.test.cases.util.*;
import org.osgi.util.tracker.*;

public class ServiceTrackerControl extends DefaultTestBundleControl 
                                   implements ServiceTrackerCustomizer
{


    public void testTc1() throws Exception {
        Bundle  tb;
        Object ss ;
        Object[] sss;
        Object ssr;
        ServiceReference[] srs;
        //2.23.1        Testcase1 (tc1), tracking a service
        //Tb1 contains service: testservice1
        //Install tb1
        tb = installBundle("tb1.jar");
        log("Now a bundle that contains TestService1 is started.");

        //Creates ServiceTracker object with ServiceReference to testservice1
        ServiceReference sr = getContext().getServiceReference("org.osgi.test.cases.tracker.tb1.TestService1");
        ServiceTracker st = new ServiceTracker(getContext(), sr, null);
        st.open();

        //Call ServiceTracker.size()
        //Should reply 1
        log("The number of Services being tracked by ServiceTracker is: 1 : '" + st.size() + "'");

        //Call ServiceTracker.getServiceReferences()
        srs = st.getServiceReferences();
        log("ServiceReference for the tracked service can be reached at this time: true : '"+((srs!=null)&&(srs.length!=0))+"'");
        //Call ServiceTracker.getService()
        ss =  st.getService();
        //Call ServiceTracker.getServices()
        sss = st.getServices();
        //Call ServiceTracker.getService(ServiceReference)
        ssr =st.getService(sr);
        //All should be equal and testservice1
        log("Tracked services can be reached at this time and are equal in the different methods: true : '"+(ss.toString().equals(sss[0].toString()) && ss.toString().equals(ssr.toString()))+"'");

        //Call ServiceTracker.close()
        st.close();
        log("Now the ServiceTracker is closed.");

        //Call ServiceTracker.getServiceReferences()
        srs = st.getServiceReferences();
        log("No ServiceReferences for tracked services can be reached at this time: true : '"+(srs==null)+"'");
        //Call ServiceTracker.getService()
        ss =  st.getService();
        //Call ServiceTracker.getServices()
        sss = st.getServices();
        //Call ServiceTracker.getService(ServiceReference)
        ssr =st.getService(sr);
        //All should be null
        log("No Services for tracked services can be reached at this time: true : '"+((ssr==null)&&(ss==null)&&(sss== null))+"'");

        //Call ServiceTracker.size()
        //Should reply 0
        log("The number of Services being tracked by ServiceTracker is: 0 : '"+st.size()+"'");

        st.open();

        log("Now the ServiceTracker is opened.");
        //Call ServiceTracker.size()
        //Should reply 1
        log("The number of Services being tracked by ServiceTracker is: 1 : '"+st.size()+"'");

        uninstallBundle(tb);
        log("Now the Testbundle is uninstalled.");
        //Call ServiceTracker.getServiceReferences()
        srs = st.getServiceReferences();
        log("No ServiceReferences for tracked services can be reached at this time: true : '"+(srs==null)+"'");
        //Call ServiceTracker.getService()
        ss =  st.getService();
        //Call ServiceTracker.getServices()
        sss = st.getServices();
        //Call ServiceTracker.getService(ServiceReference)
        ssr =st.getService(sr);
        //Should reply with null
        log("No Services for tracked services can be reached at this time: true : '"+((ssr==null)&&(ss==null)&&(sss== null))+"'");

        //Call ServiceTracker.size()
        //Should reply 0
        log("The number of Services being tracked by ServiceTracker is: 0 : '"+st.size()+"'");
        st.close();
        log("ServiceTracker TestCase 1 is completed.");
    }

    public void testTc2() throws Exception {
        ServiceTracker st1;
        ServiceTracker st2;
        ServiceTracker st3;
        
        BundleContext context = getContext();

        //2.23.2 Testcase2 (tc2), waitforService
        //Tb1 contains service: testservice1
        //Tb2 contains service: testservice2
        //Tb3 contains service: testservice3
        //Install tb1, tb2 and tb3

        //Creates ServiceTracker1 object with testservice1
        //Call ServiceTracker.open()
        st1 = new ServiceTracker(context,
                                 "org.osgi.test.cases.tracker.tb1.TestService1",null);
        st1.open();
        //Creates ServiceTracker2 object with testservice2
        //Call ServiceTracker.open()
        st2 = new ServiceTracker(context,
                                 "org.osgi.test.cases.tracker.tb2.TestService2",null);
        st2.open();
        //Creates ServiceTracker3 object with testservice3
        //Call ServiceTracker.open()
        st3 = new ServiceTracker(context,
                                 "org.osgi.test.cases.tracker.tb3.TestService3",null);
        st3.open();
        log("ServiceTracker is created for Testbundle 1, Testbundle 2 and Testbundle 3");

        //Call ServiceTracker.size()
        log("The number of Services being tracked by ServiceTracker 1 is: 0 : '"+st1.size()+"'");
        //Call ServiceTracker.size()
        log("The number of Services being tracked by ServiceTracker 2 is: 0 : '"+st2.size()+"'");
        //Call ServiceTracker.size()
        log("The number of Services being tracked by ServiceTracker 3 is: 0 : '"+st3.size()+"'");

        Semaphore s1 = new Semaphore();
        BundleStarter t1 = new BundleStarter("tb1.jar", s1);
        s1.signal();
        log("ServiceTracker 1 uses waitForService(0).");
        Object tt1 = st1.waitForService(0);
        log("Returned an object in ServiceTracker 1?:  true  : '"+(tt1!=null)+"'");

        Semaphore s2 = new Semaphore();
        BundleStarter t2 = new BundleStarter("tb2.jar", s2);
        log("ServiceTracker 2 uses waitForService(500).");
        Object tt2 =  st2.waitForService(500);
        log("Returned an object in ServiceTracker 2?: false  : '"+(tt2!=null)+"'");
        s2.signal();

        Semaphore s3 = new Semaphore(1);
        BundleStarter t3 = new BundleStarter("tb3.jar", s3);

        synchronized(this)
        {
            try {
        		wait(5000);
            } catch (Exception e) {
                log("Wait didn't work: Thread 1"+e);
            }
        }

        log("ServiceTracker 3 doesn't use waitForService().");
        log("The number of Services being tracked by ServiceTracker 1 is: 1 : '"+st1.size()+"'");
        log("The number of Services being tracked by ServiceTracker 2 is: 1 : '"+st2.size()+"'");
        log("The number of Services being tracked by ServiceTracker 3 is: 1 : '"+st3.size()+"'");

        //Call ServiceTracker.close()
        st1.close();
        st2.close();
        st3.close();

		t3.close();
		t2.close();
		t1.close();

        log("ServiceTracker TestCase 2 is completed.");

    }



    public class BundleStarter extends Thread  {
        String bundleName;
        Semaphore semaphore;
        Bundle bundle;
        boolean isRunning() { return bundle.getState()==Bundle.ACTIVE; }
                
        public BundleStarter(String bundleName, Semaphore semaphore){
            this.bundleName = bundleName;
            this.semaphore = semaphore;
            
            start();
        }

        public void run() {
            try {
                semaphore.waitForSignal();
                bundle = installBundle(bundleName);
            }
            catch(Exception e) {
                log(e.getMessage());
                e.printStackTrace();
            }
            
        }
		
		void close() { 
			try {
				uninstallBundle( bundle ); 
			}
			catch( Exception e ) {
				e.printStackTrace();
				log("Uninstalling bundle from BundleStarter " + e.getMessage());
			}
		}
    }





    public java.lang.Object addingService(ServiceReference reference){
        try {
            log("Now addingService is activated.");
        } catch (Exception e){System.err.println("Now something went wrong in addingService.: "+e );}
        Object obj = getContext().getService(reference);
        return obj;

    }
    public void modifiedService(ServiceReference reference,
                                java.lang.Object service){
        try {
            log("Now modifiedService is activated.");
        } catch (Exception e){System.err.println("Now something went wrong in modifiedService.: "+e );}
    }
    public void removedService(ServiceReference reference,
                               java.lang.Object service){
        try {
            log("Now removedService is activated.");
        } catch (Exception e){System.err.println("Now something went wrong in removedService.: "+e );}
        getContext().ungetService(reference);

    }
    
    public void testTc3() throws Exception
    {
        BundleContext context = getContext();
        Bundle  tb;
        //2.23.3 Testcase3 (tc3), ServiceTrackerCustomizer
        //Tb1 contains service: testservice1
        //Implement ServiceTrackerCustomizer

        //Create ServiceTracker object with testservice1
        //Call ServiceTracker.open()
        log("Install a ServiceTracker.");
        ServiceTracker st = new ServiceTracker(context,
                                               "org.osgi.test.cases.tracker.tb1.TestService1",this);
        st.open();
        //Call ServiceTracker.size()
        log("The number of Services being tracked by ServiceTracker 1 is: 0 : '"+st.size()+"'");
        //Install tb1
        log("Install Testbundle 1.");
        log("Now addingService in ServiceTrackerCustomizer should send a message.");
        tb = installBundle("tb1.jar");
        log("The number of Services being tracked by ServiceTracker 1 is: 1 : '"+st.size()+"'");
        //Addingservice should do something

        //Uninstall tb1
        log("Uninstall Testbundle 1.");
        log("Now removedService in ServiceTrackerCustomizer should send a message.");
        uninstallBundle(tb);
        //RemovedService should do something
        //Call ServiceTracker.close()
        st.close();

        log("ServiceTracker TestCase 3 is completed.");
    }



    public void NOTtestTc4() throws Exception {
        BundleContext context = getContext();

        Bundle  tb1;
        Bundle  tb2;
        Bundle  tb3;
        Bundle  tb4;
        ServiceTracker st;

        //2.23.4 Testcase4 (tc4), tracking a classname
        //Tb1 contains service: testservice1, testservice2, testservice3
        //Tb2 contains service: testservice1
        //Tb3 contains service: testservice1
        //Tb4 uses testservice2
        //Creates ServiceTracker object with classname testservice1
        //Call ServiceTracker.open()
        Filter f =context.createFilter("(name=TestService1)");
        log("A ServiceTracker for TestService1 is created.");
        st = new ServiceTracker(context, f, null);
        st.open();

        log("Testbundles 1, 2, 3 and 4 is started.");
        //Install tb1, tb2, tb3 and tb4
        tb1 = installBundle("tb1.jar");
        tb2 = installBundle("tb2.jar");
        tb3 = installBundle("tb3.jar");
        tb4 = installBundle("tb4.jar");

        //Call ServiceTracker.getServiceReferences()
        ServiceReference[] srs = st.getServiceReferences();
        //Should find tb1,tb2, tb3
        // Arrays.sort(srs);

        if(srs!=null){
            String[] names = new String[srs.length];
            for(int i=0;i<srs.length;i++){
                //ServiceReferences.getBundle()
                Bundle tb = (Bundle)srs[i].getBundle();
                names[i]=(String)tb.getHeaders().get("Bundle-name");
            }
            quicksort(names, 0, names.length-1);
            for(int i=0;i<srs.length;i++){
                log("The bundles that has TestService1: '"+names[i]+"'");
            }
        } else{
            log("There were no ServiceReferences in this ServiceTracker.");
        }
        //Call ServiceTracker.getServices()
        Object[] os = st.getServices();
        if (os!=null){
            //Should find testservice1
            boolean ts=false;
            for(int i=0;i<os.length;i++){
                if( os[i].toString().indexOf("TestService1")>=0){
                    ts=true;
                }
            }
            log("TestService1 is a part of the Services being tracked by this ServiceTracker: true : '"+ts+"'");

        }else
            log("There were no Services in this ServiceTracker.");

        //Call ServiceTracker.size()
        //Should reply 3
        log("The number of Services being tracked by ServiceTracker 1 is: 3 : '"+st.size()+"'");

        try {
            //Tb1.getServicesInUse()
            ServiceReference[] so = tb4.getServicesInUse();
            //Should reply testservice2
            boolean t2t4 = false;
            if(so!=null){
                for(int i=0;i<so.length;i++){
                    if(so[i].getProperty("name").toString().indexOf("TestService2")>=0)
                        t2t4=true;
                }
            }
            log("A service in use by Testbundle 4 is TestService2: true : '"+t2t4+"'");

        } catch (Exception e){
            log("GetServicesInUse() does not work: "+e);
            e.printStackTrace();
        }

        //ServiceReference.getUsingBundles
        ServiceReference sru = context.getServiceReference("org.osgi.test.cases.tracker.tb2.TestService2");
        if(sru!=null){
            Bundle[] bus = sru.getUsingBundles();
            boolean u1=false;
            boolean u2=false;
            boolean u3=false;
            boolean u4=false;
            for(int i=0;i<bus.length;i++){
                if(bus[i].equals(tb1))
                    u1=true;
                else if(bus[i].equals(tb2))
                    u2=true;
                else if(bus[i].equals(tb3))
                    u3=true;
                else if(bus[i].equals(tb4))
                    u4=true;
            }
            log("Is Testbundle 1 using TestService2: false : '"+u1+"'");
            log("Is Testbundle 2 using TestService2: false : '"+u2+"'");
            log("Is Testbundle 3 using TestService2: false : '"+u3+"'");
            log("Is Testbundle 4 using TestService2: true : '"+u4+"'");
        } else {
            log("org.osgi.test.cases.tracker.tb2.TestService2 does not seem to exist.");
        }


        //Tb1.getRegisteredServices()
        ServiceReference[] oss = tb1.getRegisteredServices();
        //Should reply testservice1, testservice2, testservice3
        boolean ts1 = false;
        boolean ts2 = false;
        boolean ts3 = false;
        if(oss!=null){
            for(int i=0;i<oss.length;i++){
                if(oss[i].getProperty("name").toString().indexOf("TestService1")>=0)
                    ts1=true;
                else if (oss[i].getProperty("name").toString().indexOf("TestService2")>=0)
                    ts2=true;
                else if (oss[i].getProperty("name").toString().indexOf("TestService3")>=0)
                    ts3=true;
            }
            log("Testbundle 1 has TestService1 as registered Service:  true : '"+ts1+"'");
            log("Testbundle 1 has TestService2 as registered Service:  true : '"+ts2+"'");
            log("Testbundle 1 has TestService3 as registered Service:  true : '"+ts3+"'");

        } else{
            log("There  are no Services in Testbundle 1.");
        }

        //Call ServiceTracker.remove(ServiceReference to testservice1 in tb2)
        log("TestService1 in Testbundle 2 is removed");
        ServiceReference sr = context.getServiceReference("org.osgi.test.cases.tracker.tb2.TestService2");
        st.remove(sr);
        //Call ServiceTracker.getServiceReferences()
        //Should find tb1, tb3
        srs = st.getServiceReferences();

        if(srs!=null){
            String[] names = new String[srs.length];
            for(int i=0;i<srs.length;i++){
                names[i]=(String)srs[i].getProperty("description");
            }
            quicksort(names, 0, names.length-1);
            for(int i=0;i<names.length;i++){
                String res;
                if(i==0)
                    res="TestService1 in bundle tb1";
                else
                    res="TestService1 in bundle tb3";
                log("The description of the Services that has TestService1: "+res+" : '"+names[i]+"'");
            }

        } else{
            log("There were no ServiceReferences in this ServiceTracker.");
        }

        //Call ServiceTracker.getServices()
        //Should find testservice1
        os = st.getServices();
        if (os!=null){
            //Should find testservice1
            boolean ts=false;
            for(int i=0;i<os.length;i++){
                if( os[i].toString().indexOf("TestService1")>=0){
                    ts=true;
                }
            }
            log("TestService1 is a part of the Services being tracked by this ServiceTracker: true : '"+ts+"'");
        }else
            log("There were no Services in this ServiceTracker.");

        //Call ServiceTracker.size()
        //Should reply 2
        log("The number of Services being tracked by ServiceTracker 1 is: 2 : '"+st.size()+"'");
        //Call ServiceTracker.close()
        st.close();

        uninstallBundle(tb4);
        uninstallBundle(tb3);
        uninstallBundle(tb2);
        uninstallBundle(tb1);

        log("ServiceTracker TestCase 4 is completed.");

    }

    private void quicksort(String[] a, int lo, int hi) {
	int i, j;
	String h, x;

	i = lo;
	j = hi;
	x = a[(lo+hi)/2];
	
	do {
	    while (a[i].compareTo(x) < 0) i++;
	    while (a[j].compareTo(x) > 0) j--;

	    if (i <= j) {
		h = a[i];
		a[i] = a[j];
		a[j] = h;
		i++;
		j--;
	    }
	} while (i <= j);

	if (lo < j) quicksort(a, lo, j);
	if (i < hi) quicksort(a, i, hi);
    }




    public void testTc5() throws Exception {
        BundleContext context = getContext();
        ServiceTracker st;
        Bundle  tb1;
        //2.23.5    Testcase5 (tc5), filter match
        //Tb1 contains service: testservice1
        //Call BundleContext.BundleContext.createFilter(String) (testservice1)


        Filter f =context.createFilter("(name=TestService1)");
        log("A ServiceTracker for TestService1 is created.");
        //Creates ServiceTracker object with Filter
        st = new ServiceTracker(context, f, null);
        //Call ServiceTracker.open()
        st.open();

        log("A ServiceTracker containing a Filter that finds TestService1 is created.");
        //Call ServiceTracker.size()
        //Should reply 0
        log("The number of Services being tracked by ServiceTracker 1 is: 0 : '"+st.size()+"'");
        //Install tb1
        log("Install Testbundle 1.");
        tb1 = installBundle("tb1.jar");
        //Call ServiceTracker.size()
        //Should reply 1
        log("The number of Services being tracked by ServiceTracker 1 is: 1 : '"+st.size()+"'");
        //Call ServiceTracker.getServiceReferences()
        //Should find all
        //Call ServiceTracker.getServiceReferences()
        //Should find tb1, tb3
        ServiceReference[] srs = st.getServiceReferences();

        if(srs!=null){
            for(int i=0;i<srs.length;i++){
                log("The ServiceReferences contains: TestService1 : '"+srs[i].getProperty("name")+"'");
            }
        } else{
            log("There were no ServiceReferences in this ServiceTracker.");
        }


        //Change property for TestService1 so that the filter doesn't match
        //The only way to change property is to have the ServiceRegistration, i.e. reg a new TestService1
        log("Register a new TestService1.");
        TestService1 ts1;
        Properties ts1Props;
        ServiceRegistration tsr1;

        ts1 = new TestService1Impl();
        ts1Props = new Properties();
        ts1Props.put("name", "TestService1");
        ts1Props.put("version", new Float(1.0));
        ts1Props.put("compatible", new Float(1.0));
        ts1Props.put("description", "TestService 1 in tbc");

        tsr1 = context.registerService(TestService1.class.getName(),
                ts1, ts1Props);

        log("The number of Services being tracked by ServiceTracker 1 is: 2 : '"+st.size()+"'");
        log("Now the properties for one of the TestService1's is changed to not match the filter for which this ServiceTracker.");
        ts1Props.put("name","TestService2");
        tsr1.setProperties(ts1Props);
        //Check that the servicetracker doesn't find the TestService
        log("The number of Services being tracked by ServiceTracker 1 is: 1 : '"+st.size()+"'");
        //Change property for tb1 so that the filter match again
        log("Now the property is changed back again.");
        ts1Props.put("name","TestService1");
        tsr1.setProperties(ts1Props);
        //Check that the servicetracker find TestService
        log("The number of Services being tracked by ServiceTracker 1 is: 2 : '"+st.size()+"'");

        try
        {
            tsr1.unregister();
        }
        catch (IllegalStateException e) {  }
        tsr1 = null;
        ts1 = null;

        //Call ServiceTracker.close()
        st.close();
        uninstallBundle(tb1);

        log("ServiceTracker TestCase 5 is completed.");
    }

  public void testTc6() throws Exception {
    BundleContext context = getContext();
    ServiceTracker st;
    st = new ServiceTracker(context, Object.class.getName(), null);
    st.open();
    // Should be 0
    log("ServiceTracker.getTrackingCount() == " + st.getTrackingCount());
    
    ServiceRegistration sr = context.registerService(Object.class.getName(), new Object(), null);
    // Should be 1
    log("ServiceTracker.getTrackingCount() == " + st.getTrackingCount());
    
    sr.unregister();
    // Should be 2
    log("ServiceTracker.getTrackingCount() == " + st.getTrackingCount());

    st.close();
    log("ServiceTracker TestCase 6 is completed.");
  }
}

