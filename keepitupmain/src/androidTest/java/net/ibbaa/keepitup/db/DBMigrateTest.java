/*
 * Copyright (c) 2023. Alwin Ibba
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

package net.ibbaa.keepitup.db;

import static org.junit.Assert.assertEquals;

import android.database.sqlite.SQLiteException;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import net.ibbaa.keepitup.logging.Dump;
import net.ibbaa.keepitup.model.Interval;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class DBMigrateTest {

    private DBSetup setup;
    private DBMigrate migrate;
    private IntervalDAO intervalDAO;

    @Before
    public void beforeEachTestMethod() {
        Dump.initialize(null);
        setup = new DBSetup(TestRegistry.getContext());
        migrate = new DBMigrate(setup);
        intervalDAO = new IntervalDAO(TestRegistry.getContext());
        setup.dropTables(TestRegistry.getContext());
    }

    @After
    public void afterEachTestMethod() {
        setup.dropTables(TestRegistry.getContext());
    }

    @Test
    public void testUgradeFrom1To2() {
        setup.createTables(TestRegistry.getContext());
        setup.dropIntervalTable(TestRegistry.getContext());
        migrate.doUpgrade(TestRegistry.getContext(), 1, 2);
        intervalDAO.insertInterval(new Interval());
        List<Interval> intervals = intervalDAO.readAllIntervals();
        assertEquals(1, intervals.size());
    }

    @Test(expected = SQLiteException.class)
    public void testDowngradeFrom2To1() {
        setup.createTables(TestRegistry.getContext());
        migrate.doDowngrade(TestRegistry.getContext(), 2, 1);
        intervalDAO.readAllIntervals();
    }
}
