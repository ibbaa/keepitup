/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.ui.validation.ValidationResult;
import net.ibbaa.keepitup.util.BundleUtil;

import java.util.ArrayList;
import java.util.List;

public class ValidatorErrorDialog extends GridBasedMessageDialogBase {

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
        initEdgeToEdgeInsets(view);
        List<ValidationResult> resultList = BundleUtil.validationResultListFromBundle(getValidationResultBaseKey(), requireArguments());
        prepareErrorMessages(view, toErrorMessageList(resultList));
        prepareOkButton(view, resultList.size() + 1);
        return view;
    }

    public String getValidationResultBaseKey() {
        return ValidatorErrorDialog.class.getSimpleName() + "ValidationResult";
    }

    private List<GridMessage> toErrorMessageList(List<ValidationResult> resultList) {
        List<GridMessage> messages = new ArrayList<>(resultList.size());
        for (ValidationResult currentResult : resultList) {
            messages.add(new GridMessage(currentResult.getFieldName(), currentResult.getMessage()));
        }
        return messages;
    }

    protected GridLayout getErrorGridLayout(View view) {
        return view.findViewById(R.id.gridlayout_dialog_validator_error);
    }

    protected ImageView getOkImageView(View view) {
        return view.findViewById(R.id.imageview_dialog_validator_error_ok);
    }
}
