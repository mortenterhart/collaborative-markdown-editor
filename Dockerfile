# Specify WildFly version and app home
# directory as arguments
ARG WILDFLY_VERSION=16.0.0.Final

# 1. Stage: Build frontend and backend
# Use Maven base image
FROM maven:3.6.0-jdk-11-slim AS build

ENV APP_HOME /app

COPY frontend ${APP_HOME}/frontend
COPY src ${APP_HOME}/src
COPY pom.xml ${APP_HOME}/pom.xml

# Set the working directory to the app home directory
WORKDIR ${APP_HOME}

# Install Git as this is required for a custom
# npm dependency
RUN apt-get update && apt-get --assume-yes install git

# Build the frontend and backend of the application
RUN mvn install -P deployment -DskipTests=true -Dmaven.javadoc.skip=true -B -V

# 2. Stage: Configure WildFly and deploy application
# Use the WildFly base image to deploy the built application
FROM jboss/wildfly:${WILDFLY_VERSION} AS server

ARG buildno

# Set the working directory to the WildFly home directory
WORKDIR ${JBOSS_HOME}

# Ports used by the WildFly instance
ENV WEBAPP_PORT 8080/tcp
ENV ADMINISTRATION_PORT 9990/tcp

# WildFly settings
ENV WILDFLY_USER wildfly
ENV WILDFLY_PASSWORD wildfly

# MySQL version
ENV MYSQL_VERSION 8.0.15

# Environment variables for the WildFly datasource
ENV DB_NAME cmd
ENV DB_USER mysqluser
ENV DB_PASSWORD mysqlpass
ENV DB_PORT 3306
ENV DB_URI mysql-instance:${DB_PORT}

# Path variables
ENV JBOSS_CLI ${JBOSS_HOME}/bin/jboss-cli.sh
ENV DEPLOYMENT_DIR ${JBOSS_HOME}/standalone/deployments
ENV APP_DIR ./collaborative-markdown-editor

RUN echo "Build Number: ${buildno}"

# Add the configuration script to add MySQL driver
# and datasource to the WildFly instance
COPY docker/scripts/jboss-configure-mysql-datasource.sh .

# Switch temporarily to root to get the permission to
# make the configuration script executable
USER root
RUN chmod +x ./jboss-configure-mysql-datasource.sh
USER jboss

# Execute the configuration script
RUN ./jboss-configure-mysql-datasource.sh

# Remove the script
RUN rm -f ./jboss-configure-datasource.sh

# Deploy the application as ROOT app to WildFly
COPY --from=build /app/target/ROOT.war ${DEPLOYMENT_DIR}/

# Expose the webapp port
EXPOSE ${WEBAPP_PORT}

# Start the WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
