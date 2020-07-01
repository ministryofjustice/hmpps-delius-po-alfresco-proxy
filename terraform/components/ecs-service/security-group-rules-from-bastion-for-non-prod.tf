resource "aws_security_group_rule" "rule_8080_from_bastion" {
  count                    = "${var.is_wiremock ? 1 : 0}" # do not allow access if on official data enviro (prod, preprod etc)
  security_group_id        = "${data.terraform_remote_state.ecs_cluster.alfresco_proxy_task_security_group_id}"
  type                     = "ingress"
  from_port                = "8080"
  to_port                  = "8080"
  protocol                 = "tcp"
  cidr_blocks             = [ "${values(data.terraform_remote_state.vpc.bastion_vpc_public_cidr)}" ]
  description              = "from SPG developers' computers via bastion"
}