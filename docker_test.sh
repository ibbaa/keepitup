#!/bin/sh
#
# Copyright (c) 2021. Alwin Ibba
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
docker build --no-cache --file Dockerfile.test --build-arg UID=$UID --build-arg GID=$GID -t ibbaa/keepitup:test .
docker run $DOCKER_OPTS --privileged --name test --rm -v $PWD:/var/keepitup -w /var/keepitup ibbaa/keepitup:test /bin/bash -c "sudo ./start_emulator.sh && ./gradlew connectedAndroidTest"
RET=$?
docker image prune -f
exit $RET