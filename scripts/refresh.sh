#!/bin/bash

echo "> destroying docker-compose..."
docker-compose rm -f -s -v
docker volume prune -f

docker-compose up -d

sleep 5
echo "initiating mongo cluster..."

docker-compose exec mongo /bin/bash -c 'mongo localhost:27017/datasite-poc <<-EOF
    rs.initiate({
        _id: "rs0",
        "version" : 1,
        members: [
            { _id: 0, host: "localhost:27017" }
        ]
    });
EOF'

sleep 15
echo "initiating debezium connector"

curl -i -X POST \
  -H "Accept:application/json" \
  -H  "Content-Type:application/json" \
  http://localhost:8083/connectors/ \
  -d '{
  "name": "mongodb_connector",
  "config": {
    "connector.class" : "io.debezium.connector.mongodb.MongoDbConnector",
    "tasks.max" : "1",
    "mongodb.hosts" : "rs0/mongo:27017",
    "mongodb.name" : "mongo",
    "mongodb.members.auto.discover" : false,
    "collection.whitelist" : "datasite-poc.*",
    "provide.transaction.metadata" : true,
    "database.history.kafka.bootstrap.servers" : "kafka:9092"
  }
}'


