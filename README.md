# hmpps-po-alfresco-proxy

To run tests locally, do: `make build`

To run application, do: `make run` and in another terminal `curl http://localhost:8080/api/healthcheck`

---

# sandpit

Terraform plan (it is assumed you have already done `.aws_mfa_login`): `make sandpit-plan`

To deploy to ECS sandpint:

    make sandpit-ecs-deploy
    
Note that `make sandpit-ecs-deploy` will build, test, build docker image, upload to the docker image to ECR, and run ecs-deploy