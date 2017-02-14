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

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.DocumentLoader;
import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;
import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.osgi.tools.fop.FOPMain;

/**
 *
 */
public class Rasterizer {

	/**
	 * @throws Exception
	 */
	public Rasterizer() throws Exception {
		FOPMain.registerFonts();
		cssFile = File.createTempFile("batik-default-override-", ".css");
		cssFile.deleteOnExit();
		Files.write(cssFile.toPath(), css.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * @param svgPaths
	 * @param outputPath
	 * @param scale
	 * @param dpi
	 */
	public void processPaths(List<String> svgPaths, Path outputPath,
			Float scale, Float dpi) {

		for (String pathString : svgPaths) {
			if (!pathString.endsWith(".svg")) {
				continue;
			}

			try {
				processPath(Paths.get(pathString), outputPath, scale,
						dpi);
			} catch (Exception e) {
				System.err.printf("ERROR on %s\n", pathString);
				e.printStackTrace();
			}
		}
	}

	/**
	 * Convert an SVG image into a PNG image.
	 *
	 * @param inputPath the full path to an SVG image
	 * @param outputPath a directory in which to output the PNG image
	 * @param scale
	 * @param dpi
	 * @throws Exception if there was a processing exception
	 */
	public void processPath(Path inputPath, Path outputPath,
			Float scale,
			Float dpi) throws Exception {

		TranscoderInput input = new TranscoderInput(
				Files.newInputStream(inputPath, StandardOpenOption.READ));

		Path outputFile = Paths.get(outputPath.toString(),
				getFileNameNoExtension(inputPath) + ".png");

		System.out.printf("Rasterizing %s to %s\n", inputPath,
				inputPath.relativize(outputFile).toString());

		OutputStream output = new FileOutputStream(outputFile.toFile());

		TranscoderOutput image = new TranscoderOutput(output);

		Rectangle2D bounds = loadBounds(inputPath);

		Transcoder transcoder = getTranscoder(bounds, scale, dpi);

		transcoder.transcode(input, image);

		output.flush();
		output.close();
	}

	private PNGTranscoder getTranscoder(Rectangle2D bounds, Float scale,
			Float dpi) {

		PNGTranscoder transcoder = new PNGTranscoder() {

			@Override
			protected ImageRenderer createRenderer() {
				ImageRenderer renderer = super.createRenderer();

				RenderingHints renderingHints = renderer.getRenderingHints();

				renderingHints.add(new RenderingHints(
						RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
				renderingHints
						.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON));
				renderingHints.add(
						new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
								RenderingHints.VALUE_COLOR_RENDER_QUALITY));
				renderingHints
						.add(new RenderingHints(RenderingHints.KEY_DITHERING,
								RenderingHints.VALUE_DITHER_DISABLE));
				renderingHints
						.add(new RenderingHints(RenderingHints.KEY_RENDERING,
								RenderingHints.VALUE_RENDER_QUALITY));
				renderingHints.add(
						new RenderingHints(RenderingHints.KEY_STROKE_CONTROL,
								RenderingHints.VALUE_STROKE_DEFAULT));
				renderingHints.add(
						new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS,
								RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT));
				renderingHints.add(
						new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING,
								RenderingHints.VALUE_TEXT_ANTIALIAS_GASP));

				renderer.setRenderingHints(renderingHints);

				return renderer;
			}
		};

		if (scale != null) {
			transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT,
					new Float(bounds.getHeight() * scale.floatValue()));
			transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH,
					new Float(bounds.getWidth() * scale.floatValue()));
		}

		transcoder.addTranscodingHint(PNGTranscoder.KEY_BACKGROUND_COLOR,
				Color.WHITE);

		if (dpi != null) {
			transcoder.addTranscodingHint(
					PNGTranscoder.KEY_PIXEL_UNIT_TO_MILLIMETER,
					new Float(25.4f / dpi.floatValue()));
		}

		transcoder.addTranscodingHint(PNGTranscoder.KEY_USER_STYLESHEET_URI,
				cssFile.toURI().toString());

		return transcoder;
	}

	private Rectangle2D loadBounds(Path inputPath) throws IOException {

		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
		SVGOMDocument doc = (SVGOMDocument) factory
				.createSVGDocument(inputPath.toUri().toString());
		UserAgent userAgent = new UserAgentAdapter();
		DocumentLoader loader = new DocumentLoader(userAgent);
		BridgeContext ctx = new BridgeContext(userAgent, loader);

		ctx = ctx.createBridgeContext(doc);
		ctx.setDynamicState(BridgeContext.DYNAMIC);

		GVTBuilder builder = new GVTBuilder();
		GraphicsNode node = builder.build(ctx, doc);

		return node.getGeometryBounds();
	}

	private String getFileNameNoExtension(Path path) {
		String fileName = path.getFileName().toString();

		int index = fileName.lastIndexOf(".svg");

		String parentDirName = path.getParent().getFileName().toString();

		fileName = fileName.substring(0, index);

		if (parentDirName.matches("\\d{3}")) {
			fileName = parentDirName + "-" + fileName;
		}

		return fileName;
	}

	private static final String css = "svg {"
			+ "shape-rendering: geometricPrecision;"
			+ "text-rendering:  geometricPrecision;"
			+ "color-rendering: optimizeQuality;"
			+ "image-rendering: optimizeQuality;" + "}";

	private final File			cssFile;
}
