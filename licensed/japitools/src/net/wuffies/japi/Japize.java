///////////////////////////////////////////////////////////////////////////////
// Japize - Output a machine-readable description of a Java API.
// Copyright (C) 2000,2002,2003,2004,2005,2006  Stuart Ballard <stuart.a.ballard@gmail.com>
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

import java.lang.reflect.Modifier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.Set;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.io.Writer;
import java.util.Iterator;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.BufferedOutputStream;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Process a Java API and emit a machine-readable description of the API,
 * suitable for comparison to other APIs. Specifically, the perl script
 * japicompat can test APIs for source/binary compatibility with each other.
 *
 * @author Stuart Ballard &lt;<a href="mailto:stuart.a.ballard@gmail.com">stuart.a.ballard@gmail.com</a>&gt;
 */
public class Japize {

  /**
   * The path to scan for classes in.
   */
  private static List path;

  /**
   * The package roots to scan in.
   */
  private static SortedSet roots;

  /**
   * The packages to exclude.
   */
  private static SortedSet exclusions;

  /**
   * Packages that are included exactly and whose subpackages shouldn't be
   * descended into.
   */
  private static SortedSet exact;

  /**
   * Classes and packages to exclude from serialization
   */
  private static SortedSet serialExclusions;

  /**
   * Classes and packages that would otherwise be excluded from serialization
   * that shouldn't be. Also includes "," representing the root package to
   * ensure that serialization defaults to included.
   */
  private static SortedSet serialRoots;

  /**
   * The output writer to write results to.
   */
  private static PrintWriter out;

  /**
   * The output writer to write "lint" output to, if any.
   */
  private static PrintWriter lintOut;

  /* Disambiguation rules */
  private static final int UNSPECIFIED = 0;
  private static final int EXPLICITLY = 1;
  private static final int APIS = 2;
  private static final int BYNAME = 3;
  private static final int PACKAGES = 4;
  private static final int CLASSES = 5;

  /**
   * Parse the program's arguments and perform the main processing.
   */
  public static void main(String[] args)
      throws NoSuchMethodException, IllegalAccessException, IOException, ClassNotFoundException {

    // init for case of multiple invocations
    path = new ArrayList();
    roots = new TreeSet();
    exclusions = new TreeSet();
    exact = new TreeSet();
    serialExclusions = new TreeSet();
    serialRoots = new TreeSet(Arrays.asList(new String[] {","}));

    // Interpret any arguments that start with @ as filenames, and replace the argument with the
    // contents of those files.
    args = scanForFileArgs(args);

    // Scan the arguments until the end of keywords is reached, interpreting
    // all the intermediate arguments and dealing with them as appropriate.
    int i = 0;
    boolean zipIt = true;
    String fileName = null;
    String lintFileName = null;

    if (i < args.length && "unzip".equals(args[i])) {
      zipIt = false;
      i++;
    }
    if (i < args.length && "as".equals(args[i])) {
      fileName = args[++i];
      i++;
    }
    if (i < args.length && "lint".equals(args[i])) {
      lintFileName = args[++i];
      i++;
    }

    // The next word indicates the method used to decide whether an ambiguous
    // argument of the form a.b.c is a class or a package. Any other word is an
    // error, but checks further down will catch that.
    int disambig = UNSPECIFIED;
    if (i < args.length) {
      if ("explicitly".equals(args[i])) {
        disambig = EXPLICITLY;
      } else if ("apis".equals(args[i])) {
        disambig = APIS;
      } else if ("byname".equals(args[i])) {
        disambig = BYNAME;
      } else if ("packages".equals(args[i])) {
        disambig = PACKAGES;
      } else if ("classes".equals(args[i])) {
        disambig = CLASSES;
      }
      i++;
    }

    // Correct syntax requires that one of the previous cases must have matched,
    // and also that there be at least one more word. Both these cases, however,
    // can be errored below because they will result in both the path and the
    // set of roots being empty.
    if (i < args.length && disambig != UNSPECIFIED) {

      // Identify each argument that's prefixed with + or - and put it in
      // either the "roots" or the "exclusions" TreeSet as appropriate. Use
      // the disambiguation method specified above for arguments that do not
      // explicitly indicate if they are classes or packages.
      for (; i < args.length; i++) {
        char first = args[i].charAt(0);
        String pkgpath = args[i].substring(1);
        if (first == '+' || first == '-' || first == '=') {
          SortedSet setToAddTo;
          boolean isExact = false;
          if (pkgpath.endsWith(":serial")) {
            if (first == '=') {
              System.err.println("Cannot use '=' with ':serial' qualifier");
              printUsage();
            }
            setToAddTo = first == '+' ? serialRoots : serialExclusions;
            pkgpath = pkgpath.substring(0, pkgpath.lastIndexOf(':'));
          } else {
            setToAddTo = first == '-' ? exclusions : roots;
            isExact = (first == '=');
          }

          String pathToAdd = null;

          // First identify *whether* it's ambiguous - and whether it's legal.
          int commapos = pkgpath.indexOf(',');
          
          // If it contains a comma, and doesn't have a dot or a comma after
          // that, then it's unambiguous.
          if (commapos >= 0) {
            if (pkgpath.indexOf(',', commapos + 1) >= 0 ||
                pkgpath.indexOf('.', commapos + 1) >= 0) {
              System.err.println("Illegal package/class name " + pkgpath +
                                 " - skipping");
            } else {
              pathToAdd = pkgpath;
            }

          // Otherwise it's ambiguous. Figure out what to do based on the
          // disambiguation rule set above.
          } else {
            switch (disambig) {
              case EXPLICITLY:
                System.err.println("Ambiguous package/class name " + pkgpath +
                                   " not allowed with 'explicitly' - skipping");
                break;
              case APIS:
                // Since "apis" results in two separate roots being added, we add the
                // class root directly here, and don't worry about "exact" or not
                // because that only applies to packages.
                setToAddTo.add(toClassRoot(pkgpath));
                pathToAdd = pkgpath + ",";
                break;
              case BYNAME:
                int dotpos = pkgpath.lastIndexOf('.');
                if (Character.isUpperCase(pkgpath.charAt(dotpos + 1))) {
                  pathToAdd = toClassRoot(pkgpath);
                } else {
                  pathToAdd = pkgpath + ",";
                }
                break;
              case PACKAGES:
                pathToAdd = pkgpath + ",";
                break;
              case CLASSES:
                pathToAdd = toClassRoot(pkgpath);
                break;
            }
          }

          if (pathToAdd != null) {
            setToAddTo.add(pathToAdd);
            if (isExact) exact.add(pathToAdd);
          }

        // If it doesn't start with + or -, it's a path component.
        } else {
          path.add(args[i]);
        }
      }
    }
    if (path.isEmpty() || roots.isEmpty()) printUsage();

    // We need to initialize the classpath to find classes in the correct
    // location.
    StringBuffer cp = new StringBuffer();
    for (Iterator j = path.iterator(); j.hasNext(); ) {
      if (cp.length() > 0) cp.append(File.pathSeparatorChar);
      cp.append(j.next());
    }
    setClasspath(cp.toString());

    // Figure out what output writer to use.
    if (fileName == null) {
      if (zipIt) {
        System.err.println("Note: for correct operation of tools that read japi files, it is strongly");
        System.err.println("recommended to use a filename ending in japi.gz for a compressed japi file.");
        out = new PrintWriter(new GZIPOutputStream(System.out));
      } else {
        System.err.println("Note: for correct operation of tools that read japi files, it is strongly");
        System.err.println("recommended to use a filename ending in japi for an uncompressed japi file.");
        out = new PrintWriter(System.out);
      }
    } else {

      // Japize will only create output to files ending in .japi (uncompressed)
      // or .japi.gz (compressed). It enforces this rule by adding .japi and .gz
      // to the specified filename if it doesn't already have them. If the user
      // specifies a .gz extension for uncompressed output, this is flagged as
      // an error - if it's really what they meant, they can specify x.gz.japi.
      if (fileName.endsWith(".gz")) {
        if (!zipIt) {
          System.err.println("Filename ending in .gz specified without zip output enabled.");
          System.err.println("Please either omit 'unzip' or specify a different filename (did you");
          System.err.println("mean '" + fileName + ".japi'?)");
          System.exit(1);
        }

        // Trim ".gz" off the end. It'll be re-added later, but ".japi" might
        // be inserted first.
        fileName = fileName.substring(0, fileName.length() - 3);
      }

      // Add ".japi" if it's not already there.
      if (!fileName.endsWith(".japi")) fileName += ".japi";

      // Produce an output writer - compressed or not, as appropriate.
      if (zipIt) {
        out = new PrintWriter(new GZIPOutputStream(new BufferedOutputStream(
              new FileOutputStream(fileName + ".gz"))));
      } else {
        out = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
      }
    }

    if (lintFileName != null) {
      lintOut = new PrintWriter(new BufferedWriter(new FileWriter(lintFileName)));
    }

    // Now actually go and japize the classes.
    try {
      doJapize();
    } finally {
      out.close();
      if (lintOut != null) lintOut.close();
    }
  }

