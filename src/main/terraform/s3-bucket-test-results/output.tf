# S3 Buckets
output "s3_spg_ci_build_test_results_bucket" {
  value = "${module.s3_spg_ci_build_test_results_bucket.s3_bucket_name}"
}
