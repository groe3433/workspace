package gov.uscourts.chapweb.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "dtFilterView")
@ViewScoped
public class FilterView implements Serializable {

	private static final long serialVersionUID = 1L;

	public boolean filterByFullName(Object value, Object filter, Locale locale) {
		String strLocale = locale.toString();
		System.out.println("@@@@@ strLocale:: " + strLocale);
		String strValue = (String)value;
		System.out.println("@@@@@ strValue:: " + strValue);
        String strFilterText = (filter == null) ? null : filter.toString().trim();
        System.out.println("@@@@@ strFilterText:: " + strFilterText);

        if(strFilterText == null || strFilterText.equals("")) {
        	System.out.println("!!!!! Error One ...");
            return true;
        }
         
        if(value == null) {
        	System.out.println("!!!!! Error Two ...");
            return false;
        }
        
		String strPattern = "";
		String[] split = strValue.split(" ");
		if(split.length == 1) {
			strPattern = "^" + split[0] + "[a-zA-Z]?[\\s+]?[a-zA-Z]?[.]?[\\s+]?[a-zA-Z]+";
		} if(split.length == 2) {
			strPattern = "^" + split[0] + "[a-zA-Z]?[\\s+]?[a-zA-Z]?[.]?[\\s+]?" + split[1] + "[a-zA-Z]?";
		} if(split.length == 3) {
			strPattern = "^" + split[0] + "[a-zA-Z]?[\\s+]?" + split[1] + "[.]?[\\s+]?" + split[2] + "[a-zA-Z]?";
		}
		System.out.println("@@@@@ strPattern:: " + strPattern);
		Pattern p = Pattern.compile(strPattern);
    	Matcher m = p.matcher(strFilterText);
        
        if(m.matches()) {
        	return true;
        } else {
        	return false;
        }
    }
}