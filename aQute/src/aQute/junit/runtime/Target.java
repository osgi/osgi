package aQute.junit.runtime;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.osgi.framework.*;

public class Target {
    final List       testNames  = new ArrayList();
    List             flattened;
    int              port       = -1;
    boolean          keepAlive;
    String           target;
    boolean          deferred   = false;
    boolean          clear      = false;
    boolean          verbose    = true;
    final Properties properties = new Properties();
    final Framework  framework  = new Framework(properties);
    String           reportName = "test-report.xml";
    boolean          waitForEver;

    /*
     * -version 3 -port 55310 -testLoaderClass
     * org.eclipse.jdt.internal.junit.runner.junit3.JUnit3TestLoader
     * -loaderpluginname org.eclipse.jdt.junit.runtime -testNameFile
     * /tmp/testNames18041.txt
     */

    public static void main(String[] args) {
        Target target = new Target();
        System.exit(target.run(args));
    }

    int run(String args[]) {
        try {
            init(args);
            if (port == -1)
                return doTesting(new XMLReport(reportName));
            else
                return doTesting(new JUnitReport(port));
        } catch (Throwable e) {
            error("bnd runtime", e);
            framework.report(System.out);
            return -1;
        }
    }

    void init(String args[]) throws Exception {
        for (int i = 0; i < args.length; i++) {
            if ("-version".equals(args[i])) {
                if (!"3".equals(args[++i].trim()))
                    throw new RuntimeException(
                            "This target only works with JUnit protocol #3");
            } else if ("-port".equals(args[i]))
                port = Integer.parseInt(args[++i]);
            else if ("-deferred".equals(args[i])) {
                deferred = true;
            } else if ("-clear".equals(args[i])) {
                clear = true;
            } else if ("-testNameFile".equals(args[i]))
                processFile(new File(args[++i]));
            else if ("-testLoaderClass".equals(args[i])) // From old
                // interface
                i++;
            else if ("-loaderpluginname".equals(args[i])) // From
                // old
                // interface
                i++;
            else if ("-test".equals(args[i]))
                testNames.add(args[++i]);
            else if ("-classNames".equals(args[i]))
                testNames.add(args[++i]);
            else if ("-bundle".equals(args[i]))
                framework.addBundle(new File(args[++i]));
            else if ("-export".equals(args[i]))
                framework.addSystemPackage(args[++i]);
            else if ("-framework".equals(args[i]))
                framework.setFramework(args[++i]);
            else if ("-keepalive".equals(args[i]))
                keepAlive = true;
            else if ("-set".equals(args[i])) {
                properties.setProperty(args[++i], args[++i]);
            } else if ("-target".equals(args[i])) {
                target = args[++i];
                framework.addBundle(new File(target));
            } else if ("-storage".equals(args[i])) {
                framework.setStorage(new File(args[++i]));
            } else if ("-report".equals(args[i]))
                reportName = args[++i];
            else if ("-verbose".equals(args[i])) {
                verbose = true;
                System.out.println("bnd OSGi Runtime");
            } else
                warning("Do not understand arg: " + args[i]);
        }
        System.getProperties().putAll(properties);
    }

    /**
     * We are not started from JUnit. This means we start the environment,
     * install, the bundles, and run. If the target bundle has a Test-Cases
     * header, we will run JUnit tests on that class.
     */
    void checkTestCases(Bundle bundle) throws Throwable {
        String testcases = (String) bundle.getHeaders().get("Test-Cases");
        if (testcases == null)
            return;

        String[] classes = testcases.split("\\s*,\\s*");
        for (int i = 0; i < classes.length; i++)
            testNames.add(classes[i]);
    }

