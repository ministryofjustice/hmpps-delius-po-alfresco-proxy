resource "aws_ecs_task_definition" "task_definition" {
  family                   = "${local.service_name}"
  task_role_arn            = "${aws_iam_role.task_role.arn}"
  execution_role_arn       = "${aws_iam_role.ecs_execute_role.arn}"
  container_definitions    = "${data.template_file.task_definition.rendered}"
  network_mode             = "awsvpc"
  memory                   = var.service_config_memory
  cpu                      = var.service_config_cpu
  requires_compatibilities = ["EC2"]
  tags                     = "${merge(var.tags, map("Name", "${local.service_name}"))}"

  placement_constraints {
    type       = "memberOf"
    expression = "${local.task_placement_expression}"
  }
}

resource "aws_ecs_service" "service" {
  name            = "${local.service_name}"
  cluster         = "${aws_ecs_cluster.ecs.id}"
  task_definition = "${aws_ecs_task_definition.task_definition.arn}"

  # TODO what does this mean?
  # When new tag and arn formats are accepted in an environment - tags can be propagated
  # propagate_tags  = "TASK_DEFINITION"

  network_configuration {
    subnets         = local.private_subnet_ids

    security_groups = [
      "${data.terraform_remote_state.ecs_cluster.outputs.alfresco_proxy_task_security_group_id}",

      #TODO remove this and add explicit outbound rules as part of security hardening ticket ALS-500
      "${data.terraform_remote_state.security-groups-and-rules.outputs.spg_common_outbound_sg_id}",
    ]
  }
  depends_on = ["aws_iam_role.task_role"]

  deployment_minimum_healthy_percent = var.service_config_deployment_minimum_healthy_percent

  load_balancer {
    target_group_arn = "${aws_lb_target_group.environment.arn}"
    container_name   = "${local.container_name}"
    container_port   = var.service_config_service_port
  }

  desired_count = "${var.task_desired_count}"
}