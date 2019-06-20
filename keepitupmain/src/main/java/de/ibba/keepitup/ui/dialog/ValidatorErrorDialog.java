package de.ibba.keepitup.ui.dialog;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import de.ibba.keepitup.R;
import de.ibba.keepitup.ui.validation.ValidationResult;
import de.ibba.keepitup.util.BundleUtil;

public class ValidatorErrorDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(ValidatorErrorDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ValidatorErrorDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_validator_error, container);
        List<ValidationResult> resultList = BundleUtil.indexedBundleToValidationResultList(Objects.requireNonNull(getArguments()));
        prepareErrorMessages(view, resultList);
        prepareOkButton(view, resultList.size() + 1);
        return view;
    }

    private void prepareErrorMessages(View view, List<ValidationResult> resultList) {
        Log.d(ValidatorErrorDialog.class.getName(), "prepareErrorMessages");
        GridLayout gridLayout = view.findViewById(R.id.gridlayout_dialog_validator_error);
        for (int ii = 0; ii < resultList.size(); ii++) {
            ValidationResult currentResult = resultList.get(ii);
            Log.d(ValidatorErrorDialog.class.getName(), "prepareErrorMessages, result with index " + ii + " is " + currentResult);
            TextView labelText = new TextView(requireContext());
            labelText.setId(View.generateViewId());
            labelText.setText(currentResult.getFieldName());
            labelText.setTypeface(null, Typeface.BOLD);
            GridLayout.LayoutParams labelTextParams = new GridLayout.LayoutParams();
            labelTextParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            labelTextParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            labelTextParams.setGravity(Gravity.CENTER);
            labelTextParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.textview_dialog_network_task_validator_error_label_margin_right);
            labelTextParams.topMargin = getResources().getDimensionPixelSize(R.dimen.textview_dialog_network_task_validator_error_label_margin_top);
            labelTextParams.columnSpec = GridLayout.spec(0, 1, GridLayout.LEFT);
            labelTextParams.rowSpec = GridLayout.spec(ii + 1, 1, GridLayout.LEFT);
            gridLayout.addView(labelText, labelTextParams);
            TextView messageText = new TextView(requireContext());
            messageText.setId(View.generateViewId());
            messageText.setText(currentResult.getMessage());
            messageText.setTypeface(null, Typeface.NORMAL);
            GridLayout.LayoutParams messageTextParams = new GridLayout.LayoutParams();
            messageTextParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            messageTextParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            messageTextParams.setGravity(Gravity.CENTER);
            messageTextParams.leftMargin = getResources().getDimensionPixelSize(R.dimen.textview_dialog_network_task_validator_error_message_margin_left);
            messageTextParams.topMargin = getResources().getDimensionPixelSize(R.dimen.textview_dialog_network_task_validator_error_message_margin_top);
            messageTextParams.columnSpec = GridLayout.spec(1, 1, GridLayout.LEFT);
            messageTextParams.rowSpec = GridLayout.spec(ii + 1, 1, GridLayout.LEFT);
            gridLayout.addView(messageText, messageTextParams);
        }
    }

    private void prepareOkButton(View view, int row) {
        Log.d(ValidatorErrorDialog.class.getName(), "prepareOkButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_validator_error_ok);
        GridLayout.LayoutParams okImageParams = new GridLayout.LayoutParams();
        okImageParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
        okImageParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        okImageParams.columnSpec = GridLayout.spec(0, 2, GridLayout.CENTER);
        okImageParams.rowSpec = GridLayout.spec(row, 1, GridLayout.CENTER);
        okImageParams.setGravity(Gravity.CENTER);
        okImageParams.topMargin = getResources().getDimensionPixelSize(R.dimen.imageview_dialog_network_task_validator_error_ok_margin_top);
        okImage.setLayoutParams(okImageParams);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(ValidatorErrorDialog.class.getName(), "onOkClicked");
        dismiss();
    }
}