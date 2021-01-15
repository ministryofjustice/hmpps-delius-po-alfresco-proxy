module "spg-po-alfresco-proxy-pipeline" {
  source            = "git::https://github.com/ministryofjustice/hmpps-delius-spg-codepipeline.git//terraform/ci-components/codepipeline?ref=main"
  environment_name  = "hmpps-sandpit"
  approval_required = false
  artefacts_bucket  = local.artefacts_bucket
  pipeline_name     = local.po_alfresco_proxy_pipeline_name
  iam_role_arn      = local.iam_role_arn
  log_group         = local.log_group_name
  tags              = local.tags
  cache_bucket      = local.cache_bucket
  codebuild_name    = local.po_alfresco_proxy_pipeline_name
  github_repositories = {
    SourceArtifact            = ["hmpps-delius-po-alfresco-proxy", var.branch_name]
    SourceSmokeTestsArtifacts = ["hmpps-delius-spg-testing-smoke-tests", "main"]
  }
  stages = [
    {
      name = "Build-Application"
      actions = [
        {
          action_name      = "Build-Application"
          codebuild_name   = "spgw-java-application-builder${local.test_var}"
          input_artifacts  = "SourceArtifact"
          output_artifacts = "BuildApplicationArtifacts"
          namespace        = "BuildApplicationVariables"
          action_env       = null
        }
      ]
    },
    {
      name = "Build-Docker-Image"
      actions = [
        {
          action_name      = "Build-Docker-Image"
          codebuild_name   = "spgw-docker-image-builder${local.test_var}"
          input_artifacts  = "BuildApplicationArtifacts"
          output_artifacts = "BuildDockerImageArtifacts"
          namespace        = "BuildDockerImageVariables"
          action_env       = null
        }
      ]
    },
    {
      name = "Pre-Build-Stack"
      actions = [
        {
          action_name      = "Pre-Build-Stack"
          codebuild_name   = "spgw-pre-stack-builder${local.test_var}"
          input_artifacts  = "SourceArtifact"
          output_artifacts = "PreBuildStackArtifacts"
          namespace        = "PreBuildStackVariable"
          action_env       = null
        }
      ]
    },
    {
      name = "Build-Stack"
      actions = [
        {
          action_name      = "Build-Stack"
          codebuild_name   = "spgw-stack-builder-0-12${local.test_var}"
          input_artifacts  = "PreBuildStackArtifacts"
          output_artifacts = "BuildStackArtifacts"
          namespace        = null
          action_env = jsonencode(
            [
              {
                "name" : "container_name",
                "value" : var.container_name,
                "type" : "PLAINTEXT"
              },
              {
                "name" : "IMAGE_NAME",
                "value" : var.image_name,
                "type" : "PLAINTEXT"
              },
              {
                "name" : "my_aws_env",
                "value" : var.aws_env,
                "type" : "PLAINTEXT"
              },
              {
                "name" : "PRE_APPLY_STACK_COMMANDS",
                "value" : var.pre_apply_stack_commands,
                "type" : "PLAINTEXT"
              },
              {
                "name" : "STACK_POST_BUILD_COMMANDS",
                "value" : var.stack_post_build_commands,
                "type" : "PLAINTEXT"
              },
              {
                "name" : "STACK_PRE_BUILD_ENVIRONMENT",
                "value" : var.stack_pre_build_environment,
                "type" : "PLAINTEXT"
              },
            ]
          )
        }
      ]
    },
    {
      name = "Deploy-Application"
      actions = [
        {
          action_name      = "Deploy-Application"
          codebuild_name   = "spgw-application-deployer${local.test_var}"
          input_artifacts  = "BuildDockerImageArtifacts"
          namespace        = "DeployApplicationVariables"
          output_artifacts = "DeployApplicationArtifacts"
          action_env       = null
        }
      ]
    },
    {
      name = "Clean-Up-Image-Repository"
      actions = [
        {
          action_name      = "Clean-Up-Image-Repository"
          codebuild_name   = "spgw-ecr-cleaner${local.test_var}"
          input_artifacts  = "BuildDockerImageArtifacts"
          output_artifacts = "CleanUpImageRepositoryArtifacts"
          namespace        = null
          action_env       = null
        }
      ]
    },
    {
      name = "Run-Smoke-Tests"
      actions = [
        {
          action_name      = "Run-Smoke-Tests"
          codebuild_name   = "spgw-smoke-tests-runner${local.test_var}"
          input_artifacts  = "SourceSmokeTestsArtifacts"
          namespace        = "RunSmokeTestsVariables",
          output_artifacts = "RunSmokeTestsArtifacts"
          action_env = jsonencode(
            [
              {
                "name" : "target_app",
                "value" : var.target_app,
                "type" : "PLAINTEXT"
              }
            ]
          )
        }
      ]
    }
  ]
}