//TODO this looks useful
//output "newtech_endpoint" {
//  value = "http://offenderapi.${data.terraform_remote_state.ecs_cluster.private_cluster_namespace["domain_name"]}:${var.conf["env_service_port"]}"
//}
//
//
////TODO this looks useful
//output "secure_fqdn" {
//  value = "{aws_route53_record.secure_alb_r53.fqdn}"
//}
//
////TODO this looks useful
//output "fqdn" {
//  value = "{aws_route53_record.alb_r53.fqdn}"
//}




//These dont look useful

output "short_environment_name" {
  value = "${var.short_environment_name}"
}


//TODO this might be useful
//output "aws_param_store_key_names" {
//  value = {
//    spring_datasource_password     = "/${var.environment_name}/${var.project_name}/delius-database/db/delius_pool_password"
//    spring_ldap_password           = "/${var.environment_name}/${var.project_name}/apacheds/apacheds/ldap_admin_password"
//    appinsights_instrumentationkey = "/${var.environment_name}/${var.project_name}/newtech/offenderapi/appinsights_key"
//  }
//}

// Added for debugging
//output "ecs_asg" {
//  value = aws_autoscaling_group.ecs_asg
//}