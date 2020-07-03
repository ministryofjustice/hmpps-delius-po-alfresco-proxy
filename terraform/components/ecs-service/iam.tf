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
