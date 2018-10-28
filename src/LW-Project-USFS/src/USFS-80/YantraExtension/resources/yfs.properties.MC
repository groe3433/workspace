# Copyright 2006, Sterling Commerce, Inc. All rights reserved.
##---------------------------------------------------------------------------
# Yantra Application Property File 
##---------------------------------------------------------------------------
##---------------------------------------------------------------------------
## Application Parameters
##---------------------------------------------------------------------------

##### Powerplay Analytics #####
# Launch URI:Uncomment the line below and replace the <machine> with the Cognos Upfront installation machine name/ip address
# Do not change anything else in the URI, else Analytics access will fail.
#analytics.portal.url=http://<machine>/cognos/cgi-bin/login.cgi?signon=#USERID#&password=#PASSWORD#&return_url=upfcgi.exe
#####

##### Reportnet Analytics #####
# Launch URI:Uncomment the line below and replace the <machine> with the Cognos ReportNet installation machine name/ip address
# Do not change anything else in the URI, else ReportNet access will fail.
#analytics.reportnet.url=http://<machine>/crn/cgi-bin/cognos.cgi
#####


##### Analytics Namespace#####
#This indicates the namespace that has been configured in COGNOS ReportNet authentication, against which the users would be autheticated
#
#analytics.namespace=<yantra>
#####

# # # # #
# This indicates whether or not your system supports COM. If COM is enabled, you will be 
# able to configure actions to call COM objects. Valid values are Y and N.

# # # # #

yfs.comsupport=Y

# # # # #
# This enables the ability in the configurator or health monitor to popup a message asking 
# the user to log back via the yantra console. Valid Values are Y and N. 
# Default value is N.
# # # # #

#yfs.config.password.noprompt=N

#-----------------------------------------------------------------------------------------------
# Yantra Health Monitor properties. The health monitor will raise the configured alerts
# when the api/appServer response time or the agent Pending Job count crosses the threshold.
#
# api threshold in milli-seconds, this is the system default value used for average response time 
# for appservers.  If this value is not specified 20000 milli-sec. will be assumed.
#yantra.hm.api.threshold=

# this is the system default value used for the pending jobs size for agents.  
# If this value is not specified 10000 will be assumed.
#yantra.hm.agent.threshold=

# appserver threshold in milli-seconds, this is the system default value used for average response 
# time for api's/services.  If this value is not specified 8000 milli-sec. will be assumed.
#yantra.hm.appserver.threshold=

# Health Monitor purge interval, this is the system default value used for purging heartbeaat and 
# snapshot records. if this value is not specified 30 days purge interval is assumed.
#yantra.hm.purge.interval=

#---------------------------------------------------------------------------------------------------

#---------------------------------------------------------------------------
# Database Logon Parameters
#---------------------------------------------------------------------------

# # # # #
# Database type. "oracle", "db2" and "sqlserver" are the only supported values
# for this property at this time.
# yfs.dblogin.dbtype
# # # # #

yfs.dblogin.dbtype=oracle


# # # # #
# JDBC driver class name to use. This is used only for non-pooled connections.
# For Oracle, specify this as "oracle.jdbc.OracleDriver" (or any other suitable driver) without quotation marks.
# For SQLServer, specify this as "com.inet.tds.TdsDriver" (or any other suitable driver) without quotation marks.
# For DB2, specify this as "com.ibm.db2.jcc.DB2Driver" (or any other suitable driver) without quotation marks.
# yfs.dblogin.driverclass
# # # # #

yfs.dblogin.driverclass=oracle.jdbc.OracleDriver
#yfs.dblogin.driverclass=com.ibm.db2.jcc.DB2Driver
#yfs.dblogin.driverclass=com.inet.tds.TdsDriver



# # # # #
# Database connection and JDBC driver configuration
# yfs.dblogin.jdbcurl
# # # # #

# For Oracle
yfs.dblogin.jdbcurl=jdbc:oracle:thin:@10.117.15.101:1521:yantradv
# For SqlServer
#yfs.dblogin.jdbcurl=jdbc:inetdae7:<Database Server HostName/IPaddress>:<Port Number>?<database=Database Name>
# For DB2
#yfs.dblogin.jdbcurl=jdbc:db2://<Database Server HostName/IPAddress>:<portNumber>/<Database Name>

# # # # #
# User ID to connect to the database
# yfs.dblogin.userid
# # # # #

yfs.dblogin.userid=nwcg_mc

# # # # #
# Password for the User ID specified in yfs.dblogin.userid
# yfs.dblogin.password
# # # # #

yfs.dblogin.password=nwcg_mc$098

# # # # #
# The database schema for the Yantra installation (if different from the userid being used) 
# yfs.dblogin.yantraschema.name
# # # # #
#yfs.dblogin.yantraschema.name=<SchemaName>

#############
# This property is required only when database is Oracle and you have integration with Yantra DCS.
# Set this to the current value of cursor_sharing parameter in your database. You can find it in v$parameter table of oracle.
# When not set, default value is FORCE.
#############
#
#yfs.cursor.sharing.mode.dcm=
#

#############
# Setting this property to Y will prevent dead locking on YFS_Inventory_Item table during order creation and when sychronizing changes
# from a shipment back to the order.  This parameter is only used if the order contains a logical kit, or when a shipment contains shipment
# lines from different orders.
# Setting this property to Y will result into holding lock on YFS_Inventory_Item table for longer period. So, this may degrade
# overall system performance. Because of this reason, you should set this property to Y only if you are getting excessive dead locks
# on YFS_Inventory_Item table.
#############
#
#yfs.inventory.sortandlock=
#