  private static String[] scanForFileArgs(String[] args) throws IOException {
    ArrayList result = new ArrayList();

    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("@")) {
        FileReader file = new FileReader(args[i].substring(1));
        try {
          LineNumberReader lines = new LineNumberReader(file);
          try {
            String line;
            while ((line = lines.readLine()) != null) {
              result.add(line);
            }
          } finally {
            lines.close();
          }
        } finally {
          file.close();
        }
      } else {
        result.add(args[i]);
      }
    }

    return (String[]) result.toArray(args);
  }

  private static String toClassRoot(String pkgpath) {
    StringBuffer sb = new StringBuffer(pkgpath);
    int dotpos = pkgpath.lastIndexOf('.');
    if (dotpos >= 0) {
      sb.setCharAt(dotpos, ',');
    } else {
      sb.insert(0, ',');
    }
    return sb.toString();
  }

  private static void progress(char ch) {
    System.err.print(ch);
    System.err.flush();
  }
  private static void progress(String str) {
    System.err.println();
    System.err.print(str);
    System.err.flush();
  }
  // See design/japi-spec.txt for why this ends in a comma rather than the usual period.
  private static final String J_LANG = "java.lang,";
  private static final String J_L_OBJECT = J_LANG + "Object";
  
  private static ClassWrapper jlObjectWrapper;
  private static HashSet objCalls = new HashSet();

  private static void doJapize()
      throws NoSuchMethodException, IllegalAccessException, IOException,
             ClassNotFoundException {

    // Print the header identifier. The syntax is "%%japi ver anything".
    // The "anything" is currently used for name/value pairs indicating the
    // creation date and creation tool.
    out.print("%%japi 0.9.7 creator=japize date=" +
        new SimpleDateFormat("yyyy/MM/dd_hh:mm:ss_z").format(new Date()));
    if (!serialExclusions.isEmpty()) {
      out.print(" noserial=");
      boolean first = true;
      for (Iterator i = serialExclusions.iterator(); i.hasNext(); ) {
        if (!first) out.print(";");
        first = false;
        out.print(i.next());
      }
      if (serialRoots.size() > 1) {
        out.print(" serial=");
        first = true;
        for (Iterator i = serialRoots.iterator(); i.hasNext(); ) {
          String root = (String) i.next();
          if (!",".equals(root)) {
            if (!first) out.print(";");
            first = false;
            out.print(root);
          }
        }
      }
    }
    out.println();

    // Identify whether java.lang,Object fits into our list of things to
    // process. If it does, process it first, then add it to the list of
    // things to avoid (and remove it from the list of roots if it appears
    // there).
    if (checkIncluded(J_L_OBJECT)) {
      processClass(J_L_OBJECT);
      if (roots.contains(J_L_OBJECT)) roots.remove(J_L_OBJECT);
      exclusions.add(J_L_OBJECT);
    }

    // Then do the same thing with java.lang as a whole.
    SortedSet langRoots = roots.subSet(J_LANG, endPackageRoot(J_LANG));
    if (checkIncluded(J_LANG)) {
      processPackage(J_LANG);
      exclusions.add(J_LANG);

    // Even if java.lang isn't included, java.lang.something might be...
    } else {
      processRootSet(langRoots);
    }
    
    // Remove all roots that are subpackages of java.lang.
    for (Iterator i = langRoots.iterator(); i.hasNext(); ) {
      i.next();
      i.remove();
    }

    jlObjectWrapper = getClassWrapper(J_L_OBJECT.replace(',', '.'));
    CallWrapper[] calls = jlObjectWrapper.getCalls();
    for (int i = 0; i < calls.length; i++) {
      if (!"".equals(calls[i].getName())) objCalls.add(getObjComparableString(calls[i]));
    }

    // Now process all the roots that are left.
    processRootSet(roots);
    progress("");
  }

  private static String packageRootTweak(String packageRoot, String tweak) {
    if (!packageRoot.endsWith(",")) throw new RuntimeException("packageRootTweak must be passed a package root ending in comma, not " + packageRoot);
    return packageRoot.substring(0, packageRoot.length() - 1) + tweak;
  }
  private static String endPackageRoot(String packageRoot) {
    return packageRootTweak(packageRoot, "/");
  }

  private static void processRootSet(SortedSet rootSet) 
      throws NoSuchMethodException, IllegalAccessException,
             ClassNotFoundException, IOException {

    // Process all roots in alphabetical order (note that the ordering is
    // implied by the use of a SortedSet).
    String skipping = null;
    for (Iterator i = rootSet.iterator(); i.hasNext(); ) {
      String root = (String) i.next();
      if (skipping != null) {
        if (root.compareTo(skipping) < 0) continue;
        skipping = null;
      }
      if (root.indexOf(',') < root.length() - 1) {
        processClass(root);
      } else {
        processPackage(root);
        skipping = endPackageRoot(root);
      }
    }
  }

  private static void lintPrint(String s) {
    if (lintOut != null) lintOut.println(s);
  }

