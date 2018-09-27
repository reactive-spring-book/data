#!/usr/bin/env bash

URL=""
if [ "$TRAVIS_OS_NAME" = "linux" ]; then
    URL=http://fastdl.mongodb.org/linux/mongodb-linux-x86_64-4.0.2.tgz
else
    URL=http://fastdl.mongodb.org/osx/mongodb-osx-ssl-x86_64-4.0.2.tgz
fi

echo URL is $URL

TGZ_FILE=archive.tgz
ls -la ${TGZ_FILE} || curl ${URL} > $TGZ_FILE
OUT=`pwd`/mongodb
echo $OUT

#mkdir -p ${OUT}
tar xfz ${TGZ_FILE} -C .
ls -la

mv mongodb* mongodb
DATA=`pwd`/data
mkdir -p ${DATA}
BIN=`pwd`/mongodb/bin
export PATH=$BIN:$PATH

mkdir -p ${DATA}
${BIN}/mongod --replSet my-replica-set --dbpath ${DATA} &
sleep 5
${BIN}/mongo --eval "rs.initiate()"