# # # # #
# To enable database connection pooling, create the pool, configure the data source 
# entry for JNDI, and specify it here. (Refer to your application server vendor's manuals 
# for details on connection pool management.)
# yfs.dblogin.datasource.name
# # # # #

yfs.dblogin.datasource.name=jdbc/yantradvsource

# # # # #
# To manage database connection pooling, while running Interoperability Agents
# specify the following properties:
# # # # #

# The interoperability agent will close a pooled connection if it has not been
# used for the timeout specified by the following property. This property is specified 
# in seconds. The default is 10*60 seconds.

#yfs.context.timeout=


# This is the frequency at which the connection reaper thread will examine
# the connection pool for connections that are eligible for being closed.
# The eligilibilty will however be determined by the previous property
# The following property is specified in seconds. The default is 10*60
# seconds.

#yfs.context.reaptime

# This is the frequency at which the JMS/SMTP connection reaper thread will examine
# the connection pool for connections that are eligible to be closed.
# The connection will be eligible to be closed if the connection is idle for the
# number of seconds specified for the reaptime.
# The following property is specified in seconds. The default is 10*60 seconds.
#yfs.smtp.session.reaptime=
#yfs.jms.session.reaptime=

# To disable SMTP connection pooling, set this property to false.
# By default this property is set to true.
# Yantra recommends true value to this property for better performance reasons.
#yfs.smtp.connectionpool.enable=

# Specifies the JMS receive timeout value (in milliseconds).
# The JMS Receiver receives the next message that arrives within the specified timeout interval. 
# The receive call blocks until a message arrives, the timeout expires, or this message consumer is closed. 
# A timeout of zero never expires, and the call blocks indefinitely.
# Default Value: 1800000 milliseconds (30 minutes)
#yantra.jms.receive.timeout


#---------------------------------------------------------------------------
# Single Signon Parameters
#---------------------------------------------------------------------------

# # # # #
# The class that handles Single Signon.
# Refer to the javadocs for the com.yantra.ycp.japi.util.YCPSSOManager interface for information 
# about how to write your own implementation for this class.
# yfs.login.singlesignon.class
# # # # #

#yfs.security.singlesignon.enabled=Y
#yfs.login.singlesignon.class=

# If the following property is set to Y, each request is validated against the singleSignOn Server.
# for the user authentication.
# If the property is set to N, user authentication against the singleSignOn Server
# is done only when the session times out.
#yfs.login.singlesignon.checkuser=

# # # # #
# This property must be set for the purge programs to run.
# It contains the absolute path to the directory where purge logs will be written.
# yfs.purge.path
# # # # #

yfs.purge.path=<full path of log directory>


# # # # #
# Property to handle logging.
# This property points to the location of the log4j configuration xml file.
# # # # #
log4j.configuration=/resources/log4jconfig.xml

#--------------------------------------------------------------------------------------------
# AgentServer Override for provider url: set the property to the provider URL which will 
# be used by ALL Agent servers. This property does not impact integration servers configured in the service
# builder. This property overrides the url configured for time triggered transactions in config.
#---------------------------------------------------------------------------------------------
#yfs.agent.override.providerurl=t3://<ip Address>:<port>
yfs.agent.override.providerurl=corbaloc:iiop:10.117.15.101:2811

#--------------------------------------------------------------------------------------------
# AgentServer Override for InitialContextFactory Name: set the property to the InitialContextFactory Name
# which will be used by ALL Agent servers. This property does not impact integration servers 
# configured in the service builder. This property overrides the InitialContextFactory configured 
# for time triggered transactions in config.
#---------------------------------------------------------------------------------------------
#yfs.agent.override.icf=<InitialContextFactory Name>
yfs.agent.override.icf=com.ibm.websphere.naming.WsnInitialContextFactory

#--------------------------------------------------------------------------------------------
# AgentServer Override for QueueConnectionFactory Name: set the property to the QueueConnectionFactory Name
# which will be used by ALL Agent servers. This property does not impact integration servers 
# configured in the service builder. This property overrides the QueueConnectionFactory configured 
# for time triggered transactions in config.
#---------------------------------------------------------------------------------------------
#yfs.agent.override.qcf=<QueueConnectionFactory Name>
yfs.agent.override.qcf=QCF

#--------------------------------------------------------------------------------------------
# AgentServer Override for RetryCount: set the property to the JMS RetryCount to be used by
# ALL Agent Servers. This property does not impact integration servers configured in the
# service builder.
#--------------------------------------------------------------------------------------------
#yfs.agent.override.retryCount=<Number of Retries>

#--------------------------------------------------------------------------------------------
# AgentServer Override for RetryInterval: set the property to the JMS RetryInterval to be
# used by ALL Agent Servers. This property does not impact integration servers configured in
# the service builder. This interval is specified in milliseconds.
#--------------------------------------------------------------------------------------------
#yfs.agent.override.retryInterval=<RetryInterval in Milliseconds>

