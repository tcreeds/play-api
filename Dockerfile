FROM zenika/alpine-kotlin

WORKDIR ~/projects/play-api

COPY target/play-api-0.0.1-SNAPSHOT.jar .

CMD ["java","-jar","play-api-0.0.1-SNAPSHOT.jar"]