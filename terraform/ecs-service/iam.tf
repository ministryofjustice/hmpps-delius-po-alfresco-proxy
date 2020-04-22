# Task Execution Role for pulling the image and putting logs to cloudwatch
resource "aws_iam_role" "execute_role" {
  name               = "${local.service_name}-ecs-execute-role"
  assume_role_policy = "${data.template_file.ecstasks_assumerole_template.rendered}"
}

resource "aws_iam_role_policy" "execute_policy" {
  name = "${local.service_name}-ecs-execute-policy"
  role = "${aws_iam_role.execute_role.name}"

  policy = "${data.template_file.policy_template.rendered}"
}


# Task role - TODO currently the old instance roles are being used for tasks, this will be used as part of ALS-473
resource "aws_iam_role" "task_role" {
  name               = "${local.service_name}-ecs-task-role"
  assume_role_policy = "${data.template_file.ecstasks_assumerole_template.rendered}"
}
