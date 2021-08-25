FROM androidsdk/android-30

ARG UID
ARG GID
ARG KEEPITUP_KEY_FILE
ARG KEEPITUP_SIGNING_FILE
ENV KEEPITUP_KEY_FILE=$KEEPITUP_KEY_FILE
ENV KEEPITUP_SIGNING_FILE=$KEEPITUP_SIGNING_FILE

RUN apt -y install sudo
RUN groupadd -r -g $GID buildusr || true
RUN useradd -m -r -u $UID -g buildusr buildusr || true
RUN echo "$(id -nu $UID) ALL=(ALL) NOPASSWD: ALL" > /etc/sudoers
RUN sdkmanager --install "system-images;android-29;google_apis;x86_64"
RUN echo "no" | avdmanager --verbose create avd --force --name "nexus_5x_api29" --device "Nexus 5X" --package "system-images;android-29;google_apis;x86_64"
USER $UID:$GID