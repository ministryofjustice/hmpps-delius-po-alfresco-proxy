[
    {
        "name": "${container_name}",
        "image": "${image_url}:${image_version}",
        "essential": true,
        "interactive": true,
        "healthCheck": {
            "command": [ "CMD-SHELL", "echo ''|nc localhost 8080" ],
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
        "command": ["--verbose"],
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
        "ulimits": [
            {
              "name": "nproc",
              "softLimit": 65535,
              "hardLimit": 65535
            },
            {
              "name": "nofile",
              "softLimit": 65535,
              "hardLimit": 65535
            }
        ],
        "cpu": 0,
        "memoryReservation": 512
    }
]
