#-------------------------------------------------------------
### ports:
# 8080
#
#-------------------------------------------------------------

#8080
resource "aws_security_group_rule" "rule_8080_from_engineering_ingress" {
  count                    = "${var.is_wiremock ? 1 : 0}" # do not allow access if not in test mode
  security_group_id        = "${data.terraform_remote_state.ecs_cluster.alfresco_proxy_task_security_group_id}"
  description              = "from the VPC when running performance tests"
  type                     = "ingress"
  cidr_blocks              = [
    "${data.terraform_remote_state.vpc.vpc_private-subnet-az1-cidr_block}",
    "${data.terraform_remote_state.vpc.vpc_private-subnet-az2-cidr_block}",
    "${data.terraform_remote_state.vpc.vpc_private-subnet-az3-cidr_block}"
  ]
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
}