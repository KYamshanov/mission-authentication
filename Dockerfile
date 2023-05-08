FROM openjdk:11
WORKDIR /app/
COPY /build/libs/authentication-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 80
CMD ["java","-jar","/app/app.jar"]