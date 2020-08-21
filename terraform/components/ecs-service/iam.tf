# Task Execution Role for pulling the image and putting logs to cloudwatch
resource "aws_iam_role" "ecs_execute_role" {
  name               = "${local.service_name}-ecs-execute-role"
  assume_role_policy = "${data.template_file.ecstasks_assumerole_template.rendered}"
}

resource "aws_iam_role_policy" "ecs_execute_policy" {
  name = "${local.service_name}-ecs-execute-policy"
  role = "${aws_iam_role.ecs_execute_role.name}"

  policy = "${data.template_file.ecstask_execution_policy_template.rendered}"
}

resource "aws_iam_role" "task_role" {
  name               = "${local.service_name}-ecs-task-role"
  assume_role_policy = "${data.template_file.ecstasks_assumerole_template.rendered}"
}


resource "aws_iam_instance_profile" "iam_instance_profile" {
  name = "${local.rolename}-ec2-instance-profile"
  role = "${aws_iam_role.alf_iam_role.name}"
}


resource "aws_iam_role" "alf_iam_role" {
  name               = "${local.rolename}-ec2-role"
  assume_role_policy = "${file("${path.module}/policies/${local.ec2_policyfile}")}"
  description        = "${local.rolename}"
}

resource "aws_iam_role_policy" "alf_iam_policy" {
  name   = "${local.rolename}-ec2-policy"
  role   = "${aws_iam_role.alf_iam_role.name}"
  policy = "${data.template_file.iam_policy_app_alf_ext.rendered}"
}


data "template_file" "iam_policy_app_alf_ext" {
  template = "${file("${path.module}/policies/${local.alfresco_external_policy_file}")}"

  vars = {
    backups-bucket               = "${local.backups-bucket-name}"
    s3-certificates-bucket       = "${local.s3-certificates-bucket}"
    decryptable_certificate_keys = "${jsonencode(local.keys_decrytable_by_alf_proxy)}"
  }
}

resource "aws_iam_role" "environment" {
  name               = "${local.rolename}-ecs-svc-role"
  assume_role_policy = "${file("${path.module}/policies/${local.ecs_policyfile}")}"
  description        = "${local.rolename}-alfproxy-ext-ecs-svc-role"
}


resource "aws_iam_role_policy" "environment" {
  name   = "${local.rolename}-ecs-svc-policy"
  role   = "${aws_iam_role.environment.name}"
  policy = "${data.template_file.iam_policy_ecs_ext.rendered}"
}


data "template_file" "iam_policy_ecs_ext" {
  template = "${file("${path.module}/policies/${local.ecs_role_policy_file}")}"

  vars = {
    aws_lb_arn = "*"
  }
}
