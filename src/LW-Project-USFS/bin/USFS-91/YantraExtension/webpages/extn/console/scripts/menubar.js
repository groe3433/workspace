var cursrc;

function logout()	{
	try {
		var bprompt = window.confirm(YFCMSG028);
		if (bprompt)	 {
			window.location.href='/smcfs/console/logout.jsp';
		}
	}
	catch (exception) {
		// Do nothing because this sometimes throws an unspecified error because of an IE bug
		// that exists in the onbeforeunload event.
	}
}
function popupAboutBox()	{
	var features = "dialogHeight:375px;dialogWidth:385px;dialogLeft:325px;dialogTop:200px;scroll:no;resizable:no;help:no;status:no;edge:sunken;unadorned:yes";
	window.showModalDialog("/smcfs/extn/console/about.jsp","",features);
}

function ChangeTheme(theme) {

	// Since we are doing a post, make sure that there are no fields are in error
	if (validateControlValues()) {
		document.body.style.cursor="wait";
		var url=window.location.href;
		var q = url.indexOf('CurrentActionID');
		var tmp = "";
		var z = url.indexOf('?');
		if (z>=0)   
		{  
			var s=url.substring(0,z+1);
			if(q >=0)
				tmp = url.substring(z+1,q-1);
			else
				tmp = url.substring(z+1);
			url= s +"ChangeAction=Theme&ID="+encodeURIComponent(theme);
			if(tmp)
				url = url +"&"+ tmp;
		}
		else    
		{
			url=url+"?ChangeAction=Theme&ID="+encodeURIComponent(theme);
		}
		postForm(url);
	}
	else
		return (false);
}

function ChangeLocale(locale)	{

    // Since we are doing a post, make sure that there are no fields are in error
	if (validateControlValues()) {
		document.body.style.cursor="wait";
		var url=window.location.href;
		var q = url.indexOf('CurrentActionID');
		var tmp = "";
		var z = url.indexOf('?');
		if (z>=0)   
		{  
			var s=url.substring(0,z+1);
			if(q >=0)
				tmp = url.substring(z+1,q-1);
			else
				tmp = url.substring(z+1);
			url= s +"ChangeAction=Locale&ID="+encodeURIComponent(locale);
			if(tmp)
				url = url +"&"+ tmp;
		}
		else    
		{
			url=url+"?ChangeAction=Locale&ID="+encodeURIComponent(locale);
		}
		displayLocaleMessage();
		postForm(url);
	}
	else
		return (false);
}

function setMenuCorner()	{
	drawMenuCorner(document.all.MenuCorner);
}

