cd $YFS_HOME/bin
nohup ./startIntegrationServer.sh $1 -DICBSSERVER=$1 $2 | /opt/apps/projects/cronolog/sbin/cronolog /opt/apps/projects/Sterling/logs/IntegLogs/%Y-%m-%d/$1.log &

