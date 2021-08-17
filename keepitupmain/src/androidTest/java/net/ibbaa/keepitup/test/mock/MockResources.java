/*
 * Copyright (c) 2021. Alwin Ibba
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

package net.ibbaa.keepitup.test.mock;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Movie;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.NonNull;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MockResources extends Resources {

    private final Resources testResources;
    private final Resources targetResources;
    private final Map<String, Integer> testStringResources;
    private final Map<Integer, String> targetStringResources;
    private final Map<String, Integer> testIntegerResources;
    private final Map<Integer, String> targetIntegerResources;
    private final Map<String, Integer> testBooleanResources;
    private final Map<Integer, String> targetBooleanResources;

    public MockResources(AssetManager assetManager, Resources testResources, Resources targetResources) {
        super(assetManager, null, null);
        this.testResources = testResources;
        this.targetResources = targetResources;
        testStringResources = new HashMap<>();
        targetStringResources = new HashMap<>();
        testIntegerResources = new HashMap<>();
        targetIntegerResources = new HashMap<>();
        testBooleanResources = new HashMap<>();
        targetBooleanResources = new HashMap<>();
        parseResources(net.ibbaa.keepitup.test.R.string.class, (String name, Integer value) -> testStringResources.put(name, value));
        parseResources(net.ibbaa.keepitup.R.string.class, (String name, Integer value) -> targetStringResources.put(value, name));
        parseResources(net.ibbaa.keepitup.test.R.integer.class, (String name, Integer value) -> testIntegerResources.put(name, value));
        parseResources(net.ibbaa.keepitup.R.integer.class, (String name, Integer value) -> targetIntegerResources.put(value, name));
        parseResources(net.ibbaa.keepitup.test.R.bool.class, (String name, Integer value) -> testBooleanResources.put(name, value));
        parseResources(net.ibbaa.keepitup.R.bool.class, (String name, Integer value) -> targetBooleanResources.put(value, name));
    }

    private void parseResources(Class<?> resources, BiConsumer<String, Integer> consumer) {
        try {
            Field[] fields = resources.getDeclaredFields();
            for (Field currentField : fields) {
                if (Modifier.isStatic(currentField.getModifiers())) {
                    String name = currentField.getName();
                    int value = currentField.getInt(null);
                    consumer.accept(name, value);
                }
            }
        } catch (IllegalAccessException exc) {
            throw new RuntimeException(exc);
        }
    }

    @NonNull
    @Override
    public CharSequence getText(int id) throws NotFoundException {
        return targetResources.getText(id);
    }

    @NonNull
    @Override
    public Typeface getFont(int id) throws NotFoundException {
        return targetResources.getFont(id);
    }

    @NonNull
    @Override
    public CharSequence getQuantityText(int id, int quantity) throws NotFoundException {
        return targetResources.getQuantityText(id, quantity);
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        String resourceName = targetStringResources.get(id);
        Integer testId = testStringResources.get(resourceName);
        if (testId != null) {
            return testResources.getString(testId);
        }
        return targetResources.getString(id);
    }

    @NonNull
    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        String resourceName = targetStringResources.get(id);
        Integer testId = testStringResources.get(resourceName);
        if (testId != null) {
            return testResources.getString(testId, formatArgs);
        }
        return targetResources.getString(id, formatArgs);
    }

    @NonNull
    @Override
    public String getQuantityString(int id, int quantity, Object... formatArgs) throws NotFoundException {
        return targetResources.getQuantityString(id, quantity, formatArgs);
    }

    @NonNull
    @Override
    public String getQuantityString(int id, int quantity) throws NotFoundException {
        return targetResources.getQuantityString(id, quantity);
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        return targetResources.getText(id, def);
    }

    @NonNull
    @Override
    public CharSequence[] getTextArray(int id) throws NotFoundException {
        return targetResources.getTextArray(id);
    }

    @NonNull
    @Override
    public String[] getStringArray(int id) throws NotFoundException {
        return targetResources.getStringArray(id);
    }

    @NonNull
    @Override
    public int[] getIntArray(int id) throws NotFoundException {
        return targetResources.getIntArray(id);
    }

    @NonNull
    @Override
    public TypedArray obtainTypedArray(int id) throws NotFoundException {
        return targetResources.obtainTypedArray(id);
    }

    @Override
    public float getDimension(int id) throws NotFoundException {
        return targetResources.getDimension(id);
    }

    @Override
    public int getDimensionPixelOffset(int id) throws NotFoundException {
        return targetResources.getDimensionPixelOffset(id);
    }

    @Override
    public int getDimensionPixelSize(int id) throws NotFoundException {
        return targetResources.getDimensionPixelSize(id);
    }

    @Override
    public float getFraction(int id, int base, int pbase) {
        return targetResources.getFraction(id, base, pbase);
    }

    @Override
    public Drawable getDrawable(int id) throws NotFoundException {
        return targetResources.getDrawable(id);
    }

    @Override
    public Drawable getDrawable(int id, Resources.Theme theme) throws NotFoundException {
        return targetResources.getDrawable(id, theme);
    }

    @Override
    public Drawable getDrawableForDensity(int id, int density) throws NotFoundException {
        return targetResources.getDrawableForDensity(id, density);
    }

    @Override
    public Drawable getDrawableForDensity(int id, int density, Resources.Theme theme) {
        return targetResources.getDrawableForDensity(id, density, theme);
    }

    @Override
    public Movie getMovie(int id) throws NotFoundException {
        return targetResources.getMovie(id);
    }

    @Override
    public int getColor(int id) throws NotFoundException {
        return targetResources.getColor(id);
    }

    @Override
    public int getColor(int id, Resources.Theme theme) throws NotFoundException {
        return targetResources.getColor(id, theme);
    }

    @NonNull
    @Override
    public ColorStateList getColorStateList(int id) throws NotFoundException {
        return targetResources.getColorStateList(id);
    }

    @NonNull
    @Override
    public ColorStateList getColorStateList(int id, Resources.Theme theme) throws NotFoundException {
        return targetResources.getColorStateList(id, theme);
    }

    @Override
    public boolean getBoolean(int id) throws NotFoundException {
        String resourceName = targetBooleanResources.get(id);
        Integer testId = testBooleanResources.get(resourceName);
        if (testId != null) {
            return testResources.getBoolean(testId);
        }
        return targetResources.getBoolean(id);
    }

    @Override
    public int getInteger(int id) throws NotFoundException {
        String resourceName = targetIntegerResources.get(id);
        Integer testId = testIntegerResources.get(resourceName);
        if (testId != null) {
            return testResources.getInteger(testId);
        }
        return targetResources.getInteger(id);
    }

    @NonNull
    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
        return targetResources.getLayout(id);
    }

    @NonNull
    @Override
    public XmlResourceParser getAnimation(int id) throws NotFoundException {
        return targetResources.getAnimation(id);
    }

    @NonNull
    @Override
    public XmlResourceParser getXml(int id) throws NotFoundException {
        return targetResources.getXml(id);
    }

    @NonNull
    @Override
    public InputStream openRawResource(int id) throws NotFoundException {
        return targetResources.openRawResource(id);
    }

    @NonNull
    @Override
    public InputStream openRawResource(int id, TypedValue value) throws NotFoundException {
        return targetResources.openRawResource(id, value);
    }

    @Override
    public AssetFileDescriptor openRawResourceFd(int id) throws NotFoundException {
        return targetResources.openRawResourceFd(id);
    }

    @Override
    public void getValue(int id, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        targetResources.getValue(id, outValue, resolveRefs);
    }

    @Override
    public void getValueForDensity(int id, int density, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        targetResources.getValueForDensity(id, density, outValue, resolveRefs);
    }

    @Override
    public void getValue(String name, TypedValue outValue, boolean resolveRefs) throws NotFoundException {
        targetResources.getValue(name, outValue, resolveRefs);
    }

    @Override
    public TypedArray obtainAttributes(AttributeSet set, int[] attrs) {
        return targetResources.obtainAttributes(set, attrs);
    }

    @Override
    public void updateConfiguration(Configuration config, DisplayMetrics metrics) {
        if (targetResources != null) {
            targetResources.updateConfiguration(config, metrics);
        }
    }

    @Override
    public DisplayMetrics getDisplayMetrics() {
        return targetResources.getDisplayMetrics();
    }

    @Override
    public Configuration getConfiguration() {
        return targetResources.getConfiguration();
    }

    @Override
    public int getIdentifier(String name, String defType, String defPackage) {
        return targetResources.getIdentifier(name, defType, defPackage);
    }

    @Override
    public String getResourceName(int resid) throws NotFoundException {
        return targetResources.getResourceName(resid);
    }

    @Override
    public String getResourcePackageName(int resid) throws NotFoundException {
        return targetResources.getResourcePackageName(resid);
    }

    @Override
    public String getResourceTypeName(int resid) throws NotFoundException {
        return targetResources.getResourceTypeName(resid);
    }

    @Override
    public String getResourceEntryName(int resid) throws NotFoundException {
        return targetResources.getResourceEntryName(resid);
    }

    @Override
    public void parseBundleExtras(XmlResourceParser parser, Bundle outBundle) throws IOException, XmlPullParserException {
        targetResources.parseBundleExtras(parser, outBundle);
    }

    @Override
    public void parseBundleExtra(String tagName, AttributeSet attrs, Bundle outBundle) throws XmlPullParserException {
        targetResources.parseBundleExtra(tagName, attrs, outBundle);
    }
}
