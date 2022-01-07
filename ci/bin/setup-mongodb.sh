#!/usr/bin/env bash
MONGO_VERSION=5.0.3
#MONGO_VERSION=4.4.3
mkdir -p downloads
mkdir -p var/db var/log


MONGO_DL_NAME=mongodb-linux-x86_64-ubuntu1804-${MONGO_VERSION}
MONGO_HOME=$GITHUB_WORKSPACE/ci/bin/downloads/$MONGO_DL_NAME

if [[ ! -d $MONGO_HOME ]] ; then
    cd downloads \
        && wget https://fastdl.mongodb.org/linux/${MONGO_DL_NAME}.tgz \
        && tar xzf ${MONGO_DL_NAME}.tgz  \
        && cd ..;
fi

${MONGO_HOME}/bin/mongod --version
${MONGO_HOME}/bin/mongod --dbpath var/db --replSet rs0 --fork --logpath var/log/mongod.log
sleep 10
${MONGO_HOME}/bin/mongo --eval "rs.initiate({_id: 'rs0', members:[{_id: 0, host: '127.0.0.1:27017'}]});"
sleep 15

