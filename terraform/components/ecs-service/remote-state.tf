# Load in VPC state data for subnet placement
data "terraform_remote_state" "vpc" {
  backend = "s3"

  config = {
    bucket = "${var.network_and_legacy_spg_remote_state_bucket_name}"
    key    = "vpc/terraform.tfstate"
    region = "${var.region}"
  }
}

# Load in sub VPC state data for custom domains
data "terraform_remote_state" "sub_vpc" {
  count   = "${var.environment_name == "delius-core-sandpit-2" ? 1 : 0}"
  backend = "s3"

  config = {
    bucket = "${var.remote_state_bucket_name}"
    key    = "hosted-zones-for-additional-sandpit-environments-only/terraform.tfstate"
    region = "${var.region}"
  }
}

# Load in VPC security groups to reference db sgs
data "terraform_remote_state" "vpc_security_groups" {
  backend = "s3"

  config = {
    bucket = "${var.network_and_legacy_spg_remote_state_bucket_name}"
    key    = "security-groups/terraform.tfstate"
    region = "${var.region}"
  }
}

data "terraform_remote_state" "engineering_nat" {
  backend = "s3"

  config = {
    bucket   = "${var.eng_remote_state_bucket_name}"
    key      = "natgateway/terraform.tfstate"
    region   = "${var.region}"
    role_arn = "${var.eng_role_arn}"
  }
}

#TODO remove this and add explicit outbound rules as part of security hardening ticket ALS-500
#SPG Common Security Groups & Rules (Used for common outbound rules
data "terraform_remote_state" "security-groups-and-rules" {
  backend = "s3"

  config = {
    bucket = "${var.network_and_legacy_spg_remote_state_bucket_name}"
    key    = "spg/security-groups-and-rules/terraform.tfstate"
    region = "${var.region}"
  }
}

# Load in shared ECS cluster state file for target cluster arn
data "terraform_remote_state" "ecs_cluster" {
  backend = "s3"

  config = {
    bucket = "${var.remote_state_bucket_name}"
    key    = "spg/common_stack/ecs-cluster/terraform.tfstate"
    region = "${var.region}"
  }
}

#-------------------------------------------------------------
### Getting the IAM details
#-------------------------------------------------------------
data "terraform_remote_state" "iam" {
  backend = "s3"

  config = {
    bucket = "${var.network_and_legacy_spg_remote_state_bucket_name}"
    key    = "spg/iam/terraform.tfstate"
    region = "${var.region}"
  }
}

#-------------------------------------------------------------
### Getting the common details
#-------------------------------------------------------------
data "terraform_remote_state" "common" {
  backend = "s3"

  config = {
    bucket = "${var.network_and_legacy_spg_remote_state_bucket_name}"
    key    = "spg/common/terraform.tfstate"
    region = "${var.region}"
  }
}

data "terraform_remote_state" "kms" {
  backend = "s3"

  config = {
    bucket = "${var.network_and_legacy_spg_remote_state_bucket_name}"
    key    = "spg/kms-certificates-spg/terraform.tfstate"
    region = "${var.region}"
  }
}