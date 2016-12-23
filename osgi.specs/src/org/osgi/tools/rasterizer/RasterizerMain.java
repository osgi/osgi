/*
 * Copyright (c) OSGi Alliance (2012, 2016). All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.osgi.tools.rasterizer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Front end to Apache Batik.
 *
 * This class converts svg images into png images for
 * use with the HTML version of the specs.
 *
 * @author $Id$
 */
public class RasterizerMain {

	/**
	 * @param args Command line arguments for rasterization.
	 * @throws Throwable If unable to execute the rasterization process.
	 */
	public static void main(String[] args) throws Throwable {
		if (args.length < 2) {
			System.err
					.println(
							"Usage: <cmd> [output path] [svg file]... ");

			return;
		}

		Float scale = null;
		String scaleProperty = System.getProperty("scale");

		if (scaleProperty != null) {
			scale = Float.valueOf(scaleProperty);
		}

		Float dpi = null;
		String dpiProperty = System.getProperty("dpi");

		if (dpiProperty != null) {
			dpi = Float.valueOf(dpiProperty);
		}

		Path outputPath = Paths.get(args[0]);

		List<String> svgPaths = Arrays.asList(args).subList(1, args.length);

		if (svgPaths.isEmpty()) {
			System.err.println("No SVG files were specified");

			return;
		}

		Rasterizer rasterizer = new Rasterizer();

		rasterizer.processPaths(svgPaths, outputPath, scale, dpi);
	}

}
