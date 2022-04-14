sbt docker:publishLocal
docker-compose -f ./redis-docker/docker-compose.yml up
docker-compose -f docker-compose.yml up
