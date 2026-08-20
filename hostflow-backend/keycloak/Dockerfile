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