#--------------------------------------------------------------------------------------------
# AgentServer Backup for JMS. Set the folowing three properties below to the JMSprovider URL 
# InitialContextFactory Name and QueueConnectionFactory Name which will 
# be used by ALL Agent servers as a Backup if the primary JMSServer becomes unavailable, the
# QueueName used by the Agent Server on the backup JMSServer will be the same as the one configured
# on the primary JMSServer.
# These properties will be used only after a successful start of the Agent Server using the
# primary JMSServer configuration.
# If the AgentServer Override properties are specified, they are used as the primary JMSServer properties.
# The backup JMSServer will be used by the Agent Server only if all three properties
# are specified.
# This property does not impact integration servers configured in the service
# builder. 
#---------------------------------------------------------------------------------------------
#yfs.agent.backup.providerurl=t3://<ip Address>:<port>
#yfs.agent.backup.icf=<InitialContextFactory Name>
#yfs.agent.backup.qcf=<QueueConnectionFactory Name>
#yfs.agent.backup.retryCount=<Number of times to retry a failed JMS connection, defaults to 0>
#yfs.agent.backup.retryInterval=<Number of milliseconds to wait between retries, defaults to 0>
#--------------------------------------------------------------------------------------------


#--------------------------------------------------------------------------------------------
# The agent overrides to override the JMS Security parameter values specified in the Agent critera.
#--------------------------------------------------------------------------------------------
#yfs.agent.override.auth.enabled=<Valid Values are Y or N>
# If this value is set to Y then the following properties (userid and password) must be present,   
# otherwise an error is thrown.

#yfs.agent.override.auth.userid=
#yfs.agent.override.auth.password=
#--------------------------------------------------------------------------------------------


#--------------------------------------------------------------------------------------------
# The flow overrides to override the JMS Security parameter values specified in the service 
# definition framework.
#--------------------------------------------------------------------------------------------
#yfs.flow.override.auth.enabled=<Valid Values are Y or N>
# If this value is set to Y then the following properties (userid and password) must be present,   
# otherwise an error is thrown.

#yfs.flow.override.auth.userid=
#yfs.flow.override.auth.password=
#--------------------------------------------------------------------------------------------


#--------------------------------------------------------------------------------
# Extensible/Customizable Components (To override the default, specify your class name.)
#--------------------------------------------------------------------------------


# # # # #
# The class that handles encryption and decryption of credit card numbers.
# Refer to the javadocs for the YCPEncrypter interface for information about how to write your 
# own encrypter class.
# If this property is not specified, then no encryption will be performed.
# # # # #

#yfs.encrypter.class=

# This class will be used for encrypting and decrypting properties specified in 
# yfs.properties, yiclient.properties and management.properties files.
# All properties which end with  ".encrypted" are automatically decrypted
# using this class at runtime. use this property to encrypt critical data like user/password.
# for example the property "yfs.dblogin.userid=<DB-User>"  and "yfs.dblogin.password=<password>"
# could be specified as 
# "yfs.dblogin.userid.encrypted=<encrypted-db-user>  
# "yfs.dblogin.password.encrypted=<encrypted-db-password>  
# Refer to the javadocs for the YCPEncrypter interface for information about how to write your own 
# property encrypter class. 

#yfs.propertyencrypter.class=

# # # # #
# The class that handles automated e-mail communication to and from this application.
# yfs.emailer.class
# # # # #


yfs.emailer.class=com.yantra.util.YFCEmailerImpl

yfs.email.template.encoding=UTF-8


# # # # #
# This class can be used to provide a custom URIResolver during XSL processing.
# Refer to the Java documentation for more information on this interface.
# If this property is not present, then the default implementation provided by
# the XSL processor will be used.
# 
# If set to com.yantra.interop.util.YantraDefaultURIResolver, then an attempt
# will be made to resolve the URI within the classpath.
#
# If set to another class name, this class will be used in place of the
# YantraDefaultURIResolver. The class given here must implement the
# javax.xml.transform.URIResolver interface.
# # # # #

yfs.xsl.uriresolver=com.yantra.interop.util.YantraDefaultURIResolver

#---------------------------------------------------------------------------
# Installation locale code
# This localecode has to match the underlying OS default timezone where the 
# database is installed to avoid timezone calculation errors.
#---------------------------------------------------------------------------
yfs.install.localecode=en_US_EST

#---------------------------------------------------------------------------
# set this property to "N" if allocation should not optimize releases based on ReqShipDate.
#---------------------------------------------------------------------------
yfs.install.applyshipdateoptimization=Y

#---------------------------------------------------------------------------
# Installation
# Set this property to "Y" in installs using fractional quantities for order lines (Currently, it is supported for APIs only)
#---------------------------------------------------------------------------
yfs.install.displaydoublequantity=Y

#---------------------------------------------------------------------------
# Installation
# Set this property to "Y" to use the old date format prior to Yantra 5.5 version.
#---------------------------------------------------------------------------
yfs.install.date.bcmode=N


#---------------------------------------------------------------------------
# Externalized User Authentication Parameters
# --------------------------------------------
# These parameters are required only if you use external user authentication.
#---------------------------------------------------------------------------

# # # # #
# The class that will be invoked for user authentication.
# Uncomment and change the following only if you *DO NOT* want to use application authentication.
# Refer to the javadocs for the YFSAuthenticator interface for information about how to write your 
# own implementation for this class.
# If you want to use the default implementation for LDAP authentication, 
# uncomment the property and set it to com.yantra.yfs.util.YFSLdapAuthenticator
# yfs.security.authenticator
# # # # #

yfs.security.authenticator=

# # # # #
# LDAP Configuration...
# yfs.security.ldap.factory - The LDAP context factory classname as specified in your LDAP Server configuration.
# yfs.security.ldap.url     - The URL for accessing your LDAP Server as specified in your LDAP Server configuration.
# yfs.security.ldap.ou      - The value specified for the organizational unit in your LDAP Server configuration.
# yfs.security.ldap.o       - The value specified for the organization in your LDAP Server configuration.
# Uncomment and change the following properties only if you use the default LDAP authentication implementation.

