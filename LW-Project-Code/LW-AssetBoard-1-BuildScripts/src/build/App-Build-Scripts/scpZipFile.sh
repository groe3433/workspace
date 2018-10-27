######## QA
echo Copying the Zip File to QA Agent One!
scp /tmp/TOGZipContents.zip wasuser@qusjc01togagtapp01.sjc01.caas.local:/home/wasuser/TOGScripts
echo Copying the Zip File to QA Agent Two!
scp /tmp/TOGZipContents.zip wasuser@qusjc01togagtapp02.sjc01.caas.local:/home/wasuser/TOGScripts
echo Copying the Zip File to QA App Two!
scp /tmp/TOGZipContents.zip wasuser@qusjc01togdomapp02.sjc01.caas.local:/home/wasuser/TOGScripts

######## Preprod
#echo Copying the Zip File to Preprod Agent One!
#scp /tmp/TOGZipContents.zip wasuser@suwdc01togagtapp01.wdc01.caas.local:/home/wasuser/TOGScripts
#echo Copying the Zip File to Preprod Agent Two!
#scp /tmp/TOGZipContents.zip wasuser@suwdc01togagtapp02.wdc01.caas.local:/home/wasuser/TOGScripts
#echo Copying the Zip File to Preprod App Two!
#scp /tmp/TOGZipContents.zip wasuser@suwdc01togdomapp02.wdc01.caas.local:/home/wasuser/TOGScripts