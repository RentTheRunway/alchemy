# For running maven tests, for Java repos
FROM centos:6.10

ARG JAVA_VERSION=1.8.0
ARG GIT_VERSION=1.7.1
ARG MAVEN_VERSION=3.3.9

RUN yum install -y epel-release \
    java-$JAVA_VERSION-openjdk \
    java-$JAVA_VERSION-openjdk-devel \
    git-$GIT_VERSION \
    rpm-build && \
  yum clean all && \
  rm -rf /var/cache/*

ENV JAVA_HOME=/usr/lib/jvm/java
ENV PATH=$JAVA_HOME:$PATH

# Maven
# Setting the pipefail option on,
#  i.e. non-zero exit codes within any part of pipeline will break
#  entire process early
SHELL ["/bin/bash", "-o", "pipefail", "-c"]
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_VERSION=${MAVEN_VERSION}
ENV M2_HOME /usr/share/maven
ENV M2 $M2_HOME/bin
ENV PATH $M2:$PATH

ENV SONAR_RUNNER_OPTS="-Xmx4192m -Djavax.net.ssl.trustStore=$JAVA_HOME/jre/lib/security/cacerts -Djavax.net.ssl.trustStorePassword=changeit"

# Download our SonarQube cert and add it to java cacerts, so that
#   we can run sonar scanning tasks
RUN keytool \
    -printcert \
    -rfc \
    -sslserver "sonarqube01.m.dfw.rtrdc.net:443" | \
    keytool \
        -importcert \
        -noprompt \
        -keystore "$JAVA_HOME/jre/lib/security/cacerts" \
        -storepass changeit \
        -alias "sonarqube01.m.dfw.rtrdc.net"

# Define default command, can be overriden by passing an argument when running the container
CMD ["mvn","-version"]
