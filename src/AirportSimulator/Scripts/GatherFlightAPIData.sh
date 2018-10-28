#!/bin/bash

APIID="34d1b95b"
APIKey="afbb7ea1bf1b7b3d74d06b336abfcca1"
echo "API ID: $APIID";
echo "API Key: $APIKey";

echo "Downloading Data...";
echo "---------------------------------";
echo "Downloading Airport Category...";
echo "Active Airport";
curl -v -X GET "https://api.flightstats.com/flex/airports/rest/v1/json/active?appId=$APIID&appKey=$APIKey" >> /Users/Administrator/Desktop/Airport-API-Data/textfiles/Active-Airport.txt;
sleep 10s;
echo "Airport By City Code (CDC)";
curl -v -X GET "https://api.flightstats.com/flex/airports/rest/v1/json/cityCode/CDC?appId=$APIID&appKey=$APIKey" >> /Users/Administrator/Desktop/Airport-API-Data/textfiles/Airport-By-City-Code-CDC.txt;
sleep 10s;
echo "Airport By FlightStats Code (CDC)";
curl -v -X GET "https://api.flightstats.com/flex/airports/rest/v1/json/fs/CDC?appId=$APIID&appKey=$APIKey" >> /Users/Administrator/Desktop/Airport-API-Data/textfiles/Airport-By-FlightStats-Code-CDC.txt;
sleep 10s;
echo "Airport By IATA Code (CDC)";
curl -v -X GET "https://api.flightstats.com/flex/airports/rest/v1/json/iata/CDC?appId=$APIID&appKey=$APIKey" >> /Users/Administrator/Desktop/Airport-API-Data/textfiles/Airport-By-IATA-Code-CDC.txt;
sleep 10s;
echo "Airport By ICAO Code (CDC)";
curl -v -X GET "https://api.flightstats.com/flex/airports/rest/v1/json/icao/KCDC?appId=$APIID&appKey=$APIKey" >> /Users/Administrator/Desktop/Airport-API-Data/textfiles/Airport-By-ICAO-Code-CDC.txt;
sleep 10s;
echo "Airports By Country Code";
curl -v -X GET "https://api.flightstats.com/flex/airports/rest/v1/json/countryCode/US?appId=$APIID&appKey=$APIKey" >> /Users/Administrator/Desktop/Airport-API-Data/textfiles/Airports-By-Country-Code.txt;
sleep 10s;
echo "Airports within (200) Radius of (Heber City)";
curl -v -X GET "https://api.flightstats.com/flex/airports/rest/v1/json/withinRadius/-111/40/200?appId=$APIID&appKey=$APIKey" >> /Users/Administrator/Desktop/Airport-API-Data/textfiles/Airports-within-200Radius-of-HeberCity.txt;
sleep 10s;
echo "All Airports";
curl -v -X GET "https://api.flightstats.com/flex/airports/rest/v1/json/all?appId=$APIID&appKey=$APIKey" >> /Users/Administrator/Desktop/Airport-API-Data/textfiles/All-Airports.txt;
sleep 10s;
echo "Current Airport By Code (CDC)";
curl -v -X GET "https://api.flightstats.com/flex/airports/rest/v1/json/CDC/today?appId=$APIID&appKey=$APIKey" >> /Users/Administrator/Desktop/Airport-API-Data/textfiles/Current-Airport-ByCode-CDC.txt;
sleep 10s;
echo "---------------------------------";
echo "Downloading Airline Category...";
echo "All Airport";
curl -v  -X GET "https://api.flightstats.com/flex/airlines/rest/v1/json/all?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
sleep 10s;
echo "Active Airlines";
curl -v  -X GET "https://api.flightstats.com/flex/airlines/rest/v1/json/active?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
sleep 10s;
echo "Airline Flight Status Code (AA)";
curl -v  -X GET "https://api.flightstats.com/flex/airlines/rest/v1/json/fs/AA?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
sleep 10s;
echo "Airline By IATA Code (AA)";
curl -v  -X GET "https://api.flightstats.com/flex/airlines/rest/v1/json/iata/AA?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
sleep 10s;
echo "Airline Status ICAO Code (AAL)";
curl -v  -X GET "https://api.flightstats.com/flex/airlines/rest/v1/json/icao/AAL?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
sleep 10s;
echo "---------------------------------";
echo "Downloading Connection Category...";
echo "Find connections arriving as early as possible before the given time (First Flight In)";
echo "passenger/cargo/all"
curl -v  -X GET "https://api.flightstats.com/flex/connections/rest/v2/json/firstflightin/SLC/to/DEN/arriving_before/2017/12/24/12/0?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&numHours=6&maxConnections=2&includeSurface=false&payloadType=passenger&includeCodeshares=true&includeMultipleCarriers=true&maxResults=25"
sleep 10s;
echo "Find connections leaving as early as possible after the given time (First Flight Out)";
echo "passenger/cargo/all"
curl -v  -X GET "https://api.flightstats.com/flex/connections/rest/v2/json/firstflightout/SLC/to/DEN/leaving_after/2016/12/24/12/0?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&numHours=6&maxConnections=2&includeSurface=false&payloadType=passenger&includeCodeshares=true&includeMultipleCarriers=true&maxResults=25"
sleep 10s;
echo "Find connections arriving as late as possible before the given time (Last Flight In)";
echo "passenger/cargo/all"
curl -v  -X GET "https://api.flightstats.com/flex/connections/rest/v2/json/lastflightin/SLC/to/DEN/arriving_before/2016/12/24/12/0?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&numHours=6&maxConnections=2&includeSurface=false&payloadType=passenger&includeCodeshares=true&includeMultipleCarriers=true&maxResults=25"
sleep 10s;
echo "Find connections leaving as late as possible after the given time (Last Flight Out)";
echo "passenger/cargo/all"
curl -v  -X GET "https://api.flightstats.com/flex/connections/rest/v2/json/lastflightout/SLC/to/DEN/leaving_after/2016/12/24/12/0?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&numHours=6&maxConnections=2&includeSurface=false&payloadType=passenger&includeCodeshares=true&includeMultipleCarriers=true&maxResults=25"
sleep 10s;
echo "---------------------------------";
echo "Downloading Alerts Category...";
# requires HTTP post catcher...
sleep 10s;
echo "---------------------------------";
echo "Downloading Delay Index Category...";
curl -v  -X GET "https://api.flightstats.com/flex/delayindex/rest/v1/json/airports/CDC?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&classification=5&score=3"
curl -v  -X GET "https://api.flightstats.com/flex/delayindex/rest/v1/json/country/US?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&classification=5&score=3"
curl -v  -X GET "https://api.flightstats.com/flex/delayindex/rest/v1/json/region/Africa?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&classification=5&score=3"
curl -v  -X GET "https://api.flightstats.com/flex/delayindex/rest/v1/json/state/UT?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&classification=5&score=3"
sleep 10s;
echo "---------------------------------";
echo "Downloading Equipment Category...";
curl -v  -X GET "https://api.flightstats.com/flex/equipment/rest/v1/json/all?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/equipment/rest/v1/json/iata/72W?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
sleep 10s;
echo "---------------------------------";
echo "Downloading FIDS Category...";
curl -v  -X GET "https://api.flightstats.com/flex/fids/rest/v1/json/CDC/departures?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&requestedFields=airlineCode%2CflightNumber%2Ccity%2CcurrentTime%2Cgate%2Cremarks&lateMinutes=15&useRunwayTimes=false&excludeCargoOnlyFlights=false"
curl -v  -X GET "https://api.flightstats.com/flex/fids/rest/v1/json/CDC/arrivals?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&requestedFields=airlineCode%2CflightNumber%2Ccity%2CcurrentTime%2Cgate%2Cremarks&lateMinutes=15&useRunwayTimes=false&excludeCargoOnlyFlights=false"
sleep 10s;
echo "---------------------------------";
echo "Downloading Flight Status and Track Category...";
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/status/882287039?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/status/AA/100/arr/2017/04/26?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&utc=false"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/status/AA/100/dep/2017/04/26?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&utc=false"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/tracks/AA/100/arr/2017/04/26?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&utc=false&includeFlightPlan=false&maxPositions=2"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/track/882287039?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&includeFlightPlan=false&maxPositions=2"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/rest/v2/json/flight/tracks/AA/100/dep/2017/04/26?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1&utc=false&includeFlightPlan=false&maxPositions=2"
sleep 10s;
echo "---------------------------------";
echo "Downloading Historical Flight Status Category...";
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/historical/rest/v3/json/flight/status/882287039?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/historical/rest/v3/json/flight/status/AA/100/dep/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&utc=false"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/historical/rest/v3/json/flight/status/AA/100/arr/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&utc=false"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/historical/rest/v3/json/airport/status/SLC/dep/2017/04/26/12?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&utc=false&numHours=1&maxFlights=5"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/historical/rest/v3/json/airport/status/SLC/arr/2017/04/26/12?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&utc=false&numHours=1&maxFlights=5"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/historical/rest/v3/json/route/status/SLC/DEN/dep/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&hourOfDay=0&utc=false&numHours=24&maxFlights=5"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/historical/rest/v3/json/route/status/SLC/DEN/dep/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&hourOfDay=12&utc=false&numHours=24&maxFlights=5"
curl -v  -X GET "https://api.flightstats.com/flex/flightstatus/historical/rest/v3/json/flight/status/tailNumber/N645RW/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76"
sleep 10s;
echo "---------------------------------";
echo "Downloading Historical Flight Track Category...";
curl -v  -X GET "https://api.flightstats.com/flight/tracks/882287039?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76"
curl -v  -X GET "https://api.flightstats.com/flight/tracks/AA/100/dep/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&utc=false"
curl -v  -X GET "https://api.flightstats.com/flight/tracks/AA/100/arr/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&utc=false"
curl -v  -X GET "https://api.flightstats.com/airport/tracks/SLC/dep/2017/04/26/12?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&utc=false&numHours=1&maxFlights=5"
curl -v  -X GET "https://api.flightstats.com/airport/tracks/SLC/arr/2017/04/26/12?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&utc=false&numHours=1&maxFlights=5"
curl -v  -X GET "https://api.flightstats.com/route/tracks/SLC/DEN/dep/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&hourOfDay=0&utc=false&numHours=24&maxFlights=5"
curl -v  -X GET "https://api.flightstats.com/route/tracks/SLC/DEN/dep/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76&hourOfDay=0&utc=false&numHours=24&maxFlights=5"
curl -v  -X GET "https://api.flightstats.com/flight/tracks/tailNumber/N645RW/2017/04/26?appId=456bf348&appKey=4a3ccedd81407d3880fdd95b36afef76"
sleep 10s;
echo "---------------------------------";
echo "Downloading Ratings Category...";
curl -v  -X GET "https://api.flightstats.com/flex/ratings/rest/v1/json/route/SLC/DEN?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/ratings/rest/v1/json/flight/HA/25?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
sleep 10s;
echo "---------------------------------";
echo "Downloading Scheduled/Routes/Flights Category...";
curl -v  -X GET "https://api.flightstats.com/flex/schedules/rest/v1/json/flight/AA/100/departing/2017/04/26?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/schedules/rest/v1/json/flight/AA/100/arriving/2017/04/26?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/schedules/rest/v1/json/from/ABQ/to/DFW/departing/2017/04/26?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/schedules/rest/v1/json/from/ABQ/to/DFW/arriving/2017/04/26?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/schedules/rest/v1/json/from/ABQ/departing/2017/04/26/12?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/schedules/rest/v1/json/to/ABQ/arriving/2017/04/26/12?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
sleep 10s;
echo "---------------------------------";
echo "Downloading Weather Category...";
curl -v  -X GET "https://api.flightstats.com/flex/weather/rest/v1/json/all/CDC?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/weather/rest/v1/json/metar/CDC?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/weather/rest/v1/json/taf/CDC?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
curl -v  -X GET "https://api.flightstats.com/flex/weather/rest/v1/json/zf/CDC?appId=34d1b95b&appKey=afbb7ea1bf1b7b3d74d06b336abfcca1"
sleep 10s;
echo "---------------------------------";
echo "Downloading Data...";












