/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup;

import net.ibbaa.keepitup.ui.ContextOptionsSupportManagerTest;
import net.ibbaa.keepitup.ui.DefaultsActivityTest;
import net.ibbaa.keepitup.ui.GlobalHeaderHandlerTest;
import net.ibbaa.keepitup.ui.GlobalSettingsActivityTest;
import net.ibbaa.keepitup.ui.IntervalHandlerTest;
import net.ibbaa.keepitup.ui.LogHandlerTest;
import net.ibbaa.keepitup.ui.NetworkTaskHandlerTest;
import net.ibbaa.keepitup.ui.NetworkTaskLogActivityTest;
import net.ibbaa.keepitup.ui.NetworkTaskMainActivityTest;
import net.ibbaa.keepitup.ui.SAFGlobalSettingsActivityMockTest;
import net.ibbaa.keepitup.ui.SAFSystemActivityMockTest;
import net.ibbaa.keepitup.ui.SystemActivityTest;
import net.ibbaa.keepitup.ui.dialog.AlarmPermissionDialogTest;
import net.ibbaa.keepitup.ui.dialog.BatteryOptimizationDialogTest;
import net.ibbaa.keepitup.ui.dialog.ConfirmDialogTest;
import net.ibbaa.keepitup.ui.dialog.ContextOptionTest;
import net.ibbaa.keepitup.ui.dialog.ContextOptionsDialogTest;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialogFileModeTest;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialogFolderModeTest;
import net.ibbaa.keepitup.ui.dialog.FileChooseDialogMockTest;
import net.ibbaa.keepitup.ui.dialog.GeneralMessageDialogTest;
import net.ibbaa.keepitup.ui.dialog.GlobalHeadersDialogTest;
import net.ibbaa.keepitup.ui.dialog.GlobalHeadersEditDialogTest;
import net.ibbaa.keepitup.ui.dialog.InfoDialogTest;
import net.ibbaa.keepitup.ui.dialog.NetworkTaskEditDialogTest;
import net.ibbaa.keepitup.ui.dialog.PermissionExplainDialogTest;
import net.ibbaa.keepitup.ui.dialog.PlaceholderFocusChangeListenerTest;
import net.ibbaa.keepitup.ui.dialog.ProgressDialogTest;
import net.ibbaa.keepitup.ui.dialog.RawTextDialogTest;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialogTest;
import net.ibbaa.keepitup.ui.dialog.SettingsInputTest;
import net.ibbaa.keepitup.ui.dialog.SuspensionIntervalSelectDialogTest;
import net.ibbaa.keepitup.ui.dialog.SuspensionIntervalsDialogTest;
import net.ibbaa.keepitup.ui.dialog.TimeNumberPickerFormatterTest;
import net.ibbaa.keepitup.ui.dialog.ValidatorErrorDialogTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        //net.ibbaa.keepitup.ui
        ContextOptionsSupportManagerTest.class,
        DefaultsActivityTest.class,
        GlobalHeaderHandlerTest.class,
        GlobalSettingsActivityTest.class,
        IntervalHandlerTest.class,
        LogHandlerTest.class,
        NetworkTaskHandlerTest.class,
        NetworkTaskLogActivityTest.class,
        NetworkTaskMainActivityTest.class,
        SAFGlobalSettingsActivityMockTest.class,
        SAFSystemActivityMockTest.class,
        SystemActivityTest.class,
        //net.ibbaa.keepitup.ui.dialog
        AlarmPermissionDialogTest.class,
        BatteryOptimizationDialogTest.class,
        ConfirmDialogTest.class,
        ContextOptionsDialogTest.class,
        ContextOptionTest.class,
        FileChooseDialogFileModeTest.class,
        FileChooseDialogFolderModeTest.class,
        FileChooseDialogMockTest.class,
        GeneralMessageDialogTest.class,
        GlobalHeadersDialogTest.class,
        GlobalHeadersEditDialogTest.class,
        InfoDialogTest.class,
        NetworkTaskEditDialogTest.class,
        PermissionExplainDialogTest.class,
        PlaceholderFocusChangeListenerTest.class,
        ProgressDialogTest.class,
        RawTextDialogTest.class,
        SettingsInputDialogTest.class,
        SettingsInputTest.class,
        SuspensionIntervalsDialogTest.class,
        SuspensionIntervalSelectDialogTest.class,
        TimeNumberPickerFormatterTest.class,
        ValidatorErrorDialogTest.class})
public class ActivityDialogTestSuite {
}
