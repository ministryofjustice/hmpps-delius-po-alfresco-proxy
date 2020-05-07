[
    {
        "name": "${container_name}",
        "image": "${image_url}:${image_version}",
        "essential": true,
        "interactive": true,
        "healthCheck": {
            "command": [ "CMD-SHELL", "curl -s http://localhost:8080/actuator/health" ],
            "interval": 60,
            "retries": 2,
            "startPeriod": 60,
            "timeout": 5
        },
        "logConfiguration": {
            "logDriver": "awslogs",
            "options": {
                "awslogs-group": "${log_group_name}",
                "awslogs-region": "${region}",
                "awslogs-stream-prefix": "ecs-${container_name}"
            }
        },
        "environment": [
            {
              "name": "APPLICATION_NAME",
              "value": "${application_name}"
            },
            {
              "name": "SPG_ALFRESCO_HEALTH_ENDPOINT",
              "value": "${alfresco_health_endpoint}"
            },
            {
              "name": "SPG_ALFRESCO_BASE_URL",
              "value": "${alfresco_base_url}"
            }
          ],

        "volumesFrom": [],
        "mountPoints": [],
        "portMappings": [
            {
                "containerPort": ${env_service_port},
                "hostPort": ${env_service_port},
                "protocol": "tcp"
            }
        ],
        "cpu": 0
        "memory": 1024
    }
]
