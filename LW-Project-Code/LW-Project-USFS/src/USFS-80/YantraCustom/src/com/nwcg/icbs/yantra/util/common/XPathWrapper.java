/*
 * XPathWrapper.java
 *
 * This software is the confidential and proprietary information of
 * Yantra Corp. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Yantra.
 */


package com.nwcg.icbs.yantra.util.common;

import java.util.Collections;

import java.util.HashMap;
import java.util.Map;
import org.apache.xml.utils.PrefixResolverDefault;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 *
 * @author ssankar
 */
public class XPathWrapper {
    private static Map pathCache = Collections.synchronizedMap( new HashMap(  ) );
    XPathContext ctx = null;
    int dtm = -1;
    PrefixResolverDefault resolver = null;
    
    public XPathWrapper( Node node ) {
        ctx = new XPathContext(  );
        
        if ( node.getNodeType(  ) == Node.DOCUMENT_NODE ) {
            Document doc = ( Document ) node;
            dtm = ctx.getDTMHandleFromNode( doc.getDocumentElement(  ) );
            resolver = new PrefixResolverDefault( doc.getDocumentElement(  ) );
        } else {
            dtm = ctx.getDTMHandleFromNode( node );
            resolver = new PrefixResolverDefault( node );
        }
    }
    
    public NodeList getNodeList( String xpathExpr )
    throws Exception {
        XPath path = ( XPath ) pathCache.get( xpathExpr );
        
        if ( path == null ) {
            path = new XPath( xpathExpr, null, null, XPath.SELECT );
            pathCache.put( xpathExpr, path );
        }
        
        XObject o = path.execute( ctx, dtm, resolver );
        NodeList nl = o.nodelist(  );
        
        return nl;
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
