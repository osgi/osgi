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

package javax.imageio.event;
public interface IIOReadProgressListener extends java.util.EventListener {
	void imageComplete(javax.imageio.ImageReader var0);
	void imageProgress(javax.imageio.ImageReader var0, float var1);
	void imageStarted(javax.imageio.ImageReader var0, int var1);
	void readAborted(javax.imageio.ImageReader var0);
	void sequenceComplete(javax.imageio.ImageReader var0);
	void sequenceStarted(javax.imageio.ImageReader var0, int var1);
	void thumbnailComplete(javax.imageio.ImageReader var0);
	void thumbnailProgress(javax.imageio.ImageReader var0, float var1);
	void thumbnailStarted(javax.imageio.ImageReader var0, int var1, int var2);
}

