# Same multi-module reactor caveat as app/Dockerfile — build from the repo
# root:
#
#   docker build -f gateway-service/Dockerfile -t hostflow-gateway .
#
FROM eclipse-temurin:21-jdk AS build
WORKDIR /build
COPY . .
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -pl gateway-service -am package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
RUN useradd --system --create-home --home-dir /app appuser
WORKDIR /app
COPY --from=build /build/gateway-service/target/gateway-service-*.jar app.jar
USER appuser

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

EXPOSE 8085
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
