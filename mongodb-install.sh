#!/usr/bin/env bash


MONGODB_VERSION=4.0.0
DATA=$HOME/data/db
FOLDER=mongodb-linux-x86_64-${MONGODB_VERSION}
TGZ_FILE=${FOLDER}.tgz

wget http://fastdl.mongodb.org/linux/${TGZ_FILE}
tar xfz ${TGZ_FILE}
export PATH=`pwd`/${FOLDER}/bin:$PATH

mkdir -p ${DATA}
mongod --replSet my-replica-set --dbpath $DATA &
sleep 10
mongo --eval "rs.initiate()"
