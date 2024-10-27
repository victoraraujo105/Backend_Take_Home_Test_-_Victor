FROM openjdk:11

WORKDIR /app

COPY build.gradle gradlew settings.gradle ./
COPY gradle/ gradle/

COPY src/ src

RUN ./gradlew build -x test --no-daemon

ENTRYPOINT ["java","-jar","build/libs/sleep-0.0.1-SNAPSHOT.jar"]
