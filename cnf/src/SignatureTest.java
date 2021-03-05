/*******************************************************************************
 * Copyright (c) Contributors to the Eclipse Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0 
 *******************************************************************************/
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import aQute.bnd.header.Parameters;
import aQute.bnd.make.coverage.CoverageResource;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.AnalyzerPlugin;

public class SignatureTest implements AnalyzerPlugin {

	public boolean analyzeJar(Analyzer analyzer) throws Exception {
        String s = analyzer.getProperty("-signaturetest");
		Parameters hdr = analyzer.parseHeader(s);
		Set<Clazz> classes = new HashSet<Clazz>();
        for (String key : hdr.keySet()) {
			Instruction instr = new Instruction(key.replace('.', '/'));
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
							if (path.endsWith("package-info.class")) {
								continue;
							}
                            if (path.endsWith(".class")) {
                                path = path.substring(0, path.length() - 6);
                                analyzer.getJar().putResource(
                                        "OSGI-INF/signature/" + path,
                                        r.getValue());
                                
								classes.add(new Clazz(analyzer, path, r
										.getValue()));
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
        	analyzer.getJar().putResource("OSGI-INF/coverage.xml",  new CoverageResource(analyzer.getClassspace().values(), classes));
        }
        return false;
    }
	
}
