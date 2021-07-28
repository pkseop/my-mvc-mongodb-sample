FROM openjdk:14-jdk-alpine3.10
RUN  apk update && apk upgrade && apk add netcat-openbsd
RUN mkdir -p /usr/local/app
WORKDIR /usr/local/app
ADD build/libs/my-mvc-sample-*.jar /usr/local/app/app.jar
ADD docker_build/genjks.sh genjks.sh
ADD docker_build/run.sh run.sh
RUN chmod +x run.sh
# configure ssl to jdk to connect AWS DocumentDB
RUN chmod +x genjks.sh
RUN	apk add curl
RUN	apk add openssl
RUN	apk add perl
# set docker container timezone to Asia\Seoul
# RUN apk --no-cache add tzdata && \
#         cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
#         echo "Asia/Seoul" > /etc/timezone

RUN ./genjks.sh

CMD ./run.sh