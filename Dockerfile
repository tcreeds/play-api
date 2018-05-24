FROM zenika/alpine-kotlin

WORKDIR ~/projects/tak-api

COPY target/tak-api-0.0.1-SNAPSHOT.jar .

CMD ["java","-jar","tak-api-0.0.1-SNAPSHOT.jar"]