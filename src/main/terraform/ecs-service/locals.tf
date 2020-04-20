locals {
  service_name   = "${var.short_environment_name}-spgw-alfproxy"
  container_name = "alfresco-proxy"

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

  public_certificate_arn = "${data.aws_acm_certificate.cert.arn}"
}
