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
public interface Filer {
	javax.tools.JavaFileObject createClassFile(java.lang.CharSequence var0, javax.lang.model.element.Element... var1) throws java.io.IOException;
	javax.tools.FileObject createResource(javax.tools.JavaFileManager.Location var0, java.lang.CharSequence var1, java.lang.CharSequence var2, javax.lang.model.element.Element... var3) throws java.io.IOException;
	javax.tools.JavaFileObject createSourceFile(java.lang.CharSequence var0, javax.lang.model.element.Element... var1) throws java.io.IOException;
	javax.tools.FileObject getResource(javax.tools.JavaFileManager.Location var0, java.lang.CharSequence var1, java.lang.CharSequence var2) throws java.io.IOException;
}

