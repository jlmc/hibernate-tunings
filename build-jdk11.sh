#!/bin/bash

#mvn clean install -Dmaven.test.skip=true -X -Dnet.bytebuddy.experimental=true
#mvn test -Dnet.bytebuddy.experimental=true -Dmaven.test.skip=true
#mvn test -Dnet.bytebuddy.experimental=true
# mvn clean install -X -Dnet.bytebuddy.experimental=true

#mvn clean compile -Dnet.bytebuddy.experimental=true -DskipTests
#mvn package -Dnet.bytebuddy.experimental=true -DskipTests
#mvn test-compile -Dnet.bytebuddy.experimental=true
#mvn test -Dnet.bytebuddy.experimental=true

mvn clean
mvn compile
mvn test-compile
mvn test
