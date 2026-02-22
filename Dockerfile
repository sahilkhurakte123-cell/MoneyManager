FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/money-manager-0.0.1-SNAPSHOT.jar moneymanagerapp.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "moneymanagerapp.jar"]