FROM anapsix/alpine-java
MAINTAINER Javi Moreno <javi.moreno@capside.com>

ADD target/pokemon-0.0.3-SNAPSHOT.jar .

#RUN && \
#    apk add --update curl && \
#    rm -rf /var/cache/apk/* && \
#    RUN curl -SL https://s3-us-west-2.amazonaws.com/ciberado/pokemon-0.0.3-SNAPSHOT.jar > pokemon-0.0.3-SNAPSHOT.jar

EXPOSE 8080

CMD ["java", "-jar", "pokemon-0.0.3-SNAPSHOT.jar"]
