resource "aws_security_group_rule" "http_in_from_haproxy" {
  type                     = "ingress"
  from_port                = var.service_config_service_port
  to_port                  = var.service_config_service_port
  protocol                 = "tcp"
  source_security_group_id = data.terraform_remote_state.ecs_cluster.outputs.haproxy_task_security_group_id
  security_group_id        = data.terraform_remote_state.ecs_cluster.outputs.alfresco_proxy_task_security_group_id
}

# Parses the created NLB for its private IP addresses
data "aws_network_interface" "nlb_subnets" {
  count = length(local.private_subnet_ids)
  filter {
    name   = "description"
    values = ["ELB ${aws_lb.environment.arn_suffix}"]
  }
  filter {
    name   = "subnet-id"
    values = [element(local.private_subnet_ids, count.index)]
  }
}

#inbound from loadbalancer
resource "aws_security_group_rule" "all_traffic_from_nlb" {
  security_group_id = data.terraform_remote_state.ecs_cluster.outputs.alfresco_proxy_task_security_group_id
  type              = "ingress"
  from_port         = 0
  to_port           = 65535
  protocol          = "tcp"
  #  protocol          = -1
  cidr_blocks = formatlist("%s/32", flatten(data.aws_network_interface.nlb_subnets.*.private_ips))
  description = "traffic from NLB"
}