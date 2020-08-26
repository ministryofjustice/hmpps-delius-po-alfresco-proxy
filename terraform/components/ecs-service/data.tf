# Host userdata template
data "template_file" "ecs_host_userdata_template" {
  template = "${file("${path.module}/templates/bootstrap/ecs-host-userdata.tpl")}"

  vars = {
    ecs_cluster_name  = "${local.service_name}"
    region            = "${var.region}"
    log_group_name    = "${var.environment_name}/spg-ecs-cluster"
    bastion_inventory = "${var.bastion_inventory}"

    ebs_device_name = "/dev/xvdb"
    ebs_encrypted   = "true"
    ebs_volume_size = "150"
    ebs_volume_type = "standard"
    volume_size     = "150"

    data_volume_host_path = "/var/log/"
    data_volume_name      = "spg-log"
  }
}