package net.ibbaa.keepitup.logging;

import android.content.Context;
import android.net.Uri;

import androidx.documentfile.provider.DocumentFile;

import net.ibbaa.phonelog.LogFileManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class DocumentFileHousekeeper implements Runnable {

    private final static String ZIP_FILE_EXTENSION = "zip";
    private final static String UNKNOWN_MIME_TYPE = "unknown/unknown";

    private final static ReentrantLock housekeepingLock = new ReentrantLock();

    private final Context context;

    private final String directory;
    private final String baseFileName;
    private final int archiveFileCount;
    private final int deleteFileCount;
    private final Predicate<String> filter;

    public DocumentFileHousekeeper(Context context, String directory, String baseFileName, int archiveFileCount, int deleteFileCount, Predicate<String> filter) {
        this.context = context;
        this.directory = directory;
        this.baseFileName = baseFileName;
        this.archiveFileCount = archiveFileCount;
        this.deleteFileCount = deleteFileCount;
        this.filter = filter;
    }

    @Override
    public void run() {
        try {
            housekeepingLock.lock();
            DocumentFile documentDirectory = DocumentFile.fromTreeUri(getContext(), Uri.parse(directory));
            if (documentDirectory == null) {
                return;
            }
            DocumentFile[] filesToArchive = documentDirectory.listFiles();
            List<DocumentFile> filesToArchiveList = new ArrayList<>();
            if (filter != null) {
                for (DocumentFile currentDocumentFile : filesToArchive) {
                    if (filter.test(currentDocumentFile.getName())) {
                        filesToArchiveList.add(currentDocumentFile);
                    }
                }
            } else {
                filesToArchiveList = Arrays.asList(filesToArchive);
            }
            if (filesToArchiveList.size() >= archiveFileCount) {
                LogFileManager logFileManager = new LogFileManager();
                DocumentFileManager documentFileManager = new DocumentFileManager();
                String zipFileName = logFileManager.getFileNameWithoutExtension(baseFileName) + "." + ZIP_FILE_EXTENSION;
                zipFileName = logFileManager.suffixFileName(zipFileName, logFileManager.getTimestampSuffix(System.currentTimeMillis()));
                zipFileName = documentFileManager.getValidFileName(documentDirectory, zipFileName, null);
                DocumentFile zipFile = documentDirectory.createFile(UNKNOWN_MIME_TYPE, zipFileName);
                if (zipFile == null) {
                    return;
                }
                documentFileManager.zipDocumentFiles(getContext(), filesToArchiveList, zipFile);
                if (deleteFileCount > 0) {
                    DocumentFile[] filesToDelete = documentDirectory.listFiles();
                    List<DocumentFile> filesToDeleteList = new ArrayList<>();
                    for (DocumentFile currentDocumentFile : filesToDelete) {
                        if (isDeletableArchive(currentDocumentFile.getName())) {
                            filesToDeleteList.add(currentDocumentFile);
                        }
                    }
                    if (filesToDeleteList.size() >= deleteFileCount) {
                        documentFileManager.deleteOldestDocumentFile(filesToDeleteList);
                    }
                }
            }
        } catch (Exception exc) {
            // Do nothing
        } finally {
            housekeepingLock.unlock();
        }
    }

    private boolean isDeletableArchive(String name) {
        LogFileManager fileManager = new LogFileManager();
        String zipFileName = fileManager.getFileNameWithoutExtension(baseFileName);
        if ((zipFileName + "." + ZIP_FILE_EXTENSION).equals(name)) {
            return false;
        }
        return name.startsWith(zipFileName) && name.endsWith(ZIP_FILE_EXTENSION);
    }

    private Context getContext() {
        return context;
    }

    @FunctionalInterface
    public interface Predicate<S> {
        @SuppressWarnings({"unused"})
        boolean test(S value);
    }
}