#yfs.security.ldap.factory=com.sun.jndi.ldap.LdapCtxFactory
#yfs.security.ldap.url=ldap://<ldapservername>:<portnum>
#yfs.security.ldap.ou=
#yfs.security.ldap.o=

##########exception logging##################
#When this property is turned on (set to Y), errors encountered are directed to the alert console.
#Errors are grouped by certain criteria.  Not every error is a separate entry in the alert console.
#Possible values are 'Y/N'. The default value is 'Y'. 

yfs.onerror.raisealert=Y

#This property is to set the logging interval. Possible values are 'day/hour'. The default value is 'day'
#If it is set to 'day' then errors will be grouped on daily basis.  Otherwise, they are grouped on an hourly basis. 
#If an error is repeated within a logging interval, it is not directed multiple times to alert console.  Instead, the alert entry shows an increased occurrence count.

yfs.onerror.raisealert.logging.interval=day

#This property controls how many days of inactivity are required before the alert is eligible to be automatically
#closed by the Inbox Purge Agent. The default is seven days. A value of 0 means the alert will never be closed
#by this agent.

yfs.onerror.raisealert.expiration.days=7

#############################

########################### Caching ####################################

###---------------------------------------------------------------------------------------------------------------------
# This is the master list of cached classes (tables). The class name corresponds to the table being cached.
# The name is in the following format: com.yantra.shared.dbclasses.<Table name>DBCacheHome
#
# If you do not want a particular table (class) to be cached, remove that name from this list.
# All subsequent properties (see below) for a cached class apply only if that classname is in this list to begin with.
#
# To cache custom tables, add class name corresponding to the custom table in the list below.
# For example : If you want to cache custom table CT_ORGANIZATION, class name would be com.yantra.shared.dbclasses.CT_OrganizationDBCacheHome.
# Note:  This property is a comma separated list of classes, separated into multiple lines with a "\" (Line continuation character).
#        If you are adding additional classes, ensure that the "," and "\" character is placed appropriately.
###-----------------------------------------------------------------------------------------------------------------------
yfs.dbcache.classes=com.yantra.shared.dbclasses.YFS_ActionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_ActivityDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Activity_ConstraintDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_AdapterDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Additional_AttributeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Adjustment_Location_SequenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Adjustment_ReasonDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Adjustment_Reason_DetailDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Agent_CriteriaDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Agent_Criteria_TemplateDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Allocation_RuleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Application_MenuDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_AssociationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Atp_RulesDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_BarCode_TranslationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Activity_GroupDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Common_CodeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Config_Grp_DefnDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Config_TablesDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Document_ParamsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Document_TemplateDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_EventDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Pickup_Drop_StsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Process_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Purge_CriteriaDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_RulesDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Ship_ConstraintsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_TransactionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Batch_RuleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Business_DocumentDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Buyer_InstructionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_CalendarDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Calendar_Day_ExcpDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Calendar_Excp_ShiftDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Calendar_ShiftDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Calendar_Shift_Eff_PerdDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_CarrierDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Carrier_ServiceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_CategoryDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Category_DomainDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Category_ItemDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Charge_CategoryDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Charge_NameDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Classification_PurposeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Cntr_Buyer_AssocDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Cntr_Capacity_DefnDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Cntr_Category_AssocDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Common_CodeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Common_Code_AttributeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_ConditionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Condition_VariablesDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_ConsumerDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Count_ProgramDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Count_Program_ConditionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Count_Program_Condition_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Count_StrategyDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_CurrencyDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Currency_ConversionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_CustomerDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Customer_Additional_AddressDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Customer_ContactDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Customer_Payment_MethodDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Customer_Slot_PreferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Data_Security_Doc_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Data_Security_EnterpriseDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Data_Security_GroupDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Date_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_DepartmentDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_DeviceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Device_Sub_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Device_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Device_Type_TemplateDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Disposition_TransitionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Distribution_RuleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Document_FormatDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Document_ParamsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Document_TemplateDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_EnterpriseDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Equip_Type_Task_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_EquipmentDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Equipment_DetailDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Equipment_DeviceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Equipment_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Equipment_Type_DetailDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Error_Cause_ActionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Error_CodeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_EventDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Event_ConditionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Execution_ExceptionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_FlowDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Freight_TermsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_HMGroupsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_HMThresholdsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Tran_Hold_Process_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Hold_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Hold_Type_TranDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Hold_Type_Mod_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Hold_Type_User_GroupDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inbound_ComplianceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inbound_Vas_ComplianceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inventory_ConsiderationsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inventory_Demand_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inventory_Monitor_RulesDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inventory_Node_ControlDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inventory_StatusDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inventory_Status_TranDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inventory_Supply_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Invoked_FlowsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inv_Avail_Safety_FactorDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Inv_Node_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_ItemDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_AliasDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Attr_GroupDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Attr_Group_SeqDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Attr_Group_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_ExclusionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_InstructionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Instruction_CodeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Node_DefnDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_OptionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Price_SetDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Price_Set_DtlDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Service_AssocDBCacheHome, \
        com.yantra.shared.dbclasses.YFS_Item_Service_Assoc_SkillDBCacheHome, \
        com.yantra.shared.dbclasses.YFS_Item_Service_SkillDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Ship_NodeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_Status_RulesDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_TagDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_UOMDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Item_UOM_MasterDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Kit_ItemDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Label_FormatDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Label_PreferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Language_DescriptionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_LocaleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_LocationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Location_CapacityDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Location_PreferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Location_SizeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Location_Storage_CodeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Locn_Monitor_FilterDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Locn_Monitor_RuleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Master_CatalogDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_MenuDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Modification_AttributeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Module_Document_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Monitor_ConsolidationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Monitor_RuleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Monitor_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Negotiable_Hdr_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Negotiable_Line_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Negotiation_RuleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Node_DispositionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Node_Receiving_PreferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Node_Transfer_ScheduleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Order_Line_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Order_ValidationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Org_DocumentDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Org_EnterpriseDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Org_Group_OverrideDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Org_ProtocolDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Org_Protocol_ReferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Org_ServiceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_OrganizationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Outbound_ConstraintsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pack_Stgy_Ship_ModeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pack_StrategyDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pack_Strategy_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Payment_RuleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Payment_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_PipelineDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pipeline_ConditionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pipeline_DefinitionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pipeline_Drop_Status_ConditionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pipeline_ListenerDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pipeline_Pickup_TranDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Price_ProgramDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Price_Program_DefnDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Price_SetDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Print_DocumentDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Print_Document_Tran_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Printer_Type_Prnt_DocsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Process_Task_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Process_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Productivity_ReferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Productivity_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_ProtocolDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Purge_CriteriaDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Purge_Criteria_AddnlDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Putaway_StrategyDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Putaway_Strategy_DetailDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_QueryDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_QueueDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Queue_SubscriptionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Receiving_Discr_ReasonDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Receiving_PreferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_RegionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Region_Addnl_CapacityDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Region_DetailDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Region_LevelDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Region_Level_AssocDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Region_SchemaDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Region_UsageDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Res_PoolDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Res_Pool_ItemDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Res_Pool_RegionDBCacheHome, \
        com.yantra.shared.dbclasses.YFS_Res_Pool_Service_SkillDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_ResourceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Resource_PermissionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Return_DispositionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_RoleDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Role_DocumentDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_RulesDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_SCACDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_SCAC_And_ServiceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_SKU_DedicationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Saved_SearchDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_ServerDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_ServiceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Service_ActivityDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Service_Activity_InstructionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Service_ResourceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Service_SlotDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Service_Slot_GroupDBCacheHome, \
        com.yantra.shared.dbclasses.YFS_Service_SkillDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Ship_NodeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Shipping_PreferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Sourcing_Rule_DetailDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Sourcing_Rule_HeaderDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Special_Service_RefDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Special_ServicesDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_StatusDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Status_ConditionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Status_Inventory_TypesDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Status_Inv_SelectionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Status_MilestoneDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Status_ModificationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Status_Modification_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Storage_ConstraintDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_SubFlowDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Task_ReferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Task_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Tran_FlowDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Tran_User_ExitDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_TransactionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Transaction_Drop_StatusDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Transaction_Pickup_StatusDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_UomDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Uom_ConversionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_UserDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_User_ExitDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_User_Exit_ImplDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_User_GroupDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_User_Group_ListDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_User_Printer_PreferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_User_Task_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_User_ZoneDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_VendorDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Work_Order_Alloc_ConsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Xref_CodeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Xref_Config_DtlDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Xref_Config_HdrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Xref_MappingDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Xref_ValueDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Zip_Code_LocationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_ZoneDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Zone_EnterpriseDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Pack_Do_Not_MixDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pack_Do_Not_MixDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Chute_DedicationDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Shipment_GroupDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Shipment_SelectorDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Shipment_Select_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Sort_StrategyDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Wave_Size_ConstraintDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pick_StrategyDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pla_Activity_DeterDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pla_Activity_Task_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pla_Condition_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pla_Item_ConditionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pla_Uom_ConditionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pla_Upgrade_UomDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pla_Zone_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pla_Zone_SetDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Retrieval_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Retrieval_StrategyDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Esp_TermsDBCacheHome, \
    	com.yantra.shared.dbclasses.YFS_Cost_Factor_GroupDBCacheHome, \
    	com.yantra.shared.dbclasses.YFS_Cost_Factor_Group_DtlDBCacheHome, \
    	com.yantra.shared.dbclasses.YFS_Cost_Factor_PurposeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Routing_GuideDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Routing_Guide_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Service_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Routing_Guide_DetailDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Pro_No_DataDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Service_Resource_UserDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_QuestionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Answer_OptionDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Answer_Capacity_ImpactDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Answer_Set_MasterDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Answer_MasterDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Condition_PropsDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Tran_Hold_Pro_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Customer_Sch_PreferenceDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Node_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Node_Can_Ship_To_NTDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Org_Relationship_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Org_RelationshipDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Class_Item_AttrDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Ent_Node_TypeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Sourcing_ConfigDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Ship_ConstraintsDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Hazmat_ComplianceDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Routing_Guide_Detail_CarrierDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Region_Match_PrefDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Install_DefaultsDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Nonlocalized_StringsDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Pla_Item_Pick_Staging_ZoneDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Item_Pick_Pla_Item_ConditionDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Order_Pick_Pla_Item_ConditionDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Batch_Pick_Pla_Item_ConditionDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Retrieval_Pla_Item_ConditionDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Item_Pick_Pla_Zone_SetDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Order_Pick_Pla_Zone_SetDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Batch_Pick_Pla_Zone_SetDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Retrieval_Pla_Zone_SetDBCacheHome,\
	com.yantra.shared.dbclasses.YFS_Dock_AppointmentDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Config_TaskDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Config_Task_KnwgeDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Doc_Type_DeterDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Config_Task_DepDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Base_Config_Task_StatusDBCacheHome, \
	com.yantra.shared.dbclasses.YFS_Data_Security_NodesDBCacheHome

