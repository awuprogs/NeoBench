#!/bin/bash

if [ "x$DIR" == "x" ]; then
	DIR=.
fi

CLASSPATH=$DIR/dist/NeoBench.jar:`for i in $DIR/lib/*; do echo $i; done | tr '\n' ':'`

java -Xms4g -Xmx4g -cp $CLASSPATH Main $*

