# ECS Cluster
resource "aws_ecs_cluster" "ecs" {
  name = "${local.service_name}"
  tags = "${merge(var.tags, map("Name", "${local.service_name}"))}"
}

# Host Launch Configuration
resource "aws_launch_configuration" "ecs_host_lc" {
  name_prefix                 = "${local.service_name}"
  associate_public_ip_address = false
  iam_instance_profile        = "${data.terraform_remote_state.iam.iam_policy_iso_ext_app_instance_profile_name}"
  image_id                    = "${data.terraform_remote_state.ecs_cluster.ecs_ami_id}"
  instance_type               = "${var.ecs_instance_type}"

  security_groups = [
    "${data.terraform_remote_state.vpc_security_groups.sg_ssh_bastion_in_id}",
    "${data.terraform_remote_state.security-groups-and-rules.spg_common_outbound_sg_id}"
  ]

  user_data_base64 = "${base64encode(data.template_file.ecs_host_userdata_template.rendered)}"
  key_name         = "${data.terraform_remote_state.vpc.ssh_deployer_key}"

  lifecycle {
    create_before_destroy = true
  }
}

