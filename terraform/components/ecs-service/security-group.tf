resource "aws_security_group_rule" "http_in_from_haproxy" {
  type                     = "ingress"
  from_port                = "${var.service_config_map["env_service_port"]}"
  to_port                  = "${var.service_config_map["env_service_port"]}"
  protocol                 = "tcp"
  source_security_group_id = "${data.terraform_remote_state.ecs_cluster.haproxy_task_security_group_id}"
  security_group_id        = "${data.terraform_remote_state.ecs_cluster.alfresco_proxy_task_security_group_id}"
}

# Parses the created NLB for its private IP addresses
data "aws_network_interface" "nlb_subnets" {
  count = "${length(local.private_subnet_ids)}"
  filter = {
    name   = "description"
    values = ["ELB ${aws_lb.environment.arn_suffix}"]
  }
  filter = {
    name   = "subnet-id"
    values = ["${element(local.private_subnet_ids, count.index)}"]
  }
}

#inbound from loadbalancer
resource "aws_security_group_rule" "all_traffic_from_nlb" {
  security_group_id        = "${data.terraform_remote_state.ecs_cluster.alfresco_proxy_task_security_group_id}"
  type                     = "ingress"
  from_port                = 0
  to_port                  = 65535
  protocol                 = "tcp"
  #  protocol          = -1
  cidr_blocks              = ["${formatlist("%s/32",flatten(data.aws_network_interface.nlb_subnets.*.private_ips))}"]
  description              = "traffic from NLB"
}

#-------------------------------------------------------------
### ports:
# 8080
# use case is for spg developers to prime wiremock in order to run perf tests against non prod environments from their laptop when in the MoJ VPN
#-------------------------------------------------------------

resource "aws_security_group_rule" "8080_from_mojVPN_ingress" {
  count                    = "${var.is_wiremock ? 0 : 1}" # do not allow access if on official data enviro (prod, preprod etc)
  security_group_id        = "${data.terraform_remote_state.ecs_cluster.alfresco_proxy_task_security_group_id}"
  description              = "from moj VPN for use by spg developers"
  type                     = "ingress"
  cidr_blocks              = ["81.134.202.29/32"]
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
}