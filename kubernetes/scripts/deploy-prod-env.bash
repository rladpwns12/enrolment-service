#!/usr/bin/env bash

kubectl create configmap config-repo-student --from-file=config-repo/application.yml --from-file=config-repo/student.yml --save-config
kubectl create configmap config-repo-course           --from-file=config-repo/application.yml --from-file=config-repo/course.yml --save-config
kubectl create configmap config-repo-enrolment    --from-file=config-repo/application.yml --from-file=config-repo/enrolment.yml --save-config

kubectl create secret generic rabbitmq-credentials \
    --from-literal=SPRING_RABBITMQ_USERNAME=rabbit-user-prod \
    --from-literal=SPRING_RABBITMQ_PASSWORD=rabbit-pwd-prod \
    --save-config

kubectl create secret generic rabbitmq-zipkin-credentials \
    --from-literal=RABBIT_USER=rabbit-user-prod \
    --from-literal=RABBIT_PASSWORD=rabbit-pwd-prod \
    --save-config

kubectl create secret generic mongodb-credentials \
    --from-literal=SPRING_DATA_MONGODB_AUTHENTICATION_DATABASE=admin \
    --from-literal=SPRING_DATA_MONGODB_USERNAME=mongodb-user-prod \
    --from-literal=SPRING_DATA_MONGODB_PASSWORD=mongodb-pwd-prod \
    --save-config

kubectl create secret tls tls-certificate --key kubernetes/cert/tls.key --cert kubernetes/cert/tls.crt

docker-compose up -d mongodb rabbitmq
docker tag allclegiwon/student-service allclegiwon/student-service:v1
docker tag allclegiwon/enrolment-service           allclegiwon/enrolment-service:v1
docker tag allclegiwon/course-service    allclegiwon/course-service:v1

kubectl apply -k kubernetes/services/overlays/prod

kubectl wait --timeout=600s --for=condition=ready pod --all
