FROM eclipse-temurin:21

RUN mkdir /opt/app

COPY projects/indicators-backend/target/indicators-backend.jar /opt/app

EXPOSE 8080

CMD java -jar /opt/app/indicators-backend.jar 8080
