#!/bin/zsh

# Copia o certificado do Elasticsearch para o diretório de resources da Aplicação
./gradlew build
docker build -f src/main/docker/Dockerfile.jvm -t quarkus/produto-jvm .

docker-compose -f ../docker-compose.yml -p trevo_quarkus up -d produtos_trevo