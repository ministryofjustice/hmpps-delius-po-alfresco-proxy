#workaround as mkdir -p /tmp/tmp_allinone/keyStores/ resulted in subdiirs having root permissions

rm -rf /tmp/tmp_allinone/
mkdir /tmp/tmp_allinone/
mkdir /tmp/tmp_allinone/keyStores/

scp -i src/test/certificates/virtuoso_rsa -o StrictHostKeyChecking=no -P 22  virtuoso@spg-all-200:/opt/spg/servicemix/etc/keystores/*.jks  /tmp/tmp_allinone/keyStores/

