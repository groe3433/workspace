/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.commCore;

import java.util.Hashtable;

import com.nwcg.icbs.yantra.handler.NWCGMessageHandler;
import com.nwcg.icbs.yantra.handler.NWCGMessageHandlerInterface;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogLevel;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.dom.YFCDocument;

public class NWCGMessageHandlerFactory {

	private static Hashtable handlerHash;

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGMessageHandlerFactory.class);

	static {
		init();
	}

	private NWCGMessageHandlerFactory() {
	}

	private static void init() {
		handlerHash = new Hashtable();
	}

	public static NWCGMessageHandlerInterface getHandler(String serviceName) {
		logger.verbose("@@@@@ Entering NWCGMessageHandlerFactory::getHandler ");
		String handlerClassName = ResourceUtil.get(serviceName + ".handlerClass");
		if (handlerClassName == null) {
			logger.warn("!!!!! Required handlerClass not defined!");
			logger.verbose("!!!!! Exiting NWCGMessageHandlerFactory::getHandler (0)");
			return null;
		}
		NWCGMessageHandlerInterface handler = (NWCGMessageHandlerInterface) handlerHash.get(handlerClassName);
		if (handler != null) {
			logger.verbose("@@@@@ Exiting NWCGMessageHandlerFactory::getHandler (1)");
			return handler;
		}
		try {
			Class handlerObjClass = ClassLoader.getSystemClassLoader().loadClass(handlerClassName);
			Object handlerObj = handlerObjClass.newInstance();
			handler = (NWCGMessageHandlerInterface) handlerObj;
			handlerHash.put(handlerClassName, handler);
			logger.verbose("@@@@@ Exiting NWCGMessageHandlerFactory::getHandler (2)");
			return handler;
		} catch (ClassNotFoundException e) {
			logger.error("!!!!! ClassNotFoundException :", e);
		} catch (IllegalAccessException e) {
			logger.error("!!!!! IllegalAccessException :", e);
		} catch (InstantiationException e) {
			logger.error("!!!!! InstantiationException :", e);
		}
		logger.verbose("!!!!! Exiting NWCGMessageHandlerFactory::getHandler (3)");
		return null;
	}

	public static NWCGMessageHandler createHandler(String serviceGroupName, String serviceName) throws Exception {
		logger.verbose("@@@@@ Entering NWCGMessageHandlerFactory::createHandler");
		if (serviceGroupName.equals(NWCGAAConstants.AUTH_SERVICE_GROUP_NAME)) {
			if (serviceName.equals(NWCGAAConstants.AUTH_USER_REQ_IB_SERVICE_NAME)) {
				// return new NWCGOBAuthMessageHandler();
			}
			if (serviceName.equals(NWCGAAConstants.RESPONSE_STATUS_TYPE_SERVICE_NAME)) {
				// return new NWCGResponseStatusTypeHandler();
			}
		}
		String handlerClassName = NWCGProperties.getProperty(serviceName + ".handlerClass");
		if (handlerClassName == null) {
			throw new Exception("Could not find handler class for service '" + serviceName + "'");
		}
		Class handlerObjClass = ClassLoader.getSystemClassLoader().loadClass(handlerClassName);
		Object handlerObj = handlerObjClass.newInstance();
		NWCGMessageHandler handler = (NWCGMessageHandler) handlerObj;
		logger.verbose("@@@@@ Exiting NWCGMessageHandlerFactory::createHandler");
		return handler;
	}
}