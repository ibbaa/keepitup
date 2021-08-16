package net.ibbaa.keepitup.test.mock;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

public class TestRegistry {

    public static Context getContext() {
        return new MockContext(InstrumentationRegistry.getInstrumentation().getContext(), InstrumentationRegistry.getInstrumentation().getTargetContext());
    }
}
