package de.ibba.keepitup.test.mock;

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

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MockResources extends Resources {

    private final Resources testResources;
    private final Resources targetResources;
    private final Map<Integer, String> testStringResources;
    private final Map<Integer, String> targetStringResources;

    public MockResources(AssetManager assetManager, Resources testResources, Resources targetResources) {
        super(assetManager, null, null);
        this.testResources = testResources;
        this.targetResources = targetResources;
        testStringResources = new HashMap<>();
        targetStringResources = new HashMap<>();
    }

    private void parseStringResources(Class resources) {
        Field[] fields = resources.getDeclaredFields();
        for (Field currentField : fields) {

        }
    }

    @Override
    public CharSequence getText(int id) throws NotFoundException {
        return targetResources.getText(id);
    }

    @Override
    public Typeface getFont(int id) throws NotFoundException {
        return targetResources.getFont(id);
    }

    @Override
    public CharSequence getQuantityText(int id, int quantity) throws NotFoundException {
        return targetResources.getQuantityText(id, quantity);
    }

    @Override
    public String getString(int id) throws NotFoundException {
        return targetResources.getString(id);
    }

    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        return targetResources.getString(id, formatArgs);
    }

    @Override
    public String getQuantityString(int id, int quantity, Object... formatArgs) throws NotFoundException {
        return targetResources.getQuantityString(id, quantity, formatArgs);
    }

    @Override
    public String getQuantityString(int id, int quantity) throws NotFoundException {
        return targetResources.getQuantityString(id, quantity);
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        return targetResources.getText(id, def);
    }

    @Override
    public CharSequence[] getTextArray(int id) throws NotFoundException {
        return targetResources.getTextArray(id);
    }

    @Override
    public String[] getStringArray(int id) throws NotFoundException {
        return targetResources.getStringArray(id);
    }

    @Override
    public int[] getIntArray(int id) throws NotFoundException {
        return targetResources.getIntArray(id);
    }

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

    @Override
    public ColorStateList getColorStateList(int id) throws NotFoundException {
        return targetResources.getColorStateList(id);
    }

    @Override
    public ColorStateList getColorStateList(int id, Resources.Theme theme) throws NotFoundException {
        return targetResources.getColorStateList(id, theme);
    }

    @Override
    public boolean getBoolean(int id) throws NotFoundException {
        return targetResources.getBoolean(id);
    }

    @Override
    public int getInteger(int id) throws NotFoundException {
        return targetResources.getInteger(id);
    }

    @Override
    public XmlResourceParser getLayout(int id) throws NotFoundException {
        return targetResources.getLayout(id);
    }

    @Override
    public XmlResourceParser getAnimation(int id) throws NotFoundException {
        return targetResources.getAnimation(id);
    }

    @Override
    public XmlResourceParser getXml(int id) throws NotFoundException {
        return targetResources.getXml(id);
    }

    @Override
    public InputStream openRawResource(int id) throws NotFoundException {
        return targetResources.openRawResource(id);
    }

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
        targetResources.updateConfiguration(config, metrics);
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
