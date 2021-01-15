alfresco_base_url = "https://alfresco.dev.delius-core.probation.hmpps.dsd.io"

service_config_deployment_minimum_healthy_percent = 0

network_and_legacy_spg_remote_state_bucket_name = "tf-eu-west-2-hmpps-delius-core-dev-remote-state"

spg_certificate_path = "/unclassified-data/hmpps-delius-dev/current/"



//Codepipeline vars
branch_name = "develop"
container_name = "alfresco-proxy"
image_name = "hmpps/spgw-alfresco-proxy"
aws_env = "dev"
pre_apply_stack_commands = "dummy-command.sh"
stack_post_build_commands = "afresco-proxy-stack-post-build-commands.sh"
stack_pre_build_environment = "export-alfresco-proxy-environment.sh"
target_app = "@alfresco-proxy"