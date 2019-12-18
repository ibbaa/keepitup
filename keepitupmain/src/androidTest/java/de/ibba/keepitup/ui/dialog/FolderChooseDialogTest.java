package de.ibba.keepitup.ui.dialog;

import android.os.Bundle;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;

import de.ibba.keepitup.R;
import de.ibba.keepitup.model.FileEntry;
import de.ibba.keepitup.ui.BaseUITest;
import de.ibba.keepitup.ui.GlobalSettingsActivity;
import de.ibba.keepitup.ui.adapter.FileEntryAdapter;
import de.ibba.keepitup.util.BundleUtil;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class FolderChooseDialogTest extends BaseUITest {

    @Rule
    public final ActivityTestRule<GlobalSettingsActivity> rule = new ActivityTestRule<>(GlobalSettingsActivity.class, false, false);

    private GlobalSettingsActivity activity;
    private String root;

    @Before
    public void beforeEachTestMethod() {
        super.beforeEachTestMethod();
        root = getFileManager().getExternalRootDirectory().getAbsolutePath();
        createTestFiles();
        activity = (GlobalSettingsActivity) launchSettingsInputActivity(rule);
        getFileManager().delete(getFileManager().getExternalDirectory("log"));
    }

    @Test
    public void testDisplayInitialFileListLevel1FolderExists() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        assertEquals("folder2", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(6)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withText("folder3")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(6, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(5), getFileEntry("folder3", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testDisplayInitialFileListLevel1FolderDoesNotExist() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder4");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder4")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder4")));
        assertEquals("folder4", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(6)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withText("folder3")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(6, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(5), getFileEntry("folder3", true, false, true)));
        assertFalse(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testDisplayInitialFileListLevel1Empty() {
        getFileManager().delete(getFileManager().getExternalRootDirectory());
        FolderChooseDialog dialog = openFolderChooseDialog("xyz");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/xyz")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("xyz")));
        assertEquals("xyz", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(1, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertFalse(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testDisplayInitialFileListLevel2FolderExists() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2/folder2_folder1");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder1")));
        assertEquals("folder2/folder2_folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder2_file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder2_file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder2_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2_folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(5, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder2_file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder2_file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder2_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2_folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testDisplayInitialFileListLevel2FolderDoesNotExist() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2/folder2_folder6");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder6")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder6")));
        assertEquals("folder2/folder2_folder6", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder2_file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder2_file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder2_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2_folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(5, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder2_file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder2_file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder2_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2_folder2", true, false, true)));
        assertFalse(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testDisplayInitialFileListLevel3() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2/folder2_folder2/folder2_folder2_folder1");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder2/folder2_folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder2/folder2_folder2_folder1")));
        assertEquals("folder2/folder2_folder2/folder2_folder2_folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder2_folder2_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(2, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder2_folder2_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testDisplayInitialFileListLevel3Empty() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder1/folder1_folder1/xyz");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder1/xyz")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder1/xyz")));
        assertEquals("folder1/folder1_folder1/xyz", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(1, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertFalse(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testShowFilesLevel1() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(6)));
        onView(withId(R.id.checkbox_dialog_folder_choose_show_files)).perform(click());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder3")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(4, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder3", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.checkbox_dialog_folder_choose_show_files)).perform(click());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(6)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withText("folder3")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        assertEquals(6, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(5), getFileEntry("folder3", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testShowFilesLevel3NoFiles() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2/folder2_folder2/folder2_folder2_folder1");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder2/folder2_folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder2/folder2_folder2_folder1")));
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder2_folder2_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(2, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder2_folder2_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.checkbox_dialog_folder_choose_show_files)).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder2/folder2_folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder2/folder2_folder2_folder1")));
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder2_folder2_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        assertEquals(2, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder2_folder2_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testSelectFolderLevel1InitialSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        assertEquals("folder2", dialog.getFolder());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1")));
        assertEquals("folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1")));
        assertEquals("folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root)));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("")));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("..", true, true, false)));
        assertTrue(adapter.isItemSelected());
        assertEquals("", dialog.getFolder());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testSelectFolderLevel1NoInitialSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("xyz");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/xyz")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("xyz")));
        assertEquals("xyz", dialog.getFolder());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertFalse(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1")));
        assertEquals("folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1")));
        assertEquals("folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root)));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("")));
        assertEquals("", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("..", true, true, false)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testSelectFolderLevel2InitialSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2/folder2_folder2");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder2")));
        assertEquals("folder2/folder2_folder2", dialog.getFolder());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder2", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder1")));
        assertEquals("folder2/folder2_folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder1")));
        assertEquals("folder2/folder2_folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        assertEquals("folder2", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("..", true, true, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testSelectFolderLevel2NoInitialSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2/xyz");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/xyz")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/xyz")));
        assertEquals("folder2/xyz", dialog.getFolder());
        FileEntryAdapter adapter = dialog.getAdapter();
        assertFalse(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder1")));
        assertEquals("folder2/folder2_folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder1")));
        assertEquals("folder2/folder2_folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        assertEquals("folder2", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("..", true, true, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testOpenFolderLevel1InitialSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        assertEquals("folder2", dialog.getFolder());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1")));
        assertEquals("folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder1_file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder1_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1_folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(4, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder1_file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder1_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1_folder2", true, false, true)));
        assertFalse(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder1")));
        assertEquals("folder1/folder1_folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1")));
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(6)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withText("folder3")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        assertEquals(6, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(5), getFileEntry("folder3", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testOpenFolderLevel1NoInitialSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder4");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder4")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder4")));
        assertEquals("folder4", dialog.getFolder());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        assertEquals("folder2", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(5)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder2_file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder2_file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder2_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2_folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(5, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder2_file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder2_file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder2_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2_folder2", true, false, true)));
        assertFalse(adapter.isItemSelected());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder1")));
        assertEquals("folder2/folder2_folder1", dialog.getFolder());
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(6)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withText("folder3")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        assertEquals(6, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(5), getFileEntry("folder3", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testOpenFolderLevel2InitialSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder1/folder1_folder1");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder1")));
        assertEquals("folder1/folder1_folder1", dialog.getFolder());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder1")));
        assertEquals("folder1/folder1_folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(1, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertFalse(adapter.isItemSelected());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder1")));
        assertEquals("folder1/folder1_folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder1_file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder1_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1_folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        assertEquals(4, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder1_file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder1_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1_folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testOpenFolderLevel2NoInitialSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder1/folder1_folder5");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder5")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder5")));
        assertEquals("folder1/folder1_folder5", dialog.getFolder());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder1")));
        assertEquals("folder1/folder1_folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(1, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertFalse(adapter.isItemSelected());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder1")));
        assertEquals("folder1/folder1_folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder1_file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder1_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1_folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        assertEquals(4, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder1_file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder1_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1_folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testFolderLevel1InputAndSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        assertEquals("folder2", dialog.getFolder());
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).perform(replaceText("folder2/xyz/download"));
        assertEquals("folder2/xyz/download", dialog.getFolder());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1")));
        assertEquals("folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(6)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withText("folder3")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(6, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(5), getFileEntry("folder3", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testFolderLevel1InputAndOpen() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2")));
        assertEquals("folder2", dialog.getFolder());
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).perform(replaceText("folder2/abc"));
        assertEquals("folder2/abc", dialog.getFolder());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1")));
        assertEquals("folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder1_file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder1_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1_folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(4, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder1_file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder1_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1_folder2", true, false, true)));
        assertFalse(adapter.isItemSelected());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1")));
        assertEquals("folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(6)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("file2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withText("folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 4))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withText("folder3")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 5))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        assertEquals(6, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("file2", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(4), getFileEntry("folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(5), getFileEntry("folder3", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testFolderLevel2InputAndSelect() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder1/folder1_folder1");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder1")));
        assertEquals("folder1/folder1_folder1", dialog.getFolder());
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).perform(replaceText("folder2/xyz/download"));
        assertEquals("folder2/xyz/download", dialog.getFolder());
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder2")));
        assertEquals("folder1/folder1_folder2", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder1_file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder1_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1_folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(4, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder1_file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder1_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1_folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1_folder2", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testFolderLevel2InputAndOpen() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder1/folder1_folder1");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder1")));
        assertEquals("folder1/folder1_folder1", dialog.getFolder());
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).perform(replaceText("folder2/xyz/download"));
        assertEquals("folder2/xyz/download", dialog.getFolder());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder2")));
        assertEquals("folder1/folder1_folder2", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(1, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertFalse(adapter.isItemSelected());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder1/folder1_folder2")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder1/folder1_folder2")));
        assertEquals("folder1/folder1_folder2", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(4)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_file)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder1_file1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(not(isDisplayed())));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withText("folder1_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 2))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withText("folder1_folder2")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 3))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        assertEquals(4, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder1_file1", false, false, false)));
        assertTrue(areEnrtriesEqual(adapter.getItem(2), getFileEntry("folder1_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(3), getFileEntry("folder1_folder2", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder1_folder2", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    @Test
    public void testFolderLevel3InputAndOpen() {
        FolderChooseDialog dialog = openFolderChooseDialog("folder2/folder2_folder2/folder2_folder2_folder1");
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder2/folder2_folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder2/folder2_folder2_folder1")));
        assertEquals("folder2/folder2_folder2/folder2_folder2_folder1", dialog.getFolder());
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).perform(replaceText("folder2/xyz/download"));
        assertEquals("folder2/xyz/download", dialog.getFolder());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder2/folder2_folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder2/folder2_folder2_folder1")));
        assertEquals("folder2/folder2_folder2/folder2_folder2_folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(1)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        FileEntryAdapter adapter = dialog.getAdapter();
        assertEquals(1, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertFalse(adapter.isItemSelected());
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).perform(click());
        onView(withId(R.id.textview_dialog_folder_choose_absolute)).check(matches(withText(root + "/folder2/folder2_folder2/folder2_folder2_folder1")));
        onView(withId(R.id.edittext_dialog_folder_choose_folder)).check(matches(withText("folder2/folder2_folder2/folder2_folder2_folder1")));
        assertEquals("folder2/folder2_folder2/folder2_folder2_folder1", dialog.getFolder());
        onView(withId(R.id.listview_dialog_folder_choose_file_entries)).check(matches(withListSize(2)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withText("..")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 0))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_symbol), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder)));
        onView(allOf(withId(R.id.textview_list_item_file_entry_name), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withText("folder2_folder2_folder1")));
        onView(allOf(withId(R.id.imageview_list_item_file_entry_open), withChildDescendantAtPosition(withId(R.id.listview_dialog_folder_choose_file_entries), 1))).check(matches(withDrawable(R.drawable.icon_folder_open_shadow)));
        assertEquals(2, adapter.getItemCount());
        assertTrue(areEnrtriesEqual(adapter.getItem(0), getFileEntry("..", true, true, true)));
        assertTrue(areEnrtriesEqual(adapter.getItem(1), getFileEntry("folder2_folder2_folder1", true, false, true)));
        assertTrue(areEnrtriesEqual(adapter.getSelectedItem(), getFileEntry("folder2_folder2_folder1", true, false, true)));
        assertTrue(adapter.isItemSelected());
        onView(withId(R.id.imageview_dialog_folder_choose_cancel)).perform(click());
    }

    private FolderChooseDialog openFolderChooseDialog(String folder) {
        FolderChooseDialog folderChooseDialog = new FolderChooseDialog();
        Bundle bundle = BundleUtil.messagesToBundle(new String[]{folderChooseDialog.getFolderRootKey(), folderChooseDialog.getFolderKey()}, new String[]{root, folder});
        folderChooseDialog.setArguments(bundle);
        folderChooseDialog.show(activity.getSupportFragmentManager(), GlobalSettingsActivity.class.getName());
        return folderChooseDialog;
    }

    private void createTestFiles() {
        try {
            File folder1 = new File(root, "folder1");
            File file1 = new File(root, "file1");
            File file2 = new File(root, "file2");
            File folder2 = new File(root, "folder2");
            File folder3 = new File(root, "folder3");
            assertTrue(folder1.mkdir());
            assertTrue(file1.createNewFile());
            assertTrue(file2.createNewFile());
            assertTrue(folder2.mkdir());
            assertTrue(folder3.mkdir());
            File folder1Folder1 = new File(folder1, "folder1_folder1");
            File folder1Folder2 = new File(folder1, "folder1_folder2");
            File folder1File1 = new File(folder1, "folder1_file1");
            assertTrue(folder1Folder1.mkdir());
            assertTrue(folder1Folder2.mkdir());
            assertTrue(folder1File1.createNewFile());
            File folder2Folder1 = new File(folder2, "folder2_folder1");
            File folder2File1 = new File(folder2, "folder2_file1");
            File folder2File2 = new File(folder2, "folder2_file2");
            File folder2Folder2 = new File(folder2, "folder2_folder2");
            assertTrue(folder2Folder1.mkdir());
            assertTrue(folder2File1.createNewFile());
            assertTrue(folder2File2.createNewFile());
            assertTrue(folder2Folder2.mkdir());
            File folder2Folder2Folder1 = new File(folder2Folder2, "folder2_folder2_folder1");
            assertTrue(folder2Folder2Folder1.mkdir());
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