### These properties will be used to limit the number of rows that are cached for each class.
### The individual class properties override the default values specified below.
### If the property yfs.dbcache.defaults is not provided, limit.rows is defaulted to 10000.
#yfs.dbcache.defaults=limit.rows=1000,enabled=true

### --------------------------------------------------------------------------
# Individual cached class properties for limiting the rows cached at a class level.
# The memory used by setting the rows for each cached class can be approximated by using this formula:
# memory (mb) = rows X DB row size (bytes) of the cached class's table / 1000000.
# For example, for YFS_Item_Alias (rows=10000), the memory used by the cache would be APPROXIMATELY
# 10000 X 380 / 1000000 = 3.8 mb 
### --------------------------------------------------------------------------
#yfs.dbcache.com.yantra.shared.dbclasses.YFS_ActionDBCacheHome=limit.rows=100
#yfs.dbcache.com.yantra.shared.dbclasses.YFS_Item_AliasDBCacheHome=limit.rows=10000,enabled=false
#yfs.dbcache.com.yantra.shared.dbclasses.YFS_CategoryDBCacheHome=limit.rows=100000,enabled=false
#yfs.dbcache.com.yantra.shared.dbclasses.YFS_ResourceDBCacheHome=limit.rows=100000,enabled=false
#yfs.dbcache.com.yantra.shared.dbclasses.YFS_Resource_PermissionDBCacheHome=limit.rows=100000,enabled=false
###############################################################


