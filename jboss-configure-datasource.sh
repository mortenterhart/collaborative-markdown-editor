#!/usr/bin/env bash

RED=$'\e[31m'
GREEN=$'\e[32m'
RESET=$'\e[0m'

function info() {
    printf "%s==> %s%s\n" "${GREEN}" "$*" "${RESET}"
}

function error() {
    printf "%s==> ERROR: %s%s\n" "${RED}" "$*" "${RESET}"
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

info "Adding WildFly administration user"
safe_run ${JBOSS_HOME}/bin/add-user.sh --user "${WILDFLY_USER}" --password "${WILDFLY_PASSWORD}" --silent

info "Starting WildFly server"
${JBOSS_HOME}/bin/standalone.sh &

info "Waiting for the server to boot"
until check_server_state 2> /dev/null | grep -q running; do
    check_server_state 2> /dev/null;
    sleep 1;
done
info "WildFly has started up"

info "Downloading MySQL driver"
MYSQL_CONNECTOR="/tmp/mysql-connector-java-${MYSQL_VERSION}.jar"
safe_run curl --location \
     --output "${MYSQL_CONNECTOR}" \
     --url "https://repo.maven.apache.org/maven2/mysql/mysql-connector-java/${MYSQL_VERSION}/mysql-connector-java-${MYSQL_VERSION}.jar"

info "Adding MySQL module"
MODULE_NAME="com.mysql"
safe_run ${JBOSS_CLI} --connect \
             --command="module add --name=${MODULE_NAME} --resources=${MYSQL_CONNECTOR} --dependencies=javax.api,javax.transaction.api"

info "Adding MySQL driver"
safe_run ${JBOSS_CLI} --connect \
             --command="/subsystem=datasources/jdbc-driver=mysql:add(driver-name=mysql,driver-module-name=${MODULE_NAME},driver-class-name=com.mysql.cj.jdbc.Driver,driver-xa-datasource-class-name=com.mysql.cj.jdbc.MysqlXADataSource)"

info "Creating a new datasource"
safe_run ${JBOSS_CLI} --connect --command="data-source add
        --name=${DB_NAME}DS
        --jndi-name=java:jboss/datasources/${DB_NAME}DS
        --user-name=${DB_USER}
        --password=${DB_PASS}
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
rm -f /tmp/*.jar
