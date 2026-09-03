FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/money-manager-0.0.1-SNAPSHOT.jar moneymanagerapp.jar
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "moneymanagerapp.jar"]