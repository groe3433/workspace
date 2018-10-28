package com.nwcg.icbs.yantra.ob.handler;

import java.util.HashMap;

import org.w3c.dom.Document;

/**
 * This interface should be used as a dynamic handler for OB Responses.
 * For now, we will be dealing with Incident and Catalog 
 * @author sgunda
 *
 */
public interface NWCGOBProcessorHandler {
	public Document process(HashMap<Object, Object> msgMap) throws Exception;
}
