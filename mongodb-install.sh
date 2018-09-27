#!/usr/bin/env bash
#- echo "replSet = myReplSetName" | sudo tee -a /etc/mongodb.conf
#- ls -la /etc/init.d/
#- /etc/init.d/mongo  stop
#- sleep 20
#- mongo --eval 'rs.initiate()'
#- sleep 15


DATA=$HOME/data/db
FOLDER=mongodb-linux-x86_64-${MONGODB_VERSION}
TGZ_FILE=${FOLDER}.tgz

wget http://fastdl.mongodb.org/linux/${TGZ_FILE}
tar xfz ${TGZ_FILE}
export PATH=`pwd`/${FOLDER}/bin:$PATH

mkdir -p ${DATA}
mongod --replSet my-replica-set --dbpath $DATA &
mongo --eval "rs.initiate()"
