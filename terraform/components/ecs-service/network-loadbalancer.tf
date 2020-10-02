resource "aws_lb" "environment" {
  name               = "${local.service_name}-nlb"
  internal           = "true"
  load_balancer_type = "network"
  enable_cross_zone_load_balancing = true
  enable_deletion_protection = "false"

  access_logs {
    bucket  = "${var.environment_identifier}-spgw-lb-logs"
    prefix  = "${local.service_name}-nlb"
    enabled = false
    //only logs when tls is enabled
  }

  tags = merge(var.tags, map("Name", "${local.service_name}-lb"))

  lifecycle {
    //TODO should this be set to true (was set to false when using EIPS, this task does not use EIPs)
    create_before_destroy = false
  }

  subnet_mapping {
    subnet_id = data.terraform_remote_state.vpc.outputs.vpc_private-subnet-az1
  }

  subnet_mapping {
    subnet_id = data.terraform_remote_state.vpc.outputs.vpc_private-subnet-az2
  }

  subnet_mapping {
    subnet_id = data.terraform_remote_state.vpc.outputs.vpc_private-subnet-az3
  }
}


resource "aws_lb_target_group" "environment" {
  name                 = "${local.service_name}-tg"
  port                 = var.service_config_service_port
  protocol             = "TCP"
  vpc_id               = data.terraform_remote_state.vpc.outputs.vpc_id
  deregistration_delay = "30"
  target_type          = "ip"

  #stickiness is only valid for ALBs. When NLB is used, it must be explicitly set to false as of 20/03/2019 otherwise terraform trips up
  #see https://github.com/terraform-providers/terraform-provider-aws/issues/2746

  stickiness {
    enabled = false
    type    = "lb_cookie"
  }

  tags = merge(var.tags, map("Name", "${local.service_name}-tg"))
}


resource "aws_lb_listener" "environment_no_https" {
  load_balancer_arn = aws_lb.environment.arn
  port              = var.service_config_service_port
  protocol          = "TCP"

  default_action {
    target_group_arn = aws_lb_target_group.environment.arn
    type             = "forward"
  }
}

//#uses special sub vpc when on sandpit-2
resource "aws_route53_record" "dns_entry_sandpit_2" {
  count   = var.environment_name == "delius-core-sandpit-2" ? 1 : 0
  zone_id = data.terraform_remote_state.sub_vpc[0].outputs.private_zone_id
  name    = local.service_name
  type    = "A"

  alias {
    name                   = aws_lb.environment.dns_name
    zone_id                = aws_lb.environment.zone_id
    evaluate_target_health = false
  }
}

#uses regular vpc when not on sandpit-2
resource "aws_route53_record" "dns_entry" {
  count   = var.environment_name == "delius-core-sandpit-2" ? 0 : 1
  zone_id = data.terraform_remote_state.vpc.outputs.private_zone_id
  name    = local.service_name
  type    = "A"

  alias {
    name                   = aws_lb.environment.dns_name
    zone_id                = aws_lb.environment.zone_id
    evaluate_target_health = false
  }
}


#ASSUMES WIREMOCE WILL NOT BE USED IN SANDPIT-2  (if this changes, repeat the conditional pattern above)
resource "aws_route53_record" "wiremock_public_dns_entry" {
  count   = var.is_wiremock ? 1 : 0
  zone_id = data.terraform_remote_state.vpc.outputs.public_zone_id
  name    = "wiremock-${local.service_name}"
  type    = "A"

  alias {
    name                   = aws_lb.environment.dns_name
    zone_id                = aws_lb.environment.zone_id
    evaluate_target_health = false
  }
}