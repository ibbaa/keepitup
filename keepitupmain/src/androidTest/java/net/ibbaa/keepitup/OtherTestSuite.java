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

import net.ibbaa.keepitup.db.AccessTypeDataDAOTest;
import net.ibbaa.keepitup.db.AccessTypeDataDBConstantsTest;
import net.ibbaa.keepitup.db.DBMigrateTest;
import net.ibbaa.keepitup.db.DBSetupTest;
import net.ibbaa.keepitup.db.IntervalDAOTest;
import net.ibbaa.keepitup.db.IntervalDBConstantsTest;
import net.ibbaa.keepitup.db.LogDAOTest;
import net.ibbaa.keepitup.db.LogDBConstantsTest;
import net.ibbaa.keepitup.db.NetworkTaskDAOTest;
import net.ibbaa.keepitup.db.NetworkTaskDBConstantsTest;
import net.ibbaa.keepitup.db.SchedulerIdGeneratorTest;
import net.ibbaa.keepitup.db.SchedulerIdHistoryDBConstantsTest;
import net.ibbaa.keepitup.db.SchedulerStateDAOTest;
import net.ibbaa.keepitup.db.SchedulerStateDBConstantsTest;
import net.ibbaa.keepitup.logging.DumpTest;
import net.ibbaa.keepitup.logging.LogTest;
import net.ibbaa.keepitup.logging.NetworkTaskLogTest;
import net.ibbaa.keepitup.model.AccessTypeDataTest;
import net.ibbaa.keepitup.model.AccessTypeTest;
import net.ibbaa.keepitup.model.FileEntryTest;
import net.ibbaa.keepitup.model.IntervalTest;
import net.ibbaa.keepitup.model.LogEntryTest;
import net.ibbaa.keepitup.model.NetworkTaskTest;
import net.ibbaa.keepitup.model.SchedulerIdTest;
import net.ibbaa.keepitup.model.TimeTest;
import net.ibbaa.keepitup.model.validator.AccessTypeDataValidatorTest;
import net.ibbaa.keepitup.model.validator.IntervalValidatorTest;
import net.ibbaa.keepitup.model.validator.NetworkTaskValidatorTest;
import net.ibbaa.keepitup.notification.NotificationHandlerTest;
import net.ibbaa.keepitup.resources.JSONSystemMigrateTest;
import net.ibbaa.keepitup.resources.JSONSystemSetupTest;
import net.ibbaa.keepitup.resources.PreferenceManagerTest;
import net.ibbaa.keepitup.resources.PreferenceSetupTest;
import net.ibbaa.keepitup.resources.SystemWorkerFactoryTest;
import net.ibbaa.keepitup.service.ConnectNetworkTaskWorkerTest;
import net.ibbaa.keepitup.service.DownloadNetworkTaskWorkerTest;
import net.ibbaa.keepitup.service.NetworkTaskProcessBroadcastReceiverTest;
import net.ibbaa.keepitup.service.NetworkTaskProcessPoolTest;
import net.ibbaa.keepitup.service.NetworkTaskProcessServiceSchedulerTest;
import net.ibbaa.keepitup.service.NetworkTaskRunningNotificationServiceTest;
import net.ibbaa.keepitup.service.NetworkTaskWorkerTest;
import net.ibbaa.keepitup.service.NullNetworkTaskWorkerTest;
import net.ibbaa.keepitup.service.PingNetworkTaskWorkerTest;
import net.ibbaa.keepitup.service.SystemDocumentManagerTest;
import net.ibbaa.keepitup.service.SystemFileManagerTest;
import net.ibbaa.keepitup.service.SystemThemeManagerTest;
import net.ibbaa.keepitup.service.TimeBasedSuspensionBroadcastReceiverTest;
import net.ibbaa.keepitup.service.TimeBasedSuspensionSchedulerTest;
import net.ibbaa.keepitup.service.alarm.AlarmServiceMockTest;
import net.ibbaa.keepitup.service.alarm.AlarmServiceTest;
import net.ibbaa.keepitup.service.alarm.SystemAlarmMediaPlayerTest;
import net.ibbaa.keepitup.service.network.ConnectCommandTest;
import net.ibbaa.keepitup.service.network.DownloadCommandTest;
import net.ibbaa.keepitup.service.network.PingCommandTest;
import net.ibbaa.keepitup.service.network.PingOutputParserTest;
import net.ibbaa.keepitup.ui.adapter.NetworkTaskUIWrapperTest;
import net.ibbaa.keepitup.ui.clipboard.SystemClipboardManagerTest;
import net.ibbaa.keepitup.ui.mapping.EnumMappingTest;
import net.ibbaa.keepitup.ui.sync.DBPurgeTaskTest;
import net.ibbaa.keepitup.ui.sync.ExportTaskTest;
import net.ibbaa.keepitup.ui.sync.ImportTaskTest;
import net.ibbaa.keepitup.ui.sync.LogEntryUIBroadcastReceiverTest;
import net.ibbaa.keepitup.ui.sync.LogEntryUIInitTaskTest;
import net.ibbaa.keepitup.ui.sync.LogEntryUISyncTaskTest;
import net.ibbaa.keepitup.ui.sync.NetworkTaskMainUIInitTaskTest;
import net.ibbaa.keepitup.ui.sync.NetworkTaskMainUISyncTaskTest;
import net.ibbaa.keepitup.ui.sync.NetworkTaskUIBroadcastReceiverTest;
import net.ibbaa.keepitup.ui.validation.ConnectCountFieldValidatorTest;
import net.ibbaa.keepitup.ui.validation.FilenameFieldValidatorTest;
import net.ibbaa.keepitup.ui.validation.HostFieldValidatorTest;
import net.ibbaa.keepitup.ui.validation.IntervalFieldValidatorTest;
import net.ibbaa.keepitup.ui.validation.NotificationAfterFailuresFieldValidatorTest;
import net.ibbaa.keepitup.ui.validation.NullAccessTypeDataValidatorTest;
import net.ibbaa.keepitup.ui.validation.NullNetworkTaskValidatorTest;
import net.ibbaa.keepitup.ui.validation.NumberPickerColorListenerTest;
import net.ibbaa.keepitup.ui.validation.PingCountFieldValidatorTest;
import net.ibbaa.keepitup.ui.validation.PingPackageSizeFieldValidatorTest;
import net.ibbaa.keepitup.ui.validation.PortFieldValidatorTest;
import net.ibbaa.keepitup.ui.validation.StandardAccessTypeDataValidatorTest;
import net.ibbaa.keepitup.ui.validation.StandardHostPortValidatorTest;
import net.ibbaa.keepitup.ui.validation.StandardIntervalValidatorTest;
import net.ibbaa.keepitup.ui.validation.TextColorValidatingWatcherTest;
import net.ibbaa.keepitup.ui.validation.URLFieldValidatorTest;
import net.ibbaa.keepitup.ui.validation.URLValidatorTest;
import net.ibbaa.keepitup.ui.validation.ValidationResultTest;
import net.ibbaa.keepitup.util.BundleUtilTest;
import net.ibbaa.keepitup.util.DebugUtilTest;
import net.ibbaa.keepitup.util.ExceptionUtilTest;
import net.ibbaa.keepitup.util.FileUtilTest;
import net.ibbaa.keepitup.util.HTTPUtilTest;
import net.ibbaa.keepitup.util.JSONUtilTest;
import net.ibbaa.keepitup.util.LogUtilTest;
import net.ibbaa.keepitup.util.NumberUtilTest;
import net.ibbaa.keepitup.util.StreamUtilTest;
import net.ibbaa.keepitup.util.StringUtilTest;
import net.ibbaa.keepitup.util.TimeUtilTest;
import net.ibbaa.keepitup.util.UIUtilTest;
import net.ibbaa.keepitup.util.URLUtilTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        //net.ibbaa.keepitup.db
        AccessTypeDataDAOTest.class,
        AccessTypeDataDBConstantsTest.class,
        DBMigrateTest.class,
        DBSetupTest.class,
        IntervalDAOTest.class,
        IntervalDBConstantsTest.class,
        LogDAOTest.class,
        LogDBConstantsTest.class,
        NetworkTaskDAOTest.class,
        NetworkTaskDBConstantsTest.class,
        SchedulerIdGeneratorTest.class,
        SchedulerIdHistoryDBConstantsTest.class,
        SchedulerStateDAOTest.class,
        SchedulerStateDBConstantsTest.class,
        //net.ibbaa.keepitup.logging
        DumpTest.class,
        LogTest.class,
        NetworkTaskLogTest.class,
        //net.ibbaa.keepitup.model
        AccessTypeDataTest.class,
        AccessTypeTest.class,
        FileEntryTest.class,
        IntervalTest.class,
        LogEntryTest.class,
        NetworkTaskTest.class,
        SchedulerIdTest.class,
        TimeTest.class,
        //net.ibbaa.keepitup.model.validator
        AccessTypeDataValidatorTest.class,
        IntervalValidatorTest.class,
        NetworkTaskValidatorTest.class,
        //net.ibbaa.keepitup.notification
        NotificationHandlerTest.class,
        //net.ibbaa.keepitup.resources
        JSONSystemMigrateTest.class,
        JSONSystemSetupTest.class,
        PreferenceManagerTest.class,
        PreferenceSetupTest.class,
        SystemWorkerFactoryTest.class,
        //net.ibbaa.keepitup.service
        ConnectNetworkTaskWorkerTest.class,
        DownloadNetworkTaskWorkerTest.class,
        NetworkTaskProcessBroadcastReceiverTest.class,
        NetworkTaskProcessPoolTest.class,
        NetworkTaskProcessServiceSchedulerTest.class,
        NetworkTaskRunningNotificationServiceTest.class,
        NetworkTaskWorkerTest.class,
        NullNetworkTaskWorkerTest.class,
        PingNetworkTaskWorkerTest.class,
        SystemDocumentManagerTest.class,
        SystemFileManagerTest.class,
        SystemThemeManagerTest.class,
        TimeBasedSuspensionBroadcastReceiverTest.class,
        TimeBasedSuspensionSchedulerTest.class,
        //net.ibbaa.keepitup.service.alarm
        AlarmServiceMockTest.class,
        AlarmServiceTest.class,
        SystemAlarmMediaPlayerTest.class,
        //net.ibbaa.keepitup.service.network
        ConnectCommandTest.class,
        DownloadCommandTest.class,
        PingCommandTest.class,
        PingOutputParserTest.class,
        //net.ibbaa.keepitup.ui.adaper
        NetworkTaskUIWrapperTest.class,
        //net.ibbaa.keepitup.ui.clipboard
        SystemClipboardManagerTest.class,
        //net.ibbaa.keepitup.ui.mapping
        EnumMappingTest.class,
        //net.ibbaa.keepitup.ui.sync
        DBPurgeTaskTest.class,
        ExportTaskTest.class,
        ImportTaskTest.class,
        LogEntryUIBroadcastReceiverTest.class,
        LogEntryUIInitTaskTest.class,
        LogEntryUISyncTaskTest.class,
        NetworkTaskMainUIInitTaskTest.class,
        NetworkTaskMainUISyncTaskTest.class,
        NetworkTaskUIBroadcastReceiverTest.class,
        //net.ibbaa.keepitup.ui.validation
        ConnectCountFieldValidatorTest.class,
        FilenameFieldValidatorTest.class,
        HostFieldValidatorTest.class,
        IntervalFieldValidatorTest.class,
        NotificationAfterFailuresFieldValidatorTest.class,
        NullAccessTypeDataValidatorTest.class,
        NullNetworkTaskValidatorTest.class,
        NumberPickerColorListenerTest.class,
        PingCountFieldValidatorTest.class,
        PingPackageSizeFieldValidatorTest.class,
        PortFieldValidatorTest.class,
        StandardAccessTypeDataValidatorTest.class,
        StandardHostPortValidatorTest.class,
        StandardIntervalValidatorTest.class,
        TextColorValidatingWatcherTest.class,
        URLFieldValidatorTest.class,
        URLValidatorTest.class,
        ValidationResultTest.class,
        //net.ibbaa.keepitup.util
        BundleUtilTest.class,
        DebugUtilTest.class,
        ExceptionUtilTest.class,
        FileUtilTest.class,
        HTTPUtilTest.class,
        JSONUtilTest.class,
        LogUtilTest.class,
        NumberUtilTest.class,
        StreamUtilTest.class,
        StringUtilTest.class,
        TimeUtilTest.class,
        UIUtilTest.class,
        URLUtilTest.class})
public class OtherTestSuite {
}
