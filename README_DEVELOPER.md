## Building Docker containers for production

The script below can be run to build the jar files for each server, and then build the Docker images and push to Docker Hub.  

**IMPORTANT** Be sure to update the version/tag number for each image (current reads 2.0).

```
cd /path/to/omh-dsu

./gradlew :authorization-server:build -x test &&
docker build -t smalldatalab/ohmage-auth-server:2.0 authorization-server/docker/ &&
docker push smalldatalab/ohmage-auth-server:2.0

./gradlew :resource-server:build -x test &&
docker build -t smalldatalab/ohmage-resource-server:2.0 resource-server/docker/ &&
docker push smalldatalab/ohmage-resource-server:2.0

./gradlew :ohmageomh-manage-server:build -x test &&
docker build -t smalldatalab/ohmage-manage-server:2.0 ohmageomh-manage-server/docker/  &&
docker push smalldatalab/ohmage-manage-server:2.0

./gradlew :ohmageomh-dpu-server:build -x test &&
docker build -t smalldatalab/ohmage-dpu-server:2.0 ohmageomh-dpu-server/docker/  &&
docker push smalldatalab/ohmage-dpu-server:2.0
```

The shim-server is in a different repository, `omh-shims`.  You can similarily build and push that image with

```
./gradlew :ohmageomh-shim-server:build -x test &&
docker build -t smalldatalab/ohmage-shim-server:2.0 shim-server/docker/  &&
docker push smalldatalab/ohmage-shim-server:2.0
```

## Run development environment locally

The configuration files and instructions for running a local development environment with debugging, is in the repository `ohmage-omh-configs`, hosted by SDL.  Contact them for access.
