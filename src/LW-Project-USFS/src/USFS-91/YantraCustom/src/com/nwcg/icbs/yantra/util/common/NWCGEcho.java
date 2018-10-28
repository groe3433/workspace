package com.nwcg.icbs.yantra.util.common;

import com.yantra.yfs.japi.YFSEnvironment;
import org.w3c.dom.Document;

public class NWCGEcho 
{
	public Document echo(YFSEnvironment env, Document inDoc) throws Exception 
	{
		//logger.verbose("++++++++++++++++++++++++++++++++++++++++++++++++++  Begin NWCGEcho +++++++++++++++++++++++++" + "\n\n" + XMLUtil.getXMLString(inDoc) + "\n\n");
		//logger.verbose("++++++++++++++++++++++++++++++++++++++++++++++++++  End NWCGEcho +++++++++++++++++++++++++++");
		return inDoc;
	}
}
