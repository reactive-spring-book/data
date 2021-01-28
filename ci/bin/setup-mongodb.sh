#!/usr/bin/env bash
MONGO_VERSION=4.4.3
mkdir -p downloads
mkdir -p var/db var/log

# https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-ubuntu1804-4.4.3.tgz

if [[ ! -d downloads/mongodb-linux-x86_64-ubuntu1604-${MONGO_VERSION} ]] ; then
    cd downloads \
        && wget https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-ubuntu1804-${MONGO_VERSION}.tgz \
        && tar xzf mongodb-linux-x86_64-ubuntu1604-${MONGO_VERSION}.tgz \
        && cd ..;
fi

# ubuntu-18.04 
# is what github actions uses 
 
MONGO_HOME=$(pwd)/downloads/mongodb-linux-x86_64-ubuntu1804-${MONGO_VERSION}
${MONGO_HOME}/bin/mongod --version
${MONGO_HOME}/bin/mongod --dbpath var/db --replSet rs0 --fork --logpath var/log/mongod.log
sleep 10

${MONGO_HOME}/bin/mongo --eval "rs.initiate({_id: 'rs0', members:[{_id: 0, host: '127.0.0.1:27017'}]});"
sleep 15

