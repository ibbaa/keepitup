package de.ibba.keepitup.test.mock;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

public class TestRegistry {

    public static Context getContext() {
        return new MockContext(InstrumentationRegistry.getContext(), InstrumentationRegistry.getTargetContext());
    }
}
