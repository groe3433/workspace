package com.nwcg.icbs.yantra.ajax;

import org.w3c.dom.Document;

import com.yantra.yfs.japi.YFSEnvironment;

public interface NWCGAjaxAPI {
	
	public Document processAjaxRequest(YFSEnvironment env,Document doc);

}
