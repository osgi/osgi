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
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;

/** An <a href="http://ant.apache.org">Ant</a> task to
 *  wrap the embed the Japize into Ant scripts.
 *
 * @author Jaroslav Tulach
 */
public class JapiantTask extends Task {
    /** JARs with classes */
    private FileSet jars;
    /** dirs with classes */
    private DirSet dirs;
    /** output file */
    private File out;
    /** gzip the output or not, by default taken from the out file extension */
    private Boolean gzip;
    /** packages to scan */
    private String packages;
    
    public JapiantTask() {
    }

    public FileSet createJars() {
        return jars = new FileSet();
    }
    public DirSet createDirs() {
        return dirs = new DirSet();
    }
    public void setFile(File f) {
        out = f;
    }
    public void setGzip(boolean g) {
        gzip = Boolean.valueOf(g);
    }
    public void setPackages(String p) {
        packages = p;
    }

    private boolean isGzip() {
        if (gzip != null) {
            return gzip.booleanValue();
        } else {
            return out.getName().endsWith(".gz");
        }
    }

    public void execute() throws BuildException {
        if (jars == null && dirs == null) {
            throw new BuildException("Either <jars> or <dirs> element must be used!");
        }
        if (out == null) {
            throw new BuildException("file attribute must be set!");
        }
        if (packages == null) {
            throw new BuildException("packages attribute must be set!");
        }

        ArrayList/*<String>*/ args = new ArrayList/*<String>*/();
        if (!isGzip()) {
            args.add("unzip");
        }
        args.add("as");
        args.add(out.getPath());
        args.add("packages");

        int srcs = 0;
        if (jars != null) {
            FileScanner scanner = jars.getDirectoryScanner(getProject());
            File dir = scanner.getBasedir();
            String[] files = scanner.getIncludedFiles();
            srcs += files.length;
            for (int i = 0; i < files.length; i++) {
                File f = new File(dir, files[i]);
                if (!f.isFile()) {
                    throw new BuildException("Not a file: " + f);
                }
                args.add(f.getPath());
            }
        }
        if (dirs != null) {
            DirectoryScanner scanner = dirs.getDirectoryScanner(getProject());
            File dir = scanner.getBasedir();
            log("dirs: " + dir, Project.MSG_VERBOSE);
            String[] files = scanner.getIncludedDirectories();
            srcs += files.length;
            for (int i = 0; i < files.length; i++) {
                File f = new File(dir, files[i]);
                if (!f.isDirectory()) {
                    throw new BuildException("Not a file: " + f);
                }
                args.add(f.getPath());
            }
        }

        if (srcs == 0) {
            throw new BuildException("No <jars> or <dirs> found");
        }

        {
            args.add(System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar");
        }

        {
            StringTokenizer t = new StringTokenizer(packages, " ,");
            while (t.hasMoreElements()) {
                String pkg = t.nextToken();
                int len = pkg.length();
                if (pkg.endsWith(".**")) {
                    args.add("+" + pkg.substring(0, len - 3));
                } else if (pkg.endsWith(".*")) {
                    args.add("=" + pkg.substring(0, len - 2));
                } else {
                    throw new BuildException("Unknown package: " + pkg);
                }
            }
        }

        log("Running japize with: " + args);
        try {
            Japize.main((String[])args.toArray(new String[0]));
        } catch (Exception ex) {
            throw new BuildException(ex);
        }
    }
}
