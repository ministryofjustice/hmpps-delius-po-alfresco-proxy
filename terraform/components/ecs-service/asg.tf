resource "aws_autoscaling_group" "ecs_asg" {
  name                 = "${local.service_name}-asg"
  launch_configuration = aws_launch_configuration.ecs_host_lc.id

  # Not setting desired count as that could cause scale in when deployment runs and lead to resource exhaustion
  max_size                  = var.ecs_scaling_max_capacity
  min_size                  = var.ecs_scaling_min_capacity
  desired_capacity  = var.asg_desired_capacity
  health_check_grace_period = 0
  termination_policies      = ["NewestInstance"]
  vpc_zone_identifier       = local.private_subnet_ids

  lifecycle {
    create_before_destroy = true
    ignore_changes = [
      desired_capacity,
    ]
  }

  tags = concat(
    data.null_data_source.tags.*.outputs,
    [
      {
        key                 = "Name"
        value               = "${local.service_name}-asg"
        propagate_at_launch = true
      }
    ]
  )
}

# Autoscaling Policies and trigger alarms
resource "aws_autoscaling_policy" "cpu_utilization_high_scaling_policy" {
  name                   = "${local.service_name}-cpu_utilization_high_scaling_policy"
  scaling_adjustment     = "1"
  adjustment_type        = "ChangeInCapacity"
  autoscaling_group_name = aws_autoscaling_group.ecs_asg.name
  cooldown               = 700
}

resource "aws_autoscaling_policy" "cpu_utilization_low_scaling_policy" {
  name                   = "${local.service_name}-cpu_utilization_low_scaling_policy"
  scaling_adjustment     = "-1"
  adjustment_type        = "ChangeInCapacity"
  autoscaling_group_name = aws_autoscaling_group.ecs_asg.name
  cooldown               = 700
}


# Hack to merge additional tag into existing map and convert to list for use with asg tags input
data "null_data_source" "tags" {
  count = length(keys(var.tags))

  inputs = {
    key                 = element(keys(var.tags), count.index)
    value               = element(values(var.tags), count.index)
    propagate_at_launch = true
  }
}