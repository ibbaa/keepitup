package de.ibba.keepitup.ui.dialog;

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

    private ConfirmSupport confirmSupport;

    public enum Type {
        DELETETASK,
        DELETELOGS
    }

    public ConfirmDialog(ConfirmSupport confirmSupport) {
        this.confirmSupport = confirmSupport;
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
        String message = BundleUtil.stringFromBundle(ConfirmDialog.class.getSimpleName(), requireArguments());
        prepareConfirmMessage(view, message);
        prepareOkCancelImageButtons(view);
        return view;
    }

    private void prepareConfirmMessage(View view, String message) {
        Log.d(ConfirmDialog.class.getName(), "prepareConfirmMessage");
        TextView messageText = view.findViewById(R.id.textview_dialog_confirm_message);
        messageText.setText(message);
    }

    private void prepareOkCancelImageButtons(View view) {
        Log.d(ConfirmDialog.class.getName(), "prepareOkCancelImageButtons");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_confirm_ok);
        ImageView cancelImage = view.findViewById(R.id.imageview_dialog_confirm_cancel);
        okImage.setOnClickListener(this::onOkClicked);
        cancelImage.setOnClickListener(this::onCancelClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(ConfirmDialog.class.getName(), "onOkClicked");
        String typeString = BundleUtil.stringFromBundle(ConfirmDialog.Type.class.getSimpleName(), requireArguments());
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

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(ConfirmDialog.class.getName(), "onCancelClicked");
        confirmSupport.onConfirmDialogCancelClicked(this);
    }
}
