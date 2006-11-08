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

import java.io.File;
import java.util.zip.GZIPInputStream;
import junit.framework.TestCase;
import junit.framework.*;
import org.apache.tools.ant.Task;

/**
 *
 * @author Jaroslav Tulach
 */
public class JapiantTaskTest extends TestCase {
    
    public JapiantTaskTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    public void testSupportsNonRecursivePackages() throws Exception {
        doCompute("x.y.*", false);
    }

    public void testSupportsRecursivePackages() throws Exception {
        doCompute("x.y.**", true);
    }

    public void testSupportsListOfPackages() throws Exception {
        doCompute("x.y.*, x.y.subpkg.*", true);
    }

    private void doCompute(String pkgs, boolean expectSubpkg) throws Exception {

        File sources = new File(AntUtils.getWorkDir(this), "src");
        sources.mkdirs();
        File build = new File(AntUtils.getWorkDir(this), "build");


        AntUtils.extractString(new File(sources, "A.java"),
            "package x.y;" +
            "public class A {" +
            "}" +
            ""
        );
        AntUtils.extractString(new File(sources, "B.java"),
            "package x.y.subpkg;" +
            "public class B {" +
            "}" +
            ""
        );
        File out = new File(sources, "out.japi");
        out.delete();


        String script =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<project name=\"Test Non-Recursive Packages\" basedir=\".\" default=\"all\" >" +
            "  <taskdef name=\"japi\" classname=\"net.wuffies.japi.JapiantTask\" />" +
            "<target name=\"all\" >" +
            "  <mkdir dir='" + build + "'/>" +
            "  <javac srcdir='" + sources + "' destdir='" + build + "'/>" +
            "  <japi file='" + out + "' packages='" + pkgs + "' gzip='false'>" +
            "    <dirs dir='" + build.getParent() + "'>" +
            "      <include name='" + build.getName() + "'/>" +
            "    </dirs>" +
            "  </japi>" +
            "</target>" +
            "</project>";

        File buildScript = AntUtils.extractString(script);

        AntUtils.execute (buildScript, new String[] { "-verbose" });

        assertTrue("Output generated: " + out, out.isFile());

        String txt = AntUtils.readFile(out, false);

        if (!expectSubpkg) {
            if (txt.indexOf("subpkg") >= 0) {
                fail("No subpkg should be there:\n" + txt);
            }
        } else {
            if (txt.indexOf("subpkg") < 0) {
                fail("subpkg should be there:\n" + txt);
            }
        }

        if (txt.indexOf("x.y") < 0) {
            fail("x.y should be there:\n" + txt);
        }
    }
    
}
