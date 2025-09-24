#!/bin/bash
echo "ex) jar.sh {java}.java"
javac $1

javac=$(echo "$1" | cut -d"." -f 1)
jar cf $javac.jar $javac.class


echo "완료"
