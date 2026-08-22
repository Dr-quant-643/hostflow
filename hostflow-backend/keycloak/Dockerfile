# Railway has no host-disk bind mounts (unlike docker-compose.yml's
# ./modules/core-security/.../keycloak:/opt/keycloak/data/import:ro), so the
# realm-import file has to be baked into the image instead. Functionally
# identical to the bind-mount version: Keycloak still reads it via
# --import-realm at "start" time.
#
# Build from the repo root:
#   docker build -f keycloak/Dockerfile -t hostflow-keycloak .
FROM quay.io/keycloak/keycloak:26.0
COPY modules/core-security/src/main/resources/keycloak/hostflow-realm-export.json /opt/keycloak/data/import/hostflow-realm-export.json
COPY keycloak/themes/hostflow /opt/keycloak/themes/hostflow

# Baked in rather than relying on Railway's startCommand field: that field
# gets passed through as a single string (effectively `sh -c "start ..."`),
# which lands as kc.sh's first *positional* arg being "sh" -- kc.sh doesn't
# recognize that as a command and silently falls back to printing its help
# text and exiting 0, which Railway then reads as a successful deploy of a
# server that was never actually started. Baking the real argv into
# ENTRYPOINT avoids that string-splitting step entirely.
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start", "--http-port=8081", "--import-realm"]
