package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.FileEntry;
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
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    public void testAbsoluteFolderError() {
        fileManager.setAbsoluteFolder(null);
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
        assertEquals("", dialog.getAbsoluteFolderText().getText());
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

    @Test
    public void testInitialFolderEmptyError() {
        fileManager.setRelativeParent(null);
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal file error.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testSelectFileParentIsSelectedError() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder")));
        FileEntryAdapter adapter = dialog.getAdapter();
        adapter.selectItem(0);
        fileManager.setRelativeParent(null);
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).perform(click());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("dir1", true, false, true)));
    }

    @Test
    public void testSelectFileNonParentIsSelectedError() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder")));
        FileEntryAdapter adapter = dialog.getAdapter();
        adapter.selectItem(2);
        fileManager.setRelativeSibling(null);
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("dir3", true, false, true)));
    }

    @Test
    public void testSelectFileNonParentNotSelectedError() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder")));
        fileManager.setNestedFolder(null);
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertFalse(adapter.isItemSelected());
    }

    @Test
    public void testOpenFileParentAbsoluteFolderError() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder")));
        fileManager.setAbsoluteFolder(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal file error.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testOpenFileParentAbsoluteParentError() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder")));
        fileManager.setAbsoluteParent(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testOpenFileNonParentError() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder")));
        fileManager.setAbsoluteFolder(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
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
        fileManager.setFileEntries(getTestFileEntries());
    }

    private List<FileEntry> getTestFileEntries() {
        FileEntry fileEntry1 = new FileEntry();
        fileEntry1.setName("dir1");
        fileEntry1.setDirectory(true);
        fileEntry1.setParent(false);
        fileEntry1.setCanVisit(true);
        FileEntry fileEntry2 = new FileEntry();
        fileEntry2.setName("dir2");
        fileEntry2.setDirectory(true);
        fileEntry2.setParent(true);
        fileEntry2.setCanVisit(true);
        FileEntry fileEntry3 = new FileEntry();
        fileEntry3.setName("dir3");
        fileEntry3.setDirectory(true);
        fileEntry3.setParent(false);
        fileEntry3.setCanVisit(true);
        FileEntry fileEntry4 = new FileEntry();
        fileEntry4.setName("file2");
        fileEntry4.setDirectory(false);
        fileEntry4.setParent(false);
        fileEntry4.setCanVisit(false);
        return Arrays.asList(fileEntry1, fileEntry2, fileEntry3, fileEntry4);
    }

    private FileEntry getFileEntry(String name, boolean directory, boolean parent, boolean canVisit) {
        FileEntry fileEntry = new FileEntry();
        fileEntry.setName(name);
        fileEntry.setDirectory(directory);
        fileEntry.setParent(parent);
        fileEntry.setCanVisit(canVisit);
        return fileEntry;
    }

    private boolean areEnrtriesEqual(FileEntry entry1, FileEntry entry2) {
        if (!entry1.getName().equals(entry2.getName())) {
            return false;
        }
        if (entry1.isDirectory() != entry2.isDirectory()) {
            return false;
        }
        if (entry1.canVisit() != entry2.canVisit()) {
            return false;
        }
        return entry1.isParent() == entry2.isParent();
    }
}
