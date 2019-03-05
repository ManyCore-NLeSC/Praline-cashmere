#!/bin/bash

createClassPath() {
    basedir=$1
    classpath=""

    if [ -d $basedir/lib ]
    then
	for i in $basedir/lib/*.jar
	do
	    classpath+=":$i"
	done
    fi
    echo $classpath
}
