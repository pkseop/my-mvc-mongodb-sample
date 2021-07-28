#!/bin/sh

echo "********************************************************"
echo "Starting Archisket-API Service                           "
echo "Using profile: $PROFILE"
echo "********************************************************"
java -Djava.security.egd=file:/dev/./urandom \
	 $JVM_OPTIONS \
	 -Djava.net.preferIPv4Stack=true \
     -Dspring.profiles.active=$PROFILE \
     -jar /usr/local/app/app.jar