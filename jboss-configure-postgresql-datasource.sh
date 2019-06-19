#!/usr/bin/env bash

RED=$'\e[31m'
GREEN=$'\e[32m'
RESET=$'\e[0m'

function info() {
    echo -e "${GREEN}==> $*${RESET}"
}

function error() {
    echo -e "${RED}==> ERROR: $*${RESET}"
}

function safe_run() {
    "$@"
    local exit_code=$?
    if [ "${exit_code}" -ne 0 ]; then
        error "The command '$*' exited with non-zero exit code ${exit_code}."
        exit "${exit_code}"
    fi
}

function check_server_state() {
    ${JBOSS_CLI} -c ":read-attribute(name=server-state)"
}

info "Setting up PostgreSQL Datasource for WilFly ${WILDFLY_VERSION}\n"

info "Adding WildFly administration user"
safe_run ${JBOSS_HOME}/bin/add-user.sh --user "${WILDFLY_USER}" --password "${WILDFLY_PASSWORD}" --silent

info "Starting WildFly server"
${JBOSS_HOME}/bin/standalone.sh &

info "Waiting for the server to boot"
until check_server_state 2> /dev/null | grep -q running; do
    sleep 1;
done
info "WildFly has started up"

info "Downloading PostgreSQL driver"
POSTGRESQL_CONNECTOR="/tmp/postgresql-${POSTGRESQL_VERSION}.jar"
MAVEN_DOWNLOAD_URL="http://central.maven.org/maven2/org/postgresql/postgresql/${POSTGRESQL_VERSION}/postgresql-${POSTGRESQL_VERSION}.jar"
safe_run curl --location \
     --output "${POSTGRESQL_CONNECTOR}" \
     --url "${MAVEN_DOWNLOAD_URL}"

info "PostgreSQL Driver downloaded, verifying SHA1 checksum"
if ! sha1sum "${POSTGRESQL_CONNECTOR}" | grep "${POSTGRESQL_SHA1}" > /dev/null; then
    error "Invalid SHA1 for ${POSTGRESQL_CONNECTOR} downloaded from ${MAVEN_DOWNLOAD_URL}"
    exit 1
fi
info "Verified successfully"

info "Adding PostgreSQL module"
MODULE_NAME="org.postgresql"
safe_run ${JBOSS_CLI} --connect \
             --command="module add --name=${MODULE_NAME} --resources=${POSTGRESQL_CONNECTOR} --dependencies=javax.api,javax.transaction.api"

info "Installing PostgreSQL driver"
safe_run ${JBOSS_CLI} --connect \
             --command="/subsystem=datasources/jdbc-driver=postgresql:add(driver-name=postgresql,driver-module-name=${MODULE_NAME},driver-xa-datasource-class-name=org.postgresql.xa.PGXADataSource)"

info "Creating a new datasource"
safe_run ${JBOSS_CLI} --connect --command="data-source add
        --name=${DB_NAME}DS
        --jndi-name=java:/cmd
        --driver-name=postgresql
        --connection-url=${DATABASE_URL}
        --max-pool-size=25
        --blocking-timeout-wait-millis=5000
        --enabled=true
        --jta=true
        --use-ccm=false
        --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker
        --background-validation=true
        --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLExceptionSorter"

info "Shutting down WildFly and Cleaning up"
safe_run ${JBOSS_CLI} --connect --command=":shutdown"

rm -rf ${JBOSS_HOME}/standalone/configuration/standalone_xml_history/ ${JBOSS_HOME}/standalone/log/*
rm -f /tmp/*.jar

info "PostgreSQL Datasource configuration done"
