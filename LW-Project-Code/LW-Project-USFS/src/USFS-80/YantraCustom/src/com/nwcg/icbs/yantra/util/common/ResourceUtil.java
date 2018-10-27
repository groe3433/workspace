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

package com.nwcg.icbs.yantra.util.common;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import com.nwcg.icbs.yantra.commCore.NWCGAAConstants;
import com.nwcg.icbs.yantra.constant.common.NWCGConstants;

/**
 * Load and retrieve resource/property.
 * At class loading, it will load from the following property files: <i>/resources/yfs.properties,
 * /resources/yifclient.properties, /resources/extn/yantraimpl.properties</i>. Resources are also loaded in that order.
 * <br>
 * If open failed for any of those files, it prints the error and continues.
 * <p>
 * This class should be used to retrieve application wide properties/resources.
 * <p>
 * Subclass of this class can load its own resource files. Only need to implement a static block as:
 * <pre>
 *    static
 *    {
 * loadResourceFile("file1");
 * loadResourceFile("file2");
 *	  }
 * </pre>
 *
 */
public class ResourceUtil {
    private static Properties resources = new Properties();
    //private static Logger logger = Logger.getLogger(ResourceUtil.class.getName());

    private static ArrayList msgResBundles = new ArrayList();
    private static ArrayList msgResBundleNames = new ArrayList();
    private static int numMsgResBundlesLoaded = 0;

    private final static String DESC_NOT_FOUND = "Error Description Not Found";

    //Currently, we see the need for only PROD and DEV to be two modes
    //in which Yantra would be run. Therefore, the flag is a boolean.
    //If mode modes develop in the future (like TEST etc), then the key would
    //have to be redefined.
    public final static String YANTRA_RUNTIME_MODE = "yantra.implementation.runtime.mode";
    public static boolean IS_PRODUCTION_MODE = true;

    static {
        loadDefaultResources();
    }

    public static void loadDefaultResources() {
        msgResBundleNames.clear();
        msgResBundles.clear();
        resources.clear();
        // Jay: with yantra 8.0 these files are under root directory of properties.jar file
        // removing resources from the load directory
        //loadResourceFile("yfs.properties");
        //loadResourceFile("yifclient.properties");
        loadResourceFile("/resources/extn/NWCGAnAEnvironment.properties");
        loadResourceFile("/resources/extn/yantraimpl.properties");
        loadResourceFile("/resources/extn/extnbundle.properties");
        System.out.println("Loading A&A Resources");
        loadResourceFile("/resources/extn/NWCGAnAImpl.properties");
        
        loadMsgCodes("resources/extn/messagecodes");
        /*
        Handling the same using the build scripts
        try
        {
        	loadResourceFile("/resources/extn/build.properties");
        }
        catch(Exception ex)
        {
        	/// never mine, do nothing
        }
        */
        //Check if running in dev mode, if so reset the flag.
        try {
            IS_PRODUCTION_MODE = get(YANTRA_RUNTIME_MODE, "true").equalsIgnoreCase("false") ? false : true;
        } catch (Exception e) {
            //Ignore exception. We'll assume production mode.
        }
    }

    /**
     *	Loading resources.
     *	@param filename the resource filename, must be available on CLASSPATH.
     */
    public static void loadResourceFile(String filename) {
        InputStream is = null;
        try {
            is = ResourceUtil.class.getResourceAsStream(filename);
            resources.load(is);
            is.close();
            NWCGLoggerUtil.Log.info("Loaded Properties from: " + filename + ": "
                    + ResourceUtil.class.getResource(filename));
        } catch (Exception e) {
        	NWCGLoggerUtil.Log.info("Error loading resource from file [" + filename + "]: " + e.getMessage());
        }
    }

    /**
     *	Get resource by name
     *	@param name the resource name
     */
    public static String get(String name) {
        String retVal = resources.getProperty(name);
        if(retVal != null)
        	retVal = retVal.trim();
    	return retVal;
    }

    /**
     *	Get resource or the default value
     *	@param name the resource name.
     *	@param defaultValue the default value if the resource does not exist.
     */
    public static String get(String name, String defaultValue) {
        String retval = resources.getProperty(name);
        if (retval == null || retval.equals(NWCGConstants.EMPTY_STRING))
            retval = defaultValue;
        return retval.trim();
    }

    /**
     *
     * @param key resource name
     * @param def default value
     * @return true if the value is 'Y' or 'true' (case incensitive) false otherwise
     */
    public static boolean getAsBoolean(String key, boolean def) {
        String val = (String) resources.get(key);
        if (null == val) {
            return def;
        }
        if (val.equalsIgnoreCase("Y") ||
                val.equalsIgnoreCase("true")) {
            return true;
        }

        return false;
    }

