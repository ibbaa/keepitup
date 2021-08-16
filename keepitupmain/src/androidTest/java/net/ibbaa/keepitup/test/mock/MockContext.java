package net.ibbaa.keepitup.test.mock;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;

public class MockContext extends ContextWrapper {

    private final Context testContext;

    public MockContext(Context testContext, Context targetContext) {
        super(targetContext);
        this.testContext = testContext;
    }

    @Override
    public Resources getResources() {
        return new MockResources(getAssets(), testContext.getResources(), super.getResources());
    }
}
