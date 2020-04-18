package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.io.InputStream;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.util.StreamUtil;

public class RawTextDialog extends DialogFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(RawTextDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(RawTextDialog.class.getName(), "onCreateView");
        View view = inflater.inflate(R.layout.dialog_raw_text, container);
        prepareContent(view);
        prepareOkImageButton(view);
        return view;
    }

    public String getResourceIdKey() {
        return RawTextDialog.class.getSimpleName() + "ResourceId";
    }

    private void prepareContent(View view) {
        Log.d(RawTextDialog.class.getName(), "prepareContent");
        TextView messageText = view.findViewById(R.id.textview_dialog_raw_text_content);
        Bundle arguments = requireArguments();
        int textResourceId = arguments.getInt(getResourceIdKey());
        InputStream inputStream = null;
        try {
            Log.d(RawTextDialog.class.getName(), "Reading text");
            inputStream = getResources().openRawResource(textResourceId);
            String text = StreamUtil.inputStreamToString(inputStream, Charsets.UTF_8);
            Log.d(RawTextDialog.class.getName(), "Raw text is " + text);
            text = doReplacements(arguments, text);
            Log.d(RawTextDialog.class.getName(), "Text with applied replacements is " + text);
            messageText.setText(text);
        } catch (Exception exc) {
            Log.e(RawTextDialog.class.getName(), "Error while reading text from resource.", exc);
            messageText.setText(getResources().getString(R.string.text_dialog_raw_text_fatal_error));
        } finally {
            if (inputStream != null) {
                Log.d(RawTextDialog.class.getName(), "Closing text stream.");
                try {
                    inputStream.close();
                } catch (IOException exc) {
                    Log.e(RawTextDialog.class.getName(), "Error while closing text stream.", exc);
                }
            }
        }
    }

    private String doReplacements(Bundle arguments, String text) {
        for (String key : arguments.keySet()) {
            String replacement = "@" + key + "@";
            Object value = arguments.get(key);
            if (value instanceof String) {
                if (text.contains(replacement)) {
                    text = text.replaceAll(replacement, value.toString());
                }
            }
        }
        return text;
    }

    private void prepareOkImageButton(View view) {
        Log.d(RawTextDialog.class.getName(), "prepareOkImageButton");
        ImageView okImage = view.findViewById(R.id.imageview_dialog_raw_text_ok);
        okImage.setOnClickListener(this::onOkClicked);
    }

    private void onOkClicked(@SuppressWarnings("unused") View view) {
        Log.d(RawTextDialog.class.getName(), "onOkClicked");
        dismiss();
    }
}
