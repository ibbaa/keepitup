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

package net.ibbaa.keepitup.model;

import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.NonNull;

import java.util.Objects;

public class FileEntry {

    private String name;
    private boolean directory;
    private boolean parent;
    private boolean canVisit;

    public FileEntry() {
        this.name = "";
        this.directory = false;
        this.parent = false;
        this.canVisit = true;
    }

    public FileEntry(PersistableBundle bundle) {
        this(new Bundle(bundle));
    }

    public FileEntry(Bundle bundle) {
        this();
        this.name = bundle.getString("name");
        this.directory = bundle.getInt("directory") >= 1;
        this.parent = bundle.getInt("parent") >= 1;
        this.canVisit = bundle.getInt("canVisit") >= 1;
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

    public boolean canVisit() {
        return canVisit;
    }

    public void setCanVisit(boolean canVisit) {
        this.canVisit = canVisit;
    }

    public PersistableBundle toPersistableBundle() {
        PersistableBundle bundle = new PersistableBundle();
        bundle.putString("name", name);
        bundle.putInt("directory", directory ? 1 : 0);
        bundle.putInt("parent", parent ? 1 : 0);
        bundle.putInt("canVisit", canVisit ? 1 : 0);
        return bundle;
    }

    public Bundle toBundle() {
        return new Bundle(toPersistableBundle());
    }

    public boolean isEqual(FileEntry other) {
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        if (!Objects.equals(name, other.name)) {
            return false;
        }
        if (directory != other.directory) {
            return false;
        }
        if (parent != other.parent) {
            return false;
        }
        return canVisit == other.canVisit;
    }

    @NonNull
    @Override
    public String toString() {
        return "FileEntry{" +
                "name='" + name + '\'' +
                ", directory=" + directory +
                ", parent=" + parent +
                ", canVisit=" + canVisit +
                '}';
    }
}
