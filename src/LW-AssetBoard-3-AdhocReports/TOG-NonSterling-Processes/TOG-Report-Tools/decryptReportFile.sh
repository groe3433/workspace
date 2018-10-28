#!/bin/bash

fileToProcess=$1;
tempName=$fileToProcess.txt;
decryptPassword="TogAESEncryption256";

#Used to create a backup for encrypted report
cp $fileToProcess $fileToProcess.EncBKup


openssl aes-256-cbc -d -a -in $1 -out $tempName -k $decryptPassword;

#Renameing encrypted file to original file name
mv $tempName $fileToProcess;
chmod 775 $fileToProcess