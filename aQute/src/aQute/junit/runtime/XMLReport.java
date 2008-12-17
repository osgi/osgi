package aQute.junit.runtime;

import java.io.*;
import java.util.*;

import junit.framework.*;

import org.osgi.framework.*;

public class XMLReport implements TestReporter {
    final File        file;
    final PrintStream out;
    List             /* <Test> */tests;
    boolean           open;

    public XMLReport(String reportName) throws FileNotFoundException {
        file = new File(reportName);
        out = new PrintStream(new FileOutputStream(file));
    }

    public void begin(Framework fw, Bundle targetBundle, List classNames, int realcount) {
        out.println("<?xml version='1.0'?>");
        out.println("<testreport" );
        out.println("    target='"+targetBundle.getLocation()+"'");
        out.println("    time='"+new Date()+"' ");
        out.println("    framework='"+fw.getFramework()+"'>");
        Bundle [] bundles = fw.getSystemBundle().getBundleContext().getBundles();
        for ( int i =0; i<bundles.length; i++ ) {
            out.println("  <bundle location='"+bundles[i].getLocation()+"' ");
            out.println("     modified='"+bundles[i].getLastModified()+"' ");
            out.println("     state='"+bundles[i].getState()+"' ");
            out.println("     id='"+bundles[i].getBundleId()+"' ");
            out.println("     bsn='"+bundles[i].getSymbolicName()+"' ");
            out.println("     version='"+bundles[i].getHeaders().get("Bundle-Version")+"' ");
            out.println("  />");
        }
        
        for ( Iterator i = classNames.iterator(); i.hasNext(); ) {
            out.println("  <classname name='"+i.next()+"'/>");
        }
    }

    public void end() {
        out.println("</testreport>");
        out.close();
    }

    public void setTests(List flattened) {
        this.tests = flattened;
    }

    public void startTest(Test test) {
        out.print("  <test name='" + test + "'");
        open = true;
    }

    public void addError(Test test, Throwable t) {
        if (open)
            out.println(">");
        open = false;
        out.println(" <error name='" + test.toString() + "' exception='"
                + t.getMessage() + "' type='" + t.getClass().getName() + "'>");
        out.println("<![CDATA[");
        t.printStackTrace(out);
        out.println("]]>");
        out.println(" </error>");
    }

    public void addFailure(Test test, AssertionFailedError t) {
        if (open)
            out.println(">");
        open = false;
        out.println(" <failure name='" + test.toString() + "' exception='"
                + t.getMessage() + "' type='" + t.getClass().getName() + "'>");
        out.println("<![CDATA[");
        t.printStackTrace(out);
        out.println("]]>");
        out.println(" </failure>");
    }

    public void endTest(Test test) {
        if (open)
            out.println("/>");
        else
            out.println("  </test>");
        open = false;
    }

}
