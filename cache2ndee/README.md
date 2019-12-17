# How to use 2nd level Cache in a a Java EE application server

The purpose of this example is to analyze the principles of using the 2nd cache level on a Java EE application server.
All example was developed and demonstrated using wildfly 18.0.0 Final.

## Build
mvn clean package && docker build -t io.costax/cache2ndee .

## RUN

docker rm -f cache2ndee || true && docker run -d -p 8080:8080 -p 8787:8787 --name cache2ndee io.costax/cache2ndee 