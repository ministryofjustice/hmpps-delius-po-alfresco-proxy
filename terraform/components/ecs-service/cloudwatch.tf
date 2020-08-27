resource "aws_cloudwatch_log_group" "task_log_group" {
  name              = local.service_name
  retention_in_days = var.cloudwatch_log_retention
  tags              = merge(var.tags, map("Name", "${local.service_name}"))
}
