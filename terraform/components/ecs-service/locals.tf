locals {
  service_name   = "${var.short_environment_name}-spgw-alfproxy"
  container_name = "alfresco-proxy"
  rolename = "${var.short_environment_identifier}-${local.service_name}"

  backups-bucket-name  = "${data.terraform_remote_state.common.outputs.common_s3_backups_bucket}"
  s3-certificates-bucket   = "${data.terraform_remote_state.common.outputs.common_engineering_certificates_s3_bucket}"

  keys_decrytable_by_alf_proxy = [
    "${data.terraform_remote_state.kms.outputs.certificates_spg_tls_cert_kms_arn}",
    "${data.terraform_remote_state.kms.outputs.certificates_spg_signing_cert_kms_arn}"
  ]

  task_placement_expression = "runningTasksCount==0"

  ec2_policyfile = "ec2_policy.json"
  ecs_policyfile = "ecs_policy.json"
  ecs_role_policy_file = "ecs_role_policy.json"
  alfresco_external_policy_file = "ec2_alf_external_policy.json"

  hmpps_asset_name_prefix        = "${var.short_environment_name}"

  private_subnet_ids = [
    "${data.terraform_remote_state.vpc.outputs.vpc_private-subnet-az1}",
    "${data.terraform_remote_state.vpc.outputs.vpc_private-subnet-az2}",
    "${data.terraform_remote_state.vpc.outputs.vpc_private-subnet-az3}",
  ]

  public_subnet_ids = [
    "${data.terraform_remote_state.vpc.outputs.vpc_public-subnet-az1}",
    "${data.terraform_remote_state.vpc.outputs.vpc_public-subnet-az2}",
    "${data.terraform_remote_state.vpc.outputs.vpc_public-subnet-az3}",
  ]

  public_zone_id  = "${data.terraform_remote_state.vpc.outputs.public_zone_id}"
  external_domain = "${data.terraform_remote_state.vpc.outputs.public_zone_name}"
}
