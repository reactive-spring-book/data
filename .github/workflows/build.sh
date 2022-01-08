#!/usr/bin/env bash
set -e
cd $GITHUB_WORKSPACE 
echo "Starting build"
mkdir -p $HOME/.m2/
cp $GITHUB_WORKSPACE/.ci.settings.xml $HOME/.m2/settings.xml
mvn -e -f $GITHUB_WORKSPACE/pom.xml spring-javaformat:apply clean package
echo "Stopping build"

