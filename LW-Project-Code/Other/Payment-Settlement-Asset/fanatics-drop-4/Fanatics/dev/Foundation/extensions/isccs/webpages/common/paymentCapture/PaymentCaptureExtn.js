
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/common/paymentCapture/PaymentCaptureExtnUI", "scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!dojo/_base/lang","scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!isccs/utils/UIUtils","scbase/loader!sc/plat/dojo/utils/ResourcePermissionUtils","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/EventUtils", "scbase/loader!sc/plat/dojo/utils/WidgetUtils", "scbase/loader!isccs/utils/ModelUtils","scbase/loader!extn/common/paymentCapture/CryptoJSv3.1.2/components/core", "scbase/loader!extn/common/paymentCapture/CryptoJSv3.1.2/rollups/hmac-sha256"]

,
function(			 
			    _dojodeclare,
			    _extnPaymentCaptureExtnUI, 
			    _scScreenUtils, 
			    dLang,
			    _scModelUtils,_isccsModelUtils,
			    _isccsUIUtils,
			    _scBaseUtils,_scEventUtils, _scWidgetUtils,_scResourcePermissionUtils
				 
){ 
	return _dojodeclare("extn.common.paymentCapture.PaymentCaptureExtn", [_extnPaymentCaptureExtnUI],{
	//This method will validate if the user have the permission to use paypal payment method
	validateUserForPayPal: function(event, bEvent, ctrl, args) {
		/* if (!_scResourcePermissionUtils.hasPermission("extnPayPalAllowed")) {
			var paymentModel = null;
			var lstPaymentMethod = null;
			paymentModel = _scScreenUtils.getModel(this, "getPaymentTypeList_Output");
			lstPaymentMethod = _scModelUtils.getModelListFromPath("PaymentTypeList.PaymentType", paymentModel);
			for (var i = 0; _scBaseUtils.lessThan(i, _scBaseUtils.getAttributeCount(lstPaymentMethod)); i = _scBaseUtils.incrementNumber(i, 1)) {
				var paymentMethod = null;
				var paymentType = null;
				paymentMethod = _scModelUtils.getModelFromList(lstPaymentMethod, i);
				paymentType = _scModelUtils.getStringValueFromPath("PaymentType", paymentMethod);
				if(!_scBaseUtils.isVoid(paymentType) && _scBaseUtils.equals(paymentType,"PAYPAL")) {
					_isccsModelUtils.removeItemFromArray(lstPaymentMethod, paymentMethod);
					i = _scBaseUtils.incrementNumber(i, -1);
				}
			}
			_scScreenUtils.setModel(this, "getPaymentTypeList_Output", paymentModel, null);
		} */
	},
	handleMashupOutput : function (mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
		alert(mashupRefId);
		if (_scBaseUtils.equals(mashupRefId,"extn_getPaymentEncoding")) {
			var token=_scModelUtils.getStringValueFromPath("Payment.FanaticsToken", modelOutput);
			alert(token);
			targetModel = _scBaseUtils.getTargetModel(this, "PaymentCapture_input", null);
			_scModelUtils.setStringValueAtModelPath("PaymentMethod.CreditCardNo", token, targetModel);
			paymentTypeGroup = _scModelUtils.getStringValueFromPath("PaymentMethod.PaymentTypeGroup", targetModel);
			
            var scr = _scScreenUtils.getChildScreen(this, "scrnPaymentCapture");
			
			PaymentCaptureModel = _scBaseUtils.getTargetModel(scr, "PaymentCapture_input", null);
			var paymentMethodModel = null;
			paymentMethodModel = _scModelUtils.getModelObjectFromPath("PaymentMethod", PaymentCaptureModel);
			if (_scBaseUtils.isVoid(paymentMethodModel)) {
				paymentMethodModel = _scModelUtils.createNewModelObjectWithRootKey("PaymentMethod");
			}
			_scModelUtils.addModelToModelPath("Order.PaymentMethod", paymentMethodModel, PaymentCaptureModel);
			_scModelUtils.setStringValueAtModelPath("Order.PaymentMethod.CreditCardNo", token, PaymentCaptureModel);
			var options = null;
            options = {};
            _scBaseUtils.setAttributeValue("clearOldVals", false, options);
            _scScreenUtils.setModel(scr, "paymentCapture_Output", PaymentCaptureModel, options);
            this.onPopupConfirm(null,null,null,null);
		}
	},
	 onTokenize: function(event, bEvent, ctrl, args) {
			_scScreenUtils.setModel(this, "event", event, null);
			_scScreenUtils.setModel(this, "bEvent", bEvent, null);
			_scScreenUtils.setModel(this, "ctrl", ctrl, null);
			_scScreenUtils.setModel(this, "args", args, null);
			console.log(args);
			var targetModel = null;
			var cardType = null;
			var creditCardNo = null;
			var creditCardExpMonth = null;
			var creditCardExpYear = null;

			targetModel = _scScreenUtils.getTargetModel(this, "PaymentCapture_input", null);
			cardType = dLang.getObject("PaymentMethod.CreditCardType", false,targetModel);
			cardType="Visa";
			dLang.setObject("PaymentMethod.CreditCardType", cardType,targetModel);
			creditCardNo = dLang.getObject("PaymentMethod.CreditCardNo", false,targetModel);
			creditCardExpMonth = dLang.getObject("PaymentMethod.CreditCardExpMonth", false,targetModel);
			creditCardExpYear = dLang.getObject("PaymentMethod.CreditCardExpYear", false,targetModel);
			
			alert("Came into onTokenize");

			if(cardType == "Visa"){
				cardType = "001";
			}
			else if(cardType == "Mastercard"){
				cardType = "002";
			}

			alert(cardType + "," + creditCardNo + "," + creditCardExpMonth + "," + creditCardExpYear);
			var cardexpirydate = creditCardExpMonth+"-"+creditCardExpYear;
			
			var form = document.createElement("form");
			form.setAttribute("id", "test_form");
			form.setAttribute("method", "post");
			form.setAttribute("action", "https://testsecureacceptance.cybersource.com/silent/embedded/pay");
			form.setAttribute("target", "cs-tokenization-frame"); 

			var d = new Date();
			var isoDate = d.toISOString();
			var signed_date_time_input = isoDate.substring(0, 19) + "Z";

			var transaction_uuid_input = (Math.floor(Math.random() * 100000000));
			var reference_number_input = new Date().getTime();

			var params = {
				access_key: 'e54a34bf1ba13f65ae3384e198034f02',
				profile_id: '824409F2-555E-40ED-BA61-7EEB1F91B3F8',
				transaction_uuid: transaction_uuid_input,
				signed_field_names: 'access_key,profile_id,transaction_uuid,signed_field_names,unsigned_field_names,signed_date_time,locale,transaction_type,reference_number,amount,currency,payment_method,card_type,card_number,card_expiry_date,bill_to_forename,bill_to_surname,bill_to_email,bill_to_phone,bill_to_address_line1,bill_to_address_city,bill_to_address_state,bill_to_address_country,bill_to_address_postal_code',
				unsigned_field_names: '',
				signed_date_time: signed_date_time_input,
				locale: 'en',
				transaction_type: 'create_payment_token',
				reference_number: '',
				amount: '100.00',
				currency: 'USD',
				payment_method: 'card',
				card_type: '001',
				card_number: creditCardNo,
				card_expiry_date: cardexpirydate,
				bill_to_forename: 'Jason 1',
				bill_to_surname: 'Doe',
				bill_to_email: 'null@cybersource.com',
				bill_to_phone: '02890888888',
				bill_to_address_line1: '1295 Charleston Road',
				bill_to_address_city: 'Mountain View',
				bill_to_address_state: 'CA',
				bill_to_address_country: 'US',
				bill_to_address_postal_code: '94043'
			};

			for(var key in params) {
				if(params.hasOwnProperty(key)) {
					var hiddenField = document.createElement("input");
					hiddenField.setAttribute("type", "hidden");
					hiddenField.setAttribute("name", key);
					hiddenField.setAttribute("value", params[key]);

					form.appendChild(hiddenField);
				}
			}

			var dataToSign = [];

			for(var key in params) {
				if(params.hasOwnProperty(key)) {
					dataToSign.push(key + "=" + params[key]);
				}
			}

			dataToSign = dataToSign.join(",");
			
			var secretKey = "a3da224372b74a66a74c109fc11cd8420a1a5eab5780460caeccb57b65496716fe75356db591441fb06e77b82355ab9a9e15d8df27a84ea5911fa9c1c9af43b427021881e6fd4ec0bb6add32ce95ef6637ec5cd5dafd4a4fad95b75780571729b681909e18004d08a9bff3bafe620f9619f5fa2a9ebb4fd18ce5f636a0642f70";

			var signatureField = document.createElement("input");
    		signatureField.setAttribute("type", "hidden");
    		signatureField.setAttribute("name", "signature");
			var hmacSHA256=null;
			hmacSHA256 = CryptoJS.HmacSHA256(dataToSign, secretKey);
			alert(hmacSHA256);
			base64 = this.stringify(hmacSHA256);
			alert(base64);
			
			signatureField.setAttribute("value",base64);
			alert("5");    		
		
			form.appendChild(signatureField);        
    		document.body.appendChild(form); 
    		form.submit(); 
			
			//apiInput={};
			//_scModelUtils.setStringValueAtModelPath("Payment.CybersourceToken", "4710174816256477004106", apiInput);
			//_isccsUIUtils.callApi(this, apiInput, "extn_getPaymentEncoding", null);
        },

 	getIframeContent: function() {
		alert("getIframeContent");
		var timer = setTimeout('getIframeContent()', 5000);
		var frameObj = document.getElementById('cs-tokenization-frame');
	  	
	   	var decision = frameObj.contentWindow.document.getElementById("decision").innerHTML;
	   	var token = frameObj.contentWindow.document.getElementById("token").innerHTML;
	   	var cardNo = frameObj.contentWindow.document.getElementById("cardNo").innerHTML;

		alert("iframe content : decision-"+decision+", token-"+token+", cardNo-"+ cardNo );

		if (!_scBaseUtils.isVoid(token)) {
			alert("!_scBaseUtils.isVoid(token)");
			apiInput={};
			_scModelUtils.setStringValueAtModelPath("Payment.CybersourceToken", token, apiInput);
			_isccsUIUtils.callApi(this, apiInput, "extn_getPaymentEncoding", null);
		}

		
	},
	stringify:function(b){
			var e=b.words,f=b.sigBytes,c="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
			b.clamp();
			b=[];
			for(var a=0;a<f;a+=3)
				for(var d=(e[a>>>2]>>>24-8*(a%4)&255)<<16|(e[a+1>>>2]>>>24-8*((a+1)%4)&255)<<8|e[a+2>>>2]>>>24-8*((a+2)%4)&255,g=0;4>g&&a+0.75*g<f;g++)
					b.push(c.charAt(d>>>6*(3-g)&63));
				if(e=c.charAt(64))
					for(;b.length%4;)
						b.push(e);
					return b.join("")
		}
});
});
