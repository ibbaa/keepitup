package de.ibba.keepitup.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import de.ibba.keepitup.ui.dialog.NetworkTaskDeleteConfirmDialog;
import de.ibba.keepitup.ui.dialog.GeneralErrorDialog;
import de.ibba.keepitup.util.BundleUtil;

public abstract class RecyclerViewBaseActivity extends AppCompatActivity {

    protected abstract int getRecyclerViewId();

    protected abstract RecyclerView.Adapter createAdapter();

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
        Log.d(RecyclerViewBaseActivity.class.getName(), "showErrorDialog with message " + errorMessage);
        GeneralErrorDialog errorDialog = new GeneralErrorDialog();
        errorDialog.setArguments(BundleUtil.messageToBundle(GeneralErrorDialog.class.getSimpleName(), errorMessage));
        errorDialog.show(getSupportFragmentManager(), GeneralErrorDialog.class.getName());
    }

    protected void showConfirmDialog(String confirmMessage, NetworkTaskDeleteConfirmDialog.Type type, int position) {
        Log.d(RecyclerViewBaseActivity.class.getName(), "showConfirmDialog with message " + confirmMessage + " for type " + type + " and position " + position);
        NetworkTaskDeleteConfirmDialog confirmDialog = new NetworkTaskDeleteConfirmDialog();
        Bundle bundle = BundleUtil.messagesToBundle(new String[]{NetworkTaskDeleteConfirmDialog.class.getSimpleName(), NetworkTaskDeleteConfirmDialog.Type.class.getSimpleName()}, new String[]{confirmMessage, type.name()});
        bundle.putInt(getConfirmDialogPositionKey(), position);
        confirmDialog.setArguments(bundle);
        confirmDialog.show(getSupportFragmentManager(), NetworkTaskDeleteConfirmDialog.class.getName());
    }

    protected String getConfirmDialogPositionKey() {
        return NetworkTaskDeleteConfirmDialog.class.getSimpleName() + ".position";
    }
}
