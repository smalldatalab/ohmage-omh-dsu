## Building Docker containers for production

The script below can be run to build the jar files for each server, and then build the Docker Images and push to Docker Hub.  

**IMPORTANT** Be sure to update the version/tag number for each image (current reads 2.0).

```
./gradlew :authorization-server:build -x test &&
docker build -t smalldatalab/ohmage-auth-server:2.0 authorization-server/docker/ &&
docker push smalldatalab/ohmage-auth-server:2.0

./gradlew :resource-server:build -x test &&
docker build -t smalldatalab/ohmage-resource-server:2.0 resource-server/docker/ &&
docker push smalldatalab/ohmage-resource-server:2.0

./gradlew :ohmageomh-manage-server:build -x test &&
docker build -t smalldatalab/ohmage-manage-server:2.0 ohmageomh-manage-server/docker/  &&
docker push smalldatalab/ohmage-manage-server:2.0
```

## Run `manage-server` locally for development

