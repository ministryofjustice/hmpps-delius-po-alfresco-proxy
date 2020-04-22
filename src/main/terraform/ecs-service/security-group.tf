resource "aws_security_group_rule" "http_in_from_haproxy" {
  type                     = "ingress"
  from_port                = "${var.service_config_map["env_service_port"]}"
  to_port                  = "${var.service_config_map["env_service_port"]}"
  protocol                 = "tcp"
  source_security_group_id = "${data.terraform_remote_state.ecs_cluster.haproxy_task_security_group_id}"
  security_group_id        = "${data.terraform_remote_state.ecs_cluster.alfresco_proxy_task_security_group_id}"
}
