FROM eclipse-temurin:11
RUN mkdir /opt/app
COPY target/sos-clientes-ms-0.0.1-SNAPSHOT.jar /opt/app
CMD ["java", "-Dspring.profiles.active=mysql", "-jar", "JAVA_OPTS /opt/app/sos-clientes-ms-0.0.1-SNAPSHOT.jar"]