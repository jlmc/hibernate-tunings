#!/bin/bash

mvn clean install -Dmaven.test.skip=true -X -Dnet.bytebuddy.experimental=true
