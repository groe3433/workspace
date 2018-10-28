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

import com.yantra.yfc.log.YFCLogCategory;

/**
 * 
 * @author ssankar
 */
public class XPathWrapper {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(XPathWrapper.class);

	private static Map pathCache = Collections.synchronizedMap(new HashMap());
	XPathContext ctx = null;
	int dtm = -1;
	PrefixResolverDefault resolver = null;

	public XPathWrapper(Node node) {
		ctx = new XPathContext();

		if (node.getNodeType() == Node.DOCUMENT_NODE) {
			Document doc = (Document) node;
			dtm = ctx.getDTMHandleFromNode(doc.getDocumentElement());
			resolver = new PrefixResolverDefault(doc.getDocumentElement());
		} else {
			dtm = ctx.getDTMHandleFromNode(node);
			resolver = new PrefixResolverDefault(node);
		}
	}

	public NodeList getNodeList(String xpathExpr) throws Exception {
		XPath path = (XPath) pathCache.get(xpathExpr);

		if (path == null) {
			path = new XPath(xpathExpr, null, null, XPath.SELECT);
			pathCache.put(xpathExpr, path);
		}

		XObject o = path.execute(ctx, dtm, resolver);
		NodeList nl = o.nodelist();

		return nl;
	}

	public Node getNode(String xpathExpr) throws Exception {
		NodeList nl = getNodeList(xpathExpr);

		if (nl.getLength() > 0) {
			return nl.item(0);
		} else {
			return null;
		}
	}

	public String getAttribute(String xpathExpr) throws Exception {
		Node n = getNode(xpathExpr);

		if (n == null) {
			return null;
		} else {
			return n.getNodeValue();
		}
	}

	public String getAttribute(String xpathExpr, String defaultValue)
			throws Exception {
		Node n = getNode(xpathExpr);

		if (n == null) {
			return defaultValue;
		} else {
			return n.getNodeValue();
		}
	}

}