    /**
     * Main test routine.
     * 
     * @param tl
     * @param framework
     * @param targetBundle
     * @param testNames
     * @return
     * @throws Throwable
     */
    private int doTesting(TestReporter tl) throws Throwable {
        if (framework.activate()) {
            boolean report = properties.containsKey("report");
            if (report)
                framework.report(System.out);

            Bundle targetBundle = framework.getBundle(target);
            if (targetBundle == null)
                throw new IllegalArgumentException("No target specified");

            // Verify if we have any test names set
            if (testNames.size() == 0)
                checkTestCases(targetBundle);

            if (testNames.size() == 0) {
                System.out
                        .println("No test cases to run, waiting for the framework to quit");
                framework.waitForStop(0);
                return 0;
            }

            BasicTestReport otl = new BasicTestReport();
            TestResult result = new TestResult();
            try {
                TestSuite suite = createSuite(targetBundle, testNames);
                List flattened = new ArrayList();
                int realcount = flatten(flattened, suite);
                tl.begin(framework, targetBundle, flattened, realcount);
                otl.begin(framework, targetBundle, flattened, realcount);
                result.addListener(tl);
                result.addListener(otl);
                suite.run(result);
                if (result.wasSuccessful())
                    return 0;
                else
                    return otl.errors;
            } catch (Throwable t) {
                result.addError(null, t);
                throw t;
            } finally {
                tl.end();
                otl.end();
                if ( properties.containsKey("wait")) {
                    framework.waitForStop(10000000);
                }
                framework.deactivate();
            }
        } else
            throw new IllegalStateException("Framework does not activate");
    }

    // private void doTestRun() throws Exception {
    //
    // long startTime = System.currentTimeMillis();
    // try {
    // framework.activate();
    // if (verbose)
    // framework.report(System.out);
    // try {
    // targetBundle = framework.getBundle(target);
    // TestSuite suite = createSuite(targetBundle, testNames);
    // int realCount = flatten(flattened = new ArrayList(), suite);
    // message("%TESTC ", realCount + " v2");
    // try {
    // report(flattened);
    // TestResult result = new TestResult();
    // result.addListener(this);
    // suite.run(result);
    // } catch (Throwable t) {
    // error("Failed badly duringtest", t);
    // t.printStackTrace();
    // }
    // } finally {
    // framework.deactivate();
    // }
    // } catch (Exception e) {
    // message("%TESTC ", 1 + " v2");
    // message("%TESTS ", "<initializationerror>");
    // error("Failed to initialize", e);
    // message("%TESTE ", "<initialization error>");
    // }
    // message("%RUNTIME", "" + (System.currentTimeMillis() - startTime));
    // }

    private int flatten(List list, TestSuite suite) {
        int realCount = 0;
        for (Enumeration e = suite.tests(); e.hasMoreElements();) {
            Test test = (Test) e.nextElement();
            list.add(test);
            if (test instanceof TestSuite)
                realCount += flatten(list, (TestSuite) test);
            else
                realCount++;
        }
        return realCount;
    }

    /**
     * Convert the test names to a test suite.
     * 
     * @param tfw
     * @param testNames
     * @return
     * @throws Exception
     */
    private TestSuite createSuite(Bundle tfw, List testNames) throws Exception {
        TestSuite suite = new TestSuite();
        for (Iterator i = testNames.iterator(); i.hasNext();) {
            String fqn = (String) i.next();
            int n = fqn.indexOf(':');
            if (n > 0) {
                String method = fqn.substring(n + 1);
                fqn = fqn.substring(0, n);
                Class clazz = tfw.loadClass(fqn);
                suite.addTest(TestSuite.createTest(clazz, method));
            } else {
                Class clazz = tfw.loadClass(fqn);
                suite.addTestSuite(clazz);
            }
        }
        return suite;
    }

    private void warning(String string) {
        System.err.println("warning: " + string);
    }

    private void error(String string, Throwable e) {
        if (e instanceof BundleException)
            Framework.report((BundleException) e, System.err);
        else {
            System.err.println(string + " : " + e);

            if (verbose)
                e.printStackTrace();
        }
    }

    private void processFile(File file) throws IOException {
        FileReader rdr = new FileReader(file);
        BufferedReader brdr = new BufferedReader(rdr);
        String line = brdr.readLine();
        while (line != null) {
            testNames.add(line.trim());
            line = brdr.readLine();
        }
        rdr.close();
    }

}
