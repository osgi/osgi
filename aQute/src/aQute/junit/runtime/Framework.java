package aQute.junit.runtime;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.*;

import org.osgi.framework.*;
import org.osgi.framework.launch.*;

public class Framework {
    private String           framework;
    private final List      /* <File> */jars             = new ArrayList/* <File> */();
    private final List      /* <File> */bundles          = new ArrayList/* <File> */();
    private final List      /* <String> */systemPackages = new ArrayList/* <String> */();
    private SystemBundle     systemBundle;
    private File             storage;
    private boolean          keep;
    private final Properties properties;

    public Framework(Properties properties) {
        this.properties = properties;
        systemPackages.add("org.osgi.framework");
        systemPackages.add("org.osgi.framework.launch");
    }

    public boolean activate() throws Exception {
        boolean error = false;
        systemBundle = createFramework();
        if (systemBundle == null)
            return false;

        systemBundle.start();
        // Initialize this framework so it becomes STARTING
        BundleContext systemContext = getSystemBundleContext();

        // Install the set of bundles
        List/* <Bundle> */installed = new ArrayList/* <Bundle> */();

        for (Iterator/* <File> */i = bundles.iterator(); i.hasNext();) {
            File path = ((File) i.next()).getAbsoluteFile();

            InputStream in = new FileInputStream(path);
            try {
                Bundle bundle = systemContext
                        .installBundle(path.toString(), in);
                installed.add(bundle);
            } catch (BundleException e) {
                System.out.print("Install: " + path + " ");
                report(e, System.out);
                error = true;
            } finally {
                in.close();
            }
        }

        for (Iterator/* <Bundle> */i = installed.iterator(); i.hasNext();) {
            Bundle b = (Bundle) i.next();
            try {
                b.start();
            } catch (BundleException e) {
                System.out.print("Start: " + b.getBundleId() + " ");
                report(e, System.out);
                error = true;
            }
        }
        return !error;
    }

    public void deactivate() throws Exception {
        if (systemBundle != null) {
            getSystemBundle().stop();
            waitForStop(0);
        }
    }

    public void addSystemPackage(String packageName) {
        systemPackages.add(packageName);
    }

    private SystemBundle createFramework() throws Exception {
        Properties p = new Properties();
        p.putAll(properties);

        if (storage != null) {
            if (!keep)
                delete(storage);

            storage.mkdirs();
            if (!storage.isDirectory())
                throw new IllegalArgumentException();

            p.setProperty(Constants.FRAMEWORK_STORAGE, storage
                    .getAbsolutePath());
        }

        if (!systemPackages.isEmpty())
            p.setProperty(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
                    toPackages(systemPackages));

        URL urls[] = new URL[jars.size()];
        for (int i = 0; i < jars.size(); i++) {
            urls[i] = ((File) jars.get(i)).toURL();
        }

        URLClassLoader loader = new URLClassLoader(urls, getClass()
                .getClassLoader());

        try {
            Class/* <?> */clazz = loader.loadClass(framework);
            Constructor/* <?> */ctor = clazz
                    .getConstructor(new Class[] { Map.class });

            SystemBundle systemBundle;
            systemBundle = (SystemBundle) ctor.newInstance(new Object[] { p });
            systemBundle.init();
            return systemBundle;
        } catch (ClassNotFoundException cnfe) {
            System.err
                    .println("Can not load the framework class: " + framework);
            return null;
        } catch (NoSuchMethodException nsme) {
            System.err
                    .println("Can not find RFC 132 constructor <init>(Map) in "
                            + framework);
            return null;
        } catch (InvocationTargetException e) {
            System.err.println("Error in constructing framework");
            e.getCause().printStackTrace();
            throw e;
        }
    }

    private String toPackages(List/* <String> */packs) {
        String del = "";
        StringBuilder sb = new StringBuilder();
        for (Iterator i = packs.iterator(); i.hasNext();) {
            String s = (String) i.next();
            sb.append(del);
            sb.append(s);
            del = ", ";
        }
        return sb.toString();
    }

    public void addBundle(File resource) {
        bundles.add(resource);
    }

    public void addJar(File resource) {
        jars.add(resource);
    }

    public BundleContext getSystemBundleContext() {
        return systemBundle.getBundleContext();
    }

