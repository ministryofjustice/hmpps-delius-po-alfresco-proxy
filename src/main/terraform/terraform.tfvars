terragrunt = {
  # Configure Terragrunt to automatically store tfstate files in S3
  remote_state = {
    backend = "s3"

    config {
      encrypt = true
      bucket  = "${get_env("TG_REMOTE_STATE_BUCKET", "REMOTE_STATE_BUCKET")}"
      key     = "spg/alfresco_proxy/${path_relative_to_include()}/terraform.tfstate"
      region  = "${get_env("TG_REGION", "AWS-REGION")}"

      dynamodb_table = "${get_env("TG_ENVIRONMENT_IDENTIFIER", "ENVIRONMENT_IDENTIFIER")}-lock-table"
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
        "-var-file=../../../../env_configs/${get_env("TG_COMMON_DIRECTORY","common")}/common.tfvars",
        "-var-file=../../../../env_configs/${get_env("TG_ENVIRONMENT_NAME", "ENVIRONMENT")}/${get_env("TG_ENVIRONMENT_NAME", "ENVIRONMENT")}.tfvars",
        "-var-file=../../../../env_configs/${get_env("TG_ENVIRONMENT_NAME", "ENVIRONMENT")}/sub-projects/parent-orgs.tfvars",


        #  the old spg vars should end up in the local service-config dir
        #  "-var-file=${get_parent_tfvars_dir()}/env_configs/${get_env("TG_ENVIRONMENT_NAME", "ENVIRONMENT")}/sub-projects/spg.tfvars",
        "-var-file=../../service-config/common.tfvars",
        "-var-file=../../service-config/${get_env("TG_ENVIRONMENT_NAME", "ENVIRONMENT")}.tfvars",
      ]
    }
  }
}