    /**
     *
     * @param key
     * @param def
     * @return double value of the resource. def in case of NumberFormatException
     */
    public static double getAsDouble(String key, double def) {
        String val = (String) resources.get(key);
        if (null == val) {
            return def;
        }
        double ret = 0.0;
        try {
            ret = Double.parseDouble(val);
            return ret;
        } catch (NumberFormatException e) {
        	NWCGLoggerUtil.Log.info("Unable to convert value to double:" + val);
            return def;
        }
    }

    /**
     *
     * @param key
     * @param def
     * @return   int value of the key as defined in the properies file.
     */
    public static int getAsInt(String key, int def) {
        String val = (String) resources.get(key);
        if (null == val) {
            return def;
        }
        int ret = 0;
        try {
            ret = Integer.parseInt(val);
            return ret;
        } catch (NumberFormatException e) {
        	NWCGLoggerUtil.Log.info("Unable to convert value to int:" + val);
        }
        return def;
    }

	/**
	 *
	 * @param key
	 * @param def
	 * @return   int value of the key as defined in the properies file.
	 */
	public static long getAsLong(String key, long def) {
        String val = (String) resources.get(key);
        if (null == val) {
            return def;
        }
        long ret = 0L;
        try {
            ret = Long.parseLong(val);
            return ret;
        } catch (NumberFormatException e) {
        	NWCGLoggerUtil.Log.info("Unable to convert value to long:" + val);
            return def;
        }
    }

    public static void list() {
    	NWCGLoggerUtil.Log.info("Managing Following Properties");
        Enumeration keys = resources.keys();
        StringBuffer sb = new StringBuffer();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            sb.append(key).append("=").append(resources.getProperty(key)).append("\n");
        }
        NWCGLoggerUtil.Log.info(sb.toString());
    }

    /**
     *
     * @return the list of resources defined
     */
    public static Properties getAllResources() {
        return resources;
    }

    public static ResourceBundle loadMsgCodes(String componentName) {
        return loadMsgCodes(componentName, null);
    }

    /**
     * Use this method to load messages.
     * Message from /resources/extn/messagecodes.properties will be loaded by default
     * @param componentName
     * @param locale
     * @return
     */
    public static ResourceBundle loadMsgCodes(String componentName, Locale locale) {
        ResourceBundle rb = null;

        String key = componentName;
        if (locale != null)
            key += "_" + locale.getDisplayName();

        if (!msgResBundleNames.contains(key)) {
            try {
                if (locale != null)
                    rb = ResourceBundle.getBundle(componentName, locale);
                else
                    rb = ResourceBundle.getBundle(componentName);

                msgResBundleNames.add(componentName);
                msgResBundles.add(rb);
                numMsgResBundlesLoaded++;
            } catch (MissingResourceException mre) {
            	NWCGLoggerUtil.Log.info("Unable to load error codes from Resource Bundle: " + key);
            }
        }
        return rb;
    }

    /**
     * This method returns the error description for the given errorCode as specified in
     * the message Bundle file. If a matching entry is not found
     * then it returns "Error Description Not Found"
     * @param errorCode
     * @return
     */

    public static String resolveMsgCode(String errorCode) {
        return resolveMsgCode(errorCode, null);
    }

    /**
     * This method returns the error description for the given errorCode as specified in
     * the message Bundle file. If a matching entry is not found
     * then it returns "Error Description Not Found".
     *
     * Use errorArgs to parameterize error description
     * @param errorCode
     * @param errorArgs
     * @return
     */
    public static String resolveMsgCode(String errorCode, Object[] errorArgs) {
        String desc = null;
        int resBundleIndex = -1;

        while (desc == null && ++resBundleIndex < numMsgResBundlesLoaded) {
            ResourceBundle rb = (ResourceBundle) msgResBundles.get(resBundleIndex);
            try {
                desc = rb.getString(errorCode);
                desc = MessageFormat.format(desc, errorArgs);
            } catch (MissingResourceException mre) {
                //Ignore as we'd set it to erro desc not found
                desc = DESC_NOT_FOUND;
            } catch (IllegalArgumentException e) {
                //Ignore. If any error had occured, it'll be evident
                //from the raw text that is present as error description
            } catch (NullPointerException ex)
            {
            	// IGNORE THIS SCENARIO, this will throw an Nullpointer Exception which is misleading...
            }           
        }
        return desc;
    }
    
   public static String getNamespacePackageFromNamespace(String strNamespace) {
	   String returnStr = null ;
	   if(strNamespace == null)
		   returnStr = NWCGConstants.EMPTY_STRING;	   
	   if(strNamespace.equals(NWCGAAConstants.RESOURCE_ORDER_NAMESPACE))
		   returnStr = NWCGAAConstants.RESOURCEORDER_PACKAGE_NAME;
	   else if(strNamespace.equals(NWCGAAConstants.CATALOG_NAMESPACE_OB))
		   returnStr = NWCGAAConstants.CATALOG_PACKAGE_NAME;	   
	   return returnStr ;
   }
}