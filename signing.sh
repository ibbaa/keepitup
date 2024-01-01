#!/bin/sh
#
# Copyright (c) 2024. Alwin Ibba
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

export KEEPITUP_DEBUG_APK_PATH=./keepitupmain/build/outputs/apk/debug
export KEEPITUP_RELEASE_APK_PATH=./keepitupmain/build/outputs/apk/release

if [ -z "$KEEPITUP_KEYSTORE_FILE" ]; then
	export KEEPITUP_KEYSTORE_FILE=./signing/keepitup.jks
fi

if [ -z "$KEEPITUP_KEY_ALIAS" ]; then
	export KEEPITUP_KEY_ALIAS=keepitupkey
fi

if [ -z "$KEEPITUP_KEYSTORE_PASS" ]; then
	export KEEPITUP_KEYSTORE_PASS=keepitup
fi

if [ -z "$KEEPITUP_KEY_PASS" ]; then
	export KEEPITUP_KEY_PASS=keepitup
fi

mv $KEEPITUP_DEBUG_APK_PATH/keepitup-debug.apk $KEEPITUP_DEBUG_APK_PATH/keepitup-debug-unsigned.apk
$BUILD_TOOLS_PATH/apksigner sign --ks $KEEPITUP_KEYSTORE_FILE --ks-key-alias $KEEPITUP_KEY_ALIAS --ks-pass env:KEEPITUP_KEYSTORE_PASS --key-pass env:KEEPITUP_KEY_PASS --out $KEEPITUP_DEBUG_APK_PATH/keepitup-debug.apk $KEEPITUP_DEBUG_APK_PATH/keepitup-debug-unsigned.apk
$BUILD_TOOLS_PATH/apksigner sign --ks $KEEPITUP_KEYSTORE_FILE --ks-key-alias $KEEPITUP_KEY_ALIAS --ks-pass env:KEEPITUP_KEYSTORE_PASS --key-pass env:KEEPITUP_KEY_PASS --out $KEEPITUP_RELEASE_APK_PATH/keepitup-release.apk $KEEPITUP_RELEASE_APK_PATH/keepitup-release-unsigned.apk
rm $KEEPITUP_DEBUG_APK_PATH/keepitup-debug-unsigned.apk $KEEPITUP_RELEASE_APK_PATH/keepitup-release-unsigned.apk