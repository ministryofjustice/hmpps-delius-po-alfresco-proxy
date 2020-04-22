resource "aws_ecs_task_definition" "task_definition" {
  family                   = "${local.service_name}"
  task_role_arn            = "${aws_iam_role.task_role.arn}"
  execution_role_arn       = "${aws_iam_role.execute_role.arn}"
  container_definitions    = "${data.template_file.task_definition.rendered}"
  network_mode             = "awsvpc"
  memory                   = "${var.service_config_map["memory"]}"
  cpu                      = "${var.service_config_map["cpu"]}"
  requires_compatibilities = ["EC2"]
  tags                     = "${merge(var.tags, map("Name", "${local.service_name}"))}"
}

resource "aws_ecs_service" "service" {
  name            = "${local.service_name}"
  cluster         = "${data.terraform_remote_state.ecs_cluster.shared_ecs_cluster_id}"
  task_definition = "${aws_ecs_task_definition.task_definition.arn}"

  # TODO what does this mean?
  # When new tag and arn formats are accepted in an environment - tags can be propagated
  # propagate_tags  = "TASK_DEFINITION"

  network_configuration = {
    subnets = ["${local.private_subnet_ids}"]

    security_groups = [
      "${data.terraform_remote_state.common_stack.alfresco_proxy_task_security_group_id}",

      #TODO remove this and add explicit outbound rules as part of security hardening ticket ALS-500
      "${data.terraform_remote_state.security-groups-and-rules.spg_common_outbound_sg_id}",
    ]
  }
  depends_on = ["aws_iam_role.task_role"]



  load_balancer {
    target_group_arn = "${aws_lb_target_group.environment.arn}"
    container_name   = "${local.container_name}"
    container_port   = "${var.service_config_map["env_service_port"]}"
  }

  lifecycle {
    ignore_changes = ["desired_count"]
  }
}

resource "aws_appautoscaling_target" "scaling_target" {
  min_capacity       = "${var.service_config_map["ecs_scaling_min_capacity"]}"
  max_capacity       = "${var.service_config_map["ecs_scaling_max_capacity"]}"
  resource_id        = "service/${data.terraform_remote_state.ecs_cluster.shared_ecs_cluster_name}/${aws_ecs_service.service.name}"
  role_arn           = "${aws_iam_role.execute_role.arn}"
  scalable_dimension = "ecs:service:DesiredCount"
  service_namespace  = "ecs"

  # Use lifecycle rule as workaround for role_arn being changed every time due to
  # role_arn being required field but AWS will always switch this to the auto created service role
  lifecycle {
    ignore_changes = "role_arn"
  }
}

resource "aws_appautoscaling_policy" "scaling_policy" {
  name               = "${local.service_name}"
  policy_type        = "TargetTrackingScaling"
  resource_id        = "${aws_appautoscaling_target.scaling_target.resource_id}"
  scalable_dimension = "${aws_appautoscaling_target.scaling_target.scalable_dimension}"
  service_namespace  = "${aws_appautoscaling_target.scaling_target.service_namespace}"

  target_tracking_scaling_policy_configuration {
    predefined_metric_specification {
      predefined_metric_type = "ECSServiceAverageCPUUtilization"
    }

    target_value = "${var.service_config_map["ecs_target_cpu"]}"
  }
}
