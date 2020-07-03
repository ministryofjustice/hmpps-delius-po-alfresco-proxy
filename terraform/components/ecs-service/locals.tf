locals {
  service_name   = "${var.short_environment_name}-spgw-alfproxy"
  container_name = "alfresco-proxy"

  rolename = "${local.service_name}-ext-ec2"

  policyfile = "ec2_policy.json"
  alfresco_external_policy_file = "ec2_alf_external_policy.json"

  backups-bucket-name  = "${data.terraform_remote_state.common.common_s3_backups_bucket}"
  s3-certificates-bucket   = "${data.terraform_remote_state.common.common_engineering_certificates_s3_bucket}"

  keys_decrytable_by_alf_proxy = [
    "${data.terraform_remote_state.kms.certificates_spg_tls_cert_kms_arn}",
    "${data.terraform_remote_state.kms.certificates_spg_signing_cert_kms_arn}"
  ]

  task_placement_expression = "runningTasksCount==0"

  hmpps_asset_name_prefix        = "${var.short_environment_name}"

  private_subnet_ids = [
    "${data.terraform_remote_state.vpc.vpc_private-subnet-az1}",
    "${data.terraform_remote_state.vpc.vpc_private-subnet-az2}",
    "${data.terraform_remote_state.vpc.vpc_private-subnet-az3}",
  ]

  public_subnet_ids = [
    "${data.terraform_remote_state.vpc.vpc_public-subnet-az1}",
    "${data.terraform_remote_state.vpc.vpc_public-subnet-az2}",
    "${data.terraform_remote_state.vpc.vpc_public-subnet-az3}",
  ]

  public_zone_id  = "${data.terraform_remote_state.vpc.public_zone_id}"
  external_domain = "${data.terraform_remote_state.vpc.public_zone_name}"
}
