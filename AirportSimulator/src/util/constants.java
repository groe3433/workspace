package util;

public class constants {
	public static final String strAirportAPIBase = "https://api.flightstats.com/flex/airports/rest/v1/json";
	public static final String strAirportAPIMethod = "withinRadius";
	
	public static final String strHistoricalDepartures = "https://api.flightstats.com/flex/flightstatus/historical/rest/v3/xml";
	public static final String strHistDepartMethod = "airport/status";
	public static final String strHistDepartMethodExtn = "dep";
	
	public static final String strAppIdKey = "appId";
	//456bf348
	public static final String strAppIdVal = "34d1b95b";
	public static final String strAppKeyKey = "appKey";
	//4a3ccedd81407d3880fdd95b36afef76
	public static final String strAppKeyVal = "afbb7ea1bf1b7b3d74d06b336abfcca1";	
	public static final String strAppCredentials = "?" + strAppIdKey + "=" + strAppIdVal + "&" + strAppKeyKey + "=" + strAppKeyVal;
}
