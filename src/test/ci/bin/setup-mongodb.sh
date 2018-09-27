#!/usr/bin/env bash
MONGO_VERSION=4.0.0
mkdir -p downloads
mkdir -p var/db var/log
if [[ ! -d downloads/mongodb-linux-x86_64-ubuntu1604-${MONGO_VERSION} ]] ; then cd downloads && wget https://fastdl.mongodb.org/linux/mongodb-linux-x86_64-ubuntu1604-${MONGO_VERSION}.tgz && tar xzf mongodb-linux-x86_64-ubuntu1604-${MONGO_VERSION}.tgz && cd ..; fi
downloads/mongodb-linux-x86_64-ubuntu1604-${MONGO_VERSION}/bin/mongod --version
downloads/mongodb-linux-x86_64-ubuntu1604-${MONGO_VERSION}/bin/mongod --dbpath var/db --replSet rs0 --fork --logpath var/log/mongod.log
sleep 10

downloads/mongodb-linux-x86_64-ubuntu1604-${MONGO_VERSION}/bin/mongo --eval "rs.initiate({_id: 'rs0', members:[{_id: 0, host: '127.0.0.1:27017'}]});"
sleep 15

#
#URL=""
#if [ "$TRAVIS_OS_NAME" = "linux" ]; then
#    URL=http://fastdl.mongodb.org/linux/mongodb-linux-x86_64-4.0.2.tgz
#else
#    URL=http://fastdl.mongodb.org/osx/mongodb-osx-ssl-x86_64-4.0.2.tgz
#fi
#
#echo URL is $URL
#
#TGZ_FILE=archive.tgz
#ls -la ${TGZ_FILE} || curl ${URL} > $TGZ_FILE
#OUT=`pwd`/mongodb
#echo $OUT
#
##mkdir -p ${OUT}
#tar xfz ${TGZ_FILE} -C .
#ls -la
#
#mv mongodb* mongodb
#DATA=`pwd`/data
#mkdir -p ${DATA}
#BIN=`pwd`/mongodb/bin
#export PATH=$BIN:$PATH
#
#mkdir -p ${DATA}
#${BIN}/mongod --replSet my-replica-set --dbpath ${DATA} &
#sleep 5
#${BIN}/mongo --eval "rs.initiate()"
#sleep 5
