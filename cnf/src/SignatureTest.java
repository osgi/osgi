import java.util.*;

import aQute.bnd.service.*;
import aQute.lib.osgi.*;

public class SignatureTest implements AnalyzerPlugin {

    public boolean analyzeJar(Analyzer analyzer) throws Exception {
        String s = analyzer.getProperty("-signaturetest");
        Map<String, Map<String, String>> hdr = analyzer.parseHeader(s);
        for (String key : hdr.keySet()) {
            Instruction instr = Instruction.getPattern(key.replace('.', '/'));
            if (instr.isNegated())
                analyzer.error("Can not use negatives for signature test: %s",
                        key);

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
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

}