function drawMenuCorner(obj)	{
	obj.innerHTML='<span class="menucornerbg" style="left:0;top:0;"></span><span class="menucornerbg" style="left:0;top:1;"></span><span class="menucornerbg" style="left:1;top:1;"></span><span class="menucornerbg" style="left:0;top:2;"></span><span class="menucornerbg" style="left:1;top:2;"></span><span class="menucornerbg" style="left:2;top:2;"></span><span class="menucornerbg" style="left:0;top:3;"></span><span class="menucornerbg" style="left:1;top:3;"></span><span class="menucornerbg" style="left:2;top:3;"></span><span class="menucornerbg" style="left:3;top:3;"></span><span class="menucornerbg" style="left:0;top:4;"></span><span class="menucornerbg" style="left:1;top:4;"></span><span class="menucornerbg" style="left:2;top:4;"></span><span class="menucornerbg" style="left:3;top:4;"></span><span class="menucornerbg" style="left:4;top:4;"></span><span class="menucornerbg" style="left:0;top:5;"></span><span class="menucornerbg" style="left:1;top:5;"></span><span class="menucornerbg" style="left:2;top:5;"></span><span class="menucornerbg" style="left:3;top:5;"></span><span class="menucornerbg" style="left:4;top:5;"></span><span class="menucornerbg" style="left:5;top:5;"></span><span class="menucornerbg" style="left:0;top:6;"></span><span class="menucornerbg" style="left:1;top:6;"></span><span class="menucornerbg" style="left:2;top:6;"></span><span class="menucornerbg" style="left:3;top:6;"></span><span class="menucornerbg" style="left:4;top:6;"></span><span class="menucornerbg" style="left:5;top:6;"></span><span class="menucornerbg" style="left:6;top:6;"></span><span class="menucornerbg" style="left:0;top:7;"></span><span class="menucornerbg" style="left:1;top:7;"></span><span class="menucornerbg" style="left:2;top:7;"></span><span class="menucornerbg" style="left:3;top:7;"></span><span class="menucornerbg" style="left:4;top:7;"></span><span class="menucornerbg" style="left:5;top:7;"></span><span class="menucornerbg" style="left:6;top:7;"></span><span class="menucornerbg" style="left:7;top:7;"></span><span class="menucornerbg" style="left:0;top:8;"></span><span class="menucornerbg" style="left:1;top:8;"></span><span class="menucornerbg" style="left:2;top:8;"></span><span class="menucornerbg" style="left:3;top:8;"></span><span class="menucornerbg" style="left:4;top:8;"></span><span class="menucornerbg" style="left:5;top:8;"></span><span class="menucornerbg" style="left:6;top:8;"></span><span class="menucornerbg" style="left:7;top:8;"></span><span class="menucornerbg" style="left:8;top:8;"></span><span class="menucornerbg" style="left:0;top:9;"></span><span class="menucornerbg" style="left:1;top:9;"></span><span class="menucornerbg" style="left:2;top:9;"></span><span class="menucornerbg" style="left:3;top:9;"></span><span class="menucornerbg" style="left:4;top:9;"></span><span class="menucornerbg" style="left:5;top:9;"></span><span class="menucornerbg" style="left:6;top:9;"></span><span class="menucornerbg" style="left:7;top:9;"></span><span class="menucornerbg" style="left:8;top:9;"></span><span class="menucornerbg" style="left:9;top:9;"></span><span class="menucornerbg" style="left:0;top:10;"></span><span class="menucornerbg" style="left:1;top:10;"></span><span class="menucornerbg" style="left:2;top:10;"></span><span class="menucornerbg" style="left:3;top:10;"></span><span class="menucornerbg" style="left:4;top:10;"></span><span class="menucornerbg" style="left:5;top:10;"></span><span class="menucornerbg" style="left:6;top:10;"></span><span class="menucornerbg" style="left:7;top:10;"></span><span class="menucornerbg" style="left:8;top:10;"></span><span class="menucornerbg" style="left:9;top:10;"></span><span class="menucornerbg" style="left:10;top:10;"></span><span class="menucornerbg" style="left:0;top:11;"></span><span class="menucornerbg" style="left:1;top:11;"></span><span class="menucornerbg" style="left:2;top:11;"></span><span class="menucornerbg" style="left:3;top:11;"></span><span class="menucornerbg" style="left:4;top:11;"></span><span class="menucornerbg" style="left:5;top:11;"></span><span class="menucornerbg" style="left:6;top:11;"></span><span class="menucornerbg" style="left:7;top:11;"></span><span class="menucornerbg" style="left:8;top:11;"></span><span class="menucornerbg" style="left:9;top:11;"></span><span class="menucornerbg" style="left:10;top:11;"></span><span class="menucornerbg" style="left:11;top:11;"></span><span class="menucornerbg" style="left:0;top:12;"></span><span class="menucornerbg" style="left:1;top:12;"></span><span class="menucornerbg" style="left:2;top:12;"></span><span class="menucornerbg" style="left:3;top:12;"></span><span class="menucornerbg" style="left:4;top:12;"></span><span class="menucornerbg" style="left:5;top:12;"></span><span class="menucornerbg" style="left:6;top:12;"></span><span class="menucornerbg" style="left:7;top:12;"></span><span class="menucornerbg" style="left:8;top:12;"></span><span class="menucornerbg" style="left:9;top:12;"></span><span class="menucornerbg" style="left:10;top:12;"></span><span class="menucornerbg" style="left:11;top:12;"></span><span class="menucornerbg" style="left:12;top:12;"></span><span class="menucornerbg" style="left:0;top:13;"></span><span class="menucornerbg" style="left:1;top:13;"></span><span class="menucornerbg" style="left:2;top:13;"></span><span class="menucornerbg" style="left:3;top:13;"></span><span class="menucornerbg" style="left:4;top:13;"></span><span class="menucornerbg" style="left:5;top:13;"></span><span class="menucornerbg" style="left:6;top:13;"></span><span class="menucornerbg" style="left:7;top:13;"></span><span class="menucornerbg" style="left:8;top:13;"></span><span class="menucornerbg" style="left:9;top:13;"></span><span class="menucornerbg" style="left:10;top:13;"></span><span class="menucornerbg" style="left:11;top:13;"></span><span class="menucornerbg" style="left:12;top:13;"></span><span class="menucornerbg" style="left:13;top:13;"></span><span class="menucornerbg" style="left:0;top:14;"></span><span class="menucornerbg" style="left:1;top:14;"></span><span class="menucornerbg" style="left:2;top:14;"></span><span class="menucornerbg" style="left:3;top:14;"></span><span class="menucornerbg" style="left:4;top:14;"></span><span class="menucornerbg" style="left:5;top:14;"></span><span class="menucornerbg" style="left:6;top:14;"></span><span class="menucornerbg" style="left:7;top:14;"></span><span class="menucornerbg" style="left:8;top:14;"></span><span class="menucornerbg" style="left:9;top:14;"></span><span class="menucornerbg" style="left:10;top:14;"></span><span class="menucornerbg" style="left:11;top:14;"></span><span class="menucornerbg" style="left:12;top:14;"></span><span class="menucornerbg" style="left:13;top:14;"></span><span class="menucornerbg" style="left:14;top:14;"></span><span class="menucornerbg" style="left:0;top:15;"></span><span class="menucornerbg" style="left:1;top:15;"></span><span class="menucornerbg" style="left:2;top:15;"></span><span class="menucornerbg" style="left:3;top:15;"></span><span class="menucornerbg" style="left:4;top:15;"></span><span class="menucornerbg" style="left:5;top:15;"></span><span class="menucornerbg" style="left:6;top:15;"></span><span class="menucornerbg" style="left:7;top:15;"></span><span class="menucornerbg" style="left:8;top:15;"></span><span class="menucornerbg" style="left:9;top:15;"></span><span class="menucornerbg" style="left:10;top:15;"></span><span class="menucornerbg" style="left:11;top:15;"></span><span class="menucornerbg" style="left:12;top:15;"></span><span class="menucornerbg" style="left:13;top:15;"></span><span class="menucornerbg" style="left:14;top:15;"></span><span class="menucornerbg" style="left:15;top:15;"></span><span class="menucornerbg" style="left:0;top:16;"></span><span class="menucornerbg" style="left:1;top:16;"></span><span class="menucornerbg" style="left:2;top:16;"></span><span class="menucornerbg" style="left:3;top:16;"></span><span class="menucornerbg" style="left:4;top:16;"></span><span class="menucornerbg" style="left:5;top:16;"></span><span class="menucornerbg" style="left:6;top:16;"></span><span class="menucornerbg" style="left:7;top:16;"></span><span class="menucornerbg" style="left:8;top:16;"></span><span class="menucornerbg" style="left:9;top:16;"></span><span class="menucornerbg" style="left:10;top:16;"></span><span class="menucornerbg" style="left:11;top:16;"></span><span class="menucornerbg" style="left:12;top:16;"></span><span class="menucornerbg" style="left:13;top:16;"></span><span class="menucornerbg" style="left:14;top:16;"></span><span class="menucornerbg" style="left:15;top:16;"></span><span class="menucornerbg" style="left:16;top:16;"></span><span class="menucornerbg" style="left:0;top:17;"></span><span class="menucornerbg" style="left:1;top:17;"></span><span class="menucornerbg" style="left:2;top:17;"></span><span class="menucornerbg" style="left:3;top:17;"></span><span class="menucornerbg" style="left:4;top:17;"></span><span class="menucornerbg" style="left:5;top:17;"></span><span class="menucornerbg" style="left:6;top:17;"></span><span class="menucornerbg" style="left:7;top:17;"></span><span class="menucornerbg" style="left:8;top:17;"></span><span class="menucornerbg" style="left:9;top:17;"></span><span class="menucornerbg" style="left:10;top:17;"></span><span class="menucornerbg" style="left:11;top:17;"></span><span class="menucornerbg" style="left:12;top:17;"></span><span class="menucornerbg" style="left:13;top:17;"></span><span class="menucornerbg" style="left:14;top:17;"></span><span class="menucornerbg" style="left:15;top:17;"></span><span class="menucornerbg" style="left:16;top:17;"></span><span class="menucornerbg" style="left:17;top:17;"></span><span class="menucornerbg" style="left:0;top:18;"></span><span class="menucornerbg" style="left:1;top:18;"></span><span class="menucornerbg" style="left:2;top:18;"></span><span class="menucornerbg" style="left:3;top:18;"></span><span class="menucornerbg" style="left:4;top:18;"></span><span class="menucornerbg" style="left:5;top:18;"></span><span class="menucornerbg" style="left:6;top:18;"></span><span class="menucornerbg" style="left:7;top:18;"></span><span class="menucornerbg" style="left:8;top:18;"></span><span class="menucornerbg" style="left:9;top:18;"></span><span class="menucornerbg" style="left:10;top:18;"></span><span class="menucornerbg" style="left:11;top:18;"></span><span class="menucornerbg" style="left:12;top:18;"></span><span class="menucornerbg" style="left:13;top:18;"></span><span class="menucornerbg" style="left:14;top:18;"></span><span class="menucornerbg" style="left:15;top:18;"></span><span class="menucornerbg" style="left:16;top:18;"></span><span class="menucornerbg" style="left:17;top:18;"></span><span class="menucornerbg" style="left:18;top:18;"></span><span class="menucornerbg" style="left:0;top:19;"></span><span class="menucornerbg" style="left:1;top:19;"></span><span class="menucornerbg" style="left:2;top:19;"></span><span class="menucornerbg" style="left:3;top:19;"></span><span class="menucornerbg" style="left:4;top:19;"></span><span class="menucornerbg" style="left:5;top:19;"></span><span class="menucornerbg" style="left:6;top:19;"></span><span class="menucornerbg" style="left:7;top:19;"></span><span class="menucornerbg" style="left:8;top:19;"></span><span class="menucornerbg" style="left:9;top:19;"></span><span class="menucornerbg" style="left:10;top:19;"></span><span class="menucornerbg" style="left:11;top:19;"></span><span class="menucornerbg" style="left:12;top:19;"></span><span class="menucornerbg" style="left:13;top:19;"></span><span class="menucornerbg" style="left:14;top:19;"></span><span class="menucornerbg" style="left:15;top:19;"></span><span class="menucornerbg" style="left:16;top:19;"></span><span class="menucornerbg" style="left:17;top:19;"></span><span class="menucornerbg" style="left:18;top:19;"></span><span class="menucornerbg" style="left:19;top:19;"></span><span class="menucornerbg" style="left:0;top:20;"></span><span class="menucornerbg" style="left:1;top:20;"></span><span class="menucornerbg" style="left:2;top:20;"></span><span class="menucornerbg" style="left:3;top:20;"></span><span class="menucornerbg" style="left:4;top:20;"></span><span class="menucornerbg" style="left:5;top:20;"></span><span class="menucornerbg" style="left:6;top:20;"></span><span class="menucornerbg" style="left:7;top:20;"></span><span class="menucornerbg" style="left:8;top:20;"></span><span class="menucornerbg" style="left:9;top:20;"></span><span class="menucornerbg" style="left:10;top:20;"></span><span class="menucornerbg" style="left:11;top:20;"></span><span class="menucornerbg" style="left:12;top:20;"></span><span class="menucornerbg" style="left:13;top:20;"></span><span class="menucornerbg" style="left:14;top:20;"></span><span class="menucornerbg" style="left:15;top:20;"></span><span class="menucornerbg" style="left:16;top:20;"></span><span class="menucornerbg" style="left:17;top:20;"></span><span class="menucornerbg" style="left:18;top:20;"></span><span class="menucornerbg" style="left:19;top:20;"></span><span class="menucornerbg" style="left:20;top:20;"></span>';
}

function displayLocaleMessage() {

	alert(YFCMSG043);
}
