package main;

import lists.RegionalAirports;
import stats.FlightDepartures;

public class main {

	/**
	 * Kim's Code
	 * 
	 * @param s
	 */
	public static void main(String[] s) {
		// sets arraylist of airport codes within (150 miles of 40° N, -111° W - Heber City, UT)
		RegionalAirports region1 = new RegionalAirports(-111, 40, 150);
		
		// print the arraylist
		System.out.println("(150 miles of 40° N, -111° W - Heber City, UT)");
		for(int i = 0; i < region1.getAlCodes().size(); i++) {
			FlightDepartures.calcDepartedFlights(region1.getAlCodes().get(i), 2017, 4, 12, 12);
		}
	}
}