//- Sort all "plus" items - these will be our "roots".
//- Identify whether java.lang.Object falls within the scope of things to process.
  //If it does, process it as a class root and then add "-java.lang,Object" to
  //the list of exclusions.
//- Iteratively process each root in order. After processing each root, skip any
  //following roots that lie between "root" and "root/". Since slash sorts after
  //comma and period but before alphanumerics, this will exclude any subpackages
  //and classes but not anything like a.b.CD.

//- "Process" for a package root is a recursive function defined as follows:
  //- Scan all zips and directories for (a) classes in this package directly, and
    //(b) immediate subpackages of this package. Store everything that is found.
  //- Sort and then iterate over the items found in (a):
    //- Skip the class if there is an exclusion ("-" form) for this class
      //specified on the commandline.
    //- Otherwise Japize the class.
  //- Sort and then iterate over the items found in (b):
    //- If there is an exclusion for this subpackage found on the commandline,
      //skip it, but also do the following:
      //- Using SortedSet.subSet(), identify if there are any global roots that
        //lie between "excludedpkg" and "excludedpkg/". If there are, process
        //those in order using the appropriate process method for the type.
    //- If the package is not excluded, process it recursively using this
      //process method.

  // Process an individual class, by Japizing it.
  // FIXME: We should scan all zips and directories for this class, and only
  // Japize it if it's found. Optimization: on the first scan through, compare
  // all classes to all class roots and remove class roots that aren't found.
  // Also set a flag to indicate that this has been done - then you can skip
  // the scan for all subsequent class roots.
  static void processClass(String cls)
      throws NoSuchMethodException, IllegalAccessException,
             ClassNotFoundException {
    progress("Processing class " + cls + ":");
    japizeClass(cls);
  }
  static void processPackage(String pkg)
      throws NoSuchMethodException, IllegalAccessException,
             ClassNotFoundException, IOException {
    progress("Processing package " + pkg + ":");
    SortedSet classes = new TreeSet();
    SortedSet subpkgs = new TreeSet();

    // Scan the paths for classes and subpackages. Store everything in
    // classes and subpkgs.
    for (Iterator i = path.iterator(); i.hasNext(); ) {
      String pathElem = (String) i.next();
      scanForPackage(pathElem, pkg, classes, subpkgs);
    }

    // Iterate over the classes found, and Japize each in turn, unless they are
    // explicitly excluded.
    for (Iterator i = classes.iterator(); i.hasNext(); ) {
      String cls = (String) i.next();
      if (!exclusions.contains(cls)) japizeClass(cls);
    }

    // For packages included with '=' instead of '+', we only include subpackages
    // if they're themselves roots.
    if (exact.contains(pkg)) {
      // The '/' character sorts after '.' and ',', but before any
      // alphanumerics, so it covers a.b.c.d and a.b.c,D but not a.b.cd. The
      // character ' ' comes before all of these.
      processRootSet(roots.subSet(packageRootTweak(pkg, ", "), endPackageRoot(pkg)));

    // Otherwise descend recursively into subpackages.
    } else {

      // Iterate over the packages found, and process each in turn, unless they
      // are explicitly excluded. If they *are* explicitly excluded, check for and
      // process any roots that lie within the excluded package.
      for (Iterator i = subpkgs.iterator(); i.hasNext(); ) {
        String subpkg = (String) i.next();
        if (!exclusions.contains(subpkg)) {
          processPackage(subpkg);
        } else {
          // Identify any roots that lie within the excluded package and process
          // them. The '/' character sorts after '.' and ',', but before any
          // alphanumerics, so it covers a.b.c.d and a.b.c,D but not a.b.cd.
          processRootSet(roots.subSet(subpkg, endPackageRoot(subpkg)));
        }
      }
    }
  }
  static void scanForPackage(String pathElem, String pkg, SortedSet classes,
                             SortedSet subpkgs) throws IOException {
    if (new File(pathElem).isDirectory()) {
      scanDirForPackage(pathElem, pkg, classes, subpkgs);
    } else {
      scanZipForPackage(pathElem, pkg, classes, subpkgs);
    }
    progress('=');
  }

  /**
   * Process a directory as entered on the command line (ie, a root of the
   * class hierarchy - the same thing that would appear in a Classpath).
   *
   * @param pathElem The name of the directory to process.
   * @param pkg The package to scan for.
   * @param classes A set to add classes found to.
   * @param subpkgs A set to add subpackages found to.
   */
  static void scanDirForPackage(String pathElem, String pkg, SortedSet classes,
                             SortedSet subpkgs) throws IOException {

    // Replace dot by slash and remove the trailing comma. It's the caller's
    // responsibility to ensure that the last character is a comma.
    pkg = pkg.substring(0, pkg.length() - 1);
    String pkgf = pkg.replace('.', '/');

    // If there is a directory of the appropriate name, recurse over it.
    File dir = new File(pathElem, pkgf);

    // Iterate over the files and directories within this directory.
    String[] entries = dir.list();
    if (entries != null) {
      for (int i = 0; i < entries.length; i++) {
        File f2 = new File(dir, entries[i]);

        // If the entry is another directory, add the package associated with
        // it to the set of subpackages.
        // "-" entry for it.
        if (f2.isDirectory()) {
          subpkgs.add(pkg + '.' + entries[i] + ',');

        // If the entry is a file ending with ".class", add the class name to
        // the set of classes.
        } else if (entries[i].endsWith(".class")) {
          classes.add(pkg + ',' +
              entries[i].substring(0, entries[i].length() - 6));
        }
      }
    }
  }

  /**
   * Process a zipfile as entered on the command line (ie, a root of the
   * class hierarchy - the same thing that would appear in a Classpath).
   *
   * @param pathElem The name of the zipfile to process.
   * @param pkg The package to scan for.
   * @param classes A set to add classes found to.
   * @param subpkgs A set to add subpackages found to.
   */
  static void scanZipForPackage(String pathElem, String pkg, SortedSet classes,
                             SortedSet subpkgs) throws IOException {

    // Replace dot by slash and remove the trailing comma. It's the caller's
    // responsibility to ensure that the last character is a comma.
    pkg = pkg.substring(0, pkg.length() - 1);
    String pkgf = pkg.replace('.', '/') + '/';

    // Iterate over all the entries in the zipfile.
    ZipFile z = new ZipFile(pathElem);
    Enumeration ents = z.entries();
    while (ents.hasMoreElements()) {
      String ze = ((ZipEntry)ents.nextElement()).getName();
  
      // If the entry is a class file and located in the package we are looking
      // for, process it.
      if (ze.startsWith(pkgf) && ze.endsWith(".class")) {

        // Trim off the package bit that we already know and the .class suffix.
        ze = ze.substring(pkgf.length(), ze.length() - 6);

        // If it's directly in the package we're processing, add it to classes.
        // If it's in a subpackage, add the top-level subpackage to subpkgs.
        if (ze.indexOf('/') >= 0) {
          subpkgs.add(pkg + '.' + ze.substring(0, ze.indexOf('/')) + ',');
        } else {
          classes.add(pkg + ',' + ze);
        }
      }
    }
  }

  /**
   * Print a usage message.
   */
  private static void printUsage() {
    System.err.println("Usage: japize [unzip] [as <name>] [lint <filename>] apis <zipfile>|<dir> ... +|-<pkg> ...");
    System.err.println("At least one +pkg is required. 'name' will have .japi and/or .gz");
    System.err.println("appended if appropriate.");
    System.err.println("The word 'apis' can be replaced by 'explicitly', 'byname', 'packages' or");
    System.err.println("'classes'. These values indicate whether something of the form a.b.C should");
    System.err.println("be treated as a class or a package. Use 'a.b,C' or 'a.b.c,' to be explicit.");
    System.exit(1);
  }

  /**
   * Construct a String consisting of every super-interface of a class
   * separated by "*".
   *
   * @param c The class to process.
   * @param s Initially "" should be passed; during recursion the string
   * produced so far is passed. This is used to ensure the same interface
   * does not appear twice in the string.
   * @return The name of every super-interface of c, separated by "*" and
   * with a leading "*".
   */
  public static String mkIfaceString(ClassWrapper c, String s) {
    return mkIfaceString(c, s, null, c);
  }
  /**
   * Construct a String consisting of every super-interface of a class
   * separated by "*".
   *
   * @param c The class to process.
   * @param s Initially "" should be passed; during recursion the string
   * produced so far is passed. This is used to ensure the same interface
   * does not appear twice in the string.
   * @param ctype If non-null, all interfaces will first be bound against
   * this type before being displayed.
   * @param wrapper The wrapper to verify type parameters against.
   * @return The name of every super-interface of c, separated by "*" and
   * with a leading "*".
   */
  public static String mkIfaceString(ClassWrapper c, String s, ClassType ctype, GenericWrapper wrapper) {

    // First iterate over the class's direct superinterfaces.
    ClassType[] ifaces = c.getInterfaces();
    for (int i = 0; i < ifaces.length; i++) {

      // Bind the interface against ctype, if supplied.
      ClassType iface = ifaces[i];
      if (ctype != null) iface = (ClassType) iface.bind(ctype);

      // If the string does not already contain the interface, and the
      // interface is public/protected, then add it to the string and
      // also process *its* superinterfaces, recursively.
      String repr = iface.getJavaRepr(wrapper);
      if ((s + "*").indexOf("*" + repr + "*") < 0) {
        int mods = iface.getWrapper().getModifiers();
        if (Modifier.isPublic(mods) || Modifier.isProtected(mods)) {
          s += "*" + repr;
        }
        s = mkIfaceString(iface.getWrapper(), s, iface, wrapper);
      }
    }

    // Finally, recursively process the class's superclass, if it has one.
    ClassType sup = c.getSuperclass();
    if (sup != null) {
      if (ctype != null) sup = (ClassType) sup.bind(ctype);
      s = mkIfaceString(sup.getWrapper(), s, sup, wrapper);
    }
    return s;
  }

  /**
   * Write out API information for a given class. Nothing will be written if
   * the class is not public/protected.
   *
   * @param n The name of the class to process.
   * @return true if the class was public/protected, false if not.
   */
  public static boolean japizeClass(String n)
      throws NoSuchMethodException, IllegalAccessException {
    try {

      // De-mangle the class name.
      if (n.charAt(0) == ',') n = n.substring(1);
      n = n.replace(',', '.');

      // Get a ClassWrapper to work on.
      ClassWrapper c = getClassWrapper(n);

      // Load the class and check its accessibility.
      if (!isEntirelyVisible(c)) {
        progress('-');
        return false;
      }

      // Construct the basic strings that will be used in the output.
      String entry = toClassRoot(c.getName()) + "!";
      String classEntry = entry;
      String type;
      if (c.isEnum()) {
        type = "enum";
      } else if (c.isAnnotation()) {
        type = "annotation";
      } else if (c.isInterface()) {
        type = "interface";
      } else {
        type = "class";
      }

      type += getTypeParamStr(c);

      int mods = c.getModifiers();
      if (c.isInterface()) {
        mods |= Modifier.ABSTRACT; // Interfaces are abstract by definition,

      } else {

        // Enums are considered non-abstract and final
        if (c.isEnum()) {
          mods |= Modifier.FINAL;
          mods &= ~Modifier.ABSTRACT;
        }

        // Classes that happen to be Serializable get their SerialVersionUID
        // output as well. The separation by the '#' character from the rest
        // of the type string has mnemonic value for Brits, as the SVUID is a
        // special sort of 'hash' of the class.
        if (c.isSerializable() && !c.isEnum() && serialIncluded(c)) {
          Long svuid = c.getSerialVersionUID();
          if (svuid == null) lintPrint(c.getName() + " has a blank final serialVersionUID");
          type += "#" + svuid;
        }
      }

      // Iterate over the class's superclasses adding them to its "type" name,
      // skipping any superclasses that are not public/protected.
      int smods = mods;
      ClassType supt = c.getSuperclass();
      while (supt != null) {
        ClassWrapper sup = supt.getWrapper();
        smods = sup.getModifiers();
        if (!Modifier.isPublic(smods) && !Modifier.isProtected(smods)) {
          lintPrint(c.getName() + " has non-public class " + sup.getName() + " among its superclasses");
          progress('^');
        } else {
          type += ":" + supt.getJavaRepr(c);
        }
        if (sup.getSuperclass() == null) {
          supt = null;
        } else {
          supt = (ClassType) sup.getSuperclass().bind(supt);
        }
      }
      type += mkIfaceString(c, "");

      // Print out the japi entry for the class itself.
      printEntry(entry, type, mods, c.isDeprecated(), false, false);

      // Get the class's members.
      BoundMemberSet members = getFieldsAndCalls(c, null);
      BoundField[] fields = members.getFields();
      BoundCall[] calls = members.getCalls();

      // Iterate over the fields in the class.
      for (int i = 0; i < fields.length; i++) {

        // Get the modifiers and type of the field.
        mods = fields[i].getModifiers();

        // Fields of interfaces are *always* public, static and final, although
        // wrapper implementations are inconsistent about telling us this.
        if (fields[i].getDeclaringClass().isInterface()) {
          mods |= Modifier.PUBLIC | Modifier.FINAL | Modifier.STATIC;
        }
        type = fields[i].getType().getTypeSig(c);

        if (fields[i].getName().equals("serialVersionUID") &&
            fields[i].getDeclaringClass() == c) {
          if (c.isInterface()) {
            lintPrint("Useless serialVersionUID field in interface " + c.getName());
          } else if (!Modifier.isStatic(fields[i].getModifiers()) ||
                     !Modifier.isFinal(fields[i].getModifiers())) {
            lintPrint("serialVersionUID field in " + c.getName() + " not 'static final'");
          } else if (!c.isSerializable()) {
            lintPrint("serialVersionUID field in non-serializable class " + c.getName());
          }
        }

        // If the field is nonfinal and either public or static, the class it's declared
        // in can affect the behavior of code that uses it. However, we don't want to
        // expose nonpublic classes so we find the nearest public/protected class instead.
        // FIXME: difficult outstanding bug here: A and B both extend (nonpublic) C,
        // C has a static field f. A.f and B.f are the same field but with this
        // algorithm you can't tell that. What to do, what to do?
        if (!Modifier.isFinal(fields[i].getModifiers()) &&
            (Modifier.isPublic(fields[i].getModifiers()) ||
             Modifier.isStatic(fields[i].getModifiers()))) {
          ClassWrapper wrapper = c;
          String declaringName = wrapper.getName();
          while (!wrapper.equals(fields[i].getDeclaringClass())) {
            wrapper = wrapper.getSuperclass().getWrapper();
            if (Modifier.isPublic(wrapper.getModifiers()) ||
                Modifier.isProtected(wrapper.getModifiers())) {
              declaringName = wrapper.getName();
            }
          }
          type += '=' + declaringName;
        }

        // A static, final field is a primitive constant if it is initialized to
        // a compile-time constant.
        if (fields[i].isPrimitiveConstant()) {
          Object o = fields[i].getPrimitiveValue();

          // Character values get int-ized to keep the output nice and 7bit.
          if (o instanceof Character) {
            type += ":" + (int)((Character)o).charValue();
          
          // String values get newlines and backslashes escaped to stop them from
          // going onto a second line.
          } else if (o instanceof String) {
            String val = (String)o;
            StringBuffer sb = new StringBuffer('\"');
            int p = 0, q = 0;
            while (q >= 0) {
              q = val.indexOf("\n", p);
              int r = val.indexOf("\\", p);
              if (r >= 0 && (r < q || q < 0)) q = r;
              if (q >= 0) {
                sb.append(val.substring(p, q));
                sb.append('\\');
                if (val.charAt(q) == '\\') sb.append('\\'); else sb.append('n');
                p = ++q;
              }
            }
            sb.append(val.substring(p));
            type += ":" + sb;

          // Floats and doubles get their toRaw*Bits() value printed as well as
          // their actual value.
          } else if (o instanceof Float) {
            type += ':' + o.toString() + '/' +
                Integer.toHexString(Float.floatToRawIntBits(
                                    ((Float) o).floatValue()));
          } else if (o instanceof Double) {
            type += ':' + o.toString() + '/' +
                Long.toHexString(Double.doubleToRawLongBits(
                                 ((Double) o).doubleValue()));

          // Other types just get output.
          } else {
            type += ":" + o;
          }
        }

        // Skip things that aren't entirely visible as defined below.
        if (!isEntirelyVisible(fields[i])) continue;

        // Output the japi entry for the field.
        printEntry(classEntry + "#" + fields[i].getName(), type, mods,
                   fields[i].isDeprecated() || c.isDeprecated(), fields[i].isEnumField(), false);
      }

      // Iterate over the methods and constructors in the class.
      for (int i = 0; i < calls.length; i++) {

        // Skip calls called <init> and <clinit>. Constructors are handled
        // with an empty method name, and class initializers are never part of
        // the public API.
        // This code is probably dead since I'm pretty sure ClassFile knows to
        // give us these in the right form.
        if ("<init>".equals(calls[i].getName()) ||
            "<clinit>".equals(calls[i].getName())) {
          continue;
        }

        // Skip methods in interfaces that are also defined identically in
        // Object. Specifically, it needs to be defined in Object with
        // *exactly* the same parameter types, return types *and* thrown
        // exceptions (because it *is* legal for an interface to specify,
        // say, "Object clone();" and thereby specify that implementors must
        // not throw CloneNotSupportedException from their clone method.
        // Surprisingly, Cloneable doesn't do this...)
        if (c.isInterface() &&
            objCalls.contains(getObjComparableString(calls[i]))) {
          progress(';');
          continue;
        }

        // Skip things that aren't entirely visible as defined below.
        if (!isEntirelyVisible(calls[i])) continue;

        // Construct the name of the method, of the form Class!method(params).
        entry = classEntry + calls[i].getName() + "(";
        Type[] params = calls[i].getParameterTypes();
        String comma = "";
        for (int j = 0; j < params.length; j++) {
          entry += comma + params[j].getTypeSig(calls[i]);
          comma = ",";
        }
        entry += ")";
        if (!calls[i].isVisible14()) {
          entry += "+";
        } else if (!calls[i].isVisible15()) {
          entry += "-";

          // Find the next call that would be included. If it has the exact same
          // nonGenericSig, we need a double-minus instead of a
          // single one.
          if (i < calls.length) {
            for (int j = i + 1; j < calls.length; j++) {
              if (!calls[j].getNonGenericSig().equals(calls[i].getNonGenericSig())) break;

              // All the reasons we might skip an item above, we skip it here as well.
              if ("<init>".equals(calls[j].getName()) ||
                  "<clinit>".equals(calls[j].getName())) {
                continue;
              }
              if (c.isInterface() &&
                  objCalls.contains(getObjComparableString(calls[j]))) {
                continue;
              }
              if (!isEntirelyVisible(calls[j])) continue;

              entry += "-";
              break;
            }
          }
        }

        // Construct the "type" field, of the form returnType*exception*except2...
        type = "";

        // ... but if it's a generic method it gets the type parameters first
        type += getTypeParamStr(calls[i]);

        Type rtnType = calls[i].getReturnType();
        type += (rtnType == null) ? "constructor" : rtnType.getTypeSig(calls[i]);

        boolean isStub = false;
        NonArrayRefType[] excps = calls[i].getExceptionTypes();
        for (int j = 0; j < excps.length; j++) {
          if (includeException(excps, j)) type += "*" + excps[j].getJavaRepr(calls[i]);
          if (excps[j] instanceof ClassType &&
              ((ClassType) excps[j]).getName().endsWith(".NotImplementedException")) {
            isStub = true;
          }
        }

        // Get the modifiers for this method. Methods of interfaces are
        // by definition public and abstract, although wrapper implementations
        // are inconsistent about telling us this.
        int mmods = calls[i].getModifiers();
        if (c.isInterface()) {
          mmods |= Modifier.ABSTRACT | Modifier.PUBLIC;
        }

        // Methods of final classes are by definition final
        if (Modifier.isFinal(c.getModifiers())) {
          mmods |= Modifier.FINAL;
        }

        // Methods of enums are by definition final and non-abstract
        if (c.isEnum()) {
          mmods |= Modifier.FINAL;
          mmods &= ~Modifier.ABSTRACT;
        }

        // Constructors are never final. The verifier should enforce this
        // so this should always be a no-op, except for when the line above
        // set it.
        if ("".equals(calls[i].getName())) {
          mmods &= ~Modifier.FINAL;
        }

        // Print the japi entry for the method.
        printEntry(entry, type, mmods, calls[i].isDeprecated() || c.isDeprecated(), false, isStub);
      }

      // Return true because we did parse this class.
      progress('+');
      return true;
    } catch (ClassNotFoundException e) {
      System.err.println("\nFailed to Japize " + n + ": " + e);
      e.printStackTrace();
    } catch (RuntimeException e) {
      System.err.println("\nFailed to Japize " + n + ": " + e);
      e.printStackTrace();
    }
    return false;
  }

  private static String getTypeParamStr(GenericWrapper wrapper) {
    TypeParam[] tparams = wrapper.getTypeParams();
    String type = "";
    if (tparams != null) {
      type += "<";
      for (int i = 0; i < tparams.length; i++) {
        if (i > 0) type += ",";
        for (int j = 0; j < tparams[i].getBounds().length; j++) {
          if (j > 0) type += "&";
          type += tparams[i].getBounds()[j].getTypeSig(wrapper);
        }
      }
      type += ">";
    }
    return type;
  }

  /**
   * Load all the fields and calls for a particular class, taking inheritance into account.
   */
  private static BoundMemberSet getFieldsAndCalls(ClassWrapper outer, ClassType ctype) {
    ClassWrapper c = (ctype != null ? ctype.getWrapper() : outer);
    BoundMemberSet members = new BoundMemberSet();
    ClassType[] ifaces = c.getInterfaces();
    for (int i = 0; i < ifaces.length; i++) {
      ClassType iface = ifaces[i];
      if (ctype != null) iface = (ClassType) iface.bind(ctype);
      members.bindAndAddAll(getFieldsAndCalls(outer, iface), ctype);
    }
    ClassType sup = c.getSuperclass();
    if (sup != null) {
      if (ctype != null) sup = (ClassType) sup.bind(ctype);
      members.bindAndAddAll(getFieldsAndCalls(outer, sup), ctype);
    }
    FieldWrapper[] fields = c.getFields();
    for (int i = 0; i < fields.length; i++) {
      members.bindAndAdd(new BoundField(fields[i]), ctype);
    }
    CallWrapper[] calls = c.getCalls();
    for (int i = 0; i < calls.length; i++) {
      BoundCall call = new BoundCall(calls[i], outer);

      if (ctype == null || call.isInheritable()) {

        // JDK15: handle bridge methods (the ACC_VOLATILE bit corresponds to the ACC_BRIDGE bit)
        // These get immediately bind14()d because they are only visible in the 1.4 view of the universe
        if (Modifier.isVolatile(calls[i].getModifiers())) call = call.bind14();

        members.bindAndAdd(call, ctype);
      }
    }
    return members;
  }

  /**
   * Get a string containing the name, parameter types and thrown exceptions
   * for a particular method. Returns null on a constructor. Designed to
   * allow comparing interface methods against Object methods. NOTE that this will
   * potentially not work correctly if generic methods are ever added to Object
   * itself (because of "@0" etc meaning different things). Oh, how I hope that
   * never happens...
   */
  private static String getObjComparableString(CallWrapper call) throws ClassNotFoundException {
    if (call.getName().equals("")) return null;
    String s = call.getName() + "(";
    Type[] params = call.getParameterTypes();
    for (int i = 0; i < params.length; i++) {
      if (i > 0) s += ",";
      s += params[i].getTypeSig(call);
    }
    s += ")" + call.getReturnType().getTypeSig(call);
    NonArrayRefType[] excps = call.getExceptionTypes();
    TreeSet exstrs = new TreeSet();
    for (int i = 0; i < excps.length; i++) {
      if (includeException(excps, i)) exstrs.add(excps[i].getJavaRepr(call));
    }
    for (Iterator i = exstrs.iterator(); i.hasNext(); ) {
      s += "*" + i.next();
    }
    return s;
  }


  /**
   * Print a japi file entry. The format of a japi file entry is space-separated
   * with 3 fields - the name of the "thing", the modifiers, and the type
   * (which generally includes more information than *just* the type; see the
   * implementation of japizeClass for what actually gets passed in here).
   * The modifiers are represented as a six-letter string consisting of 1
   * character each for the accessibility ([P]ublic or [p]rotected), the
   * abstractness ([a]bstract or [c]oncrete), the staticness ([s]tatic or
   * [i]nstance), the finalness ([f]inal or [n]onfinal, or [e]num field),
   * the deprecatedness ([d]eprecated or [u]ndeprecated) and whether the
   * method is a [S]tub or [r]eal.
   *
   * @param thing The name of the "thing" (eg class, field, etc) to print.
   * @param type The contents of the "type" field.
   * @param mods The modifiers of the thing, as returned by {Class, Field,
   * Method, Constructor}.getModifiers().
   * @param deprecated Whether the thing is deprecated.
   * @param enumField Whether the field is an enum field.
   * @param stub Whether the method is a stub.
   */
  public static void printEntry(String thing, String type, int mods,
                                boolean deprecated, boolean enumField,
                                boolean stub) {
    if (!Modifier.isPublic(mods) && !Modifier.isProtected(mods)) return;
    if (thing.startsWith("java.lang,Object!")) out.print('+');
    if (thing.startsWith("java.lang,") ||
        thing.startsWith("java.lang.")) out.print('+');
    out.print(thing);
    out.print(' ');
    out.print(Modifier.isPublic(mods) ? 'P' : 'p');
    out.print(Modifier.isAbstract(mods) ? 'a' : 'c');
    out.print(Modifier.isStatic(mods) ? 's' : 'i');
    out.print(enumField ? 'e' : Modifier.isFinal(mods) ? 'f' : 'n');
    out.print(deprecated ? 'd' : 'u');
    out.print(stub ? 'S' : 'r');
    out.print(' ');
    out.println(type);
  }


  /**
   * Trivial utility method to get the wrapper for a superclass or null if there
   * isn't one.
   */
  static ClassWrapper getWrapper(ClassType t) {
    return t == null ? null : t.getWrapper();
  }


  /**
   * Determine whether the type parameter bounds of an item are entirely visible.
   */
  static boolean paramsEntirelyVisible(GenericWrapper wrapper) {
    // Return true for now because otherwise you tend to get into infinite loops with, what else,
    // Enum<T extends Enum<T>>...
    return true;
//    TypeParam[] params = TypeParam.getAllTypeParams(wrapper);
//    if (params != null) {
//      for (int i = 0; i < params.length; i++) {
//        if (!isEntirelyVisible(params[i])) return false;
//      }
//    }
//    return true;
  }

  /**
   * Determine whether a class is entirely visible. If it's not then it should be skipped.
   * A class is entirely visible if it's public or protected, its declaring
   * / containing class, if any, is entirely visible, and all the bounds of its
   * type parameters are entirely visible.
   */
  static boolean isEntirelyVisible(ClassWrapper cls) {
    if (!cls.isPublicOrProtected()) {
      return false;
    }

    // Declaring and containing class should be the same but a redundant
    // check doesn't harm anything. The difference is subtle and I'm not
    // 100% sure.

    ClassWrapper declaring = cls.getDeclaringClass();
    if (declaring != null && !isEntirelyVisible(declaring)) return false;

    ClassWrapper containing = (ClassWrapper) cls.getContainingWrapper();
    if (containing != null && !isEntirelyVisible(containing)) return false;

    return paramsEntirelyVisible(cls);
  }

  /**
   * Determine whether a type is entirely visible. If it's not then it should be skipped.
   * A type is entirely visible if it's a class that's entirely visible and all of its
   * type arguments are entirely visible, or it's a primitive type, or it's an
   * array type whose element type is entirely visible, or it's a wildcard type or type
   * parameter whose bounds are entirely visible.
   */
  static boolean isEntirelyVisible(Type t) {
    if (t == null) {
      return true;
    } else if (t instanceof PrimitiveType) {
      return true;
    } else if (t instanceof ArrayType) {
      return isEntirelyVisible(((ArrayType) t).getElementType());
    } else if (t instanceof ClassType) {
      if (!isEntirelyVisible(((ClassType) t).getWrapper())) return false;
      RefType[] args = ((ClassType) t).getTypeArguments();
      if (args != null) {
        for (int i = 0; i < args.length; i++) {
          if (!isEntirelyVisible(args[i])) return false;
        }
      }
      return true;
    } else if (t instanceof TypeParam) {
      // return true for now because otherwise we tend to get into an infinite loop on, what else,
      // Enum<T extends Enum<T>>
//      NonArrayRefType[] bounds = ((TypeParam) t).getBounds();
//      for (int i = 0; i < bounds.length; i++) {
//        if (!isEntirelyVisible(bounds[i])) return false;
//      }
      return true;
    } else if (t instanceof WildcardType) {
      RefType upper = ((WildcardType) t).getUpperBound();
      RefType lower = ((WildcardType) t).getLowerBound();
      if (lower != null && !isEntirelyVisible(lower)) return false;
      return isEntirelyVisible(upper);
    } else {
      throw new RuntimeException("Unknown kind of Type " + t.getClass());
    }
  }

  /**
   * Determine whether a field is entirely visible. If it's not then it should be skipped.
   * A field is entirely visible if it is itself public or protected and its type is
   * entirely visible.
   */
  static boolean isEntirelyVisible(FieldWrapper field) {
    if (!Modifier.isPublic(field.getModifiers()) && !Modifier.isProtected(field.getModifiers())) {
      return false;
    }

    if (!isEntirelyVisible(field.getType())) {
      lintPrint("field " + field.getDeclaringClass().getName() + "." + field.getName() +
                " has non-public type " + field.getType().getTypeSig(field.getDeclaringClass()));
      return false;
    }
    return true;
  }

  /**
   * Determine whether a method or constructor is entirely visible. If it's not then it
   * should be skipped.
   * A call is entirely visible if its return type is entirely visible, all of its
   * parameter types are entirely visible, all of its thrown exception types are
   * entirely visible, and all the bounds of its type parameters are entirely visible.
   */
  static boolean isEntirelyVisible(CallWrapper call) {
    if (!Modifier.isPublic(call.getModifiers()) && !Modifier.isProtected(call.getModifiers())) {
      return false;
    }
    if (!paramsEntirelyVisible(call)) return false;

    boolean result = true;
    if (!isEntirelyVisible(call.getReturnType())) {
      result = false;
      lintPrint("method " + call.getDeclaringClass().getName() + "." + call.getName() +
                "() has non-public return type " + call.getReturnType().getTypeSig(call.getDeclaringClass()));
    }
    for (int i = 0; i < call.getParameterTypes().length; i++) {
      if (!isEntirelyVisible(call.getParameterTypes()[i])) {
        result = false;
        lintPrint("method " + call.getDeclaringClass().getName() + "." + call.getName() +
                  "() has non-public type " + call.getParameterTypes()[i].getTypeSig(call.getDeclaringClass()) +
                  " among its parameters");
      }
    }
    // For now don't worry about exception types. Later we may handle them a different way
    // (eg by rendering each one as its closest accessible superclass). But we'll still
    // print the error.
    for (int i = 0; i < call.getExceptionTypes().length; i++) {
      if (!isEntirelyVisible(call.getExceptionTypes()[i])) {
        lintPrint("method " + call.getDeclaringClass().getName() + "." + call.getName() +
                  "() throws non-public exception " + call.getExceptionTypes()[i].getTypeSig(call.getDeclaringClass()));
      }
    }
    return result;
  }

  /**
   * Check to see if an exception should be included in the list of exceptions.
   * Subclasses of RuntimeException and Error should be omitted, as should
   * subclasses of other exceptions also thrown.
   */
  static boolean includeException(NonArrayRefType[] excps, int index)
      throws ClassNotFoundException {
    boolean isSuper = false;
    ClassType excp;
    if (excps[index] instanceof ClassType) {
      excp = (ClassType) excps[index];
    } else {
      TypeParam tp = (TypeParam) excps[index];
      while (tp.getPrimaryConstraint() instanceof TypeParam) {
        tp = (TypeParam) tp.getPrimaryConstraint();
      }
      excp = (ClassType) tp.getPrimaryConstraint();
    }
    for (ClassWrapper supclass = excp.getWrapper();
         supclass != null;
         supclass = getWrapper(supclass.getSuperclass())) {
      String supname = supclass.getName();
      if ("java.lang.RuntimeException".equals(supname) ||
          "java.lang.Error".equals(supname)) {
        return false;
      }
      if (isSuper) {
        for (int i = 0; i < excps.length; i++) {
          if (i != index && excps[i] instanceof ClassType &&
              supname.equals(((ClassType) excps[i]).getName())) return false;
        }
      }
      isSuper = true;
    }
    return true;
  }
  
  /**
   * Check a class name against the global 'roots' and 'exclusions' sets
   * to see if it should be included. A class should be included if
   * it is inside a package that has a roots entry, and not inside a deeper
   * package that has an exclusions entry.
   *
   * @param cname the name of the class to check.
   * @return true if the class should be included, false if not.
   */
  public static boolean checkIncluded(String cname) {
    return checkIncluded(cname, roots, exclusions);
  }
  public static boolean serialIncluded(String cname) {
    return checkIncluded(cname, serialRoots, serialExclusions);
  }
  public static boolean serialIncluded(ClassWrapper c) {
    if (!serialIncluded(toClassRoot(c.getName()))) return false;

    ClassType supt = c.getSuperclass();
    if (supt != null && !serialIncluded(supt.getWrapper())) return false;

    return true;
  }
  public static boolean checkIncluded(String cname, Set roots, Set exclusions) {

    if (roots.contains(cname)) return true;
    if (exclusions.contains(cname)) return false;
    
    // Loop backwards over the "."s in the class's name.
    int i = cname.indexOf(',');
    while (i >= 0) {
      cname = cname.substring(0, i);
      String mangled = cname + ',';

      // Check whether there is an entry for the package name up to the ".".
      // If so we know what to do so we return the result; otherwise we
      // continue at the next ".".
      if (roots.contains(mangled)) return true;
      if (exclusions.contains(mangled)) return false;
      i = cname.lastIndexOf('.');
    }

    // If we ran out of dots before finding a match, we need to check the root
    // package.
    return roots.contains(",");
  }

  /**
   * Set the classpath for the appropriate implementation we are using.
   *
   * @param cp The classpath to set.
   */
  public static void setClasspath(String cp) throws IOException {
    ClassFile.setClasspath(cp);
  }
  /**
   * Construct the appropriate type of ClassWrapper object for the processing we
   * are doing. 
   *
   * @param className The fully-qualified name of the class to get a wrapper
   * for.
   * @return A ClassWrapper object for the specified class.
   */
  public static ClassWrapper getClassWrapper(String className) 
      throws  ClassNotFoundException {
    return ClassFile.forName(className);
  }

  /**
   * Encode a string. The encoding is:
   * "\" translates to "\\"
   * newline translates to "\n"
   * all other characters except for 0-9a-zA-Z translate to \ uNNNN where N is the unicode value.
   */
  private static String jencode(String str) {
    StringBuffer sb = new StringBuffer(str.length());
    for (int i = 0; i < str.length(); i++) {
      char ch = str.charAt(i);
      if (ch == '\\') {
        sb.append("\\\\");
      } else if (ch == '\n') {
        sb.append("\\n");
      } else if ((ch >= '0' && ch <= '9') ||
                 (ch >= 'a' && ch <= 'z') ||
                 (ch >= 'A' && ch <= 'Z')) {
        sb.append(ch);
      } else {
        sb.append("\\u" + to4charHexString(ch));
      }
    }
    return sb.toString();
  }
  private static String to4charHexString(char ch) {
    String result = Integer.toHexString((int) ch);
    if (result.length() > 4) throw new RuntimeException("toHexString gave a longer than 4-char output");
    while (result.length() < 4) result = "0" + result;
    return result;
  }
}
