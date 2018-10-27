package stats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import util.constants;
import util.tools;

/**
 * results returned in json or xml format for this API
 * Sample: https://api.flightstats.com/flex/flightstatus/historical/rest/v3/xml/airport/status/SLC/dep/2017/4/11/12?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&utc=false
 * 
 * Output: <flightStatuses>
 * 			<flightStatus>
 * 
 * @author Administrator
 */
public class FlightDepartures {
	public static void calcDepartedFlights(String strAirportCode, int year, int month, int day, int hourOfDay) {
		String strAPIMethod = tools.prepAPIMethod(strAirportCode, year, month, day, hourOfDay);
		String strUrl = constants.strHistoricalDepartures + strAPIMethod + constants.strAppCredentials;
		try {
			URL url = new URL(strUrl);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"))) {
				for (String line; (line = reader.readLine()) != null;) {
					System.out.println(line);
					// convert line to xml document. 
					// count "flightstatus" elements. (these are flights that left the given airport in your range) 
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
}