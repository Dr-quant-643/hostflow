# app is one leaf of a multi-module Maven reactor (~20 sibling modules under
# modules/) — the whole hostflow-backend/ tree is required to build it, not
# just this directory. Build from the repo root:
#
#   docker build -f app/Dockerfile -t hostflow-app .
#
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY . .
# clean, not just package: a stale modules/*/target/ from a local build
# on the host was previously slipping past .dockerignore's non-recursive
# `target/` pattern (fixed to `**/target/`) and getting repackaged as-is
# by Maven, which doesn't recompile a module whose source file was
# deleted -- the leftover .class file just rides along.
RUN --mount=type=cache,target=/root/.m2 \
    mvn -q -B -pl app -am clean package -DskipTests

FROM eclipse-temurin:21-jre AS runtime
RUN useradd --system --create-home --home-dir /app appuser
WORKDIR /app
COPY --from=build /build/app/target/app-*.jar app.jar
USER appuser

# Unset, not a fixed -Xmx: let the container's own memory limit (docker run
# -m / Kubernetes resources.limits.memory) drive JVM sizing via
# MaxRAMPercentage rather than baking in a value that fights whatever the
# deployer actually allocates. Override JAVA_OPTS at runtime for anything
# else (e.g. -XX:+UseSerialGC on a small instance).
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75"

EXPOSE 9090
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
