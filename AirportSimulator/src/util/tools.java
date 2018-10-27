package util;

public class tools {
	public static String prepAPIMethod(int longitude, int latitude, int radius) {
		String strAPIMethod = "/" + constants.strAirportAPIMethod + "/" + longitude + "/" + latitude + "/" + radius;
		return strAPIMethod;
	}

	public static String prepAPIMethod(String strAirportCode, int year, int month, int day, int hourOfDay) {
		String strAPIMethod = "/" + constants.strHistDepartMethod + "/" + strAirportCode + "/" + constants.strHistDepartMethodExtn + "/" + year + "/" + month + "/" + day + "/" + hourOfDay;
		return strAPIMethod;
	}
}
