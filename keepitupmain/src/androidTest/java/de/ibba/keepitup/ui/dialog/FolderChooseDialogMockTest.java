package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;
import de.ibba.keepitup.test.mock.MockFileManager;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.GlobalSettingsActivity;
import de.ibba.keepitup.ui.adapter.FileEntryAdapter;
import de.ibba.keepitup.util.BundleUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class FolderChooseDialogMockTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<GlobalSettingsActivity> rule = new ActivityTestRule<>(GlobalSettingsActivity.class, false, false);

    private GlobalSettingsActivity activity;
    private MockFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        setMockFileManagerData();
        activity.injectFileManager(fileManager);
    }

    @Test
    public void testFolderError() {
        fileManager.setAbsoluteFolder(null);
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
        setMockFileManagerData();
        fileManager.setAbsoluteParent(null);
        openFolderChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(0)));
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testFileListError() {
        fileManager.setFileEntries(null);
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    private FolderChooseDialog openFolderChooseDialog(String folder) {
        FolderChooseDialog folderChooseDialog = new FolderChooseDialog();
        Bundle bundle = BundleUtil.messagesToBundle(new String[]{folderChooseDialog.getFolderRootKey(), folderChooseDialog.getFolderKey()}, new String[]{"root", folder});
        folderChooseDialog.setArguments(bundle);
        folderChooseDialog.show(activity.getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
        return folderChooseDialog;
    }

    private void setMockFileManagerData() {
        fileManager = new MockFileManager();
        fileManager.setRelativeSibling("relativeSibling");
        fileManager.setRelativeParent("relativeParent");
        fileManager.setAbsoluteParent("absoluteParent");
        fileManager.setAbsoluteFolder("absoluteFolder");
        fileManager.setNestedFolder("nestedFolder");
    }
}