#---------------------------------------------------------------------------
# If you wish to install the Java plugin (used for configurator) in 
# intranet environments, configure this parameter to point to the location 
# of the plugin. If this is not configured, Yantra uses 
# http://java.sun.com/update/1.4.2/jinstall-1_4_2_03-windows-i586.cab.
# Refer to http://java.sun.com/products/archive/j2se/1.4.2/jre/install-windows.html for more details
#-----------------------------------------------------------------------------
#yfs.config.java.plugin.codebase=

# # # # #
# Used by the application to give the JDBC driver a hint as to the number of rows that should be fetched from 
# the database when more rows are needed.
##  DO NOT UNCOMMENT OR DEFINE THIS PROPERTY UNLESS INSTRUCTED BY A TECHNICAL SUPPORT PROFESSIONAL
# # # # #
#yfs.dblogin.dataFetchSize=

#-----------------------------------------------------------------------------------------------
# The following three properties help is obtaining logs for SQL tuning.
# IF the application server is running on a windows environment use the directory delimeter 
# as "/" for the proxy log directory since ONLY unix delimeters ("/") will be recognized.
# DO NOT UNCOMMENT OR DEFINE THIS PROPERTY UNLESS INSTRUCTED BY A TECHNICAL SUPPORT PROFESSIONAL
#-----------------------------------------------------------------------------------------------
#yfs.enable.proxy.sql.logging=N
#yfs.enable.source.logging=N
#yfs.proxy.log.dir=c:/perflogs


#---------------------------------------------------------------------------
# This property controls the encoding of the HTML sent to the client.
#-----------------------------------------------------------------------------
yfs.ui.defaultEncoding=UTF-8

#---------------------------------------------------------------------------
# This property contains a comma-separated list of characters that could signify potentially unsafe HTML content
# If a information being written to the browser is found to contain any of the following characters, the output is safely encoded 
# to prevent exploitation of cross-site scripting vulnerabilites in the application.
#-----------------------------------------------------------------------------
yfs.htmlencoding.triggers=<,>,),]

#----------------------------------------------------------------------------------------------------------------
# This property controls the encoding of the files, like API template , theme xmls, exception template (except email templates which is controlled by yfs.email.template.encoding).
#----------------------------------------------------------------------------------------------------------------
yfs.file.encoding=UTF-8

#---------------------------------------------------------------------------
# This property set the number of records displayed on a list screen. 
#	Increase the application server JVM heap settings if these parameters are increased. Change will affect search limits for all users.
#-----------------------------------------------------------------------------
yfs.ui.MaxRecords=200

#---------------------------------------------------------------------------
# This property sets the default number of records returned by Yantra list apis. 
#	Increase the application server JVM heap settings if these parameters are increased. Change will affect search limits for all users.
# if this property is not set the application will default this value to 5000
#-----------------------------------------------------------------------------
yantra.app.maxrecords=5000



#---------------------------------------------------------------------------
# This property sets the number of seconds Yantra Console will wait for a SQL Statement to execute.
# If the limit is exceeded, an SQLException is thrown.
# Set to zero means unlimited.
#-----------------------------------------------------------------------------
yfs.ui.queryTimeout=60

#---------------------------------------------------------------------------
# This property can be set to "Y" when developing and customizing the Yantra Console UI. 
#	When set to "Y", refresh actions will be available within the Resource Configurator screens. These actions
# can be used to refresh the resources used in the Console UI Framework without having to restart the
# application server. If this property is not set to "Y", then the actions will be disabled, and it will be necessary
# to restart the application server whenever any resource is changed within the configurator.
# IMPORTANT: This property should NOT be set to "Y" for live production systems because the refresh only
# works for a single user development environment.
#-----------------------------------------------------------------------------
yfs.uidev.refreshResources=Y

#--------------------------------------------------------------------------------------------
# Set this property to "Y" to use the Hot SKU feature.  
# For more information about the Hot SKU feature, please see Product Concept Guide and 
# the Performance Management Guide
#--------------------------------------------------------------------------------------------
yfs.hotsku.useHotSKUFeature=N




#--------------------------------------------------------------------------------
# Manifesting options. Set this property to N to close manifest asyncronously using the CLOSE_MANIFEST Agent.

yfs.closemanifest.online=Y

