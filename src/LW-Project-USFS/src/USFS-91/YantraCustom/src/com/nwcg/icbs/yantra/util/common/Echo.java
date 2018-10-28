package com.nwcg.icbs.yantra.util.common;

import com.yantra.yfs.japi.YFSEnvironment;
import org.w3c.dom.Document;


public class Echo {
    public Document echo(YFSEnvironment env, Document inDoc) throws Exception {
        //logger.verbose("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-  INSIDE ECHO -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        //logger.verbose(XMLUtil.extractStringFromDocument(inDoc));
        //logger.verbose("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-  End    ECHO -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
		return inDoc;
	}

}
