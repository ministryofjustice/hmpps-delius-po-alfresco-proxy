
#mvn -f .. -e clean install
#mvn -f . -e clean install test -DENVIRONMENT_DATA_FILE_PATH=all-in-one.local.properties -Dmaven.test.failure.ignore=false
scp -i ~/.ssh/id_rsa_insecure vagrant@spg-all-200:/opt/spg/servicemix/etc/keystores/*.jks /tmp
scp -i ~/.ssh/id_rsa_insecure src/test/resources/eicar.txt vagrant@spg-all-200:/tmp/
#mvn test -Dtest=Test_Sending_Unstructured_Data#testUploadNewVirus
mvn -f .. -e clean install
mvn -f . -e clean install test -DENVIRONMENT_DATA_FILE_PATH=all-in-one.local.properties -Dmaven.test.failure.ignore=false

