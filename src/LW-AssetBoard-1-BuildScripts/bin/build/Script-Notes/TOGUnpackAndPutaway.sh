#!/bin/bash

if [ -f /home/wasuser/TOGScripts/TOGZipContents.zip ] 
then
	now=$(date)
	echo "Current Date/Time: $now" >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
	
	sleep 30
	
	echo /home/wasuser/TOGScripts/TOGZipContents.zip Found! >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
	
	##### Extract zip contents to TOGZipContents folder
	echo Extracting Zip Contents to TOGZipContents folder... >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
	rm -R TOGZipContents
	mkdir TOGZipContents
	unzip TOGZipContents.zip -d /home/wasuser/TOGScripts/TOGZipContents
	chmod 775 -R TOGZipContents
		
	##### Copy TOG.jar to Agent Folder
	echo Copying TOG.jar to Agent Folder... >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
	cp -p /home/wasuser/TOGScripts/TOGZipContents/TOG.jar /opt/ssfs/runtime/jar/TOG/94_1/TOG.jar
	echo Copied TOG.jar to /opt/ssfs/runtime/jar/TOG/94_1 >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
		
	##### Copy resources.jar to Agent Folder
	echo Copying resources.jar to Agent Folder... >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
	cp -p /home/wasuser/TOGScripts/TOGZipContents/resources.jar /opt/ssfs/runtime/jar/platform/9_4/resources.jar
	echo Copied resources.jar to /opt/ssfs/runtime/jar/platform/9_4 >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
		
	##### Copy entities.jar to Agent Folder
	echo Copying entities.jar to Agent Folder... >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
	cp -p /home/wasuser/TOGScripts/TOGZipContents/entities.jar /opt/ssfs/runtime/jar/platform/9_4/entities.jar
	echo Copied entities.jar to /opt/ssfs/runtime/jar/platform/9_4 >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
		
	##### Copy customer_overrides.properties to Sterling Properties folder
	echo Copying customer_overrides.properties to Sterling Properties folder... >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
	cp -p /home/wasuser/TOGScripts/TOGZipContents/customer_overrides.properties /opt/ssfs/runtime/properties/customer_overrides.properties
	echo Copied customer_overrides.properties to /opt/ssfs/runtime/properties >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
	
	rm TOGZipContents.zip
		
	echo ________________________________________________________________________ >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
	echo ________________________________________________________________________ >> /home/wasuser/TOGScripts/TOGUnpackAndPutaway.log
fi