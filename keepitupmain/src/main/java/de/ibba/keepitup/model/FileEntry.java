package de.ibba.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

public class FileEntry {

    private String name;
    private boolean directory;
    private boolean parent;

    public FileEntry() {
        this.name = "";
        this.directory = false;
        this.parent = false;
    }

    public FileEntry(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public FileEntry(Bundle bundle) {
        this.name = bundle.getString("name");
        this.directory = bundle.getInt("directory") >= 1;
        this.parent = bundle.getInt("parent") >= 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    public boolean isParent() {
        return parent;
    }

    public void setParent(boolean parent) {
        this.parent = parent;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("name", name);
        bundle.putInt("directory", directory ? 1 : 0);
        bundle.putInt("parent", parent ? 1 : 0);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    @NonNull
    @Override
    public String toString() {
        return "FileEntry{" +
                "name='" + name + '\'' +
                ", directory=" + directory +
                ", parent=" + parent +
                '}';
    }
}
