#!/usr/bin/env bash

cd $GITHUB_WORKSPACE 

sudo apt-get -y update   
sudo apt-get -y upgrade
sudo apt-get -y install libcurl3  
sudo apt-get -y install libcurl-openssl1.0-dev

# ./ci/bin/setup-mongodb.sh
# ./ci/bin/setup-postgresql.sh


# echo "Starting build"
# mkdir -p $HOME/.m2/
# cp .ci.settings.xml $HOME/.m2/settings.xml
# mvn -e -f $GITHUB_WORKSPACE/pom.xml  verify deploy
# echo "Stopping build" 
