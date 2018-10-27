package com.nwcg.icbs.yantra.util.common;

import com.yantra.yfs.japi.YFSEnvironment;
import org.w3c.dom.Document;


public class Echo {
    public Document echo(YFSEnvironment env, Document inDoc) throws Exception {
        System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-  INSIDE ECHO -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        System.out.println(XMLUtil.extractStringFromDocument(inDoc));
        System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-  End    ECHO -+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
		return inDoc;
	}

}
