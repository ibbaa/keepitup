/*
 * Copyright (c) 2024. Alwin Ibba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ibbaa.keepitup.service;

import androidx.documentfile.provider.DocumentFile;

public interface IDocumentManager {

    DocumentFile getArbitraryDirectory(String arbitraryFolder);

    String getValidFileName(DocumentFile folder, String file);

    DocumentFile getFolder(String folder);

    DocumentFile getFile(DocumentFile folder, String fileName);

    boolean delete(DocumentFile file);

    boolean fileExists(DocumentFile folder, String fileName);
}
