package com.nwcg.icbs.yantra.agents;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.management.ObjectName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.ibm.websphere.management.AdminClient;
import com.ibm.websphere.management.AdminClientFactory;
import com.ibm.websphere.management.exception.AdminException;
import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.Logger;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGQueueMonitoringAgent extends YCPBaseAgent {

	private static Logger logger = Logger.getLogger(NWCGQueueMonitoringAgent.class.getName());
	private Hashtable<String, String> htQueueMonitorConf = new Hashtable<String, String>();
	private Vector<String> qNames2Monitor = new Vector<String>();
    
	
	@Override
	public void executeJob(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("NWCGQueueMonitoringAgent::executeJobs, " +
				"This should not be called as we are returning NULL from getJobs");
	}
	
	private void getAndSetMonitorConfigInfo(Document docIP){
		Element elmDocMonitor = docIP.getDocumentElement();
		NamedNodeMap nnm = elmDocMonitor.getAttributes();
		if (nnm == null){
			logger.error("NWCGQueueMonitoringAgent::getAndSetMonitorConfigInfo, " +
					"Attributes are not set as part of input document");
		}
		for (int i=0; i < nnm.getLength(); i++){
			Node tempNode = nnm.item(i);
			String nodeName = tempNode.getNodeName();
			if (nodeName.startsWith("Q_")){
				nodeName = nodeName.substring(2, nodeName.length());
				qNames2Monitor.add(nodeName);
			}
			htQueueMonitorConf.put(nodeName, tempNode.getNodeValue());
		}
		
	}
	
	public List getJobs(YFSEnvironment env, Document docIP) throws Exception {
		logger.verbose("NWCGQueueMonitoringAgent::getJobs, Entered");
		logger.verbose("NWCGQueueMonitoringAgent::getJobs, Input XML : " + XMLUtil.extractStringFromDocument(docIP));
		System.out.println("NWCGQueueMonitoringAgent::getJobs, Input XML : " + XMLUtil.extractStringFromDocument(docIP));
		getAndSetMonitorConfigInfo(docIP);
		if (qNames2Monitor.size() < 1){
			logger.error("NWCGQueueMonitoringAgent::getJobs, There are no queues to monitor. " +
					"Agent will not try to check the queue depth");
			return null;
		}
		
		AdminClient ac = getAdminClient();
		if (ac == null){
			return null;
		}
		checkQSizeAndTriggerEmail(ac);

		return null;
	}

    /**
     * get AdminClient using the given host, port, and connector
     */
    private AdminClient getAdminClient()
    {
        String host = htQueueMonitorConf.get("HOST");
        String port = htQueueMonitorConf.get("PORT");
        String connector = htQueueMonitorConf.get("CONNECTOR");
        logger.verbose("host=" + host + " , port=" + port + ", connector=" + connector);

        if ((host == null || host.trim().length() < 1) ||
        	(port == null || port.trim().length() < 1) ||
        	(connector == null || connector.trim().length() < 1) ){
        	System.out.println("NWCGQueueMonitoringAgent::getAdminClient, HOST, PORT or CONNECTOR is not mentioned " +
        			"as part of agent criteria. Please populate them and restart");
        	logger.error("NWCGQueueMonitoringAgent::getAdminClient, HOST, PORT or CONNECTOR is not mentioned " +
        			"as part of agent criteria. Please populate them and restart");
        	return null;
        }
        
        System.out.println("NWCGQueueMonitoringAgent::getAdminClient, Host=" + host + " , Port=" + port + ", Connector=" + connector);
        
        AdminClient ac = null;
        java.util.Properties props = new java.util.Properties();
        props.put(AdminClient.CONNECTOR_TYPE, connector);
        props.put(AdminClient.CONNECTOR_HOST, host);
        props.put(AdminClient.CONNECTOR_PORT, port);

        try
        {
            ac = AdminClientFactory.createAdminClient(props);
        }
        catch(Exception e)
        {
            System.out.println("NWCGQueueMonitoringAgent::getAdminClient, Exception : " + e.getMessage());
            new AdminException(e).printStackTrace();
        }
        return ac;
    }
	
    
    private void checkQSizeAndTriggerEmail(AdminClient ac)
    {
        try
        {
        	System.out.println("NWCGQueueMonitoringAgent::checkQSizeAndTriggerEmail, " +
        			"Queue names to monitor are " + qNames2Monitor.toString());
            //----------------------------------------------------------------------------
            // Get a list of object names
            //----------------------------------------------------------------------------
            javax.management.ObjectName on = new javax.management.ObjectName("WebSphere:*");

            //----------------------------------------------------------------------------
            // get all objectnames for this server
            //----------------------------------------------------------------------------
            Set objectNameSet= ac.queryNames(on, null);

            if(objectNameSet != null)
            {
                System.out.println("NWCGQueueMonitoringAgent::checkQSizeAndTriggerEmail, Object names found");
                Iterator i = objectNameSet.iterator();
                while(i.hasNext())
                {
                    on = (ObjectName)i.next();
                    String type = on.getKeyProperty("type");

                    if(type != null && type.equals("SIBQueuePoint"))
                    {
    					Object object = ac.getAttribute(on , "depth");
    					String propName = on.getKeyProperty("name");
    					long noOfMsgs = 0;
    					System.out.println("NWCGQueueMonitoringAgent::checkQSizeAndTriggerEmail, The Queue is : " + propName + ". " +
    									   "Number of messages is : " + ((Long)object).longValue());
    					if (qNames2Monitor.contains(propName)){
    						noOfMsgs = ((Long)object).longValue();
    						String triggerLen = htQueueMonitorConf.get(propName);
    						long qMonitorLen = (new Long(triggerLen)).longValue();
    						
    						
    						if (noOfMsgs > qMonitorLen){
    	    					System.out.println("NWCGQueueMonitoringAgent::checkQSizeAndTriggerEmail, No of messages for Queue " + 
    	    							propName + " " + "is greater than " + triggerLen + ", so triggering an email.");
    	    					sendEmail(propName, (new Long(noOfMsgs)).toString(), triggerLen);
    						}
    					} // end of if qNames2Monitor
                    } // end of if(SIBQueuePoint)
                }
            }
            else
            {
                System.err.println("NWCGQueueMonitoringAgent::checkQSizeAndTriggerEmail, ERROR: no object names found");
                logger.error("NWCGQueueMonitoringAgent::checkQSizeAndTriggerEmail, ERROR: no object names found");
            }
        }
        catch(Exception ex)
        {
        	System.out.println("NWCGQueueMonitoringAgent::checkQSizeAndTriggerEmail, Exception Message : " + ex.getMessage());
        	logger.error("NWCGQueueMonitoringAgent::checkQSizeAndTriggerEmail, Exception Message : " + ex.getMessage());
            new AdminException(ex).printStackTrace();
            ex.printStackTrace();
        }
    }    

    /**
     * This will send email by taking the information of SMTP_HOST, FROM_EMAIL and TO_EMAIL
     * from agent criteria parameters
     * @param qName
     * @param actualCount
     * @param expectedCount
     * @throws MessagingException
     */
    public void sendEmail(String qName, String actualCount, String expectedCount) throws MessagingException
    {
    	boolean debug = false;
    	Properties propMail = new Properties();
    	String smtpHost = htQueueMonitorConf.get("SMTP_HOST");
    	if (smtpHost == null || smtpHost.trim().length() < 2){
    		System.out.println("SMTP Host information is not provided in the configuration file. " +
    				"Populate SMTP_HOST in the config file from /etc/sendmail.cf");
    		System.exit(2);
    	}
    	propMail.put("mail.smtp.host", smtpHost);
    	
    	Session sessionMail = Session.getDefaultInstance(propMail, null);
    	sessionMail.setDebug(debug);
    	
    	Message msgMail = new MimeMessage(sessionMail);
    	
    	String fromEmail = htQueueMonitorConf.get("FROM_EMAIL");
    	if (fromEmail == null || fromEmail.trim().length() < 2){
    		fromEmail = "admin@icbsr.com";
    	}
    	InternetAddress fromEmailAddr = new InternetAddress(fromEmail);
    	msgMail.setFrom(fromEmailAddr);
    	
    	String toEmailList = htQueueMonitorConf.get("TO_EMAIL");
    	if (toEmailList == null || toEmailList.trim().length() < 2){
    		System.out.println("To email addresses are not mentioned in the agent criteria/config file. " +
    				"List the email addresses to whom this alerts needs to go for string TO_EMAIL " +
    				"separated by a comma delimiter in config file");
    		System.exit(2);
    	}
    	
    	String toEmails[] = toEmailList.split(",");
    	InternetAddress[] toEmailAddresses = new InternetAddress[toEmails.length];
    	for (int i=0; i < toEmails.length; i++){
    		toEmailAddresses[i] = new InternetAddress(toEmails[i]);
    	}
    	msgMail.setRecipients(Message.RecipientType.TO, toEmailAddresses);
    	
    	msgMail.setSubject(qName + " queue exceeded normal count");
    	String content = qName + " queue size is " + actualCount + ". Average count should be less than " + 
    					 expectedCount + ". Please check the appropriate agent. Current time stamp is " 
    					 + CommonUtilities.formatDate("yyyy-MM-dd HH:mm:ss Z", Calendar.getInstance().getTime());

    	msgMail.setContent(content, "text/plain");
    	Transport.send(msgMail);
    }
    
}
