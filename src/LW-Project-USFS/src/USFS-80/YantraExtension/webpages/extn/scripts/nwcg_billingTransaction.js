function CheckInput(viewId, chkName) {
	var eleArray = document.forms["containerform"].elements;

	var IsReviewed;
	var IsExtracted;
	var ItemIds = "";
	var foundChk = false;
	
	for (var i = 0; i < eleArray.length; i++) {
		if (eleArray[i].name == chkName) {
			if (eleArray[i].checked) {
				var entityKey = eleArray[i].value;
				IsReviewed = parseValue(entityKey, 'IsReviewed');
				IsExtracted = parseValue(entityKey, 'IsExtracted');

				if (IsReviewed == "N" && IsExtracted == "N") {
					foundChk = true;
				} else {
					ItemIds += parseValue(entityKey, 'ItemId') + " ";
					eleArray[i].checked = false;
				}
			}
		}
	}
	var answer;
	var error = "Billing Transaction for ItemID: " + ItemIds + " cannot be deleted.";
	if (foundChk) {
		answer = confirm("Do you want to Delete this Transaction?");
		if (answer && ItemIds != "") {
			alert(error);
		}
	} else {
		alert(error);

	}

	if (!answer) {
		return false;
	}

	return true;
}

function parseValue(entityKey, variable) {

	var vars = entityKey.split('+');
	for (var i = 0; i < vars.length; i++) {
		var keyValue = vars[i].split('=');
		var entityKeyValue = decodeURIComponent(keyValue);
		if (entityKeyValue.indexOf(variable) == 0) {
			var indexOfN = entityKeyValue.indexOf('=') + 2;
			var lastIndexOf = entityKeyValue.length - 1;
			return entityKeyValue.substring(indexOfN, lastIndexOf);
		}
	}
}