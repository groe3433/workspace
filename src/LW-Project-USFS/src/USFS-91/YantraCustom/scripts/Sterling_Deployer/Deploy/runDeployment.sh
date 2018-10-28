echo "...Creating the $DEPLOY/tmp folder"
mkdir tmp
echo "...Moving the ZIP from $DEPLOY to $DEPLOY/tmp"
cp $1 tmp/.
echo "...Unzipping the ZIP file"
cd tmp
unzip $1
echo "...Changing permission of all unzipped files"
chmod 775 smcfs.ear
chmod 775 customer_overrides.properties.QA
chmod 775 customer_overrides.properties.TRN
chmod 775 customer_overrides.properties.PROD
cd ../
echo "...Deploying Agent Contents"
${ANT_HOME}/bin/ant -f placeAgentContent.xml | /opt/apps/projects/cronolog/sbin/cronolog $DEPLOY/Build_LogFile/%Y-%m-%d/%Y-%m-%d.log &
