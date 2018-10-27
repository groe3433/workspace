package com.fanatics.sterling.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.XPathAPI;
import org.apache.xpath.XPathContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
* 
* This is an utility class which uses  
* 					Xpath to access the nodes in a xml document.
* 
* @(#) XPathWrapper.java
* Created on         July 18, 2007
*                    11:30:22 AM
*
* Package Declaration:
* File Name:                  XPathWrapper.java
* Package Name:               com.fanatics.sterling.util
* Project name:               Fanatics
* Type Declaration:
* Class Name:                 XPathWrapper
* Type Comment:
* 	
*
* @author ssankar
* @version        1.0
* @history        
* 
*
*
* (C) Copyright 2006-2007 by owner.
* All Rights Reserved.
*
* This software is the confidential and proprietary information
* of the owner. ("Confidential Information").
* Redistribution of the source code or binary form is not permitted
* without prior authorization from the owner.
*
*/
public class XPathWrapper {
    private static Map pathCache = Collections.synchronizedMap( new HashMap(  ) );
    XPathContext ctx = null;
    int dtm = -1;
    PrefixResolverDefault resolver = null;
    
    //SOM performance issue fix
    Node sourceNode = null;
    public XPathWrapper( Node node ) {
        ctx = new XPathContext(  );
        sourceNode = node;
        if ( node.getNodeType(  ) == Node.DOCUMENT_NODE ) {
            Document doc = ( Document ) node;
            dtm = ctx.getDTMHandleFromNode( doc.getDocumentElement(  ) );
            resolver = new PrefixResolverDefault( doc.getDocumentElement(  ) );
        } else {
            dtm = ctx.getDTMHandleFromNode( node );
            resolver = new PrefixResolverDefault( node );
        }
    }
    
    /*
     * Commenting out this method as Apache Xpath util is causing performance implications.
     public NodeList getNodeList( String xpathExpr )
    throws Exception {
		*//** Defect 8634 commented caching the XPath Object in Mapping to avoid filling heap size **//*
       // XPath path = ( XPath ) pathCache.get( xpathExpr );
        
       // if ( path == null ) {
          XPath  path = new XPath( xpathExpr, null, null, XPath.SELECT );
           // pathCache.put( xpathExpr, path );
       // }
        
        XObject o = path.execute( ctx, dtm, resolver );
        NodeList nl = o.nodelist(  );
        
        return nl;
    }*/
    
    public NodeList getNodeList( String xpathExpr )
    throws Exception {
    	Element sourceElement = null;
    	if ( sourceNode.getNodeType() == Node.DOCUMENT_NODE ) {
            Document doc = ( Document ) sourceNode;
            sourceElement = doc.getDocumentElement();
    	}else{
    		sourceElement = (Element)sourceNode;
    	}
    	NodeList ret = null;
        try {
            ret = XPathAPI.selectNodeList(sourceElement, xpathExpr);
        } catch (TransformerException e) {
            throw e;
        }
        return ret;
   }
    
    public Node getNode( String xpathExpr ) throws Exception {
        NodeList nl = getNodeList( xpathExpr );
        
        if ( nl.getLength(  ) > 0 ) {
            return nl.item( 0 );
        } else {
            return null;
        }
    }
    
    public String getAttribute( String xpathExpr )
    throws Exception {
        Node n = getNode( xpathExpr );
        
        if ( n == null ) {
            return null;
        } else {
            return n.getNodeValue(  );
        }
    }
    
    public String getAttribute( String xpathExpr, String defaultValue )
    throws Exception {
        Node n = getNode( xpathExpr );
        
        if ( n == null ) {
            return defaultValue;
        } else {
            return n.getNodeValue(  );
        }
    }
    
}
