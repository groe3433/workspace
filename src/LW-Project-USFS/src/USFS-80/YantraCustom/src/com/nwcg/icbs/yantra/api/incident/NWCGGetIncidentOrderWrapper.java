package com.nwcg.icbs.yantra.api.incident;

import java.util.Properties;

import org.w3c.dom.Document;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class NWCGGetIncidentOrderWrapper implements YIFCustomApi{

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	//getNWCGIncidentOrderWrapper
	
	public Document getNWCGIncidentOrderWrapper(YFSEnvironment env, Document inDoc)
    throws Exception
{
		
		String strYear = inDoc.getDocumentElement().getAttribute("Year");
		
		if(strYear.equalsIgnoreCase("")){
			inDoc.getDocumentElement().setAttribute("Year", " ");
		}
		return inDoc;
}

}
