#-------------------------------------------------------------
### ports:
# 8080
# use case is for spg developers to prime wiremock in order to run perf tests against non prod environments from their laptop when in the studio
#-------------------------------------------------------------

resource "aws_security_group_rule" "8080_from_digital_studio_ingress" {
  count                    = "${var.is_wiremock ? 0 : 1}" # do not allow access if on official data enviro (prod, preprod etc)
  security_group_id        = "${data.terraform_remote_state.ecs_cluster.alfresco_proxy_task_security_group_id}"
  description              = "from digital studio for use by spg developers"
  type                     = "ingress"
  cidr_blocks              = ["217.33.148.210/32"]
  from_port                = 8080
  to_port                  = 8080
  protocol                 = "tcp"
}

