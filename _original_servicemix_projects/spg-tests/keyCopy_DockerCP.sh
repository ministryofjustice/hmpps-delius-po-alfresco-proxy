#workaround as mkdir -p /tmp/tmp_allinone/keyStores/ resulted in subdiirs having root permissions

rm -rf /tmp/tmp_allinone/
mkdir -p /tmp/tmp_allinone/

docker cp spg-container:/opt/spg/servicemix/etc/keystores/  /tmp/tmp_allinone/

#match the current paths in the testing project
mv /tmp/tmp_allinone/keystores /tmp/tmp_allinone/keyStores

