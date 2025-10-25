/*
 * Copyright (c) 2025 Alwin Ibba
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

package net.ibbaa.keepitup.ui.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import net.ibbaa.keepitup.ui.dialog.GlobalHeadersDialog;

public class GlobalHeaderViewHolder extends RecyclerView.ViewHolder {

    private final GlobalHeadersDialog headersDialog;

    public GlobalHeaderViewHolder(@NonNull View itemView, GlobalHeadersDialog headersDialog) {
        super(itemView);
        this.headersDialog = headersDialog;
    }

    public void setHeaderText(String text) {

    }

    public void onHeaderOpenClicked(View view) {

    }

    public void onHeaderDeleteClicked(View view) {

    }

    public void showHeadersCardView() {

    }

    public void hideHeadersCardView() {

    }

    public void showNoHeadersTextView() {

    }

    public void hideNoHeadersTextView() {

    }
}
