package de.ibba.keepitup.ui.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.logging.Log;
import de.ibba.keepitup.ui.BatteryOptimizationSupport;
import de.ibba.keepitup.ui.ContextOptionsSupport;
import de.ibba.keepitup.ui.adapter.ContextOptionAdapter;
import de.ibba.keepitup.util.BundleUtil;

public class ContextOptionsDialog extends DialogFragment {

    private View dialogView;
    private RecyclerView contextOptionRecyclerView;

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

    public RecyclerView getContextOptionRecyclerView() {
        return contextOptionRecyclerView;
    }

    private void prepareFolderRecyclerView() {
        Log.d(ContextOptionsDialog.class.getName(), "prepareFolderRecyclerView");
        contextOptionRecyclerView = dialogView.findViewById(R.id.listview_dialog_context_options);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        contextOptionRecyclerView.setLayoutManager(layoutManager);
        contextOptionRecyclerView.setItemAnimator(new DefaultItemAnimator());
        contextOptionRecyclerView.setAdapter(createAdapter());
    }

    private void prepareCancelImageButton(View view) {
        Log.d(ContextOptionsDialog.class.getName(), "prepareCancelImageButton");
        ImageView cacnelImage = view.findViewById(R.id.imageview_dialog_context_options_cancel);
        cacnelImage.setOnClickListener(this::onCancelClicked);
    }

    public ContextOptionAdapter getAdapter() {
        return (ContextOptionAdapter) getContextOptionRecyclerView().getAdapter();
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
        return new ContextOptionAdapter(contextOptionList, this);
    }

    private void onCancelClicked(@SuppressWarnings("unused") View view) {
        Log.d(ContextOptionsDialog.class.getName(), "onCancelClicked");
        dismiss();
    }

    public void onContextOptionClicked(View view, int position) {
        Log.d(ContextOptionsDialog.class.getName(), "onContextOptionClicked, position is " + position);
        getAdapter().selectItem(position);
        int sourceResourceId = requireArguments().getInt(getSourceResourceIdKey());
        ContextOption contextOption = getAdapter().getItem(position);
        Log.d(ContextOptionsDialog.class.getName(), "sourceResourceId is " + sourceResourceId);
        Log.d(ContextOptionsDialog.class.getName(), "contextOption is " + contextOption);
        ContextOptionsSupport contextOptionsSupport = getContextOptionsSupport();
        if (contextOptionsSupport != null) {
            contextOptionsSupport.onContextOptionsDialogClicked(this, sourceResourceId, contextOption);
        } else {
            Log.e(ContextOptionsDialog.class.getName(), "contextOptionsSupport is null");
            dismiss();
        }
    }

    protected ContextOptionsSupport getContextOptionsSupport() {
        Log.d(ContextOptionsDialog.class.getName(), "getContextOptionsSupport");
        List<Fragment> fragments = getParentFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment instanceof ContextOptionsSupport) {
                    return (ContextOptionsSupport) fragment;
                }
            }
        }
        Log.e(BatteryOptimizationDialog.class.getName(), "getContextOptionsSupport, no parent fragment implementing " + ContextOptionsSupport.class.getSimpleName());
        Activity activity = getActivity();
        if (activity == null) {
            Log.e(ContextOptionsDialog.class.getName(), "getContextOptionsSupport, activity is null");
            return null;
        }
        if (!(activity instanceof BatteryOptimizationSupport)) {
            Log.e(ContextOptionsDialog.class.getName(), "getContextOptionsSupport, activity is not an instance of " + ContextOptionsSupport.class.getSimpleName());
            return null;
        }
        return (ContextOptionsSupport) activity;
    }
}
