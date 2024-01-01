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

import net.ibbaa.keepitup.model.FileEntry;

import java.io.File;
import java.net.URL;
import java.util.List;

public interface IFileManager {

    File getInternalDownloadDirectory();

    File getInternalRootDirectory();

    File getExternalDirectory(String directoryName, int externalStorage);

    File getExternalRootDirectory(int externalStorage);

    String getDefaultDownloadDirectoryName();

    String getRelativeSibling(String folder, String sibling);

    String getRelativeParent(String folder);

    String getAbsoluteParent(String root, String absoluteFolder);

    String getAbsolutePath(String root, String path);

    String getNestedPath(String path1, String path2);

    List<FileEntry> getFiles(String root, String absoluteFolder);

    boolean delete(File file);

    String getDownloadFileName(URL url, String specifiedFileName, String mimeType);

    String getLogFileName(String baseFileName, String extension, int id, int index, String address);

    boolean doesFileExist(File folder, String file);

    String getValidFileName(File folder, String file);

    boolean isSDCardSupported();
}
