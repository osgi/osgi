import java.io.File;
import java.util.jar.Manifest;

import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Constants;
import aQute.bnd.osgi.Jar;
import aQute.bnd.version.Version;

public class BundleInfo {
	public final String		bsn;
	public final Version	bv;

	public BundleInfo(Analyzer analyzer, File sub) throws Exception {
		try (Jar s = new Jar(sub)) {
			Manifest m = s.getManifest();
			String name = m.getMainAttributes()
					.getValue(Constants.BUNDLE_SYMBOLICNAME);
			if (name == null) {
				analyzer.error(
						"Invalid bundle in flattening a path (no Bundle-SymbolicName set): %s",
						sub.getAbsolutePath());
				bsn = sub.getName();
				bv = null;
				return;
			}

			int n = name.indexOf(';');
			if (n > 0)
				name = name.substring(0, n);
			bsn = name.trim();

			String version = m.getMainAttributes()
					.getValue(Constants.BUNDLE_VERSION);
			bv = Version.parseVersion(version);
		}
	}

	public String canonicalName() {
		if (bv == null) {
			return bsn;
		}
		String name = bsn + "-" + bv.getMajor() + "." + bv.getMinor() + "."
				+ bv.getMicro() + ".jar";
		return name;
	}
}
