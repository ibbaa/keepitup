package net.ibbaa.keepitup.resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.test.mock.TestRegistry;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class SystemServiceFactoryTest {

    private SystemServiceFactory factory;

    @Before
    public void beforeEachTestMethod() {
        factory = new SystemServiceFactory();
    }

    @Test
    public void testCreateSystemSetup() {
        ISystemSetup jsonSetup = factory.createSystemSetup(TestRegistry.getContext(), TestRegistry.getContext().getResources().getString(R.string.system_setup_json_implementation));
        assertNotNull(jsonSetup);
        assertTrue(jsonSetup instanceof JSONSystemSetup);
    }

    @Test(expected = RuntimeException.class)
    public void testCreateSystemSetupFailure() {
        factory.createSystemSetup(TestRegistry.getContext(), "de.test.failure.NoSetup");
    }
}
