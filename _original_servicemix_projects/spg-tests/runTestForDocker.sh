#!/bin/sh
# Always copy the keystores to the local /tmp folder (ensure the docker.properties file specifies this pathname)
scp -i ~/.ssh/id_rsa_insecure vagrant@spg-all-200:/opt/spg/servicemix/etc/keystores/*.jks /tmp
scp -i ~/.ssh/id_rsa_insecure src/test/resources/eicar.txt vagrant@spg-all-200:/tmp/

# If you want to run a single test uncommet this line and amend the relevant test class (and comment out the other mvn line below)
#mvn -e test -Dtest=uk.gov.gsi.justice.spg.test.system.fromdelius.Test_Sending_Multiple_Messages_From_Delius_To_CRC -DENVIRONMENT_DATA_FILE_PATH=docker.properties

#
# Run all tests
# The run assert after testing flag means that if this is not set in the maven run the test will assume that you want
# to test in line or at the end of the class as normal rather than at the end of the test run
#
#   -DassertAfterTesting=true
#
#   The profile selection for the test only activates once so if both slow and fast are activated
#   it will activate the first one it comes across in maven which means you will need a run for slow and a
#   run for fast tests to cover all the tests.  Activating both profiles does not run all tests.
#
#   -DdontRunFastTests
#   -DdontRunSlowTests
#
#    the rest of the setup is done using the maven pom file which has two groups defined for fast and slow test
#    and the use of an ElkTestListener which runs the tests at the end and summarises
#
#

if [ "$#" -ne 1 ]; then
  RUN_THIS_ONE="dontRunSlowTests"
else
  RUN_THIS_ONE=$1
fi

mvn -e test -DENVIRONMENT_DATA_FILE_PATH=docker.properties -DassertAfterTesting=true -D$RUN_THIS_ONE -DdontRunPerformanceTests

#fmvn -f ./pom.xml -e test -DENVIRONMENT_DATA_FILE_PATH=docker.properties
#mvn -f ./pom.xml -e test -Dtest=uk.gov.gsi.justice.spg.test.system.ignore.Test_Camel_Threads -DENVIRONMENT_DATA_FILE_PATH=docker.properties
