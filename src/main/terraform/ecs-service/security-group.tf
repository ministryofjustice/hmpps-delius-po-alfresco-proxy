# Localised Task security group for changes w/out dependency on delius core sgs
resource "aws_security_group" "task_security_group" {
  name        = "${local.service_name}"
  description = "PO Alfresco Proxy Task Security Group"
  vpc_id      = "${data.terraform_remote_state.vpc.vpc_id}"
  tags        = "${merge(var.tags, map("Name", "${local.service_name}"))}"
}



resource "aws_security_group_rule" "http_in_from_haproxy" {
  type                     = "ingress"
  from_port                = "${var.service_config_map["env_service_port"]}"
  to_port                  = "${var.service_config_map["env_service_port"]}"
  protocol                 = "tcp"
  source_security_group_id = "${data.terraform_remote_state.security-groups-and-rules.haproxy_external_instance_sg_id}"
  security_group_id        = "${aws_security_group.task_security_group.id}"
}
