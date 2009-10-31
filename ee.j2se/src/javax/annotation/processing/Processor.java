/*
 * Copyright (c) OSGi Alliance (2001, 2009). All Rights Reserved.
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

package javax.annotation.processing;
public interface Processor {
	java.lang.Iterable<? extends javax.annotation.processing.Completion> getCompletions(javax.lang.model.element.Element var0, javax.lang.model.element.AnnotationMirror var1, javax.lang.model.element.ExecutableElement var2, java.lang.String var3);
	java.util.Set<java.lang.String> getSupportedAnnotationTypes();
	java.util.Set<java.lang.String> getSupportedOptions();
	javax.lang.model.SourceVersion getSupportedSourceVersion();
	void init(javax.annotation.processing.ProcessingEnvironment var0);
	boolean process(java.util.Set<? extends javax.lang.model.element.TypeElement> var0, javax.annotation.processing.RoundEnvironment var1);
}

