package com.nwcg.icbs.yantra.commCore;

import java.util.Hashtable;

import com.nwcg.icbs.yantra.handler.NWCGMessageHandler;
import com.nwcg.icbs.yantra.handler.NWCGMessageHandlerInterface;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;

public class NWCGMessageHandlerFactory {

	private static Hashtable handlerHash;
	
	static {init();}
	private NWCGMessageHandlerFactory() {}
    
    private static void init() {
    	handlerHash = new Hashtable();
    }
	public static NWCGMessageHandlerInterface getHandler(String serviceName){

		
		String handlerClassName = ResourceUtil.get(serviceName+".handlerClass");
		
		if(handlerClassName == null){
			NWCGLoggerUtil.Log.warning("Required handlerClass not defined!");
			return null;
		}
		
		NWCGMessageHandlerInterface handler = (NWCGMessageHandlerInterface)handlerHash.get(handlerClassName);
		if( handler != null) return handler;
		
		try {
			Class handlerObjClass = ClassLoader.getSystemClassLoader().loadClass(handlerClassName);
			Object handlerObj = handlerObjClass.newInstance();
			handler = (NWCGMessageHandlerInterface)handlerObj;
		
			handlerHash.put(handlerClassName, handler);
			
			return handler;
		} catch(ClassNotFoundException e){
			NWCGLoggerUtil.Log.warning("ClassNotFoundException :"+ e.toString());
			NWCGLoggerUtil.printStackTraceToLog(e);
		}catch(IllegalAccessException e){
			NWCGLoggerUtil.Log.warning("IllegalAccessException :"+ e.toString());
			NWCGLoggerUtil.printStackTraceToLog(e);
		}catch(InstantiationException e){
			NWCGLoggerUtil.Log.warning("InstantiationException :"+ e.toString());
			NWCGLoggerUtil.printStackTraceToLog(e);
		}
		
		return null;
				
	}
	
	public static NWCGMessageHandler createHandler(String serviceGroupName, String serviceName) throws Exception {

		if(serviceGroupName.equals(NWCGAAConstants.AUTH_SERVICE_GROUP_NAME)){
	
			if(serviceName.equals(NWCGAAConstants.AUTH_USER_REQ_IB_SERVICE_NAME)){
				//return new NWCGOBAuthMessageHandler();
			}
			
			if(serviceName.equals(NWCGAAConstants.RESPONSE_STATUS_TYPE_SERVICE_NAME)){
		
				//return new NWCGResponseStatusTypeHandler();
			}
			
		}
		
		String handlerClassName = NWCGProperties.getProperty(serviceName+".handlerClass");
		
		if(handlerClassName == null){
			throw new Exception("Could not find handler class for service '"+serviceName+"'");
		}
				
		Class handlerObjClass = ClassLoader.getSystemClassLoader().loadClass(handlerClassName);
		Object handlerObj = handlerObjClass.newInstance();
		NWCGMessageHandler handler = (NWCGMessageHandler)handlerObj;
		
		return handler;
				
	}
}
