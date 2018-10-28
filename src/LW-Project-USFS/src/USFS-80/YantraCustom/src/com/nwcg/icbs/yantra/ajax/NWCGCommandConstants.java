package com.nwcg.icbs.yantra.ajax;

import com.nwcg.icbs.yantra.util.common.ResourceUtil;

public class NWCGCommandConstants {

	public static final String COMMAND ="c";
	public static final String REQUEST_ID ="rid";
	
	public static final String RESPONSE_CONTENT_TYPE="text/xml";
	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String NO_CACHE = "no-cache";
	public static final String YFS_ENVIRONMENT = "YFS_ENVIRONMENT";
	public static final String USER_ID = "yantra";
	public static final String KEY_YANTRA_AJAX_ENV_UID ="yantra.ajax.env.userid";
	public static final String KEY_YANTRA_AJAX_ENV_PROGID ="yantra.ajax.env.progid";
	public static final String YANTRA_AJAX_ENV_UID =ResourceUtil.get(KEY_YANTRA_AJAX_ENV_UID);
	public static final String YANTRA_AJAX_ENV_PROGID = ResourceUtil.get(KEY_YANTRA_AJAX_ENV_PROGID);
	

	
	// Elements of the Command
	public static final String COMMAND_ELEM = "Command";

	public static final String INPUT_TEMPLATE_ELEM = "InputTemplate";

	public static final String API_OUTPUT_TEMPLATE = "ApiOutputTemplate";

	// Attributes
	public static final String COMMAND_NAME_ATTR = "Name";

	public static final String COMMAND_API_NAME_ATTR = "APIName";

	public static final String COMMAND_API_TYPE_ATTR = "APIType";
	
	public static final String API_OUTPUT_TEMPLATE_PATH_ATTR = "TemplatePath";
	
	//API types
	public static final String API_TYPE_FLOW ="FLOW";
	public static final String API_TYPE_API ="API";

	//Commandcache constants
	public  static final String COMMAND_FILE_KEY = "com.nwcg.icbs.yantra.ajax.commandfile";
	
	//Commandprocessor constants
	public static final String BINDING_IDENTIFIER = "xml:";
	public static final String API_TYPE_CUSTOM_API = "CUSTOM_API";
	
}
