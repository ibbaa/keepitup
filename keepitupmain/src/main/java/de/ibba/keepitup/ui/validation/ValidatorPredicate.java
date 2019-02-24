package de.ibba.keepitup.ui.validation;

@FunctionalInterface
public interface ValidatorPredicate<T> {
    boolean validate(T t);
}
