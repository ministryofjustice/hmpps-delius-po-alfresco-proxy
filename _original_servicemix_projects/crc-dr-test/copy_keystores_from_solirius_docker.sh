on docker as root
cp /opt/spg/servicemix/etc/keystores/one*.jks /home/vagrant/
chown vagrant:vagrant /home/vagrant/*

on local

scp -i ~/.ssh/id_rsa_insecure -o StrictHostKeyChecking=no -P 22  vagrant@spg-all-200:/home/vagrant/one*.jks  ~/git/spg-parent/spg-parent/crc-dr-test/src/test/resources/etc/keystores
