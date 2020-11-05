Playground for Kafka Stream processing.

### Cleanup

If you want to reset your processor and reprocess from the input topics, you
the following command with the application ID and all input topics specified.

```text
docker-compose exec kafka kafka-streams-application-reset \
  --application-id <app-id> \
  --input-topics <topic-1>,<topic-1> \
  --zookeeper zookeeperHost:2181
```
