package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.permission.PermissionManager;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.StringUtil;

public class PermissionExplainDialog extends DialogFragment {

    public enum Permission {
        EXTERNAL_STORAGE
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(PermissionExplainDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(PermissionExplainDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_permission_explain, container);
        String message = BundleUtil.bundleToMessage(PermissionExplainDialog.class.getSimpleName(), Objects.requireNonNull(getArguments()));
        prepareExplainMessage(view, message);
        prepareOkImageButton(view);
        return view;
    }

    private void prepareExplainMessage(View view, String message) {
        Log.d(PermissionExplainDialog.class.getName(), "prepareExplainMessage");
        TextView messageText = view.findViewById(R.id.textview_dialog_general_permission_explain_message);
        messageText.setText(message);
    }

    private void prepareOkImageButton(View view) {
        Log.d(PermissionExplainDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_permission_explain_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(PermissionExplainDialog.class.getName(), "onOkClicked");
        FragmentActivity activity = Objects.requireNonNull(getActivity());
        PermissionManager permissionManager = new PermissionManager(activity);
        String permissionString = BundleUtil.bundleToMessage(PermissionExplainDialog.Permission.class.getSimpleName(), Objects.requireNonNull(getArguments()));
        if (StringUtil.isEmpty(permissionString)) {
            Log.e(PermissionExplainDialog.class.getName(), PermissionExplainDialog.Permission.class.getSimpleName() + " not specified.");
            permissionManager.onPermissionExplainDialogOkClicked(this, null);
            return;
        }
        Permission permission = null;
        try {
            permission = Permission.valueOf(permissionString);
        } catch (IllegalArgumentException exc) {
            Log.e(PermissionExplainDialog.class.getName(), PermissionExplainDialog.Permission.class.getSimpleName() + "." + permissionString + " does not exist");
        }
        permissionManager.onPermissionExplainDialogOkClicked(this, permission);
    }
}
