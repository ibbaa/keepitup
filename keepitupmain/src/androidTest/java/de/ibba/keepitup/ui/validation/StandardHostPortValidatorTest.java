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
import static org.junit.Assert.assertTrue;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StandardHostPortValidatorTest {

    private StandardHostPortValidator validator;
    private Resources resources;

    @Before
    public void beforeEachTestMethod() {
        validator = new StandardHostPortValidator(InstrumentationRegistry.getTargetContext());
        resources = InstrumentationRegistry.getTargetContext().getResources();
    }

    @Test
    public void testValidateAddress() {
        ValidationResult result = validator.validateAddress("www.host.com");
        assertTrue(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_host_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.validation_successful), result.getMessage());
        result = validator.validateAddress("3ffe:1900:4545:3:200:f8ff:fe21:67cf");
        assertTrue(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_host_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.validation_successful), result.getMessage());
        result = validator.validateAddress("192.168.178.100");
        assertTrue(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_host_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.validation_successful), result.getMessage());
        result = validator.validateAddress("not valid");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_host_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.invalid_host_format), result.getMessage());
    }

    @Test
    public void testValidatePort() {
        ValidationResult result = validator.validatePort("80");
        assertTrue(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_port_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.validation_successful), result.getMessage());
        result = validator.validatePort("abc");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_port_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.invalid_number_format), result.getMessage());
        result = validator.validatePort("-1");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_port_field_name), result.getFieldName());
        assertEquals(String.format(resources.getString(R.string.invalid_range_minimim), 0), result.getMessage());
        result = validator.validatePort("12345678");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_port_field_name), result.getFieldName());
        assertEquals(String.format(resources.getString(R.string.invalid_range_maximum), 65535), result.getMessage());
    }

    @Test
    public void testValidateInterval() {
        ValidationResult result = validator.validateInterval("15");
        assertTrue(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_interval_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.validation_successful), result.getMessage());
        result = validator.validateInterval("33x");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_interval_field_name), result.getFieldName());
        assertEquals(resources.getString(R.string.invalid_number_format), result.getMessage());
        result = validator.validateInterval("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_interval_field_name), result.getFieldName());
        assertEquals(String.format(resources.getString(R.string.invalid_range_minimim), 1), result.getMessage());
        result = validator.validateInterval("0");
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_interval_field_name), result.getFieldName());
        assertEquals(String.format(resources.getString(R.string.invalid_range_minimim), 1), result.getMessage());
        result = validator.validateInterval(String.valueOf((long) Integer.MAX_VALUE + 1));
        assertFalse(result.isValidationSuccessful());
        assertEquals(resources.getString(R.string.task_interval_field_name), result.getFieldName());
        assertEquals(String.format(resources.getString(R.string.invalid_range_maximum), Integer.MAX_VALUE), result.getMessage());
    }
}
