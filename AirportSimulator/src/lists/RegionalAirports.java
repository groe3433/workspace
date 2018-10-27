package lists;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.constants;
import util.tools;

/**
 * results returned in json format only for this API
 * Sample: https://api.flightstats.com/flex/airports/rest/v1/json/withinRadius/-111/40/150?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1
 * 
 * @author Administrator
 */
public class RegionalAirports {

	private ArrayList<String> alCodes = new ArrayList<String>();

	public ArrayList<String> getAlCodes() {
		return alCodes;
	}

	public RegionalAirports(int longitude, int latitude, int radius) {
		String strAPIMethod = tools.prepAPIMethod(longitude, latitude, radius);
		String strUrl = constants.strAirportAPIBase + strAPIMethod + constants.strAppCredentials;
		try {
			URL url = new URL(strUrl);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
				ArrayList<String> alAirportEntries = new ArrayList<String>();
				for (String line; (line = reader.readLine()) != null;) {
					int intBraceCnt = 0;
					String strSubLine = "";
					Boolean isStartSubLine = false;
					for (int i = 0; i < line.length(); i++) {
						if (line.charAt(i) == '{') {
							// start on second brace
							if (intBraceCnt != 0) {
								isStartSubLine = true;
							}
							intBraceCnt++;
							continue;
						}
						if (line.charAt(i) == '}') {
							isStartSubLine = false;
							alAirportEntries.add(strSubLine);
							strSubLine = "";
							continue;
						}
						if (isStartSubLine && line.charAt(i) != '\"') {
							strSubLine = strSubLine + line.charAt(i);
						}
					}
				}
				for (String mySubString : alAirportEntries) {
					List<String> listSubLineEntries = Arrays.asList(mySubString.split(","));
					for (String strSubLineEntry : listSubLineEntries) {
						List<String> listKeyValuePair = Arrays.asList(strSubLineEntry.split(":"));
						if (listKeyValuePair.size() == 2) {
							if (listKeyValuePair.get(0).equals("faa")) {
								alCodes.add(listKeyValuePair.get(1));
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
