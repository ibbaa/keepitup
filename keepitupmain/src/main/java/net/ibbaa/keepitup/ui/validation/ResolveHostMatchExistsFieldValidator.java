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

package net.ibbaa.keepitup.ui.validation;

import android.content.Context;
import android.content.res.Resources;

import net.ibbaa.keepitup.R;
import net.ibbaa.keepitup.logging.Log;
import net.ibbaa.keepitup.model.Resolve;
import net.ibbaa.keepitup.util.NumberUtil;
import net.ibbaa.keepitup.util.StringUtil;
import net.ibbaa.keepitup.util.URLUtil;

import java.net.URL;
import java.util.List;

public class ResolveHostMatchExistsFieldValidator implements FieldValidator {

    private final String field;
    private final Context context;
    private final List<Resolve> resolves;
    private final URL url;

    public ResolveHostMatchExistsFieldValidator(String field, List<Resolve> resolves, URL url, Context context) {
        this.field = field;
        this.resolves = resolves;
        this.context = context;
        this.url = url;
    }

    @Override
    public ValidationResult validate(String value) {
        Log.d(ResolveHostMatchExistsFieldValidator.class.getName(), "validate, value is " + value);
        String successMessage = getResources().getString(R.string.validation_successful);
        String failedMessage = getResources().getString(R.string.invalid_exists);
        String[] hostPort = StringUtil.splitAtLastColon(value);
        String host = hostPort[0].trim();
        int port = NumberUtil.getIntValue(hostPort[1].trim(), -1);
        if (url != null) {
            host = StringUtil.isEmpty(host) ? url.getHost() : host;
            port = port < 0 ? URLUtil.getPort(url) : port;
        }
        for (Resolve resolve : resolves) {
            String currentHost;
            int currentPort;
            if (url != null) {
                currentHost = URLUtil.getSourceAddress(resolve, url);
                currentPort = URLUtil.getSourcePort(resolve, url);
            } else {
                currentHost = StringUtil.trim(resolve.getSourceAddress());
                currentPort = resolve.getSourcePort();
            }
            if (URLUtil.isSameHostAndPort(host, port, currentHost, currentPort)) {
                Log.d(ResolveHostMatchExistsFieldValidator.class.getName(), "validate, value exists");
                return new ValidationResult(false, field, failedMessage);
            }
        }
        return new ValidationResult(true, field, successMessage);
    }

    private Context getContext() {
        return context;
    }

    private Resources getResources() {
        return getContext().getResources();
    }
}
