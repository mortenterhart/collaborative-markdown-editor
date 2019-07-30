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

info "Setting up MySQL ${MYSQL_VERSION} Datasource for WildFly ${WILDFLY_VERSION}"

info "Adding WildFly administration user"
safe_run ${JBOSS_HOME}/bin/add-user.sh --user "${WILDFLY_USER}" --password "${WILDFLY_PASSWORD}" --silent

info "Starting WildFly server"
${JBOSS_HOME}/bin/standalone.sh &

info "Waiting for the server to boot"
until check_server_state 2> /dev/null | grep -q running; do
    sleep 1;
done
info "WildFly has started up"

info "Downloading MySQL driver"
MYSQL_CONNECTOR="mysql-connector-java-${MYSQL_VERSION}.jar"
MYSQL_CONNECTOR_LOCATION="/tmp/${MYSQL_CONNECTOR}"
MYSQL_CONNECTOR_URL="https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/${MYSQL_VERSION}/${MYSQL_CONNECTOR}"
safe_run curl --location \
     --output "${MYSQL_CONNECTOR_LOCATION}" \
     --url "${MYSQL_CONNECTOR_URL}"

info "Download completed, verifying SHA1 checksum for ${MYSQL_CONNECTOR}"
MYSQL_CONNECTOR_SHA1="${MYSQL_CONNECTOR}.sha1"
MYSQL_CONNECTOR_SHA1_LOCATION="/tmp/${MYSQL_CONNECTOR_SHA1}"
safe_run curl --location \
    --output "${MYSQL_CONNECTOR_SHA1_LOCATION}" \
    --url "https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/${MYSQL_VERSION}/${MYSQL_CONNECTOR_SHA1}"

if ! printf "%s %s\n" "$(cat "${MYSQL_CONNECTOR_SHA1_LOCATION}")" "${MYSQL_CONNECTOR_LOCATION}" | sha1sum --check; then
    error "Invalid SHA1 checksum for ${MYSQL_CONNECTOR} downloaded from ${MYSQL_CONNECTOR_URL}"
    exit 1
fi
info "Verification successful"

info "Adding MySQL module"
MODULE_NAME="com.mysql"
safe_run ${JBOSS_CLI} --connect \
             --command="module add --name=${MODULE_NAME} --resources=${MYSQL_CONNECTOR_LOCATION} --dependencies=javax.api,javax.transaction.api"

info "Installing MySQL driver"
safe_run ${JBOSS_CLI} --connect \
             --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=${MODULE_NAME},driver-class-name=com.mysql.cj.jdbc.Driver,driver-xa-datasource-class-name=com.mysql.cj.jdbc.MysqlXADataSource)"

info "Creating a new datasource"
safe_run ${JBOSS_CLI} --connect --command="data-source add
        --name=${DB_NAME}DS
        --jndi-name=java:jboss/datasources/cmdDS
        --user-name=${DB_USER}
        --password=${DB_PASSWORD}
        --driver-name=mysql
        --connection-url=jdbc:mysql://${DB_URI}/${DB_NAME}
        --max-pool-size=25
        --blocking-timeout-wait-millis=5000
        --enabled=true
        --jta=true
        --use-ccm=false
        --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLValidConnectionChecker
        --background-validation=true
        --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.mysql.MySQLExceptionSorter"

info "Shutting down WildFly and Cleaning up"
safe_run ${JBOSS_CLI} --connect --command=":shutdown"

rm -rf ${JBOSS_HOME}/standalone/configuration/standalone_xml_history/ ${JBOSS_HOME}/standalone/log/*
rm -f /tmp/*.jar /tmp/*.sha1