    public SystemBundle getSystemBundle() {
        return systemBundle;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getFramework() {
        return framework;
    }

    public BundleContext getBundleContext() {
        return systemBundle.getBundleContext();
    }

    public void waitForStop(long time) throws Exception {
        getSystemBundle().waitForStop(time);
    }

    public Bundle getBundle(String target) {
        Bundle bundles[] = getBundleContext().getBundles();
        for (int i = 0; i < bundles.length; i++) {
            if (bundles[i].getLocation().equals(target)) {
                return bundles[i];
            }
        }
        return null;
    }

    public void setKeep() {
        this.keep = true;
    }

    public void setStorage(File storage) {
        this.storage = storage;
    }

    void delete(File f) {
        if (f.getAbsolutePath().matches("(/|[a-zA-Z]:\\\\*|\\\\)"))
            throw new IllegalArgumentException(
                    "You can not make the root the storage area because it will be deleted");
        if (f.isDirectory()) {
            File fs[] = f.listFiles();
            for (int i = 0; i < fs.length; i++)
                delete(fs[i]);
        }
        f.delete();
    }

    public void report(PrintStream out) {
        out.println();
        out.println("Framework             " + framework);
        out.println("SystemBundle          " + systemBundle.getClass());
        out.println("Storage               " + storage);
        out.println("Keep                  " + keep);
        list("Jars                  ", jars);
        list("System Packages       ", systemPackages);
        list("Classpath             ", Arrays.asList(System.getProperty(
                "java.class.path").split(File.pathSeparator)));
        out.println("Properties");
        for (Iterator i = properties.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            out.print(fill(key, 40));
            out.println(value);
        }
        BundleContext context = systemBundle.getBundleContext();
        if (context != null) {
            Bundle bundles[] = context.getBundles();
            System.out.println();
            System.out.println("Id    State Modified      Location");

            for (int i = 0; i < bundles.length; i++) {
                File f = new File(bundles[i].getLocation());
                out.print(fill(Long.toString(bundles[i].getBundleId()), 6));
                out.print(fill(toState(bundles[i].getState()), 6));
                if (f.exists())
                    out.print(fill(toDate(f.lastModified()), 14));
                else
                    out.print(fill("<>", 14));
                out.println(bundles[i].getLocation());
            }
        }
    }

    String toDate(long t) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(t);
        return fill(c.get(Calendar.YEAR), 4) + fill(c.get(Calendar.MONTH), 2)
                + fill(c.get(Calendar.DAY_OF_MONTH), 2)
                + fill(c.get(Calendar.HOUR_OF_DAY), 2)
                + fill(c.get(Calendar.MINUTE), 2);
    }

    private String fill(int n, int width) {
        return fill(Integer.toString(n), width, '0', -1);
    }

    private String fill(String s, int width) {
        return fill(s, width, ' ', -1);
    }

    private String fill(String s, int width, char filler, int dir) {
        StringBuffer sb = new StringBuffer();
        if (s.length() > width)
            s = s.substring(0, width - 2) + "..";

        width -= s.length();
        int before = (dir == 0) ? width / 2 : (dir < 0) ? 0 : width;
        int after = width - before;

        while (before-- > 0)
            sb.append(filler);

        sb.append(s);

        while (after-- > 0)
            sb.append(filler);

        return sb.toString();
    }

    private String toState(int state) {
        switch (state) {
        case Bundle.INSTALLED:
            return "INSTL";
        case Bundle.RESOLVED:
            return "RSLVD";
        case Bundle.STARTING:
            return "STRTD";
        case Bundle.STOPPING:
            return "STPPD";
        case Bundle.ACTIVE:
            return "ACTIV";
        case Bundle.UNINSTALLED:
            return "UNNST";
        }
        return "? " + state;
    }

    private void list(String del, List l) {
        for (Iterator i = l.iterator(); i.hasNext();) {
            String s = i.next().toString();
            System.out.println(del + s);
            del = "                                                                       "
                    .substring(0, del.length());
        }
    }

    public static void report(BundleException e, PrintStream out) {
        switch (e.getType()) {
        case BundleException.ACTIVATOR_ERROR:
            System.err.println("Caused by in activator: ");
            e.getCause().printStackTrace();
            break;

        default:
        case BundleException.DUPLICATE_BUNDLE_ERROR:
        case BundleException.INVALID_OPERATION:
        case BundleException.MANIFEST_ERROR:
        case BundleException.NATIVECODE_ERROR:
        case BundleException.STATECHANGE_ERROR:
        case BundleException.UNSUPPORTED_OPERATION:
        case BundleException.UNSPECIFIED:
        case BundleException.RESOLVE_ERROR:
            System.err.println(e.getMessage());
            break;
        }
    }

}
