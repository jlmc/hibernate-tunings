#!/bin/sh
mvn clean package && docker build -t io.costax/cache2ndee .
docker rm -f cache2ndee || true && docker run -d -p 8080:8080 -p 8787:8787 --name cache2ndee io.costax/cache2ndee
