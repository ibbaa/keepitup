/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.ui;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.dialog.ConfirmDialog;
import net.ibbaa.keepitup.ui.dialog.GeneralMessageDialog;
import net.ibbaa.keepitup.ui.support.ConfirmSupport;
import net.ibbaa.keepitup.util.BundleUtil;

@SuppressWarnings({"SameParameterValue"})
public abstract class RecyclerViewBaseActivity extends AppCompatActivity implements ConfirmSupport {

    private Resources resources;

    public void injectResources(Resources resources) {
        this.resources = resources;
    }

    @Override
    public Resources getResources() {
        if (resources != null) {
            return resources;
        }
        return super.getResources();
    }

    protected abstract int getRecyclerViewId();

    protected abstract RecyclerView.Adapter<?> createAdapter();

    protected void initRecyclerView() {
        RecyclerView recyclerView = findViewById(getRecyclerViewId());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(createAdapter());
    }

    protected void initEdgeToEdgeInsets(int mainLayoutId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(false);
        } else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        }
        View mainLayout = findViewById(mainLayoutId);
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout, (view, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return insets;
        });
    }

    public RecyclerView.Adapter<?> getAdapter() {
        RecyclerView recyclerView = findViewById(getRecyclerViewId());
        return recyclerView.getAdapter();
    }

    protected void showMessageDialog(String errorMessage) {
        showMessageDialog(errorMessage, Typeface.BOLD);
    }

    protected void showMessageDialog(String errorMessage, int typeface) {
        Log.d(RecyclerViewBaseActivity.class.getName(), "showMessageDialog with message " + errorMessage);
        GeneralMessageDialog errorDialog = new GeneralMessageDialog();
        Bundle bundle = BundleUtil.stringToBundle(errorDialog.getMessageKey(), errorMessage);
        bundle.putInt(errorDialog.getTypefaceStyleKey(), typeface);
        errorDialog.setArguments(bundle);
        showDialog(errorDialog, GeneralMessageDialog.class.getName());
    }

    protected void showConfirmDialog(String confirmMessage, ConfirmDialog.Type type, int position) {
        Log.d(RecyclerViewBaseActivity.class.getName(), "showConfirmDialog with message " + confirmMessage + " for type " + type + " and position " + position);
        ConfirmDialog confirmDialog = new ConfirmDialog();
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getTypeKey()}, new String[]{confirmMessage, type.name()});
        bundle.putInt(confirmDialog.getPositionKey(), position);
        confirmDialog.setArguments(bundle);
        showDialog(confirmDialog, ConfirmDialog.class.getName());
    }

    protected void showConfirmDialog(String confirmMessage, ConfirmDialog.Type type) {
        Log.d(RecyclerViewBaseActivity.class.getName(), "showConfirmDialog with message " + confirmMessage + " for type " + type);
        ConfirmDialog confirmDialog = new ConfirmDialog();
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getTypeKey()}, new String[]{confirmMessage, type.name()});
        confirmDialog.setArguments(bundle);
        showDialog(confirmDialog, ConfirmDialog.class.getName());
    }

    protected void showConfirmDialog(String confirmMessage, String description, ConfirmDialog.Type type) {
        Log.d(SettingsInputActivity.class.getName(), "showConfirmDialog with message " + confirmMessage + " and description " + description + " for type " + type);
        ConfirmDialog confirmDialog = new ConfirmDialog();
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{confirmDialog.getMessageKey(), confirmDialog.getDescriptionKey(), confirmDialog.getTypeKey()}, new String[]{confirmMessage, description, type.name()});
        confirmDialog.setArguments(bundle);
        showDialog(confirmDialog, ConfirmDialog.class.getName());
    }

    private void showDialog(DialogFragment dialog, String name) {
        try {
            dialog.show(getSupportFragmentManager(), name);
        } catch (Exception exc) {
            Log.e(SettingsInputActivity.class.getName(), "Error opening dialog", exc);
        }
    }
}
