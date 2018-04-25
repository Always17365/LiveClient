#!/bin/sh

# change dir
CUR_PATH=$(dirname $0)
cd $CUR_PATH

ECLIPSE_PROJECT_PATH=../coollive/
STUDIO_PROJECT_PATH=../coollive_studio

# copy jni version files
cp -rf $STUDIO_PROJECT_PATH/app/src/main/jni/LSVersion.h $ECLIPSE_PROJECT_PATH/jni/LSVersion.h
cp -rf $STUDIO_PROJECT_PATH/app/src/main/jni/player/* $ECLIPSE_PROJECT_PATH/jni/player
cp -rf $STUDIO_PROJECT_PATH/app/src/main/jni/publisher/* $ECLIPSE_PROJECT_PATH/jni/publisher
# copy java files
rm -rf $ECLIPSE_PROJECT_PATH/src/*
cp -rf $STUDIO_PROJECT_PATH/app/src/main/java/* $ECLIPSE_PROJECT_PATH/src
cp -rf $STUDIO_PROJECT_PATH/app/src/main/res/layout/* $ECLIPSE_PROJECT_PATH/res/layout

./export_jar.sh

echo "# Sync finish!"