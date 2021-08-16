package net.ibbaa.keepitup.ui;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.dialog.ConfirmDialog;
import net.ibbaa.keepitup.ui.dialog.GeneralErrorDialog;
import net.ibbaa.keepitup.util.BundleUtil;

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

    public RecyclerView.Adapter<?> getAdapter() {
        RecyclerView recyclerView = findViewById(getRecyclerViewId());
        return recyclerView.getAdapter();
    }

    protected void showErrorDialog(String errorMessage) {
        showErrorDialog(errorMessage, Typeface.BOLD);
    }

    protected void showErrorDialog(String errorMessage, int typeface) {
        Log.d(RecyclerViewBaseActivity.class.getName(), "showErrorDialog with message " + errorMessage);
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        Bundle bundle = BundleUtil.stringToBundle(GeneralErrorDialog.class.getSimpleName(), errorMessage);
        bundle.putInt(errorDialog.getTypefaceStyleKey(), typeface);
        errorDialog.setArguments(bundle);
        showDialog(errorDialog, GeneralErrorDialog.class.getName());
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

    private void showDialog(DialogFragment dialog, String name) {
        try {
            dialog.show(getSupportFragmentManager(), name);
        } catch (Exception exc) {
            Log.d(SettingsInputActivity.class.getName(), "Error opening dialog", exc);
        }
    }
}