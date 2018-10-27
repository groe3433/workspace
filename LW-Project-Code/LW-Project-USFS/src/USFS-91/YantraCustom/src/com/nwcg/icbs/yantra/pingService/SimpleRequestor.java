package com.nwcg.icbs.yantra.pingService;

/*

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.directory.InitialDirContext;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsDestination;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.yfc.log.YFCLogCategory;
*/

public class SimpleRequestor
{
	/*
  private static int status = 1;
  private static YFCLogCategory logger = NWCGROSSLogger.instance(SimpleRequestor.class);
  
  public static void main(String[] args)
  {
	  sendRequest();
  }
  */
  
  public static void sendRequest() {
	  /*
    Connection connection = null;
    ConnectionFactory connectionFactory = null;
    Session session = null;
    Destination destination = null;
    Destination tempDestination = null;
    MessageProducer producer = null;
    MessageConsumer consumer = null;
    try
    {
      // display the classpath and the path
      logger.verbose("CLASSPATH=<"+System.getenv("CLASSPATH")+">");
      logger.verbose("classpath="+System.getProperty("java.class.path"));
      logger.verbose("PATH=<"+System.getenv("PATH")+">");
      String contextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";
      Hashtable environment = new Hashtable();
      environment.put("java.naming.factory.initial", contextFactory);
      String dirPath = "/opt/apps/projects/JNDI/JNDIConfig";
     File dir = new File(dirPath);
     if (dir.exists()) {
		logger.verbose("File access successful");
		// now try accessing the bindings file
		String filePath = dirPath+"/.bindings";
		File file = new File(filePath);
		BufferedReader in = new BufferedReader(new FileReader(new File(filePath)));
        logger.verbose("File open successful!");
        String line = in.readLine();
        while ( (line = in.readLine())!= null )
        in.readLine();
		
	}
      environment.put("java.naming.provider.url", "file:///opt/apps/projects/JNDI/JNDIConfig");
      Context context = null;
      
      try
      {
  		  // try just creating the initial context
  		  Hashtable env = new Hashtable();
  		  env.put(Context.INITIAL_CONTEXT_FACTORY, 
  		      "com.ibm.websphere.naming.WsnInitialContextFactory");
  		  env.put(Context.PROVIDER_URL,"iiop://localhost:2809");

  		  Context ctx = new InitialContext(env);
  		  
  		  logger.verbose("Created initial ontext with WsnInit");
      	  
      	  NamingEnumeration<NameClassPair> list = ctx.list("");
      	  logger.verbose("Print context list, hasMore = "+list.hasMore());
      	  while (list.hasMore()) {
      		  logger.verbose(list.next().getName());
      	  }
  		  
  		  // now do lookup
  		  Object obj = ctx.lookup("QCF_LOOKUP");
      }
      catch ( Exception e) {
      	e.printStackTrace();
      }
      
      try {
    	  // try just creating the initial context
    	  Hashtable env = new Hashtable();
    	  env.put(Context.INITIAL_CONTEXT_FACTORY, 
	      "com.sun.jndi.fscontext.RefFSContextFactory");
    	  env.put(Context.PROVIDER_URL,"file:"+dirPath);
    	  env.put(Context.SECURITY_AUTHENTICATION, "none");
    	  logger.verbose("> Filepath:"+"file:"+dirPath);
    	  Context ctx = new InitialContext(env);
    	  logger.verbose("> Context created.");
    	  // now do lookup
    	  Object obj = ctx.lookup("QCF_LOOKUP");
      } catch (NameAlreadyBoundException nabe) {
    	  logger.verbose("> NameAlreadyBoundException.",nabe);
      } catch (NamingException ne) {
    	  logger.verbose("> NamingException.",ne);
      } catch (Exception e) {
    	  logger.verbose("> Exception.",e);
      }
      
      try {
    	  // try just creating the initial context
    	  Hashtable env = new Hashtable();
    	  env.put(Context.INITIAL_CONTEXT_FACTORY, 
	      "com.sun.jndi.fscontext.RefFSContextFactory");
    	  env.put(Context.PROVIDER_URL,"file:/"+dirPath);
    	  env.put(Context.SECURITY_AUTHENTICATION, "none");
    	  logger.verbose("> Filepath:"+"file:/"+dirPath);
    	  Context ctx = new InitialContext(env);
    	  logger.verbose("> Context created.");
    	  // now do lookup
    	  Object obj = ctx.lookup("QCF_LOOKUP");
      } catch (NameAlreadyBoundException nabe) {
    	  logger.verbose("> NameAlreadyBoundException.",nabe);
      } catch (NamingException ne) {
    	  logger.verbose("> NamingException.",ne);
      } catch (Exception e) {
    	  logger.verbose("> Exception.",e);
      }
      
      
      try {
    	  // try just creating the initial context
    	  Hashtable env = new Hashtable();
    	  env.put(Context.INITIAL_CONTEXT_FACTORY, 
	      "com.sun.jndi.fscontext.RefFSContextFactory");
    	  env.put(Context.PROVIDER_URL,"file://"+dirPath);
    	  env.put(Context.SECURITY_AUTHENTICATION, "none");
    	  logger.verbose("> Filepath:"+"file://"+dirPath);
    	  Context ctx = new InitialContext(env);
    	  logger.verbose("> Context created.");

    	  Hashtable h = ctx.getEnvironment();
    	  logger.verbose("Hashtable size = "+h.size());
    	  logger.verbose("Context as String"+ctx.toString());

    	  logger.verbose("Print context list");
    	  NamingEnumeration<NameClassPair> list = ctx.list("");
    	  logger.verbose("Print context list, hasMore = "+list.hasMore());
    	  while (list.hasMore()) {
    		  logger.verbose(list.next().getName());
    	  }
    	  // now do lookup
    	  Object obj = ctx.lookup("QCF_LOOKUP");
      } catch (NameAlreadyBoundException nabe) {
    	  logger.verbose("> NameAlreadyBoundException.",nabe);
      } catch (NamingException ne) {
    	  logger.verbose("> NamingException.",ne);
      } catch (Exception e) {
    	  logger.verbose("> Exception.",e);
      }
      
      try {
    	  Hashtable<String, String> env = new Hashtable<String, String>();
    	  env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.fscontext.RefFSContextFactory");
    	  env.put(Context.PROVIDER_URL,  "file:///opt/apps/projects/JNDI/JNDIConfig");
    	  env.put(Context.SECURITY_AUTHENTICATION, "none");
    	  logger.verbose("> about to create DirContext.");
    	  Context ctx = new InitialDirContext(env);
    	  logger.verbose("> DirContext created.");
      }
      catch (Exception e ) {
    	  logger.error(e);
      }
	  
      try {
    	  context = new InitialContext();
    	  context.rebind("fs", new Reference("javax.naming.Context",
					new StringRefAddr(Context.PROVIDER_URL, "file://" + dirPath)));
    	  logger.verbose("> Rebind succeeded.");
    	  logger.verbose("Print context list");
    	  NamingEnumeration<NameClassPair> list = context.list("");
    	  logger.verbose("Print context list, hasMore = "+list.hasMore());
    	  while (list.hasMore()) {
    		  logger.verbose(list.next().getName());
    	  }
    	  Object fs = context.lookup("fs");

      
      } catch (NameAlreadyBoundException nabe) {
    	  logger.verbose("> NameAlreadyBoundException.",nabe);
      } catch (NamingException ne) {
    	  logger.verbose("> NamingException.",ne);
      } catch (Exception e) {
    	  logger.verbose("> Exception.",e);
      }
      
      //Context context = new InitialDirContext(environment);
      
      
      
      logger.verbose("> Context created.");

      connectionFactory = (JmsConnectionFactory)context.lookup("QCF_LOOKUP");
      logger.verbose("> Connection factory located in JNDI.");
      

      destination = (JmsDestination)context.lookup("NWCG_AGENT_QUEUE");
      logger.verbose("> Destination located in JNDI. ");
      

      logger.verbose("> Creating connection to QueueManager.");
      connection = connectionFactory.createConnection();
      session = connection.createSession(false, 1);
      logger.verbose("> Connection created.");
      

      connection.setExceptionListener(new ExceptionListener()
      {
        public void onException(JMSException arg0)
        {
          logger.verbose("Exception sent to exception listener");
          ExceptionHandling.handleException(arg0);
        }
      });
      producer = session.createProducer(destination);
      tempDestination = session.createTemporaryQueue();
      

      consumer = session.createConsumer(tempDestination);
      
      String text = "BakedBeans";
      logger.verbose("\n> Sending stock request for '" + text + "'");
      TextMessage message = session
        .createTextMessage(text);
      

      message.setJMSReplyTo(tempDestination);
      

      connection.start();
      

      producer.send(message);
      logger.verbose("> Sent Message ID=" + message.getJMSMessageID());
      

      Message receivedMessage = consumer.receive(15000000L);
      if (receivedMessage != null) {
        logger.verbose("\n> Received Message ID=" + receivedMessage.getJMSMessageID() + " for '" + ((TextMessage)receivedMessage).getText() + "'");
      } else {
        logger.verbose("\n! No response message received in 15 seconds.");
      }
    }

    catch(Exception e) {
      logger.verbose("Exception:",e);
    }
    finally
    {
      logger.verbose("\n> Closing connection to QueueManager.");
      if (producer != null) {
        try
        {
          producer.close();
        }
        catch (JMSException jmsex)
        {
          logger.verbose("Producer could not be closed.");
          ExceptionHandling.handleException(jmsex);
        }
      }
      if (consumer != null) {
        try
        {
          consumer.close();
        }
        catch (JMSException jmsex)
        {
          logger.verbose("Consumer could not be closed.");
          ExceptionHandling.handleException(jmsex);
        }
      }
      if (session != null) {
        try
        {
          session.close();
        }
        catch (JMSException jmsex)
        {
          logger.verbose("Session could not be closed.");
          ExceptionHandling.handleException(jmsex);
        }
      }
      if (connection != null) {
        try
        {
          connection.close();
        }
        catch (JMSException jmsex)
        {
          logger.verbose("Connection could not be closed.");
          ExceptionHandling.handleException(jmsex);
        }
      }
      logger.verbose("> Closed Connection.");
    }
    System.exit(status);
    */
  }
}

