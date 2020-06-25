#-------------------------------------------------------------
### ports:
# 8080
#
#-------------------------------------------------------------

#8080
resource "aws_security_group_rule" "8080_from_engineering_ingress" {
  count                    = "${var.is_wiremock ? 0 : 1}" # do not allow access if on official data enviro (prod, preprod etc)
  security_group_id        = "${data.terraform_remote_state.ecs_cluster.haproxy_task_security_group_id}"
  description              = "from engineeringNAT for use by CI/CD tests"
  type                     = "ingress"
  cidr_blocks              = [
    "${data.terraform_remote_state.engineering_nat.common-nat-public-ip-az1}/32",
    "${data.terraform_remote_state.engineering_nat.common-nat-public-ip-az2}/32",
    "${data.terraform_remote_state.engineering_nat.common-nat-public-ip-az3}/32"
  ]
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
}
