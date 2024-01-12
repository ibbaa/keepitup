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

FROM debian:bookworm

ARG UID
ARG GID
ARG KEEPITUP_KEYSTORE_FILE
ARG KEEPITUP_KEY_ALIAS
ARG KEEPITUP_KEYSTORE_PASS
ARG KEEPITUP_KEY_PASS
ENV KEEPITUP_KEYSTORE_FILE=$KEEPITUP_KEYSTORE_FILE
ENV KEEPITUP_KEY_ALIAS=$KEEPITUP_KEY_ALIAS
ENV KEEPITUP_KEYSTORE_PASS=$KEEPITUP_KEYSTORE_PASS
ENV KEEPITUP_KEY_PASS=$KEEPITUP_KEY_PASS


ARG JDK_VERSION=17
# https://developer.android.com/studio#command-line-tools-only
ARG ANDROID_SDK_VERSION=11076708
ARG BUILD_TOOLS_VERSION=34.0.0

ENV JAVA_HOME /usr/lib/jvm/java-${JDK_VERSION}-openjdk-amd64
ENV ANDROID_HOME /opt/android-sdk

RUN dpkg --add-architecture i386
RUN apt -y update
RUN apt -y full-upgrade
RUN apt -y install libncurses5:i386 libc6:i386 libstdc++6:i386 lib32gcc-s1 lib32ncurses6 lib32z1 zlib1g:i386
RUN apt -y install wget unzip openjdk-${JDK_VERSION}-jdk
RUN cd /opt
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-${ANDROID_SDK_VERSION}_latest.zip
RUN unzip commandlinetools-linux*.zip -d ${ANDROID_HOME}/cmdline-tools
RUN mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest
RUN rm commandlinetools-linux*.zip

ENV PATH ${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin

RUN groupadd -r -g $GID android
RUN useradd -m -r -u $UID -g android android
RUN chown -R android ${ANDROID_HOME}

USER android

RUN echo yes | sdkmanager --install "build-tools;${BUILD_TOOLS_VERSION}"

ENV BUILD_TOOLS_PATH=${ANDROID_HOME}/build-tools/${BUILD_TOOLS_VERSION}