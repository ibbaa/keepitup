package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;

public class ContextOptionsDialog extends DialogFragment {

    private View dialogView;
    private RecyclerView contextOptionEntriesRecyclerView;

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
        return dialogView;
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

    private RecyclerView.Adapter createAdapter() {
        return null;
    }

    public void onContextOptionEntryClicked(View view, int position) {
        Log.d(ContextOptionsDialog.class.getName(), "onContextOptionEntryClicked, position is " + position);
    }
}
