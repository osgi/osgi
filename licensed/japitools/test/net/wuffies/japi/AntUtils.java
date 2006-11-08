///////////////////////////////////////////////////////////////////////////////
// Japize - Output a machine-readable description of a Java API.
// Copyright (C) 2006 Jaroslav Tulach
// 
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
///////////////////////////////////////////////////////////////////////////////

package net.wuffies.japi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.URL;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import junit.framework.Assert;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/** Handy utilities for execution of Ant scripts from tests.
 *
 * @author Jaroslav Tulach
 */
final class AntUtils extends Object {
    private AntUtils () {
    }

    static File getWorkDir(TestCase t) throws IOException {
        String name = t.getName();

        File f = File.createTempFile(name, ".dir");
        f.delete();
        f.mkdirs();

        Assert.assertTrue("Directory created: " + f, f.isDirectory());
        return f;
    }
    
    final static String readFile (java.io.File f, boolean gzip) throws java.io.IOException {
        if (!gzip) {
            int s = (int)f.length ();
            byte[] data = new byte[s];
            Assert.assertEquals ("Read all data", s, new FileInputStream (f).read (data));

            return new String (data);
        } else {
            GZIPInputStream is = new GZIPInputStream(new FileInputStream(f));
            byte[] arr = new byte[256 * 256];
            int first = 0;
            for(;;) {
                int len = is.read(arr, first, arr.length - first);
                if (first + len < arr.length) {
                    return new String(arr, 0, first + len);
                }
            }
        }
    }
    
    final static File extractString(String res) throws Exception {
        File f = File.createTempFile("res", ".xml");
        f.deleteOnExit ();
        return extractString(f, res);
    }

    final static File extractString (File f, String res) throws Exception {
        FileOutputStream os = new FileOutputStream(f);
        InputStream is = new ByteArrayInputStream(res.getBytes("UTF-8"));
        for (;;) {
            int ch = is.read ();
            if (ch == -1) break;
            os.write (ch);
        }
        os.close ();
            
        return f;
    }
    
    final static File extractResource(String res) throws Exception {
        URL u = AntUtils.class.getResource(res);
        Assert.assertNotNull ("Resource should be found " + res, u);
        
        File f = File.createTempFile("res", ".xml");
        f.deleteOnExit ();
        
        FileOutputStream os = new FileOutputStream(f);
        InputStream is = u.openStream();
        for (;;) {
            int ch = is.read ();
            if (ch == -1) break;
            os.write (ch);
        }
        os.close ();
            
        return f;
    }
    
    final static void execute (String res, String[] args) throws Exception {
        execute (extractResource (res), args);
    }
    
    private static ByteArrayOutputStream out;
    private static ByteArrayOutputStream err;
    
    final static String getStdOut() {
        return out.toString();
    }
    final static String getStdErr() {
        return err.toString();
    }
    
    final static void execute(File f, String[] args) throws Exception {
        // we need security manager to prevent System.exit
        if (! (System.getSecurityManager () instanceof MySecMan)) {
            out = new java.io.ByteArrayOutputStream ();
            err = new java.io.ByteArrayOutputStream ();
            System.setOut (new java.io.PrintStream (out));
            System.setErr (new java.io.PrintStream (err));
            
            System.setSecurityManager (new MySecMan ());
        }
        
        MySecMan sec = (MySecMan)System.getSecurityManager();
        
        List arr = new ArrayList();
        arr.add ("-f");
        arr.add (f.toString ());
        arr.addAll(Arrays.asList(args));
        
        
        out.reset ();
        err.reset ();
        
        try {
            sec.setActive(true);
            org.apache.tools.ant.Main.main ((String[])arr.toArray (new String[0]));
        } catch (MySecExc ex) {
            Assert.assertNotNull ("The only one to throw security exception is MySecMan and should set exitCode", sec.exitCode);
            ExecutionError.assertExitCode (
                "Execution has to finish without problems", 
                sec.exitCode.intValue ()
            );
        } finally {
            sec.setActive(false);
        }
    }
    
    static class ExecutionError extends AssertionFailedError {
        public final int exitCode;
        
        public ExecutionError (String msg, int e) {
            super (msg);
            this.exitCode = e;
        }
        
        public static void assertExitCode (String msg, int e) {
            if (e != 0) {
                throw new ExecutionError (
                    msg + " was: " + e + "\nOutput: " + out.toString () +
                    "\nError: " + err.toString (),  
                    e
                );
            }
        }
    }
    
    private static class MySecExc extends SecurityException {
        public void printStackTrace() {
        }
        public void printStackTrace(PrintStream ps) {
        }
        public void printStackTrace(PrintWriter ps) {
        }
    }
    
    private static class MySecMan extends SecurityManager {
        public Integer exitCode;
        
        private boolean active;
        
        public void checkExit (int status) {
            if (active) {
                exitCode = new Integer (status);
                throw new MySecExc ();
            }
        }

        public void checkPermission(Permission perm, Object context) {
        }

        public void checkPermission(Permission perm) {
        /*
            if (perm instanceof RuntimePermission) {
                if (perm.getName ().equals ("setIO")) {
                    throw new MySecExc ();
                }
            }
         */
        }

        public void checkMulticast(InetAddress maddr) {
        }

        public void checkAccess (ThreadGroup g) {
        }

        public void checkWrite (String file) {
        }

        public void checkLink (String lib) {
        }

        public void checkExec (String cmd) {
        }

        public void checkDelete (String file) {
        }

        public void checkPackageAccess (String pkg) {
        }

        public void checkPackageDefinition (String pkg) {
        }

        public void checkPropertyAccess (String key) {
        }

        public void checkRead (String file) {
        }

        public void checkSecurityAccess (String target) {
        }

        public void checkWrite(FileDescriptor fd) {
        }

        public void checkListen (int port) {
        }

        public void checkRead(FileDescriptor fd) {
        }

        public void checkAccess (Thread t) {
        }

        public void checkConnect (String host, int port, Object context) {
        }

        public void checkRead (String file, Object context) {
        }

        public void checkConnect (String host, int port) {
        }

        public void checkAccept (String host, int port) {
        }

        public void checkMemberAccess (Class clazz, int which) {
        }

        public void checkSystemClipboardAccess () {
        }

        public void checkSetFactory () {
        }

        public void checkCreateClassLoader () {
        }

        public void checkAwtEventQueueAccess () {
        }

        public void checkPrintJobAccess () {
        }

        public void checkPropertiesAccess () {
        }

        void setActive(boolean b) {
            active = b;
        }
    } // end of MySecMan
}
