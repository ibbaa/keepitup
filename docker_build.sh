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

UID=$(id -u)
GID=$(id -g)
docker build --no-cache --build-arg UID=$UID --build-arg GID=$GID --build-arg KEEPITUP_KEYSTORE_FILE=$KEEPITUP_KEYSTORE_FILE --build-arg KEEPITUP_KEY_ALIAS=$KEEPITUP_KEY_ALIAS --build-arg KEEPITUP_KEYSTORE_PASS=$KEEPITUP_KEYSTORE_PASS --build-arg KEEPITUP_KEY_PASS=$KEEPITUP_KEY_PASS -t ibbaa/keepitup:build .
docker run $DOCKER_OPTS --rm -v $PWD:/var/keepitup -w /var/keepitup ibbaa/keepitup:build /bin/bash -c "./gradlew assemble && ./signing.sh"
RET=$?
docker image prune -f
exit $RET