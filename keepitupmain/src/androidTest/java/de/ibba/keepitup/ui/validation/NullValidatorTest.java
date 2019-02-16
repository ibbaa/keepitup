package de.ibba.keepitup.ui.validation;

import android.content.res.Resources;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.ibba.keepitup.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class NullValidatorTest {

    private NullValidator validator;
    private Resources resources;

    @Before
    public void beforeEachTestMethod() {
        validator = new NullValidator(InstrumentationRegistry.getTargetContext());
        resources = InstrumentationRegistry.getTargetContext().getResources();
    }

    @Test
    public void testValidate() {
        ValidationResult result = validator.validateAddress("www.host.com");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_address_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.invalid_no_value), result.getMessage());
        result = validator.validatePort("23");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_port_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.invalid_no_value), result.getMessage());
        result = validator.validateInterval("1");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_interval_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.invalid_no_value), result.getMessage());
    }
}