#--------------------------------------------------------------------------------


#----------------------------------------------------------------------------------------------
# Batch completion options. 
# Set this property to N to complete batch asyncronously using the REQ_BATCH_COMPLETION Agent.
#----------------------------------------------------------------------------------------------
yfs.confirmbatch.online=Y


#----------------------------------------------------------------------------------------------
# The number of solver iterations for Create Wave. 
# Use this property to set the number of solver iterations through which the create wave Agent
# will undergo to optimise wave creation.
#----------------------------------------------------------------------------------------------
yfs.solver.iterations.wavecreate=1

#----------------------------------------------------------------------------------------------
# The re-open task agent will change the status of a suggested task back to open status,
# if the task is not modified for the time specified by this property. This property is specified 
# in hours. The default is 1 hour.
#----------------------------------------------------------------------------------------------
yfs.reopentask.time=1

#----------------------------------------------------------------------------------------------
# Cancellation of wave happens in background depending upon the value of the property yfs.cancelwave.offline.taskcount.greaterthan. 
# If the value of the property is for ex: 500 then cancellation of a wave, which has open tasks more than 
# 500 included in it then the wave gets cancelled in offline mode. Waves that have open tasks equal to or less than 500 gets cancelled 
# online.
#----------------------------------------------------------------------------------------------
yfs.cancelwave.offline.taskcount.greaterthan=-1

#----------------------------------------------------------------------------------------------
# Cancellation of move request happens in background depending upon the value of the property yfs.cancelmoverequest.offline.taskcount.greaterthan. 
# If the value of the property is for ex: 500 then cancellation of a move request, which has open tasks more than 
# 500 included in it then the move request gets cancelled in offline mode. Move requests that have open tasks equal to or less than 500 gets cancelled 
# online.
#----------------------------------------------------------------------------------------------
yfs.cancelmove.offline.taskcount.greaterthan=-1

#--------------------------------------------------------------------------------------------
# Request queue timetolive in seconds for Synchronous JMS messages. Use this property to set the 
# timetolive value for Synchronous JMS Messages. Note that, this property is a global level 
# override and is applicable for all the services using Synchronous JMS.    
# 
# WARNING: If this property value is set beyond the responseTimeOut value, it may result in
# un-processed request messages in the reply/response queue.
# 
# By default, in order to clean up the un-processed messages from the reply/response queue, 
# the timeToLive for request messages is set to the responseTimeOut value of the receiver.
# 
#--------------------------------------------------------------------------------------------
yfs.sync.jms.request.timetolive=

#---------------------------------------------------------------------------------------------

#--------------------------------------------------------------------------------------------
# Request queue timetolive in seconds for Synchronous JMS messages. Use this property to set the 
# timetolive value for Synchronous JMS Messages. Note that, this property is applicable only 
# for the specified service.    
# 
# WARNING: If this property value is set beyond the responseTimeOut value, it may result in
# un-processed request messages in the reply/response queue.
# 
# By default, in order to clean up the un-processed messages from the reply/response queue, 
# the timeToLive for request messages is set to the responseTimeOut value of the receiver.
#--------------------------------------------------------------------------------------------
yfs.sync.jms.request.timetolive.<SERVICE_NAME>=

#---------------------------------------------------------------------------------------------

############################################################################
##
##  DO NOT MODIFY ANY OF THE PROPERTIES BELOW...........
##  These properties are used by Yantra application internally. Do not change
##  them or remove them.
##
yfs.app.path=<YFS_APP_PATH>
yfs.xslprocessor.class=com.yantra.util.YFCXSLTransformerImpl

#---------------------------------------------------------------------------------------------
# Property to turn on Statistics logging 
# Valid values are n, N, y or Y
# If no value or property is specified, value defaults to N.
yantra.statistics.collect=Y
#
# Property to determine statistics logging time interval
# format of the property is Xm or Xh where X is an integer between 1 and 60
# and M/m for minutes or H/h for hours.
# If any unrecognized value or unit is specified, it will default to 10m (minutes).
# If a value of 61m or greater is specified, it will be reduced to 60m.
# If a value of 25h or greater is specified, it will be reduced to 24h.
# If the units are minutes (M/m), then the value is rounded up or down to the nearest equal divisor of 60 minutes.  Possible values are 1,2,3,4,5,6,10,12,15,20 or 30.
# If the units are hours (H/h), then the value is rounded up or down to the nearest equal divisor of 24 hours.  Possible values are 1,2,3,4,6,8 or 12.
yantra.statistics.persist.interval=10m
#---------------------------------------------------------------------------------------------

############################################################################

#------------------------------------------------------------------------------------------------
#Hot SKU Properties:  DO NOT MODIFY
#- Average requested quantity multiplier (This number can represent number of threads or JVMs which will running simultaneously)
yfs.hotsku.qtyMultiplier=30.0
#- Lock threshold limit in second
yfs.hotsku.secondsToClassifyAsAbnormalTime=0.5
#- Tracking window interval in minute               
yfs.hotsku.windowTimeInMinutes=20.0
#- Number of requests in tracking window to keep item hot
yfs.hotsku.numRequestsInTrackingWindowToKeepAsHotSku=3
#- Number of times lock exceeds the lock threshold limit to promote item to be hot
yfs.hotsku.numberOfAbnormalLocksForSwitchToHotSKU=1
#- High request quantity multiplier
yfs.hotsku.highRequestQuantityMultiplier=2.0
#- Max distinct ItemID of hot item map size in memory.  
yfs.hotsku.maxItemMapSizeInMemory=1000
#- Show extra messages as info.
yfs.hotsku.showExtraMessagesAsInfo=N

