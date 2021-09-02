FROM androidsdk/android-30

ARG UID
ARG GID

ENV BUILD_TOOLS_PATH=$ANDROID_SDK_ROOT/build-tools/30.0.2

RUN usermod -u $UID android
RUN groupmod -g $GID android
USER android