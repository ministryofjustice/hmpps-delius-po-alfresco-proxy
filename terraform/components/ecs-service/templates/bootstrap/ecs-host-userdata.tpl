#!/bin/bash
set -x

# Mount our EBS volume on boot
cp /usr/share/zoneinfo/Europe/London /etc/localtime

mkdir -p ${data_volume_host_path}

pvcreate ${ebs_device_name}

vgcreate data ${ebs_device_name}

lvcreate -l100%VG -n ${data_volume_name} data

mkfs.xfs /dev/data/${data_volume_name}

echo "/dev/mapper/data-${data_volume_name} ${data_volume_host_path} xfs defaults 0 0" >> /etc/fstab

mount -a

# Install additional packages
sudo yum install -y jq awslogs
# Install and start SSM Agent service - will always want the latest - used for remote access via aws console/cli
# Avoids need to manage users identity in 2 places and install ansible/dependencies
sudo yum install -y https://s3.amazonaws.com/ec2-downloads-windows/SSMAgent/latest/linux_amd64/amazon-ssm-agent.rpm
sudo systemctl enable amazon-ssm-agent
sudo systemctl start amazon-ssm-agent

# Install any docker plugins

# Set any ECS agent configuration options
echo "ECS_CLUSTER=${ecs_cluster_name}" >> /etc/ecs/ecs.config
# Block tasks running in awsvpc mode from calling host metadata
echo "ECS_AWSVPC_BLOCK_IMDS=true" >> /etc/ecs/ecs.config
# Required for ecs tasks in awsvpc mode to pull images remotely
echo "ECS_ENABLE_TASK_ENI=true" >> /etc/ecs/ecs.config

# Inject the CloudWatch Logs configuration file contents
export INSTANCE_ID="`curl http://169.254.169.254/latest/meta-data/instance-id`"
cat > /etc/awslogs/awslogs.conf <<- EOF
[general]
state_file = /var/lib/awslogs/agent-state

[/var/log/dmesg]
file = /var/log/dmesg
log_group_name = ${log_group_name}
log_stream_name = $INSTANCE_ID/dmesg

[/var/log/messages]
file = /var/log/messages
log_group_name = ${log_group_name}
log_stream_name = $INSTANCE_ID/messages
datetime_format = %b %d %H:%M:%S

[/var/log/docker]
file = /var/log/docker
log_group_name = ${log_group_name}
log_stream_name = $INSTANCE_ID/docker
datetime_format = %Y-%m-%dT%H:%M:%S.%f

[/var/log/ecs/ecs-init.log]
file = /var/log/ecs/ecs-init.log
log_group_name = ${log_group_name}
log_stream_name = $INSTANCE_ID/ecs-init
datetime_format = %Y-%m-%dT%H:%M:%SZ

[/var/log/ecs/ecs-agent.log]
file = /var/log/ecs/ecs-agent.log.*
log_group_name = ${log_group_name}
log_stream_name = $INSTANCE_ID/ecs-agent
datetime_format = %Y-%m-%dT%H:%M:%SZ

[/var/log/ecs/audit.log]
file = /var/log/ecs/audit.log.*
log_group_name = ${log_group_name}
log_stream_name = $INSTANCE_ID/ecs-audit
datetime_format = %Y-%m-%dT%H:%M:%SZ

EOF

# Set the region to send CloudWatch Logs data to (the region where the container instance is located)
sed -i -e "s/region = us-east-1/region = ${region}/g" /etc/awslogs/awscli.conf

# Start the awslogs service
sudo systemctl enable awslogsd.service
sudo systemctl start awslogsd

# ECS service is started by cloud-init once this userdata script has returned


#from original project

#TODO remove this login if not required
#this ecr login wasn't on johns set up, so probably not needed


# Set the region to send CloudWatch Logs data to (the region where the container instance is located)
region=$(curl -s 169.254.169.254/latest/dynamic/instance-identity/document | jq -r .region)
sed -i -e "s/region = us-east-1/region = $region/g" /etc/awslogs/awscli.conf

# log into AWS ECR
aws ecr get-login --no-include-email --region $region  --registry-ids 895523100917


#ansible and users
sudo -i

yum install -y \
    git \
    wget \
    yum-utils

echo 'preppip' > /tmp/paul.log

easy_install pip

PATH=/usr/local/bin:$PATH


pip install ansible==2.6 virtualenv awscli boto botocore boto3

echo 'downloading users - may need to apply some other settings to ensure users are able to read and write to spg group, ie change config'
/usr/bin/curl -o ~/users.yml https://raw.githubusercontent.com/ministryofjustice/hmpps-delius-ansible/master/group_vars/${bastion_inventory}.yml
sed -i '/users_deleted:/,$d' ~/users.yml
cat << EOF > ~/requirements.yml
---

- name: users
  src: singleplatform-eng.users
EOF
 cat << EOF > ~/bootstrap-users.yml
---

- hosts: localhost
  vars_files:
   - "{{ playbook_dir }}/users.yml"
  roles:
     - users
EOF
cat << EOF > /etc/sudoers.d/webops
# Members of the webops group may gain root privileges
%webops ALL=(ALL) NOPASSWD:ALL

Defaults  use_pty, log_host, log_year, logfile="/var/log/webops.sudo.log"
EOF
echo 'creating users'
ansible-galaxy install -f -r ~/requirements.yml
ansible-playbook ~/bootstrap-users.yml

cat << 'EOF' >> ~/update_ssh_users_from_github.sh

/usr/bin/curl -o ~/users.yml https://raw.githubusercontent.com/ministryofjustice/hmpps-delius-ansible/master/group_vars/${bastion_inventory}.yml
ansible-playbook ~/bootstrap-users.yml

EOF