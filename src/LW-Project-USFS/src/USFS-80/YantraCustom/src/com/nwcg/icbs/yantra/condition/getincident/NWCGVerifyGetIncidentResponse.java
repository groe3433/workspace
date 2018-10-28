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

package com.nwcg.icbs.yantra.condition.getincident;

import java.util.Map;

import org.w3c.dom.Document;

import com.nwcg.icbs.yantra.util.common.Logger;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author sdas
 */
public class NWCGVerifyGetIncidentResponse implements YCPDynamicConditionEx {
	private static final String className = 
		NWCGVerifyGetIncidentResponse.class.getName();
    private Map map;
	private static Logger logger = Logger.getLogger(NWCGVerifyGetIncidentResponse.class.getName());
    
    /** (non-Javadoc)
     * @see com.yantra.ycp.japi.YCPDynamicConditionEx#evaluateCondition(com.yantra.yfs.japi.YFSEnvironment, java.lang.String, java.util.Map, org.w3c.dom.Document)
     */
    public boolean evaluateCondition(YFSEnvironment env, String str, Map map, Document doc) {
        
        logger.verbose("Inside evaluate condition method");
        if(doc.getDocumentElement().getNodeName().equals("ROSSFailureDoc")){
            return true;
        }else{
            return false;
        }
    }

    /** (non-Javadoc)
     * @see com.yantra.ycp.japi.YCPDynamicConditionEx#setProperties(java.util.Map)
     */
    public void setProperties(Map map) {
        this.map = map; 
    }
    
    /*public static void main(String[] args) throws Exception{
        Document resultDoc = XMLUtil.createDocument("ROSSFailureDoc");
	    resultDoc.getDocumentElement().setAttribute("ErrorString","");
	    resultDoc.getDocumentElement().setAttribute("ErrorDetailMessage","");
	    System.out.println(resultDoc.getDocumentElement().getNodeName());
    }*/
}