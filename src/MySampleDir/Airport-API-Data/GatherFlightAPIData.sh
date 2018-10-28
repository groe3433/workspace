#!/bin/bash

APIID="34d1b95b"
APIKey="afbb7ea1bf1b7b3d74d06b336abfcca1"
echo "API ID: $APIID";
echo "API Key: $APIKey";

echo "Downloading Data...";
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
echo "---------------------------------";
echo "Downloading Data...";




