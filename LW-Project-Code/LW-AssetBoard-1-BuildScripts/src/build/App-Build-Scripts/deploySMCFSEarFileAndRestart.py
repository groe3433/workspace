def getOptions(cellName, nodeName, nodeName2, appInfo, ctxRoot = None):
    options = [ '-nopreCompileJSPs',
                '-distributeApp',
                '-nouseMetaDataFromBinary',
                '-deployejb',
                '-createMBeansForResources',
                '-noreloadEnabled',
                '-nodeployws',
                '-validateinstall warn',
                '-noprocessEmbeddedConfig',
                '-filepermission .*\.dll=755#.*\.so=755#.*\.a=755#.*\.sl=755',
                '-noallowDispatchRemoteInclude',
                '-noallowServiceRemoteInclude',
                '-cluster',appInfo["cluster"],
                '-appname', appInfo["appName"],
                '-MapWebModToVH', [['.*', '.*', 'default_host']],
				'-MapModulesToServers', [['.*', '.*', 'WebSphere:cell='+cellName+',node='+nodeName+',server='+appInfo["webserverName"]+'+WebSphere:cell='+cellName+',node='+nodeName2+',server='+appInfo["webserverName"]+'+WebSphere:cell='+cellName+',cluster='+appInfo["cluster"]]]
            ]
    if ctxRoot != None:
        options.append('-contextroot')
        options.append(ctxRoot)
    return options
 
def isAppExists(appName):
    return len(AdminConfig.getid("/Deployment:" + appName + "/" )) > 0
 
def stopApp(nodeName, serverName, appName):
    try:
        print 'Stopping Application "%s" on "%s/%s"...' %(appName, nodeName, serverName)
        appMgr = AdminControl.queryNames("type=ApplicationManager,node="+nodeName+",process="+serverName+",*" )
        AdminControl.invoke(appMgr, 'stopApplication', appName)
        print 'Application "%s" stopped on "%s/%s"!' %(appName, nodeName, serverName)
    except:
        print("Ignoring error - %s" % sys.exc_info())
 
def startApp(nodeName, serverName, appName):
    print 'Starting Application "%s" on "%s/%s"...' %(appName, nodeName, serverName)
    appMgr = AdminControl.queryNames("type=ApplicationManager,node="+nodeName+",process="+serverName+",*" )
    AdminControl.invoke(appMgr, 'startApplication', appName)
    print 'Application "%s" started "%s" on "%s/%s"!' %(appName, nodeName, serverName)
 
def removeApp(appName):
    print 'Removing Application "%s"...' %(appName)
    AdminApp.uninstall(appName)
    print 'Application "%s" removed successfully!' %(appName)
 
def synchronizeNode(nodeName):
    print 'Synchronizing node "%s"...' %(nodeName)
    AdminControl.invoke(AdminControl.completeObjectName('type=NodeSync,node='+nodeName+',*'), 'sync')
    print 'Node "%s" synchronized successfully!' %(nodeName)
 
def startServer(serverName, nodeName):
    print 'Starting server "%s" on node "%s"...' %(serverName, nodeName)
    AdminControl.startServer(serverName, nodeName)        
    print 'Server "%s" started successfully on node "%s"!' %(serverName, nodeName)
 
def stopServer(serverName, nodeName):
    print 'Stopping server "%s" on node "%s"...' %(serverName, nodeName)
    AdminControl.stopServer(serverName, nodeName)        
    print 'Server "%s" stopped successfully on node "%s"!' %(serverName, nodeName)
 
def restartServer(serverName, nodeName):
    stopServer(serverName, nodeName)
    startServer(serverName, nodeName)    
 
def installApp(location, options):
    print 'Installing application from "%s" ...' %(location)
    AdminApp.install(location, options)
    print 'Successfully installed application "%s"' %(location)
 
def save():
    print 'Saving the changes...'
    AdminConfig.save()
    print 'Changes saved successfully.'
 
if __name__ == '__main__':    
    if len(sys.argv) < 4:
        print 'ERROR: Not enough information to execute this script'
        print 'deploy.py app_name ear_file_path node_name server_name virtual_host'
        exit()
 
    print 'Initializing...'
    cellName = AdminConfig.showAttribute(AdminConfig.list('Cell'), 'name')
    appInfo = { "appName"    	: sys.argv[0],
                "filePath"   	: sys.argv[1], 
                "nodeName"   	: sys.argv[2], 
                "serverName" 	: sys.argv[3], 
                "virtualHost"	: sys.argv[4],
                "cluster"    	: sys.argv[5],
                "webserverName" : sys.argv[6],
                "nodeName2"		: sys.argv[7]}
 
    options = getOptions(cellName, appInfo["nodeName"], appInfo["nodeName2"], appInfo)
    print 'Completed the initialization successfully.'
 
    if isAppExists(appInfo["appName"]):
		stopServer(appInfo["serverName"], appInfo["nodeName"])
		stopServer(appInfo["serverName"], appInfo["nodeName2"])  
		removeApp(appInfo["appName"])
		save()
     
    installApp(appInfo["filePath"], options)
    save()
 
    synchronizeNode(appInfo["nodeName"])
    synchronizeNode(appInfo["nodeName2"])    
    startServer(appInfo["serverName"], appInfo["nodeName"])
    startServer(appInfo["serverName"], appInfo["nodeName2"])    