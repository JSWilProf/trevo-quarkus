#!/bin/zsh

# Copia o certificado do Elasticsearch para o diretório de resources da Aplicação
cp -r ../certs/ca src/main/resources/
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/proposta-jvm .

docker-compose -f ../docker-compose.yml -p trevo_quarkus up -d propostas_trevo