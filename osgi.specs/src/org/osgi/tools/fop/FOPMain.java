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

package org.osgi.tools.fop;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Front end to FOP.
 * 
 * This class makes sure all the spec fonts are registered with the VM before
 * calling FOP. The directories containing the spec fonts must be specified on
 * the JAVA_FONTS system property.
 * 
 * @author $Id$
 */
public class FOPMain {

	/**
	 * Register any TrueType fonts in JAVA_FONTS and call FOP main method with
	 * the command line arguments.
	 * 
	 * @param args Command line arguments to pass to FOP main.
	 * @throws Throwable If unable to load or call FOP main or FOP main throws
	 *         an exception.
	 */
	public static void main(String[] args) throws Throwable {
		registerFonts();

		Class<?> fop = Class.forName(System.getProperty("fop.main", "org.apache.fop.cli.Main"));
		Method main = fop.getMethod("main", String[].class);
		try {
			main.invoke(null, (Object) args);
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
	}

	/**
	 * Register any TrueType fonts on the JAVA_FONTS path which are not
	 * currently available.
	 */
	public static void registerFonts() {
		String[] fontDirNames = System.getProperty("JAVA_FONTS", "").split(File.pathSeparator);
		for (String fontDirName : fontDirNames) {
			registerFonts(fontDirName.trim());
		}
	}

	/**
	 * Register any TrueType fonts in the specified directory which are not
	 * currently available.
	 * 
	 * @param fontDirName The name of a directory containing fonts.
	 */
	private static void registerFonts(String fontDirName) {
		if (fontDirName.length() == 0) {
			return;
		}
		File fontDir = new File(fontDirName);
		if (!fontDir.isDirectory()) {
			return;
		}
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		List<String> availableFonts = Arrays.asList(ge.getAvailableFontFamilyNames());
		File[] fontFiles = fontDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".ttf");
			}
		});
		for (File fontFile : fontFiles) {
			Font font;
			try {
				font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
			} catch (FontFormatException e) {
				e.printStackTrace();
				continue;
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			String name = font.getFamily();
			if (availableFonts.contains(name)) {
				continue;
			}
			boolean registered = ge.registerFont(font);
			if (registered) {
				System.out.println("Registered font \"" + name + "\" from " + fontFile.getAbsolutePath());
			}
		}
	}
}
