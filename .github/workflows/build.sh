#!/usr/bin/env bash

cd $GITHUB_WORKSPACE 

apt update && sudo apt upgrade
apt install curl 

./ci/bin/setup-mongodb.sh
./ci/bin/setup-postgresql.sh

echo "Starting build"
mkdir -p $HOME/.m2/
cp .ci.settings.xml $HOME/.m2/settings.xml
mvn -e -f $GITHUB_WORKSPACE/pom.xml  verify deploy
echo "Stopping build" 
