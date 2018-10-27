package com.nwcg.icbs.yantra.api.trackableinventory;

import org.w3c.dom.Document;
import com.yantra.yfs.japi.YFSEnvironment;

public interface NWCGSerialRecord 
{
	public Document insertSerialRecord(YFSEnvironment env, Document doc) throws Exception ;
}
