package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.ui.ContextOptionsSupport;
import de.ibba.keepitup.ui.adapter.ContextOptionEntryAdapter;
import de.ibba.keepitup.util.BundleUtil;

public class ContextOptionsDialog extends DialogFragment {

    private ContextOptionsSupport contextOptionsSupport;
    private View dialogView;
    private RecyclerView contextOptionEntriesRecyclerView;

    public ContextOptionsDialog(ContextOptionsSupport contextOptionsSupport) {
        this.contextOptionsSupport = contextOptionsSupport;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(ContextOptionsDialog.class.getName(), "onCreate");
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.DialogTheme);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(ContextOptionsDialog.class.getName(), "onCreateView");
        dialogView = inflater.inflate(R.layout.dialog_context_options, container);
        prepareFolderRecyclerView();
        prepareCancelImageButton(dialogView);
        return dialogView;
    }

    public String getSourceResourceIdKey() {
        return ContextOptionsDialog.class.getSimpleName() + "SourceResourceId";
    }

    public RecyclerView getContextOptionEntriesRecyclerView() {
        return contextOptionEntriesRecyclerView;
    }

    private void prepareFolderRecyclerView() {
        Log.d(ContextOptionsDialog.class.getName(), "prepareFolderRecyclerView");
        contextOptionEntriesRecyclerView = dialogView.findViewById(R.id.listview_dialog_context_options_entries);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        contextOptionEntriesRecyclerView.setLayoutManager(layoutManager);
        contextOptionEntriesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        contextOptionEntriesRecyclerView.setAdapter(createAdapter());
    }

    private void prepareCancelImageButton(View view) {
        Log.d(ContextOptionsDialog.class.getName(), "prepareCancelImageButton");
        ImageView cacnelImage = view.findViewById(R.id.imageview_dialog_context_options_cancel);
        cacnelImage.setOnClickListener(this::onCancelClicked);
    }

    public ContextOptionEntryAdapter getAdapter() {
        return (ContextOptionEntryAdapter) getContextOptionEntriesRecyclerView().getAdapter();
    }

    private RecyclerView.Adapter<?> createAdapter() {
        Log.d(ContextOptionsDialog.class.getName(), "createAdapter");
        List<String> contextOptionStrings = BundleUtil.stringListFromBundle(ContextOption.class.getSimpleName(), requireArguments());
        List<ContextOption> contextOptionList = new ArrayList<>();
        for (String contextOptionString : contextOptionStrings) {
            try {
                contextOptionList.add(ContextOption.valueOf(contextOptionString));
            } catch (IllegalArgumentException exc) {
                Log.e(ContextOptionsDialog.class.getName(), ContextOption.class.getSimpleName() + "." + contextOptionString + " does not exist");
            }
        }
        return new ContextOptionEntryAdapter(contextOptionList, this);
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(ContextOptionsDialog.class.getName(), "onCancelClicked");
        dismiss();
    }

    public void onContextOptionEntryClicked(View view, int position) {
        Log.d(ContextOptionsDialog.class.getName(), "onContextOptionEntryClicked, position is " + position);
        getAdapter().selectItem(position);
        int sourceResourceId = requireArguments().getInt(getSourceResourceIdKey());
        ContextOption contextOption = getAdapter().getItem(position);
        Log.d(ContextOptionsDialog.class.getName(), "sourceResourceId is " + sourceResourceId);
        Log.d(ContextOptionsDialog.class.getName(), "contextOption is " + contextOption);
        if (contextOptionsSupport != null) {
            contextOptionsSupport.onContextOptionsDialogEntryClicked(this, sourceResourceId, contextOption);
        }
    }
}
