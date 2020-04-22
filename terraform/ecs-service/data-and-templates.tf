# Get current context for things like account id
data "aws_caller_identity" "current" {}

# Template files for offenderapi task role and execution role definitions
data "template_file" "ecstasks_assumerole_template" {
  template = "${file("${path.module}/templates/iam/ecstasks_assumerole_policy.tpl")}"
  vars     = {}
}

data "template_file" "policy_template" {
  template = "${file("${path.module}/templates/iam/ecstask_execution_policy.tpl")}"

  vars = {
    region           = "${var.region}"
    aws_account_id   = "${data.aws_caller_identity.current.account_id}"
    environment_name = "${var.environment_name}"
    project_name     = "${var.project_name}"
  }
}

# Offender API task definition template
data "template_file" "task_definition" {
  template = "${file("templates/ecs/task_definition.tpl")}"

  vars {
    region           = "${var.region}"
    aws_account_id   = "${data.aws_caller_identity.current.account_id}"
    environment_name = "${var.environment_name}"
    project_name     = "${var.project_name}"
    container_name   = "${local.container_name}"
    image_url        = "${var.service_config_map["image"]}"
    image_version    = "${var.service_config_map["image_version"]}"
    env_service_port = "${var.service_config_map["env_service_port"]}"
    log_group_name   = "${aws_cloudwatch_log_group.task_log_group.name}"

    application_name         = "${var.application_name}"
    alfresco_health_endpoint = "${var.alfresco_health_endpoint}"
    alfresco_base_url        = "${var.alfresco_base_url}"
  }
}

data "aws_acm_certificate" "cert" {
  domain      = "${data.terraform_remote_state.vpc.public_ssl_domain}"
  types       = ["AMAZON_ISSUED"]
  most_recent = true
}