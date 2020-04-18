package de.ibba.keepitup.ui;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.ui.dialog.ConfirmDialog;
import de.ibba.keepitup.ui.dialog.GeneralErrorDialog;
import de.ibba.keepitup.util.BundleUtil;

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

    protected abstract RecyclerView.Adapter createAdapter();

    protected void initRecyclerView() {
        RecyclerView recyclerView = findViewById(getRecyclerViewId());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(createAdapter());
    }

    public RecyclerView.Adapter getAdapter() {
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
        errorDialog.show(getSupportFragmentManager(), GeneralErrorDialog.class.getName());
    }

    protected void showConfirmDialog(String confirmMessage, ConfirmDialog.Type type, int position) {
        Log.d(RecyclerViewBaseActivity.class.getName(), "showConfirmDialog with message " + confirmMessage + " for type " + type + " and position " + position);
        ConfirmDialog confirmDialog = new ConfirmDialog(this);
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{ConfirmDialog.class.getSimpleName(), ConfirmDialog.Type.class.getSimpleName()}, new String[]{confirmMessage, type.name()});
        bundle.putInt(getConfirmDialogPositionKey(), position);
        confirmDialog.setArguments(bundle);
        confirmDialog.show(getSupportFragmentManager(), ConfirmDialog.class.getName());
    }

    protected void showConfirmDialog(String confirmMessage, ConfirmDialog.Type type) {
        Log.d(RecyclerViewBaseActivity.class.getName(), "showConfirmDialog with message " + confirmMessage + " for type " + type);
        ConfirmDialog confirmDialog = new ConfirmDialog(this);
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{ConfirmDialog.class.getSimpleName(), ConfirmDialog.Type.class.getSimpleName()}, new String[]{confirmMessage, type.name()});
        confirmDialog.setArguments(bundle);
        confirmDialog.show(getSupportFragmentManager(), ConfirmDialog.class.getName());
    }

    protected String getConfirmDialogPositionKey() {
        return ConfirmDialog.class.getSimpleName() + ".position";
    }
}
