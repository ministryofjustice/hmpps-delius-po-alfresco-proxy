//resource "aws_appautoscaling_policy" "cpu_utilization_low_scaling_policy" {
//  name               = "${local.service_name}-cpu-low-scaling-policy"
//  policy_type        = "StepScaling"
//  resource_id        = aws_appautoscaling_target.scaling_target.resource_id
//  scalable_dimension = aws_appautoscaling_target.scaling_target.scalable_dimension
//  service_namespace  = aws_appautoscaling_target.scaling_target.service_namespace
//
//  step_scaling_policy_configuration {
//    adjustment_type         = "ChangeInCapacity"
//    cooldown                = 700
//    metric_aggregation_type = "Average"
//
//    step_adjustment {
//      metric_interval_upper_bound = 0
//      scaling_adjustment          = -1
//    }
//  }
//}
//
//resource "aws_appautoscaling_policy" "cpu_utilization_high_scaling_policy" {
//  name               = "${local.service_name}-cpu-high-scaling-policy"
//  policy_type        = "StepScaling"
//  resource_id        = aws_appautoscaling_target.scaling_target.resource_id
//  scalable_dimension = aws_appautoscaling_target.scaling_target.scalable_dimension
//  service_namespace  = aws_appautoscaling_target.scaling_target.service_namespace
//
//  step_scaling_policy_configuration {
//    adjustment_type         = "ChangeInCapacity"
//    cooldown                = 700
//    metric_aggregation_type = "Average"
//
//    step_adjustment {
//      metric_interval_lower_bound = 0
//      scaling_adjustment          = 1
//    }
//  }
//}
//
//resource "aws_appautoscaling_target" "scaling_target" {
//  min_capacity       = var.ecs_scaling_min_capacity
//  max_capacity       = var.ecs_scaling_max_capacity
//  resource_id        = "service/${local.service_name}/${local.service_name}"
//  role_arn           = aws_iam_role.ecs_execute_role.arn
//  scalable_dimension = "ecs:service:DesiredCount"
//  service_namespace  = "ecs"
//
//  # Use lifecycle rule as workaround for role_arn being changed every time due to
//  # role_arn being required field but AWS will always switch this to the auto created service role
//  lifecycle {
//    ignore_changes = [
//      role_arn,
//    ]
//  }
//}
//
//resource "aws_cloudwatch_metric_alarm" "cpu_utilization_high" {
//  alarm_name          = "${local.service_name}-cpu-high-alarm"
//  comparison_operator = "GreaterThanOrEqualToThreshold"
//  evaluation_periods  = "1"
//  metric_name         = "CPUUtilization"
//  namespace           = "AWS/ECS"
//  period              = "120"
//  statistic           = "Average"
//  threshold           = "70"
//
//  dimensions = {
//    ServiceName = local.service_name
//    ClusterName = local.service_name
//  }
//
//  alarm_description = "This metric monitors ecs cpu utilization"
//  alarm_actions = [
//    aws_appautoscaling_policy.cpu_utilization_high_scaling_policy.arn,
//    aws_autoscaling_policy.cpu_utilization_high_scaling_policy.arn
//  ] //We want to reuse this alarm for ASG scaling policy
//}
//
//resource "aws_cloudwatch_metric_alarm" "cpu_utilization_low" {
//  alarm_name          = "${local.service_name}-cpu-low-alarm"
//  comparison_operator = "LessThanOrEqualToThreshold"
//  evaluation_periods  = "1"
//  metric_name         = "CPUUtilization"
//  namespace           = "AWS/ECS"
//  period              = "120"
//  statistic           = "Average"
//  threshold           = "30"
//
//  dimensions = {
//    ServiceName = local.service_name
//    ClusterName = local.service_name
//  }
//
//  alarm_description = "This metric monitors ecs cpu utilization"
//  alarm_actions = [
//    aws_appautoscaling_policy.cpu_utilization_low_scaling_policy.arn,
//    aws_autoscaling_policy.cpu_utilization_low_scaling_policy.arn
//  ] //We want to reuse this alarm for ASG scaling policy
//}