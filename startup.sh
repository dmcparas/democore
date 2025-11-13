#!/bin/bash
set -e

echo "Waiting for Zookeeper to be ready..."
until nc -z zookeeper 2181; do
  sleep 2
done

echo "Starting Kafka..."
/etc/confluent/docker/run &
wait
