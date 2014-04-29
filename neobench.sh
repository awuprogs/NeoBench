#!/bin/bash

if [ "x$DIR" == "x" ]; then
	DIR=.
fi

java -cp $DIR/dist/NeoBench.jar:`for i in $DIR/lib/*; do echo $i; done | tr '\n' ':'` Main

