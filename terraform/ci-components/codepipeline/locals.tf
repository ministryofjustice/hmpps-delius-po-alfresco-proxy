locals {
  prefix = "spgw"

  po_alfresco_proxy_pipeline_name = "${local.prefix}-po-alfresco-proxy-pipeline${local.test_var}"

  iam_role_arn     = data.terraform_remote_state.common.outputs.codebuild_info["iam_role_arn"]
  cache_bucket     = data.terraform_remote_state.common.outputs.codebuild_info["build_cache_bucket"]
  log_group_name   = data.terraform_remote_state.common.outputs.codebuild_info["log_group"]
  artefacts_bucket = data.terraform_remote_state.common.outputs.codebuild_info["artefacts_bucket"]
  tags             = data.terraform_remote_state.common.outputs.tags


  //TODO: remove this var, we want replace real codebuild, once ready
  test_var = "-test"
}