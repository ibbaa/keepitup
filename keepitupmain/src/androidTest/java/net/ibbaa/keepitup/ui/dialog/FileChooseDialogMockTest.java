package net.ibbaa.keepitup.ui.dialog;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.model.FileEntry;
import net.ibbaa.keepitup.test.mock.MockFileManager;
import net.ibbaa.keepitup.ui.BaseUITest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivity;
import net.ibbaa.keepitup.ui.adapter.FileEntryAdapter;
import net.ibbaa.keepitup.util.BundleUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class FileChooseDialogMockTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private MockFileManager fileManager;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        setMockFileManagerData();
        activityScenario.onActivity(activity -> ((GlobalSettingsActivity) activity).injectFileManager(fileManager));
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    @Test
    public void testAbsoluteFolderError() {
        fileManager.setAbsolutePath(null);
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
        assertEquals("", dialog.getAbsoluteFolderText().getText());
        setMockFileManagerData();
        fileManager.setAbsoluteParent(null);
        openFileChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testAbsoluteFolderErrorWithFile() {
        fileManager.setAbsolutePath(null);
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
        assertEquals("", dialog.getAbsoluteFolderText().getText());
        setMockFileManagerData();
        fileManager.setAbsoluteParent(null);
        openFileChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testFileListError() {
        fileManager.setFileEntries(null);
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testFileListErrorWithFile() {
        fileManager.setFileEntries(null);
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testInitialFolderEmptyError() {
        fileManager.setRelativeParent(null);
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal file error.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testSelectFileParentIsSelectedError() {
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        FileEntryAdapter adapter = dialog.getAdapter();
        adapter.selectItem(0);
        fileManager.setRelativeParent(null);
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        assertTrue(areEntriesEqual(adapter.getSelectedItem(), getFileEntry("dir1", true, false, true)));
    }

    @Test
    public void testSelectFileParentIsSelectedErrorWithFile() {
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file")));
        FileEntryAdapter adapter = dialog.getAdapter();
        adapter.selectItem(0);
        fileManager.setRelativeParent(null);
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        assertTrue(areEntriesEqual(adapter.getSelectedItem(), getFileEntry("dir1", true, false, true)));
    }

    @Test
    public void testSelectFileNonParentIsSelectedError() {
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        FileEntryAdapter adapter = dialog.getAdapter();
        adapter.selectItem(2);
        fileManager.setRelativeSibling(null);
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).perform(click());
        assertTrue(areEntriesEqual(adapter.getSelectedItem(), getFileEntry("dir3", true, false, true)));
    }

    @Test
    public void testSelectFileNonParentIsSelectedErrorWithFile() {
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file")));
        FileEntryAdapter adapter = dialog.getAdapter();
        adapter.selectItem(2);
        fileManager.setRelativeSibling(null);
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).perform(click());
        assertTrue(areEntriesEqual(adapter.getSelectedItem(), getFileEntry("dir3", true, false, true)));
    }

    @Test
    public void testSelectFileNonParentNotSelectedError() {
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        fileManager.setNestedPath(null);
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).perform(click());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertFalse(adapter.isItemSelected());
    }

    @Test
    public void testSelectFileNonParentNotSelectedErrorWithFile() {
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file")));
        fileManager.setNestedPath(null);
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).perform(click());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertFalse(adapter.isItemSelected());
    }

    @Test
    public void testOpenFileParentAbsoluteFolderError() {
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        fileManager.setAbsolutePath(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal file error.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testOpenFileParentAbsoluteFolderErrorWithFile() {
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file")));
        fileManager.setAbsolutePath(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal file error.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testOpenFileParentAbsoluteParentError() {
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        fileManager.setAbsoluteParent(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testOpenFileParentAbsoluteParentErrorWithFile() {
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file")));
        fileManager.setAbsoluteParent(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testOpenFileParentRelativeParentError() {
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        fileManager.setRelativeParent(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal file error.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testOpenFileParentRelativeParentErrorWithFile() {
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file")));
        fileManager.setRelativeParent(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal file error.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testOpenFileParentRelativeParent() {
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_file_choose_absolute)).check(matches(withText("absoluteFolder")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("relativeParent")));
        assertEquals("relativeParent", dialog.getFolder());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(4)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(4, adapter.getItemCount());
    }

    @Test
    public void testOpenFileParentRelativeParentWitHFile() {
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_file_choose_absolute)).check(matches(withText("absoluteFolder")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("relativeParent")));
        assertEquals("relativeParent", dialog.getFolder());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(4)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(4, adapter.getItemCount());
    }

    @Test
    public void testOpenFileNonParentError() {
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        fileManager.setAbsolutePath(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testOpenFileNonParentErrorWithFile() {
        FileChooseDialog dialog = openFileChooseDialog("folder", "file");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file")));
        fileManager.setAbsolutePath(null);
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal error reading file list from folder.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }

    @Test
    public void testShowFilesError() {
        FileChooseDialog dialog = openFileChooseDialog("folder");
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("folder")));
        fileManager.setRelativeParent(null);
        onView(withId(R.id.checkbox_dialog_file_choose_show_files)).perform(click());
        onView(withId(R.id.textview_dialog_general_error_message)).check(matches(withText("Fatal file error.")));
        onView(withId(R.id.imageview_dialog_general_error_ok)).perform(click());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(0)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(0, adapter.getItemCount());
    }


    private FileChooseDialog openFileChooseDialog(String folder) {
        FileChooseDialog fileChooseDialog = new FileChooseDialog();
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{fileChooseDialog.getFolderRootKey(), fileChooseDialog.getFolderKey()}, new String[]{"root", folder});
        bundle = BundleUtil.stringToBundle(fileChooseDialog.getFileModeKey(), FileChooseDialog.Mode.FOLDER.name(), bundle);
        fileChooseDialog.setArguments(bundle);
        fileChooseDialog.show(getActivity(activityScenario).getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
        return fileChooseDialog;
    }

    private FileChooseDialog openFileChooseDialog(String folder, String file) {
        FileChooseDialog fileChooseDialog = new FileChooseDialog();
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{fileChooseDialog.getFolderRootKey(), fileChooseDialog.getFolderKey()}, new String[]{"root", folder});
        bundle = BundleUtil.stringToBundle(fileChooseDialog.getFileModeKey(), FileChooseDialog.Mode.FILE.name(), bundle);
        bundle = BundleUtil.stringToBundle(fileChooseDialog.getFileKey(), file, bundle);
        fileChooseDialog.setArguments(bundle);
        fileChooseDialog.show(getActivity(activityScenario).getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
        onView(isRoot()).perform(waitFor(1000));
        return fileChooseDialog;
    }

    private void setMockFileManagerData() {
        fileManager = new MockFileManager();
        fileManager.setRelativeSibling("relativeSibling");
        fileManager.setRelativeParent("relativeParent");
        fileManager.setAbsoluteParent("absoluteParent");
        fileManager.setAbsolutePath("absoluteFolder");
        fileManager.setNestedPath("nestedFolder");
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

    private boolean areEntriesEqual(FileEntry entry1, FileEntry entry2) {
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
