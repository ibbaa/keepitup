package de.ibba.keepitup.ui.validation;

public interface Validator {

    ValidationResult validateAddress(String address);

    ValidationResult validatePort(String address);

    ValidationResult validateInterval(String address);
}
