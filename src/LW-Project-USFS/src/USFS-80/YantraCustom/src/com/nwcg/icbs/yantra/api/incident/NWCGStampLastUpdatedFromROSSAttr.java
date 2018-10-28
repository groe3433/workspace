/*
 * Created on May 21, 2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.nwcg.icbs.yantra.api.incident;

import java.util.GregorianCalendar;
import java.util.Properties;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.w3c.dom.Document;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author sdas
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class NWCGStampLastUpdatedFromROSSAttr implements YIFCustomApi{

    /* (non-Javadoc)
     * @see com.yantra.interop.japi.YIFCustomApi#setProperties(java.util.Properties)
     */
    public void setProperties(Properties arg0) throws Exception {
        // TODO Auto-generated method stub
        
    }
    
    public Document stampLastUpdatedFromROSSAttr(YFSEnvironment environment, Document document) throws Exception{
        
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        XMLGregorianCalendar xmlGregorialCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        String calStr = xmlGregorialCal.toString();
        document.getDocumentElement().setAttribute("LastUpdatedFromROSS",calStr);
        
        return document;
        
    }
    
    public static void main(String[] args)throws Exception{ 
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        XMLGregorianCalendar xmlGregorialCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        String calStr = xmlGregorialCal.toString();
        System.out.println(calStr);
    }

}
