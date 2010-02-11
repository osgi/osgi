import java.util.*;

import aQute.bnd.make.coverage.*;
import aQute.bnd.service.*;
import aQute.lib.osgi.*;

public class SignatureTest implements AnalyzerPlugin {

	public boolean analyzeJar(Analyzer analyzer) throws Exception {
        String s = analyzer.getProperty("-signaturetest");
        Map<String, Map<String, String>> hdr = analyzer.parseHeader(s);
		Set<Clazz> classes = new HashSet<Clazz>();
        for (String key : hdr.keySet()) {
            Instruction instr = Instruction.getPattern(key.replace('.', '/'));
            if (instr.isNegated())
                analyzer.error("Can not use negatives for signature test: %s",
                        key);

			
			foreachclasspathentry:
            for (Jar cpe : analyzer.getClasspath()) {

                for (String packageName : cpe.getDirectories().keySet()) {
                    
                    if (instr.matches(packageName)) {
                        
                        Map<String, Resource> contents = cpe.getDirectories().get(
                                packageName);
                        
                        for (Map.Entry<String, Resource> r : contents.entrySet()) {
                            String path = r.getKey();
                            if (path.endsWith(".class")) {
                                path = path.substring(0, path.length() - 6);
                                analyzer.getJar().putResource(
                                        "OSGI-INF/signature/" + path,
                                        r.getValue());
                                
                                classes.add(new Clazz(path,r.getValue()));
                            }
                        }
						/*
						 * Only use the first instance of the package
						 * encountered. Do not merge.
						 */
						break foreachclasspathentry;
                    }
                }
            }
        }
        if ( classes.size() > 0 ) {
        	analyzer.setProperty("Bnd-AddXMLToTest", "OSGI-INF/coverage.xml");        	
        	analyzer.getJar().putResource("OSGI-INF/coverage.xml",  new CoverageResource(analyzer.getClassspace().values(), classes));
        }
        return false;
    }
	
}