#############################################################################
# This property should be set to Y to handle namespaces in XML.
# By default it will be N. 
#############################################################################
yantra.document.isnamespaceaware=N
############################################################################

##################################################################################
# Set the following property to Y to enable printing via TCP/IP Socket Interface.
# Set it to N to enable printing via the File Interface (file "drop" to a shared 
# network drive). Although, the default value has been set to N for the sake of 
# backward compatibility, our recommendation is to set this flag to Y.
##################################################################################
yfs.loftware.tcpip.sockets=N
##################################################################################

##################################################################################
# Set the following property to WAIT to change the Loftware print server's mode 
# to WAIT mode
##################################################################################
yfs.loftware.tcpip.sockets.mode=NOWAIT 
##################################################################################
##################################################################################
# The containerizeWave API will use this property to select number of shipments  
# to be considered for creating the outbound containers in one round.
# API will consider default value as 75 if value is not specified for this property.
##################################################################################
yfs.containerization.maxshipmentsinoneround=
##################################################################################

#################################################################################
# Set the following property (in seconds) to indicate the interval at which
# tasks need to be polled for mobile operators
#################################################################################
com.yantra.ycp.em.server.taskpollingtime=60

#################################################################################
# Set the following properties required for cancel wave agent
# Sleep time properties are in seconds
#################################################################################
com.yantra.wms.agent.server.WMSCancelWaveAgent.sleepTimeBeforeGetJob=5
com.yantra.wms.agent.server.WMSCancelWaveAgent.sleepTimeBetweenTwoTries=30
com.yantra.wms.agent.server.WMSCancelWaveAgent.maxIterationWithNoProgress=10

#################################################################################
# Set the following properties required for cancel move agent
# Sleep time properties are in seconds
#################################################################################
com.yantra.wms.agent.server.WMSCancelMoveAgent.sleepTimeBeforeGetJob=5
com.yantra.wms.agent.server.WMSCancelMoveAgent.sleepTimeBetweenTwoTries=30
com.yantra.wms.agent.server.WMSCancelMoveAgent.maxIterationWithNoProgress=10

##################################################################################
#If both Request Delivery date and Request Ship date are passed, setting this property will compute the 
#transit time as difference between these two dates, and no further transit time computation will be required.
#################################################################################
OverrideTransitTime=N

##################################################################################
# Setting this property will decide the length to which the error message to be 
# stored in YFS_REPROCESS_ERROR table should be truncated.
# Should be set to an integer value. When not set, default value is 4000.
##################################################################################
yfs.reprocess.MaxErrorMessageLength=

#################################################################################
#Setting this  property to 'Y' will disable caching of Yantra pages
#Valid values = Y, y, N, n
#Default Value = N 
#-----------------------------
yfs.disable.webpages.caching=
#-----------------------------------------------------------------------------
##################################################################################
# PCA Auto update settings:
# Set the following property to specify the root folder under which the updates for 
# the PCAs are located. It can be a shared folder on a network. e.g.
# yfs.rcp.pca.updates.dir=c\:/Yantra/Garrone/pcaupdates/
#
#Set following property to a local cache directory, where the updates need to be cached.
#yfs.rcp.pca.updates.cache.dir=c\:/Yantra/Garonne/pcaupdates/updatecache
#
#IMP: The updates cache directory MUST NOT point to same location. The cache directory 
#is a working directory and involves file I/O e.g. file creations and deletions etc.
##################################################################################
yfs.rcp.pca.updates.dir=
yfs.rcp.pca.updates.cache.dir=
##################################################################################

#################################################################################
# Set the following property to 'Y' to process one serial at a time 
# during receiving i.e on scanning one serial user is taken to Disposition Code 
# entry screen.
#################################################################################
yfs.serial.receiving=N
##################################################################################
# Setting this property will decide whether Text Search is to be enabled.By default 
# the value would be N
##################################################################################
yfs.db.textsearch=N

##################################################################################
# Set this property to determine the type of text index(ctxcat/context) in oracle
#Should be set to CTXCAT or CONTEXT. 
##################################################################################
yfs.db.textsearch.oracle.contexttype=ctxcat

##################################################################################
# The CREATE WAVE FOR SHIPMENT GROUP agent uses this property to increase the 
# Available date (in hours) of YFS_Task_Q records with transaction key "CREATE_WAVE_TASK_Q.4001"
# against which no waves could be created. Agent considers default value as 1 if value is 
# not specified for this property. If set to -1, the YFS_Task_Q records with transaction key
# "CREATE_WAVE_TASK_Q.4001" for which no waves are generated, will be deleted.
##################################################################################
yfs.install.createWave.delayhours=1

##################################################################################
# Set this property to enable auditing and disable auditing.
# Default value is Y which means audit enabled
##################################################################################
yfs.audit.user.session.activity=Y


##################################################################################
# Based on this property, inventory shortages in shipments being releases in
# a wave will be handled Shipments thus created can be released in another wave.
# New shipments can be created for inventory having shortages.
# Set it to RemoveShipmentsFromWave to remove shipments having inventory shortages from wave. (DEFAULT)
# Set it to RemoveShipmentLinesFromWave to create child shipments for shipment lines having shortages and let the shipment be released without these lines.
# Set it to RemoveShortageQuantitiesFromWave to create child shipments for shortage quantity and let the shipments be released with available inventory.
##################################################################################
yfs.releaseWave.handleShortages=RemoveShipmentsFromWave
##################################################################################
