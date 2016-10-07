#!/bin/sh

export TRICE_HOME=`dirname $0`/..
CLASSPATH=$TRICE_HOME/conf
CLASSPATH=$CLASSPATH:`find $TRICE_HOME/lib/ -name trice*.jar -exec echo -n ':'{} \;`

java -Djava.security.egd=file:/dev/./urandom -classpath $CLASSPATH $@ org.springframework.boot.loader.JarLauncher
