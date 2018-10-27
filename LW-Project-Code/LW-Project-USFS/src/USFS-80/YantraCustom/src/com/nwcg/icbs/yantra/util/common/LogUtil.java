package com.nwcg.icbs.yantra.util.common;

import java.io.*;

import org.apache.log4j.*;
import org.apache.log4j.xml.DOMConfigurator;

import com.nwcg.icbs.yantra.util.common.ResourceUtil;

/**
*	Logging utility.
*	<p>
*	When MCF's  log4jconfig.xml is used, the levels in YFCLogLevel are available.
*	YFCLogLevel defined the following level, in descending priority order
*	<b>
*	<pre>
*		FATAL, 50000
*		ERRORDTL, 45000
*		ERROR, 40000
*		WARN, 30000
*		INFO, 20000
*		TIMER, 16000
*		SQLDEBUG, 14000
*		DEBUG, 10000
*		VERBOSE, 9000
*	</pre>
*	</b>
* @version 1.1 2010
* @author Michael Chen, drodriguez
*/
public class LogUtil {
	/**
	*	Get default package level Logger. The default package is "com.nwcg".
	*	@return a Logger object for the default package.
	*/
	public static org.apache.log4j.Logger getLogger()
	{
		return org.apache.log4j.Logger.getLogger("com.nwcg");
	}

	/**
	*	Get Logger by name.
	*	@param name the Logger name, usually corresponds to the class package.
	*	@return the Logger with the name (existing or created).
	*/
	public static org.apache.log4j.Logger getLogger(String name)
	{
		return org.apache.log4j.Logger.getLogger(name);
	}

	/**
	*	Initialize Log4j. Only used for agent(stand-alone application) that Yantra did not configure log4j yet.
	*
	*	@param configXMLFile If null, will use log4j.configuration setting from yfs.properties. If that is not
	*		defined, will use a BasicConfigurator.
	*/
	public static void initLog4j(String configXMLFile)
	{
		if (configXMLFile == null)
			configXMLFile = ResourceUtil.get("log4j.configuration","/resources/log4jconfig.xml");

		if (configXMLFile != null || configXMLFile.length() > 0)
		{
			try {
				InputStream is = LogUtil.class.getResourceAsStream(configXMLFile);
				DOMConfigurator ctr = new DOMConfigurator();
				ctr.doConfigure(is, LogManager.getLoggerRepository());

				is.close();
			} catch (IOException e) {
				System.out.println("Failed opening config file [" + configXMLFile + "]: " + e.getMessage());
				System.out.println("Will use BasicConfigurator.");
				BasicConfigurator.configure();
			}
		}
		else {
			// use basic configuration
			BasicConfigurator.configure();
		}
	}

}
