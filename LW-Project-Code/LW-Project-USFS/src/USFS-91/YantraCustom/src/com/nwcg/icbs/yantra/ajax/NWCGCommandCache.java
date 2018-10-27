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

package com.nwcg.icbs.yantra.ajax;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.nwcg.icbs.yantra.util.common.CommonUtilities;
import com.nwcg.icbs.yantra.util.common.NWCGApplicationLogger;
import com.nwcg.icbs.yantra.util.common.ResourceUtil;
import com.nwcg.icbs.yantra.util.common.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;

public class NWCGCommandCache {

	private static YFCLogCategory logger = NWCGApplicationLogger
			.instance(NWCGCommandCache.class);

	private HashMap commandMap = new HashMap();
	private static NWCGCommandCache cache = new NWCGCommandCache();

	private NWCGCommandCache() {
		super();
	}

	public static NWCGCommandCache getInstance() {
		return cache;
	}

	public void loadCommands() throws Exception {
		// Load the XML and call populate the command
		// first clear the cache
		commandMap.clear();

		if (logger.isVerboseEnabled())
			logger.verbose("Entering loadCommands("
					+ ResourceUtil.get(NWCGCommandConstants.COMMAND_FILE_KEY)
					+ ")");
		Document commandDoc = XMLUtil.getDocument(CommonUtilities
				.getResourceStream(ResourceUtil
						.get(NWCGCommandConstants.COMMAND_FILE_KEY)));
		NodeList nList = commandDoc
				.getElementsByTagName(NWCGCommandConstants.COMMAND_ELEM);
		for (int i = 0; i < nList.getLength(); i++) {
			Element n = (Element) nList.item(i);
			NWCGCommand c = new NWCGCommand(n);
			commandMap.put(c.getCommandName(), c);

		}
		if (logger.isVerboseEnabled())
			logger.verbose("Exiting loadCommands()");

	}

	public NWCGCommand getCommand(String commandName) {
		return (NWCGCommand) commandMap.get(commandName);
	}

}
