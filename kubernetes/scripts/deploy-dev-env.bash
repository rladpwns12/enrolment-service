#!/usr/bin/env bash

# Print commands to the terminal before execution and stop the script if any error occurs
set -ex

kubectl create configmap config-repo-student --from-file=config-repo/application.yml --from-file=config-repo/student.yml --save-config
kubectl create configmap config-repo-course           --from-file=config-repo/application.yml --from-file=config-repo/course.yml --save-config
kubectl create configmap config-repo-enrolment    --from-file=config-repo/application.yml --from-file=config-repo/enrolment.yml --save-config

# First deploy the resource managers and wait for their pods to become ready
kubectl apply -f kubernetes/services/overlays/dev/rabbitmq-dev.yml
kubectl apply -f kubernetes/services/overlays/dev/mongodb-dev.yml
kubectl wait --timeout=600s --for=condition=ready pod --all

# Next deploy the microservices and wait for their pods to become ready
kubectl apply -k kubernetes/services/overlays/dev
kubectl wait --timeout=600s --for=condition=ready pod --all

set +ex