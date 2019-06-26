# Specify the MySQL version to be downloaded
ARG MYSQL_VERSION=8.0.15

# Pull the official MySQL image
FROM mysql:${MYSQL_VERSION} AS database

ARG buildno

# Configure MySQL root user
ENV MYSQL_ROOT_PASSWORD mysqlroot

# Configure MySQL database user
ENV MYSQL_USER mysqluser
ENV MYSQL_PASSWORD mysqlpass

# Configure MySQL database
ENV MYSQL_DATABASE cmd
ENV MYSQL_PORT 3306

RUN echo "Build Number: ${buildno}"

# Expose the standard MySQL port
EXPOSE ${MYSQL_PORT}
