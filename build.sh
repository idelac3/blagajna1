#!/bin/bash

#
# Function to increment REV number in VERSION string.
#
function increment_version()
{
 # Should be in format: XX.YY.ZZ
 # where XX - major version, YY - minor, ZZ - revision
 VER=$1
 MAJOR=`echo $VER | cut -f1 -d.`
 MINOR=`echo $VER | cut -f2 -d.`
 REV=`echo $VER | cut -f3 -d.`
 ((REV++))
 return $REV
}

#
# Function to compile *.java source files into *.class files.
#
function compile_java()
{
 SOURCE_DIR="src/"
 OUTPUT_DIR="bin/"
 # Find Pegasus.java path, eg. src/hr/ericsson/Pegasus/Pegasus.java
 #ENTRY=`find $SOURCE_DIR -name Main.java`
 ENTRY=$SOURCE_DIR/Main.java

 CLASSPATH=""
 ARGS="-target 1.7 -source 1.7"

 /usr/jdk1.8.0_91/bin/javac $ARGS -d $OUTPUT_DIR -sourcepath $SOURCE_DIR $ENTRY

}

# Function to pack *.class files into pegaz.jar.
function create_jar()
{
 OUTPUT_JAR="blagajna.jar"
 MANIFEST="src/blagajna.mf"
 BIN_DIR="bin/"
 SPLASH="src/splash1.gif"

 cp $SPLASH $BIN_DIR

 cd $BIN_DIR
 /usr/jdk1.8.0_91/bin/jar cmf ../$MANIFEST  ../$OUTPUT_JAR *
 cd ..
}

# Base dir: should be where Pegaz project exist.
# Start this script from Pegaz project folder !!!
SOURCE_DIR="src/"
OUTPUT_DIR="bin/"
MANIFEST="src/blagajna.mf"

# This script is placed in src/ folder in this example
cd ..

if [ -f $MANIFEST ] ; then
 if [ -d $SOURCE_DIR ] ; then
  if [ -d $OUTPUT_DIR ] ; then
   # echo "Incrementing version revision number ..."

   echo "Compiling source ..."
   compile_java

   echo "Building jar ..."
   create_jar

   echo "Done."

  else
   echo "Folder $OUTPUT_DIR does not exist."
   exit 1
  fi
 else
  echo "Folder $SOURCE_DIR does not exist."
  exit 1
 fi
else
 echo "File $MANIFEST does not exist."
 exit 1
fi

exit 0
