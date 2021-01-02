package de.ibba.keepitup.ui.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.ui.ConfirmSupport;
import de.ibba.keepitup.util.BundleUtil;
import de.ibba.keepitup.util.StringUtil;

public class ConfirmDialog extends DialogFragment {

    public enum Type {
        DELETETASK,
        DELETELOGS,
        RESETCONFIG,
        IMPORTCONFIG,
        EXPORTCONFIGEXISTINGFILE,
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(ConfirmDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ConfirmDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_confirm, container);
        String message = BundleUtil.stringFromBundle(getMessageKey(), requireArguments());
        String description = BundleUtil.stringFromBundle(getDescriptionKey(), requireArguments());
        prepareConfirmMessage(view, message);
        prepareConfirmDescription(view, description);
        prepareOkCancelImageButtons(view);
        return view;
    }

    public String getPositionKey() {
        return ConfirmDialog.class.getSimpleName() + "Position";
    }

    public String getExtraDataKey() {
        return ConfirmDialog.class.getSimpleName() + "ExtraData";
    }

    public String getMessageKey() {
        return ConfirmDialog.class.getSimpleName() + "Message";
    }

    public String getDescriptionKey() {
        return ConfirmDialog.class.getSimpleName() + "Description";
    }

    public String getTypeKey() {
        return ConfirmDialog.Type.class.getSimpleName();
    }

    public int getPosition() {
        return BundleUtil.integerFromBundle(getPositionKey(), requireArguments());
    }

    public Bundle getExtraData() {
        return BundleUtil.bundleFromBundle(getExtraDataKey(), requireArguments());
    }

    private void prepareConfirmMessage(View view, String message) {
        Log.d(ConfirmDialog.class.getName(), "prepareConfirmMessage, message is " + message);
        TextView messageText = view.findViewById(R.id.textview_dialog_confirm_message);
        messageText.setText(message);
    }

    private void prepareConfirmDescription(View view, String description) {
        Log.d(ConfirmDialog.class.getName(), "prepareConfirmDescription, description is " + description);
        TextView descriptionText = view.findViewById(R.id.textview_dialog_confirm_description);
        if (StringUtil.isEmpty(description)) {
            descriptionText.setText("");
            descriptionText.setVisibility(View.GONE);
        } else {
            descriptionText.setText(description);
            descriptionText.setVisibility(View.VISIBLE);
        }
    }

    private void prepareOkCancelImageButtons(View view) {
        Log.d(ConfirmDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_confirm_ok);
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_confirm_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(View view) {
        Log.d(ConfirmDialog.class.getName(), "onOkClicked");
        ConfirmSupport confirmSupport = getConfirmSupport();
        if (confirmSupport == null) {
            Log.e(ConfirmDialog.class.getName(), "confirmSupport is null");
            dismiss();
            return;
        }
        String typeString = BundleUtil.stringFromBundle(getTypeKey(), requireArguments());
        if (StringUtil.isEmpty(typeString)) {
            Log.e(ConfirmDialog.class.getName(), ConfirmDialog.Type.class.getSimpleName() + " not specified.");
            confirmSupport.onConfirmDialogOkClicked(this, null);
            return;
        }
        Type type = null;
        try {
            type = Type.valueOf(typeString);
        } catch (IllegalArgumentException exc) {
            Log.e(ConfirmDialog.class.getName(), ConfirmDialog.Type.class.getSimpleName() + "." + typeString + " does not exist");
        }
        confirmSupport.onConfirmDialogOkClicked(this, type);
    }

    private void onCancelClicked(View view) {
        Log.d(ConfirmDialog.class.getName(), "onCancelClicked");
        ConfirmSupport confirmSupport = getConfirmSupport();
        if (confirmSupport != null) {
            confirmSupport.onConfirmDialogCancelClicked(this);
        } else {
            Log.e(ConfirmDialog.class.getName(), "confirmSupport is null");
            dismiss();
        }
    }

    private ConfirmSupport getConfirmSupport() {
        Log.d(ConfirmDialog.class.getName(), "getConfirmSupport");
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(ConfirmDialog.class.getName(), "getConfirmSupport, activity is null");
            return null;
        }
        if (!(activity instanceof ConfirmSupport)) {
            Log.e(ConfirmDialog.class.getName(), "getConfirmSupport, activity is not an instance of " + ConfirmSupport.class.getSimpleName());
            return null;
        }
        return (ConfirmSupport) activity;
    }
}
