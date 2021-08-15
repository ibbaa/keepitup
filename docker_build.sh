#!/bin/sh
UID=$(id -u)
GID=$(id -g)
docker build --no-cache --build-arg UID=$UID --build-arg GID=$GID --build-arg KEEPITUP_KEY_FILE=$KEEPITUP_KEY_FILE --build-arg KEEPITUP_SIGNING_FILE=$KEEPITUP_SIGNING_FILE -t ibbaa/keepitup:build .
docker run $DOCKER_OPTS --rm -v $PWD:/var/keepitup -w /var/keepitup ibbaa/keepitup:build /bin/bash -c "./gradlew assemble"
RET=$?
docker image prune -f
exit $RET