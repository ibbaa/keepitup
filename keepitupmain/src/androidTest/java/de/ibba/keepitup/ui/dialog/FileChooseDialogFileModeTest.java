package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.test.mock.MockClipboardManager;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.GlobalSettingsActivity;
import de.ibba.keepitup.ui.adapter.FileEntryAdapter;
import de.ibba.keepitup.util.BundleUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class FileChooseDialogFileModeTest extends BaseUITest {

    private ActivityScenario<?> activityScenario;
    private String root;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        root = getFileManager().getExternalRootDirectory(0).getAbsolutePath();
        createTestFiles();
        activityScenario = launchSettingsInputActivity(GlobalSettingsActivity.class);
        deleteLogFolder();
    }

    @After
    public void afterEachTestMethod() {
        super.afterEachTestMethod();
        activityScenario.close();
    }

    private void deleteLogFolder() {
        getFileManager().delete(getFileManager().getExternalDirectory("log", 0));
    }

    @Test
    public void testDisplayInitialFileListLevel1FileExists() {
        FileChooseDialog dialog = openFileChooseDialog("", "file1");
        onView(withId(R.id.textview_dialog_file_choose_absolute)).check(matches(withText(root + "/file1")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file1")));
        assertEquals("", dialog.getFolder());
        assertEquals("file1", dialog.getFile());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(5, adapter.getItemCount());
        assertTrue(areEntriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEntriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEntriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEntriesEqual(adapter.getSelectedItem(), getFileEntry("file1", false, false, false)));
        assertTrue(adapter.isItemSelected());
        assertTrue(adapter.isFileItemSelected());
        assertFalse(adapter.isFolderItemSelected());
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
    }

    @Test
    public void testDisplayInitialFileListLevel1FileExistsScreenRotation() {
        FileChooseDialog dialog = openFileChooseDialog("", "file1");
        onView(withId(R.id.textview_dialog_file_choose_absolute)).check(matches(withText(root + "/file1")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file1")));
        assertEquals("", dialog.getFolder());
        assertEquals("file1", dialog.getFile());
        rotateScreen(activityScenario);
        deleteLogFolder();
        assertEquals("", dialog.getFolder());
        assertEquals("file1", dialog.getFile());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(5, adapter.getItemCount());
        assertTrue(areEntriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEntriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEntriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEntriesEqual(adapter.getSelectedItem(), getFileEntry("file1", false, false, false)));
        assertTrue(adapter.isItemSelected());
        assertTrue(adapter.isFileItemSelected());
        assertFalse(adapter.isFolderItemSelected());
        rotateScreen(activityScenario);
        deleteLogFolder();
        assertEquals("", dialog.getFolder());
        assertEquals("file1", dialog.getFile());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        adapter = dialog.getAdapter();
        assertEquals(5, adapter.getItemCount());
        assertTrue(areEntriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEntriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEntriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEntriesEqual(adapter.getSelectedItem(), getFileEntry("file1", false, false, false)));
        assertTrue(adapter.isItemSelected());
        assertTrue(adapter.isFileItemSelected());
        assertFalse(adapter.isFolderItemSelected());
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
    }

    @Test
    public void testDisplayInitialFileListLevel1FileDoesNotExist() {
        FileChooseDialog dialog = openFileChooseDialog("", "file3");
        onView(withId(R.id.textview_dialog_file_choose_absolute)).check(matches(withText(root + "/file3")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file3")));
        assertEquals("", dialog.getFolder());
        assertEquals("file3", dialog.getFile());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(5, adapter.getItemCount());
        assertTrue(areEntriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEntriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEntriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertFalse(adapter.isItemSelected());
        assertFalse(adapter.isFileItemSelected());
        assertFalse(adapter.isFolderItemSelected());
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
    }

    @Test
    public void testDisplayInitialFileListLevel1FileDoesNotExistScreenRotation() {
        FileChooseDialog dialog = openFileChooseDialog("", "file3");
        onView(withId(R.id.textview_dialog_file_choose_absolute)).check(matches(withText(root + "/file3")));
        onView(withId(R.id.edittext_dialog_file_choose_folder)).check(matches(withText("")));
        onView(withId(R.id.edittext_dialog_file_choose_file)).check(matches(withText("file3")));
        assertEquals("", dialog.getFolder());
        assertEquals("file3", dialog.getFile());
        rotateScreen(activityScenario);
        deleteLogFolder();
        assertEquals("", dialog.getFolder());
        assertEquals("file3", dialog.getFile());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(5, adapter.getItemCount());
        assertTrue(areEntriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEntriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEntriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertFalse(adapter.isItemSelected());
        assertFalse(adapter.isFileItemSelected());
        assertFalse(adapter.isFolderItemSelected());
        rotateScreen(activityScenario);
        deleteLogFolder();
        assertEquals("", dialog.getFolder());
        assertEquals("file3", dialog.getFile());
        onView(withId(R.id.listview_dialog_file_choose_file_entries)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        adapter = dialog.getAdapter();
        assertEquals(5, adapter.getItemCount());
        assertTrue(areEntriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEntriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEntriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEntriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertFalse(adapter.isItemSelected());
        assertFalse(adapter.isFileItemSelected());
        assertFalse(adapter.isFolderItemSelected());
        onView(withId(R.id.imageview_dialog_file_choose_cancel)).perform(click());
    }

    private FileChooseDialog openFileChooseDialog(String folder, String file) {
        FileChooseDialog fileChooseDialog = new FileChooseDialog();
        Bundle bundle = BundleUtil.stringsToBundle(new String[]{fileChooseDialog.getFolderRootKey(), fileChooseDialog.getFolderKey()}, new String[]{root, folder});
        bundle = BundleUtil.stringToBundle(fileChooseDialog.getFileModeKey(), FileChooseDialog.Mode.FILE.name(), bundle);
        bundle = BundleUtil.stringToBundle(fileChooseDialog.getFileKey(), file, bundle);
        fileChooseDialog.setArguments(bundle);
        fileChooseDialog.show(getActivity(activityScenario).getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
        onView(isRoot()).perform(waitFor(1000));
        return fileChooseDialog;
    }

    private void openEntry(int index) {
        onView(isRoot()).perform(waitFor(1000));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), index))).perform(click());
    }

    private void selectEntry(int index) {
        onView(isRoot()).perform(waitFor(1000));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_file_choose_file_entries), index))).perform(click());
    }

    private FileChooseDialog getDialog() {
        return (FileChooseDialog) getActivity(activityScenario).getSupportFragmentManager().getFragments().get(0);
    }

    private FileEntryAdapter getAdapter() {
        return getDialog().getAdapter();
    }

    private void createTestFiles() {
        try {
            File folder1 = new File(root, "folder1");
            File file1 = new File(root, "file1");
            File file2 = new File(root, "file2");
            File folder2 = new File(root, "folder2");
            assertTrue(folder1.mkdir());
            assertTrue(file1.createNewFile());
            assertTrue(file2.createNewFile());
            assertTrue(folder2.mkdir());
            File folder1Folder1 = new File(folder1, "folder1_folder1");
            File folder1Folder2 = new File(folder1, "folder1_folder2");
            File folder1File1 = new File(folder1, "folder1_file1");
            assertTrue(folder1Folder1.mkdir());
            assertTrue(folder1Folder2.mkdir());
            assertTrue(folder1File1.createNewFile());
            File folder2Folder1 = new File(folder2, "folder2_folder1");
            File folder2File1 = new File(folder2, "folder2_file1");
            File folder2File2 = new File(folder2, "folder2_file2");
            assertTrue(folder2Folder1.mkdir());
            assertTrue(folder2File1.createNewFile());
            assertTrue(folder2File2.createNewFile());
        } catch (IOException exc) {
            throw new RuntimeException(exc);
        }
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

    private MockClipboardManager prepareMockClipboardManager(FileChooseDialog folderChooseDialog) {
        MockClipboardManager clipboardManager = new MockClipboardManager();
        clipboardManager.clearData();
        folderChooseDialog.injectClipboardManager(clipboardManager);
        return clipboardManager;
    }
}
