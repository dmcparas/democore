#!/bin/bash
# wait for zookeeper
echo "Waiting for Zookeeper..."
while ! nc -z zk 2181; do
  sleep 1
done

echo "Starting Kafka..."
exec /etc/confluent/docker/run