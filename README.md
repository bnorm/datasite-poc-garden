
### MongoDB configuration

After starting the Docker compose file, run the following commands to
configuration the Debezium connection to MongoDB.

Configure the replication set in MongoDB:
```shell
docker-compose exec mongo /bin/bash -c 'mongo localhost:27017/datasite-poc <<-EOF
    rs.initiate({
        _id: "rs0",
        "version" : 1,
        members: [
            { _id: 0, host: "localhost:27017" }
        ]
    });
EOF'
```

Connection Debezium to MongoDB.
```shell
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
```

### IntelliJ HTTP Requests

Create a `http-client-private.env.json` file in the project root directory with
the following template:
```json
{
  "<environment name>": {
    "user_id": "<user name>"
  }
}
```

Replace `<environment name>` and the `<user name>` values with appropriate
values. For example, you could replace with `local` and `john` respectively.
