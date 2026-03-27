/*
 * Copyright (c) 2026 Alwin Ibba
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

package net.ibbaa.keepitup.ui;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.db.DBSetup;
import net.ibbaa.keepitup.db.HeaderDAO;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.AccessType;
import net.ibbaa.keepitup.model.Header;
import net.ibbaa.keepitup.resources.PreferenceManager;
import net.ibbaa.keepitup.resources.PreferenceSetup;
import net.ibbaa.keepitup.ui.dialog.HeadersDialog;
import net.ibbaa.keepitup.ui.dialog.SettingsInput;
import net.ibbaa.keepitup.ui.dialog.SettingsInputDialog;
import net.ibbaa.keepitup.ui.mapping.EnumMapping;
import net.ibbaa.keepitup.ui.support.HeadersSupport;
import net.ibbaa.keepitup.ui.sync.DBSyncResult;
import net.ibbaa.keepitup.ui.sync.HeaderSyncHandler;
import net.ibbaa.keepitup.ui.validation.ConnectCountFieldValidator;
import net.ibbaa.keepitup.ui.validation.HostFieldValidator;
import net.ibbaa.keepitup.ui.validation.IntervalFieldValidator;
import net.ibbaa.keepitup.ui.validation.PingCountFieldValidator;
import net.ibbaa.keepitup.ui.validation.PingPackageSizeFieldValidator;
import net.ibbaa.keepitup.ui.validation.PortFieldValidator;
import net.ibbaa.keepitup.ui.validation.ResolveHostFieldValidator;
import net.ibbaa.keepitup.ui.validation.ResolvePortFieldValidator;
import net.ibbaa.keepitup.ui.validation.URLFieldValidator;
import net.ibbaa.keepitup.util.BundleUtil;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.UIUtil;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"unused"})
public class DefaultsActivity extends SettingsInputActivity implements HeadersSupport {

    private RadioGroup accessTypeGroup;
    private TextView addressText;
    private TextView portText;
    private TextView intervalText;
    private TextView intervalMinutesText;
    private TextView pingCountText;
    private TextView pingPackageSizeText;
    private TextView connectCountText;
    private TextView connectToHostText;
    private TextView connectToPortText;
    private SwitchMaterial stopOnSuccessSwitch;
    private TextView stopOnSuccessOnOffText;
    private SwitchMaterial ignoreSSLErrorSwitch;
    private TextView ignoreSSLErrorOnOffText;
    private SwitchMaterial onlyWifiSwitch;
    private TextView onlyWifiOnOffText;
    private SwitchMaterial notificationSwitch;
    private TextView notificationOnOffText;
    private SwitchMaterial highPrioSwitch;
    private TextView highPrioOnOffText;
    private boolean globalHeadersExpanded;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        initGlobalHeadersExpanded(savedInstanceState);
        setContentView(R.layout.activity_defaults);
        initEdgeToEdgeInsets(R.id.layout_activity_defaults);
        prepareAccessTypeRadioButtons();
        prepareAddressField();
        preparePortField();
        prepareIntervalField();
        preparePingCountField();
        preparePingPackageSizeField();
        prepareConnectCountField();
        prepareConnectToHostField();
        prepareConnectToPortField();
        prepareStopOnSuccessSwitch();
        prepareIgnoreSSLErrorSwitch();
        prepareGlobalHeadersField();
        prepareOnlyWifiSwitch();
        prepareNotificationSwitch();
        prepareHighPrioSwitch();
    }

    private void initGlobalHeadersExpanded(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(getGlobalHeadersExpandedKey())) {
            globalHeadersExpanded = savedInstanceState.getBoolean(getGlobalHeadersExpandedKey());
        } else {
            globalHeadersExpanded = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_defaults, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_action_activity_defaults_reset) {
            Log.d(DefaultsActivity.class.getName(), "menu_action_activity_defaults_reset triggered");
            PreferenceSetup preferenceSetup = new PreferenceSetup(this);
            preferenceSetup.removeDefaults();
            resetHeaders();
            recreateActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetHeaders() {
        try {
            HeaderDAO headerDAO = new HeaderDAO(this);
            headerDAO.deleteGlobalHeaders();
            DBSetup setup = new DBSetup(this);
            setup.initializeHeaderTable();
        } catch (Exception exc) {
            Log.e(DefaultsActivity.class.getName(), "Error deleting headers", exc);
        }
        HeaderSyncHandler handler = new HeaderSyncHandler(this);
        handler.reset();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putBoolean(getGlobalHeadersExpandedKey(), globalHeadersExpanded);
    }

    private void prepareAccessTypeRadioButtons() {
        Log.d(DefaultsActivity.class.getName(), "prepareAccessTypeRadioButtons");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        accessTypeGroup = findViewById(R.id.radiogroup_activity_defaults_accesstype);
        accessTypeGroup.setOnCheckedChangeListener(null);
        EnumMapping mapping = new EnumMapping(this);
        AccessType[] accessTypes = AccessType.values();
        AccessType type = preferenceManager.getPreferenceAccessType();
        for (int ii = 0; ii < accessTypes.length; ii++) {
            AccessType accessType = accessTypes[ii];
            RadioButton newRadioButton = new RadioButton(this);
            newRadioButton.setText(mapping.getAccessTypeText(accessType));
            newRadioButton.setTextColor(getColor(R.color.textColor));
            newRadioButton.setButtonTintList(ColorStateList.valueOf(ResourcesCompat.getColor(getResources(), R.color.textColor, null)));
            newRadioButton.setId(View.generateViewId());
            if (type == null && ii == 0) {
                newRadioButton.setChecked(true);
            } else {
                newRadioButton.setChecked(accessType.equals(type));
            }
            newRadioButton.setTag(accessType);
            LinearLayout.LayoutParams layoutParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            accessTypeGroup.addView(newRadioButton, ii, layoutParams);
        }
        accessTypeGroup.setOnCheckedChangeListener(this::onAccessTypeChanged);
    }

    private void onAccessTypeChanged(RadioGroup group, int checkedId) {
        Log.d(DefaultsActivity.class.getName(), "onAccessTypeChanged");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        RadioButton selectedAccessTypeRadioButton = accessTypeGroup.findViewById(checkedId);
        if (selectedAccessTypeRadioButton != null) {
            AccessType accessType = (AccessType) selectedAccessTypeRadioButton.getTag();
            Log.d(DefaultsActivity.class.getName(), "checked access type radio button is " + accessType);
            if (accessType != null) {
                preferenceManager.setPreferenceAccessType(accessType);
            }
        }
    }

    private void prepareAddressField() {
        Log.d(DefaultsActivity.class.getName(), "prepareAddressField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        addressText = findViewById(R.id.textview_activity_defaults_address);
        setAddress(preferenceManager.getPreferenceAddress());
        CardView addressCardView = findViewById(R.id.cardview_activity_defaults_address);
        addressCardView.setOnClickListener(this::showAddressInputDialog);
    }

    private void preparePortField() {
        Log.d(DefaultsActivity.class.getName(), "preparePortField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        portText = findViewById(R.id.textview_activity_defaults_port);
        setPort(String.valueOf(preferenceManager.getPreferencePort()));
        CardView portCardView = findViewById(R.id.cardview_activity_defaults_port);
        portCardView.setOnClickListener(this::showPortInputDialog);
    }

    private void prepareIntervalField() {
        Log.d(DefaultsActivity.class.getName(), "prepareIntervalField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        intervalText = findViewById(R.id.textview_activity_defaults_interval);
        intervalMinutesText = findViewById(R.id.textview_activity_defaults_interval_minutes);
        setInterval(String.valueOf(preferenceManager.getPreferenceInterval()));
        CardView intervalCardView = findViewById(R.id.cardview_activity_defaults_interval);
        intervalCardView.setOnClickListener(this::showIntervalInputDialog);
    }

    private void preparePingCountField() {
        Log.d(DefaultsActivity.class.getName(), "preparePingCountField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        pingCountText = findViewById(R.id.textview_activity_defaults_ping_count);
        setPingCount(String.valueOf(preferenceManager.getPreferencePingCount()));
        CardView pingCountCardView = findViewById(R.id.cardview_activity_defaults_ping_count);
        pingCountCardView.setOnClickListener(this::showPingCountInputDialog);
    }

    private void preparePingPackageSizeField() {
        Log.d(DefaultsActivity.class.getName(), "preparePingPackageSizeField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        pingPackageSizeText = findViewById(R.id.textview_activity_defaults_ping_package_size);
        setPingPackageSize(String.valueOf(preferenceManager.getPreferencePingPackageSize()));
        CardView pingPackageSizeCardView = findViewById(R.id.cardview_activity_defaults_ping_package_size);
        pingPackageSizeCardView.setOnClickListener(this::showPingPackageSizeInputDialog);
    }

    private void prepareConnectCountField() {
        Log.d(DefaultsActivity.class.getName(), "prepareConnectCountField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        connectCountText = findViewById(R.id.textview_activity_defaults_connect_count);
        setConnectCount(String.valueOf(preferenceManager.getPreferenceConnectCount()));
        CardView connectCountCardView = findViewById(R.id.cardview_activity_defaults_connect_count);
        connectCountCardView.setOnClickListener(this::showConnectCountInputDialog);
    }

    private void prepareConnectToHostField() {
        Log.d(DefaultsActivity.class.getName(), "prepareConnectToHostField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        connectToHostText = findViewById(R.id.textview_activity_defaults_connect_to_host);
        String connectToHost = preferenceManager.getPreferenceResolveAddress();
        connectToHost = UIUtil.getNotSetIfEmpty(this, connectToHost);
        setConnectToHost(connectToHost);
        CardView connectToHostCardView = findViewById(R.id.cardview_activity_defaults_connect_to_host);
        connectToHostCardView.setOnClickListener(this::showConnectToHostInputDialog);
    }

    private void prepareConnectToPortField() {
        Log.d(DefaultsActivity.class.getName(), "prepareConnectToPortField");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        connectToPortText = findViewById(R.id.textview_activity_defaults_connect_to_port);
        int resolvePort = preferenceManager.getPreferenceResolvePort();
        String connectToPort = UIUtil.getNotSetIfNegative(this, resolvePort);
        setConnectToPort(connectToPort);
        CardView connectToPortCardView = findViewById(R.id.cardview_activity_defaults_connect_to_port);
        connectToPortCardView.setOnClickListener(this::showConnectToPortInputDialog);
    }

    private void prepareStopOnSuccessSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareStopOnSuccessSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        stopOnSuccessSwitch = findViewById(R.id.switch_activity_defaults_stop_on_success);
        stopOnSuccessOnOffText = findViewById(R.id.textview_activity_defaults_stop_on_success_on_off);
        stopOnSuccessSwitch.setOnCheckedChangeListener(null);
        stopOnSuccessSwitch.setChecked(preferenceManager.getPreferenceStopOnSuccess());
        stopOnSuccessSwitch.setOnCheckedChangeListener(this::onStopOnSuccessCheckedChanged);
        prepareStopOnSuccessOnOffText();
    }

    private void prepareStopOnSuccessOnOffText() {
        stopOnSuccessOnOffText.setText(stopOnSuccessSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onStopOnSuccessCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onStopOnSuccessCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceStopOnSuccess(isChecked);
        prepareStopOnSuccessOnOffText();
    }

    private void prepareIgnoreSSLErrorSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareIgnoreSSLErrorSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        ignoreSSLErrorSwitch = findViewById(R.id.switch_activity_defaults_ignore_ssl_error);
        ignoreSSLErrorOnOffText = findViewById(R.id.textview_activity_defaults_ignore_ssl_error_on_off);
        ignoreSSLErrorSwitch.setOnCheckedChangeListener(null);
        ignoreSSLErrorSwitch.setChecked(preferenceManager.getPreferenceIgnoreSSLError());
        ignoreSSLErrorSwitch.setOnCheckedChangeListener(this::onIgnoreSSLErrorCheckedChanged);
        prepareIgnoreSSLErrorOnOffText();
    }

    private void prepareIgnoreSSLErrorOnOffText() {
        ignoreSSLErrorOnOffText.setText(ignoreSSLErrorSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onIgnoreSSLErrorCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onIgnoreSSLErrorCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceIgnoreSSLError(isChecked);
        prepareIgnoreSSLErrorOnOffText();
    }

    private void prepareGlobalHeadersField() {
        Log.d(DefaultsActivity.class.getName(), "prepareGlobalHeadersField");
        CardView globalHeadersCardView = findViewById(R.id.cardview_activity_defaults_global_headers);
        globalHeadersCardView.setOnClickListener(this::showHeadersDialog);
        prepareGlobalHeadersTextLayoutFields();
    }

    private void prepareGlobalHeadersTextLayoutFields() {
        Log.d(DefaultsActivity.class.getName(), "prepareGlobalHeadersTextLayoutFields");
        GridLayout gridLayout = findViewById(R.id.gridlayout_activity_defaults_global_headers_value);
        gridLayout.removeAllViews();
        List<Header> headers = new HeaderSyncHandler(this).getGlobalHeaders();
        if (headers.isEmpty()) {
            Log.d(DefaultsActivity.class.getName(), "No headers defined");
            gridLayout.setColumnCount(1);
            prepareGlobalHeadersTextFieldsSingleLayout(getResources().getString(R.string.text_activity_defaults_global_headers_none));
            return;
        }
        gridLayout.setColumnCount(2);
        prepareGlobalHeadersTextFieldsLayout(headers);
    }

    private void prepareGlobalHeadersTextFieldsSingleLayout(String text) {
        Log.d(DefaultsActivity.class.getName(), "prepareGlobalHeadersTextFieldsSingleLayout with text " + text);
        GridLayout gridLayout = findViewById(R.id.gridlayout_activity_defaults_global_headers_value);
        TextView globalHeaderText = getGlobalHeadersTextView(text, getGlobalHeadersTextSize(1), getColor(R.color.textColor), Typeface.NORMAL, Integer.MAX_VALUE);
        GridLayout.LayoutParams globalHeaderTextParams = getGlobalHeaderTextViewLayoutParams(0, 0);
        gridLayout.addView(globalHeaderText, globalHeaderTextParams);
    }

    private void prepareGlobalHeadersTextFieldsLayout(List<Header> headers) {
        Log.d(DefaultsActivity.class.getName(), "prepareGlobalHeadersTextFieldsLayout");
        GridLayout gridLayout = findViewById(R.id.gridlayout_activity_defaults_global_headers_value);
        int textSize = getGlobalHeadersTextSize(headers.size());
        int maxLines = getResources().getInteger(R.integer.activity_defaults_global_headers_max_value_lines);
        int maxVisibleHeaders = getResources().getInteger(R.integer.activity_defaults_global_headers_max_value_visible_headers);
        int visibleCount = globalHeadersExpanded ? headers.size() : Math.min(headers.size(), maxVisibleHeaders);
        Log.d(DefaultsActivity.class.getName(), "Header count is " + headers.size());
        Log.d(DefaultsActivity.class.getName(), "Visible count is " + visibleCount);
        for (int ii = 0; ii < visibleCount; ii++) {
            Header header = headers.get(ii);
            int color = header.isValueValid() ? getColor(R.color.textColor) : getColor(R.color.textErrorColor);
            TextView nameText = getGlobalHeadersTextView(header.getName() + ": ", textSize, color, Typeface.BOLD, Integer.MAX_VALUE);
            TextView valueText = getGlobalHeadersTextView(StringUtil.maskSecret(header.getValue(), header.isValueSecret()), textSize, color, Typeface.NORMAL, maxLines);
            GridLayout.LayoutParams nameTextParams = getGlobalHeaderTextViewLayoutParams(ii, 0);
            GridLayout.LayoutParams valueTextParams = getGlobalHeaderTextViewLayoutParams(ii, 1);
            gridLayout.addView(nameText, nameTextParams);
            gridLayout.addView(valueText, valueTextParams);
            enableHeaderTextToggleIfOverflow(valueText, maxLines);
        }
        if (headers.size() > maxVisibleHeaders) {
            String more = getResources().getString(R.string.text_activity_defaults_global_headers_more, headers.size() - maxVisibleHeaders);
            String less = getResources().getString(R.string.text_activity_defaults_global_headers_less);
            TextView toggleText = getGlobalHeadersTextView(globalHeadersExpanded ? less : more, getGlobalHeadersTextSize(headers.size()), getColor(R.color.textColor), Typeface.ITALIC, Integer.MAX_VALUE);
            GridLayout.LayoutParams toggleParams = getGlobalHeaderTextViewLayoutParams(visibleCount, 0);
            GridLayout.LayoutParams globalHeaderTextParams = getGlobalHeaderTextViewLayoutParams(0, 0);
            gridLayout.addView(toggleText, toggleParams);
            toggleText.setOnClickListener(view -> {
                globalHeadersExpanded = !globalHeadersExpanded;
                prepareGlobalHeadersTextLayoutFields();
            });
        }
    }

    private TextView getGlobalHeadersTextView(String text, int textSize, int color, int typeface, int maxLines) {
        Log.d(DefaultsActivity.class.getName(), "getGlobalHeadersTextView, text is " + text + ", textSize is " + textSize + ", maxLines is " + maxLines);
        TextView headerText = new TextView(this);
        headerText.setId(View.generateViewId());
        headerText.setText(text);
        headerText.setTextColor(color);
        headerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        headerText.setTypeface(null, typeface);
        if (maxLines < Integer.MAX_VALUE) {
            headerText.setMaxLines(maxLines);
            headerText.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            headerText.setMaxLines(Integer.MAX_VALUE);
            headerText.setEllipsize(null);
        }
        return headerText;
    }

    private GridLayout.LayoutParams getGlobalHeaderTextViewLayoutParams(int row, int column) {
        Log.d(DefaultsActivity.class.getName(), "getGlobalHeaderTextViewLayoutParams, row is " + row + ", column is " + column);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        float weight;
        if (column == 1) {
            weight = Float.parseFloat(getResources().getString(R.string.textview_activity_defaults_global_headers_value_weight));
        } else {
            weight = Float.parseFloat(getResources().getString(R.string.textview_activity_defaults_global_headers_name_weight));
        }
        params.width = 0;
        params.columnSpec = GridLayout.spec(column, weight);
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.setGravity(Gravity.FILL_HORIZONTAL);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.textview_activity_defaults_global_headers_value_margin_right);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.textview_activity_defaults_global_headers_value_margin_top);
        params.rowSpec = GridLayout.spec(row + 1);
        return params;
    }

    private int getGlobalHeadersTextSize(int headerCount) {
        if (headerCount <= 1) {
            return getResources().getInteger(R.integer.activity_defaults_global_headers_text_size_normal);
        } else if (headerCount == 2) {
            return getResources().getInteger(R.integer.activity_defaults_global_headers_text_size_smaller);
        } else {
            return getResources().getInteger(R.integer.activity_defaults_global_headers_text_size_small);
        }
    }

    @SuppressWarnings("SizeReplaceableByIsEmpty")
    private void enableHeaderTextToggleIfOverflow(TextView textView, int maxLines) {
        Log.d(DefaultsActivity.class.getName(), "enableHeaderTextToggleIfOverflow with maxLines of " + maxLines);
        textView.post(() -> {
            textView.setSingleLine(false);
            textView.setMaxLines(maxLines);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            Layout layout = textView.getLayout();
            boolean overflow = false;
            if (layout != null) {
                int visibleLines = layout.getLineCount();
                if (visibleLines > 0) {
                    int lastVisibleLineIndex = Math.min(visibleLines, maxLines) - 1;
                    if (lastVisibleLineIndex >= 0 && layout.getEllipsisCount(lastVisibleLineIndex) > 0) {
                        overflow = true;
                    }
                }
            }
            if (!overflow) {
                CharSequence text = textView.getText();
                TextPaint tp = textView.getPaint();
                int availWidth = textView.getWidth() - textView.getPaddingLeft() - textView.getPaddingRight();
                if (availWidth > 0 && text != null && text.length() > 0) {
                    StaticLayout staticLayout = StaticLayout.Builder.obtain(text, 0, text.length(), tp, availWidth).setAlignment(Layout.Alignment.ALIGN_NORMAL).setLineSpacing(textView.getLineSpacingExtra(), textView.getLineSpacingMultiplier()).setIncludePad(textView.getIncludeFontPadding()).build();
                    int realLines = staticLayout.getLineCount();
                    if (realLines > maxLines) {
                        overflow = true;
                    }
                }
            }
            Log.d(DefaultsActivity.class.getName(), "overflow is " + overflow);
            if (overflow) {
                textView.setOnClickListener(view -> toggleHeaderTextExpandCollapse(textView, maxLines));
            } else {
                textView.setOnClickListener(this::showHeadersDialog);
            }
        });
    }

    private void toggleHeaderTextExpandCollapse(TextView textView, int maxLines) {
        if (textView.getMaxLines() == Integer.MAX_VALUE) {
            textView.setMaxLines(maxLines);
            textView.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            textView.setMaxLines(Integer.MAX_VALUE);
            textView.setEllipsize(null);
        }
    }

    private void prepareOnlyWifiSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareOnlyWifiSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        onlyWifiSwitch = findViewById(R.id.switch_activity_defaults_only_wifi);
        onlyWifiOnOffText = findViewById(R.id.textview_activity_defaults_only_wifi_on_off);
        onlyWifiSwitch.setOnCheckedChangeListener(null);
        onlyWifiSwitch.setChecked(preferenceManager.getPreferenceOnlyWifi());
        onlyWifiSwitch.setOnCheckedChangeListener(this::onOnlyWifiCheckedChanged);
        prepareOnlyWifiOnOffText();
    }

    private void prepareOnlyWifiOnOffText() {
        onlyWifiOnOffText.setText(onlyWifiSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onOnlyWifiCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onOnlyWifiCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceOnlyWifi(isChecked);
        prepareOnlyWifiOnOffText();
    }

    private void prepareNotificationSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareNotificationSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        notificationSwitch = findViewById(R.id.switch_activity_defaults_notification);
        notificationOnOffText = findViewById(R.id.textview_activity_defaults_notification_on_off);
        notificationSwitch.setOnCheckedChangeListener(null);
        notificationSwitch.setChecked(preferenceManager.getPreferenceNotification());
        notificationSwitch.setOnCheckedChangeListener(this::onNotificationCheckedChanged);
        prepareNotificationOnOffText();
    }

    private void prepareNotificationOnOffText() {
        notificationOnOffText.setText(notificationSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onNotificationCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onNotificationCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceNotification(isChecked);
        prepareNotificationOnOffText();
    }

    private void prepareHighPrioSwitch() {
        Log.d(DefaultsActivity.class.getName(), "prepareHighPrioSwitch");
        PreferenceManager preferenceManager = new PreferenceManager(this);
        highPrioSwitch = findViewById(R.id.switch_activity_defaults_high_prio);
        highPrioOnOffText = findViewById(R.id.textview_activity_defaults_high_prio_on_off);
        highPrioSwitch.setOnCheckedChangeListener(null);
        highPrioSwitch.setChecked(preferenceManager.getPreferenceHighPrio());
        highPrioSwitch.setOnCheckedChangeListener(this::onHighPrioCheckedChanged);
        prepareHighPrioOnOffText();
    }

    private void prepareHighPrioOnOffText() {
        highPrioOnOffText.setText(highPrioSwitch.isChecked() ? getResources().getString(R.string.string_yes) : getResources().getString(R.string.string_no));
    }

    private void onHighPrioCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(DefaultsActivity.class.getName(), "onHighPrioCheckedChanged, new value is " + isChecked);
        PreferenceManager preferenceManager = new PreferenceManager(this);
        preferenceManager.setPreferenceHighPrio(isChecked);
        prepareHighPrioOnOffText();
    }

    private String getAddress() {
        return StringUtil.notNull(addressText.getText());
    }

    private void setAddress(String address) {
        addressText.setText(StringUtil.notNull(address));
    }

    private String getPort() {
        return StringUtil.notNull(portText.getText());
    }

    private void setPort(String port) {
        portText.setText(StringUtil.notNull(port));
    }

    private String getInterval() {
        return StringUtil.notNull(intervalText.getText());
    }

    private void setInterval(String interval) {
        intervalText.setText(StringUtil.notNull(interval));
        if (NumberUtil.isValidIntValue(interval)) {
            int value = NumberUtil.getIntValue(interval, getResources().getInteger(R.integer.task_interval_default));
            intervalMinutesText.setText(getResources().getQuantityString(R.plurals.string_minute, value));
        }
    }

    private String getPingCount() {
        return StringUtil.notNull(pingCountText.getText());
    }

    private void setPingCount(String pingCount) {
        pingCountText.setText(StringUtil.notNull(pingCount));
    }

    private String getPingPackageSize() {
        return StringUtil.notNull(pingPackageSizeText.getText());
    }

    private void setPingPackageSize(String pingPackageSize) {
        pingPackageSizeText.setText(StringUtil.notNull(pingPackageSize));
    }

    private String getConnectCount() {
        return StringUtil.notNull(connectCountText.getText());
    }

    private void setConnectCount(String connectCount) {
        connectCountText.setText(StringUtil.notNull(connectCount));
    }

    private String getConnectToHost() {
        return StringUtil.notNull(connectToHostText.getText());
    }

    private void setConnectToHost(String connectToHost) {
        connectToHostText.setText(StringUtil.notNull(connectToHost));
    }

    private String getConnectToPort() {
        return StringUtil.notNull(connectToPortText.getText());
    }

    private void setConnectToPort(String connectToPort) {
        connectToPortText.setText(StringUtil.notNull(connectToPort));
    }

    private void showAddressInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showAddressInputDialog");
        List<String> validators = Arrays.asList(HostFieldValidator.class.getName(), URLFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.ADDRESS, getAddress(), getResources().getString(R.string.label_activity_defaults_address), validators);
        showInputDialog(input.toBundle());
    }

    private void showPortInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showPortInputDialog");
        List<String> validators = Collections.singletonList(PortFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PORT, getPort(), getResources().getString(R.string.label_activity_defaults_port), validators);
        showInputDialog(input.toBundle());
    }

    private void showIntervalInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showIntervalInputDialog");
        List<String> validators = Collections.singletonList(IntervalFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.INTERVAL, getInterval(), getResources().getString(R.string.label_activity_defaults_interval), validators);
        showInputDialog(input.toBundle());
    }

    private void showPingCountInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showPingCountInputDialog");
        List<String> validators = Collections.singletonList(PingCountFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PINGCOUNT, getPingCount(), getResources().getString(R.string.label_activity_defaults_ping_count), validators);
        showInputDialog(input.toBundle());
    }

    private void showPingPackageSizeInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showPingPackageSizeInputDialog");
        List<String> validators = Collections.singletonList(PingPackageSizeFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.PINGPACKAGESIZE, getPingPackageSize(), getResources().getString(R.string.label_activity_defaults_ping_package_size), validators);
        showInputDialog(input.toBundle());
    }

    private void showConnectCountInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showConnectCountInputDialog");
        List<String> validators = Collections.singletonList(ConnectCountFieldValidator.class.getName());
        SettingsInput input = new SettingsInput(SettingsInput.Type.CONNECTCOUNT, getConnectCount(), getResources().getString(R.string.label_activity_defaults_connect_count), validators);
        showInputDialog(input.toBundle());
    }

    private void showConnectToHostInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showConnectToHostInputDialog");
        List<String> validators = Collections.singletonList(ResolveHostFieldValidator.class.getName());
        String connectToAddress = getConnectToHost().trim().equals(getResources().getString(R.string.string_not_set)) ? "" : getConnectToHost();
        SettingsInput input = new SettingsInput(SettingsInput.Type.RESOLVEADDRESS, connectToAddress, getResources().getString(R.string.label_activity_defaults_connect_to_host), validators);
        showInputDialog(input.toBundle());
    }

    private void showConnectToPortInputDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showConnectToPortInputDialog");
        List<String> validators = Collections.singletonList(ResolvePortFieldValidator.class.getName());
        String connectToPort = getConnectToPort().trim().equals(getResources().getString(R.string.string_not_set)) ? "" : getConnectToPort();
        SettingsInput input = new SettingsInput(SettingsInput.Type.RESOLVEPORT, connectToPort, getResources().getString(R.string.label_activity_defaults_connect_to_port), validators);
        showInputDialog(input.toBundle());
    }

    private void showInputDialog(Bundle bundle) {
        Log.d(DefaultsActivity.class.getName(), "showInputDialog, opening SettingsInputDialog");
        SettingsInputDialog inputDialog = new SettingsInputDialog();
        inputDialog.setArguments(bundle);
        inputDialog.show(getSupportFragmentManager(), DefaultsActivity.class.getName());
    }

    private void showHeadersDialog(View view) {
        Log.d(DefaultsActivity.class.getName(), "showHeadersDialog");
        HeadersDialog headersDialog = new HeadersDialog();
        Bundle bundle = BundleUtil.headerListToBundle(headersDialog.getInitialHeadersKey(), new HeaderSyncHandler(this).getGlobalHeaders());
        BundleUtil.longToBundle(headersDialog.getNetworkTaskIdKey(), -1, bundle);
        String title = getResources().getString(R.string.label_dialog_headers_global_headers);
        BundleUtil.stringToBundle(headersDialog.getHeadersTitleKey(), title, bundle);
        BundleUtil.booleanToBundle(headersDialog.getSupportsRestoreDefaultHeadersKey(), false, bundle);
        headersDialog.setArguments(bundle);
        headersDialog.show(getSupportFragmentManager(), HeadersDialog.class.getName());
    }

    @Override
    public void onInputDialogOkClicked(SettingsInputDialog inputDialog, SettingsInput type) {
        Log.d(DefaultsActivity.class.getName(), "onInputDialogOkClicked, type is " + type + ", value is " + inputDialog.getValue());
        PreferenceManager preferenceManager = new PreferenceManager(this);
        if (SettingsInput.Type.ADDRESS.equals(type.getType())) {
            String address = StringUtil.notNull(inputDialog.getValue()).trim();
            setAddress(address);
            preferenceManager.setPreferenceAddress(address);
        } else if (SettingsInput.Type.PORT.equals(type.getType())) {
            setPort(inputDialog.getValue());
            preferenceManager.setPreferencePort(NumberUtil.getIntValue(getPort(), getResources().getInteger(R.integer.task_port_default)));
        } else if (SettingsInput.Type.INTERVAL.equals(type.getType())) {
            setInterval(inputDialog.getValue());
            preferenceManager.setPreferenceInterval(NumberUtil.getIntValue(getInterval(), getResources().getInteger(R.integer.task_interval_default)));
        } else if (SettingsInput.Type.PINGCOUNT.equals(type.getType())) {
            setPingCount(inputDialog.getValue());
            preferenceManager.setPreferencePingCount(NumberUtil.getIntValue(getPingCount(), getResources().getInteger(R.integer.ping_count_default)));
        } else if (SettingsInput.Type.PINGPACKAGESIZE.equals(type.getType())) {
            setPingPackageSize(inputDialog.getValue());
            preferenceManager.setPreferencePingPackageSize(NumberUtil.getIntValue(getPingPackageSize(), getResources().getInteger(R.integer.ping_package_size_default)));
        } else if (SettingsInput.Type.CONNECTCOUNT.equals(type.getType())) {
            setConnectCount(inputDialog.getValue());
            preferenceManager.setPreferenceConnectCount(NumberUtil.getIntValue(getConnectCount(), getResources().getInteger(R.integer.connect_count_default)));
        } else if (SettingsInput.Type.RESOLVEADDRESS.equals(type.getType())) {
            String resolveAddress = StringUtil.notNull(inputDialog.getValue()).trim();
            if (StringUtil.isEmpty(UIUtil.getEmptyIfNotSet(this, resolveAddress))) {
                setConnectToHost(getResources().getString(R.string.string_not_set));
                preferenceManager.removePreferenceResolveAddress();
            } else {
                setConnectToHost(resolveAddress);
                preferenceManager.setPreferenceResolveAddress(resolveAddress);
            }
        } else if (SettingsInput.Type.RESOLVEPORT.equals(type.getType())) {
            String resolvePort = StringUtil.notNull(inputDialog.getValue()).trim();
            if (StringUtil.isEmpty(UIUtil.getEmptyIfNotSet(this, resolvePort))) {
                setConnectToPort(getResources().getString(R.string.string_not_set));
                preferenceManager.removePreferenceResolvePort();
            } else {
                setConnectToPort(resolvePort);
                preferenceManager.setPreferenceResolvePort(NumberUtil.getIntValue(resolvePort, getResources().getInteger(R.integer.resolve_port_default)));
            }
        } else {
            Log.e(DefaultsActivity.class.getName(), "type " + type.getType() + " unknown");
        }
        inputDialog.dismiss();
    }

    @Override
    public void onHeadersDialogOkClicked(HeadersDialog headersDialog) {
        Log.d(DefaultsActivity.class.getName(), "onHeadersDialogOkClicked");
        List<Header> newHeaders = headersDialog.getAdapter().getAllItems();
        long networkTaskId = headersDialog.getNetworkTaskId();
        HeaderSyncHandler handler = new HeaderSyncHandler(this);
        DBSyncResult syncResult = handler.synchronizeHeaders(networkTaskId, newHeaders);
        if (syncResult.dbChanged()) {
            handler.reset();
            prepareGlobalHeadersField();
        }
        headersDialog.dismiss();
        if (!syncResult.success()) {
            showMessageDialog(getResources().getString(R.string.text_dialog_general_message_synchronize_headers));
        }
    }

    @Override
    public void onHeadersDialogCancelClicked(HeadersDialog headersDialog) {
        Log.d(DefaultsActivity.class.getName(), "onHeadersDialogCancelClicked");
        headersDialog.dismiss();
    }

    private String getGlobalHeadersExpandedKey() {
        return DefaultsActivity.class.getSimpleName() + ".GlobalHeadersExpanded";
    }
}
