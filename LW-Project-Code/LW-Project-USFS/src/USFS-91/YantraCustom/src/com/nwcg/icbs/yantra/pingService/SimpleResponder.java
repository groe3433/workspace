package com.nwcg.icbs.yantra.pingService;

/*

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsDestination;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.yantra.yfc.log.YFCLogCategory;

import java.io.PrintStream;
import java.util.Hashtable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;
*/

public class SimpleResponder
{
	
	/*
  private static int status = 1;
  private static YFCLogCategory logger = NWCGROSSLogger.instance(SimpleResponder.class);
  
  public static void main(String[] args)
  {
    Connection connection = null;
    ConnectionFactory connectionFactory = null;
    Session session = null;
    Destination destination = null;
    MessageProducer producer = null;
    MessageConsumer consumer = null;
    logger.verbose("hello");
    try
    {
      String contextFactory = "com.sun.jndi.fscontext.RefFSContextFactory";
      Hashtable environment = new Hashtable();
      environment.put("java.naming.factory.initial", contextFactory);
      environment.put("java.naming.provider.url", "file:///opt/apps/projects/JNDI/JNDIConfig");
      Context context = new InitialDirContext(environment);
      

      connectionFactory = (JmsConnectionFactory)context.lookup("QCF_LOOKUP");
      logger.verbose("> Connection factory located in JNDI.");
      


      destination = (JmsDestination)context.lookup("NWCG_AGENT_QUEUE");
      logger.verbose("> Destination located in JNDI.");
      

      logger.verbose("> Creating connection to QueueManager.");
      connection = connectionFactory.createConnection();
      session = connection.createSession(false, 1);
      logger.verbose("> Created connection.");
      

      connection.setExceptionListener(new ExceptionListener()
      {
        public void onException(JMSException arg0)
        {
          logger.verbose("Exception sent to exception listener");
          ExceptionHandling.handleException(arg0);
        }
      });
      consumer = session.createConsumer(destination);
      

      connection.start();
      

      logger.verbose("\n> Waiting for message.");
      TextMessage receivedMessage = (TextMessage)consumer.receive();
      logger.verbose("\n> Received Message ID=" + receivedMessage.getJMSMessageID() + " for '" + receivedMessage.getText() + "'");
      

      Destination replyToDestination = receivedMessage.getJMSReplyTo();
      

      producer = session.createProducer(replyToDestination);
      

      String text = receivedMessage.getText() + " - 15 tins in stock";
      logger.verbose("\n> Sending Reply Message '" + text + "'");
      
      TextMessage message = session.createTextMessage();
      message.setText(text);
      

      producer.send(message);
      logger.verbose("> Sent Message ID=" + message.getJMSMessageID());
    }
    catch (NamingException ne)
    {
      logger.verbose("The initial context could not be instantiated, or the lookup failed.");
      ExceptionHandling.handleException(ne);
    }
    catch (JMSException jmsex)
    {
      ExceptionHandling.handleException(jmsex);
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
          logger.verbose("! Producer could not be closed.");
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
          logger.verbose("! Consumer could not be closed.");
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
          logger.verbose("! Session could not be closed.");
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
          logger.verbose("! Connection could not be closed.");
          ExceptionHandling.handleException(jmsex);
        }
      }
      logger.verbose("> Closed connection.");
    }
    System.exit(status);
  }
  */
}
