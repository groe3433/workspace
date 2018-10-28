function validateDeRegisterIncidentInterest()
{
	var incidentNo = document.getElementById("xml:/NWCGIncidentOrder/@IncidentNo");
	var flagValue = document.getElementById("xml:/NWCGIncidentOrder/@RegisterInterestInROSS");

	var incidentLocked = document.getElementById("xml:/NWCGIncidentOrder/@IncidentLocked");

	if( incidentLocked != null && incidentLocked.value == "Y") 
	{
		alert("Incident Locked. No action taken.");
		return false;
	}

	if(flagValue != null && !flagValue.checked)
	{
		alert("Incident Already De-registered");
		return false;
	}
	return true;
}

function validateRegisterIncidentInterest()
{
	var incidentNo = document.getElementById("xml:/NWCGIncidentOrder/@IncidentNo");
	var flagValue = document.getElementById("xml:/NWCGIncidentOrder/@RegisterInterestInROSS");

	var incidentLocked = document.getElementById("xml:/NWCGIncidentOrder/@IncidentLocked");

	if( incidentLocked != null && incidentLocked.value == "Y") 
	{
		alert("Incident Locked. No action taken.");
		return false;
	}

	if( flagValue != null && flagValue.checked)
	{
		alert("Incident Already Registered");
		return false;
	}
	return true;
}

function isIncidentLocked()
{
	
	var incidentLocked = document.getElementById("xml:/NWCGIncidentOrder/@IncidentLocked");

	if( incidentLocked != null && incidentLocked.value == "Y") 
	{
		alert("Incident Locked. No action taken.");
		return false;
	}
	return true;
}
