#!/bin/bash

. /home/wasuser/.bash_profile

if [ "$TOG_B2B_NAME" =  "QA" ]
then
	myB2Blocation=OMSExtracts/download/QA
elif [ "$TOG_B2B_NAME" =  "Preprod" ]
then
	myB2Blocation=OMSExtracts/download/Preprod
elif [ "$TOG_B2B_NAME" =  "Prod" ]
then
	myB2Blocation=OMSExtracts/download
fi

##### Approach where we supply our public keys for Agent servers to Chris who will install them on his side.
sftp -b /dev/fd/0 OMSPush@tog.b2b.lightwellinc.com<<EOF
cd $myB2Blocation
get *.*
bye
EOF