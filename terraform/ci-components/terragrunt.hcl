remote_state  {
    backend = "s3"

    config = {
      encrypt = true
      bucket  = "${get_env("TG_REMOTE_STATE_BUCKET", "REMOTE_STATE_BUCKET")}"
      key     = "spg/common_stack/${path_relative_to_include()}/terraform.tfstate"
      region  = "${get_env("TG_REGION", "AWS-REGION")}"

      dynamodb_table = "${get_env("TG_ENVIRONMENT_IDENTIFIER", "ENVIRONMENT_IDENTIFIER")}-lock-table"
    }

  generate = {
    path      = "backend.tf"
    if_exists = "overwrite_terragrunt"
   }
  }

  terraform {
    extra_arguments "common_vars" {
      commands = [
        "destroy",
        "plan",
        "import",
        "push",
        "refresh",
      ]

      arguments = [
        "-var-file=../../ci_env_configs/common.tfvars",
        "-var-file=../../ci_env_configs/${get_env("TG_ENVIRONMENT", "ENVIRONMENT")}.tfvars",
        "-var-file=../../config/common.tfvars",
        "-var-file=../../config/${get_env("environment_name", "ENVIRONMENT")}.tfvars"
      ]
    }
  }
