FROM openjdk:17.0.2-oracle
ENV SBT_VERSION 1.7.1
RUN microdnf install unzip
RUN curl -L -o sbt-$SBT_VERSION.zip https://github.com/sbt/sbt/releases/download/v$SBT_VERSION/sbt-$SBT_VERSION.zip
RUN cat /etc/os-release
RUN unzip sbt-$SBT_VERSION.zip -d ops
WORKDIR /cloudsimulation
ADD . /cloudsimulation
CMD /ops/sbt/bin/sbt run
