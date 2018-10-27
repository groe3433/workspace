package com.fanatics.sterling.stub;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.yantra.yfc.log.YFCLogCategory;
import com.fanatics.sterling.constants.CustomerMasterConstants;
import com.fanatics.sterling.util.XMLUtil;


public class AddressValidation {
	
	private static YFCLogCategory logger = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	public Document invokeAddressValidationREST (Document inDoc) throws ParserConfigurationException, SAXException, IOException{
		
		logger.verbose("Inside FanAddressValidationResponse");
		logger.verbose("Input XML " + inDoc);
		
		Document responseXml = null;

			
				
				Element eleInDocPersonInfo = (Element) inDoc.getElementsByTagName(CustomerMasterConstants.ATT_PersonInfo).item(0);
				
				


				
				
				if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("1")){
					
					
					String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"\" TotalNumberOfRecords=\"\"> "
							+ "<PersonInfo AVSReturnCode=\"VERIFIED\" IsAddressVerified=\"Y\" AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\"/>"
							+ "<AddressVerificationResponseMessages>"
							+ "<AddressVerificationResponseMessage MessageCode=\"\" MessageText=\"Address Verified\"/>"
							+ "</AddressVerificationResponseMessages>"
							+ "</PersonInfoList>";
					responseXml = XMLUtil.getDocument(responseString);
						
						Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
						Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
						Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
						Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
						
					eleCustomer.setAttribute("ProceedWithSingleAVSResult", "Y");
					eleCustomer.setAttribute("TotalNumberOfRecords", "1");

					PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
					PersonInfo.setAttribute("AddressLine2", eleInDocPersonInfo.getAttribute("AddressLine2"));
					PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
					PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
					PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
					PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));

					AddressVerificationResponseMessage.setAttribute("MessageCode", "200");


					
				}
				
				if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("2")){
					
					
					String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"N\" TotalNumberOfRecords=\"2\"> "
							+ "<PersonInfo AVSReturnCode=\"VERIFIED\" IsAddressVerified=\"Y\" AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\"/>"
							+ "<PersonInfo AVSReturnCode=\"FAILED\" IsAddressVerified=\"N\" AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\"/>"
							+ "<AddressVerificationResponseMessages>"
							+ "<AddressVerificationResponseMessage MessageCode=\"200\" MessageText=\"Address Verified\"/>"
							+ "</AddressVerificationResponseMessages>"
							+ "</PersonInfoList>";
					responseXml= XMLUtil.getDocument(responseString);
						
						Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
						Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
						Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
						Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
						


					PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
					PersonInfo.setAttribute("AddressLine2", eleInDocPersonInfo.getAttribute("AddressLine2"));
					PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
					PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
					PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
					PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));

					Element PersonInfo1 = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(1);
					
				PersonInfo1.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
				PersonInfo1.setAttribute("AddressLine2", "second line");
				PersonInfo1.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
				PersonInfo1.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
				PersonInfo1.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
				PersonInfo1.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));

				}

				if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("3")){
					
					
					String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"N\" TotalNumberOfRecords=\"2\"> "
							+ "<PersonInfo AVSReturnCode=\"VERIFIED\" IsAddressVerified=\"Y\" AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\"/>"
							+ "<PersonInfo AVSReturnCode=\"UE_MISSING\" IsAddressVerified=\"N\" AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\"/>"
							+ "<AddressVerificationResponseMessages>"
							+ "<AddressVerificationResponseMessage MessageCode=\"200\" MessageText=\"Address Verified\"/>"
							+ "</AddressVerificationResponseMessages>"
							+ "</PersonInfoList>";
					responseXml= XMLUtil.getDocument(responseString);
						
						Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
						Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
						Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
						Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
						


					PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
					PersonInfo.setAttribute("AddressLine2", eleInDocPersonInfo.getAttribute("AddressLine2"));
					PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
					PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
					PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
					PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));

					Element PersonInfo1 = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(1);
					
				PersonInfo1.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
				PersonInfo1.setAttribute("AddressLine2", "second line");
				PersonInfo1.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
				PersonInfo1.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
				PersonInfo1.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
				PersonInfo1.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));

				}
				
				if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("4")){
					
					
					String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"N\" TotalNumberOfRecords=\"0\"> "
							+ "<PersonInfo AVSReturnCode=\"\" IsAddressVerified=\"\" AddressLine1=\"\" AddressLine2=\"\" AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\"/>"
							+ "<AddressVerificationResponseMessages>"
							+ "<AddressVerificationResponseMessage MessageCode=\"\" MessageText=\"\"/>"
							+ "</AddressVerificationResponseMessages>"
							+ "</PersonInfoList>";
					responseXml= XMLUtil.getDocument(responseString);
						

				}
				
					if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("5")){
										
					String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"\" TotalNumberOfRecords=\"\"> "
							+ "<PersonInfo AVSReturnCode=\"VERIFIED\" IsAddressVerified=\"Y\" AddressLine1=\"\" AddressLine2=\"\" "
							+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" "
							+ "Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\" DayPhone=\"\" "
							+ "EMailID=\"\"    EnterpriseCode=\"\"  EveningPhone=\"\"    FirstName=\"\"  "
							+ "LastName=\"\"  PersonID=\"\" PersonInfoKey=\"\" Company=\"\"/>"							
							+ "<AddressVerificationResponseMessages>"
							+ "<AddressVerificationResponseMessage MessageCode=\"\" MessageText=\"Address Verified\"/>"
							+ "</AddressVerificationResponseMessages>"
							+ "</PersonInfoList>";
					responseXml = XMLUtil.getDocument(responseString);
						
						Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
						Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
						Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
						Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
						
					eleCustomer.setAttribute("ProceedWithSingleAVSResult", "Y");
					eleCustomer.setAttribute("TotalNumberOfRecords", "1");

					PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
					PersonInfo.setAttribute("AddressLine2", eleInDocPersonInfo.getAttribute("AddressLine2"));
					PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
					PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
					PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
					PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));
					
					PersonInfo.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
					PersonInfo.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
					PersonInfo.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
					PersonInfo.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
					PersonInfo.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
					PersonInfo.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
					PersonInfo.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
					PersonInfo.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));

					AddressVerificationResponseMessage.setAttribute("MessageCode", "200");


					
				}
				
					if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("6")){
					
					
					String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"N\" TotalNumberOfRecords=\"2\"> "
							+ "<PersonInfo AVSReturnCode=\"VERIFIED\" IsAddressVerified=\"Y\" AddressLine1=\"\" AddressLine2=\"\" "
							+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" "
							+ "Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\" DayPhone=\"\" "
							+ "EMailID=\"\"    EnterpriseCode=\"\"  EveningPhone=\"\"    FirstName=\"\"  "
							+ "LastName=\"\"  PersonID=\"\" PersonInfoKey=\"\" Company=\"\"/>"
							+ "<PersonInfo AVSReturnCode=\"UE_MISSING\" IsAddressVerified=\"N\" AddressLine1=\"\" AddressLine2=\"\" "
							+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" "
							+ "Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\" DayPhone=\"\" "
							+ "EMailID=\"\"    EnterpriseCode=\"\"  EveningPhone=\"\"    FirstName=\"\"  "
							+ "LastName=\"\"  PersonID=\"\" PersonInfoKey=\"\" Company=\"\"/>"							
							+ "<AddressVerificationResponseMessages>"
							+ "<AddressVerificationResponseMessage MessageCode=\"205\" MessageText=\"Address Suggestion\"/>"
							+ "</AddressVerificationResponseMessages>"
							+ "</PersonInfoList>";
					responseXml= XMLUtil.getDocument(responseString);
						
						Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
						Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
						Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
						Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
						


					PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
					PersonInfo.setAttribute("AddressLine2", eleInDocPersonInfo.getAttribute("AddressLine2"));
					PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
					PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
					PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
					PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));
					
					PersonInfo.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
					PersonInfo.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
					PersonInfo.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
					PersonInfo.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
					PersonInfo.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
					PersonInfo.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
					PersonInfo.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
					PersonInfo.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));

					Element PersonInfo1 = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(1);
					
				PersonInfo1.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
				PersonInfo1.setAttribute("AddressLine2", "second line");
				PersonInfo1.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
				PersonInfo1.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
				PersonInfo1.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
				PersonInfo1.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));
				
				PersonInfo1.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
				PersonInfo1.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
				PersonInfo1.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
				PersonInfo1.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
				PersonInfo1.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
				PersonInfo1.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
				PersonInfo1.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
				PersonInfo1.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));

				}
				
					if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("7")){
						
						
					String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"N\" TotalNumberOfRecords=\"2\"> "
							+ "<PersonInfo AVSReturnCode=\"VERIFIED\" IsAddressVerified=\"Y\" AddressLine1=\"\" AddressLine2=\"\" "
							+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" "
							+ "Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\" DayPhone=\"\" "
							+ "EMailID=\"\"    EnterpriseCode=\"\"  EveningPhone=\"\"    FirstName=\"\"  "
							+ "LastName=\"\"  PersonID=\"\" PersonInfoKey=\"\" Company=\"\"/>"
							+ "<PersonInfo AVSReturnCode=\"FAILED\" IsAddressVerified=\"N\" AddressLine1=\"\" AddressLine2=\"\" "
							+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" "
							+ "Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\" DayPhone=\"\" "
							+ "EMailID=\"\"    EnterpriseCode=\"\"  EveningPhone=\"\"    FirstName=\"\"  "
							+ "LastName=\"\"  PersonID=\"\" PersonInfoKey=\"\" Company=\"\"/>"							
							+ "<AddressVerificationResponseMessages>"
							+ "<AddressVerificationResponseMessage MessageCode=\"205\" MessageText=\"Address Suggestion\"/>"
							+ "</AddressVerificationResponseMessages>"
							+ "</PersonInfoList>";
					responseXml= XMLUtil.getDocument(responseString);
						
						Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
						Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
						Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
						Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
						


					PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
					PersonInfo.setAttribute("AddressLine2", eleInDocPersonInfo.getAttribute("AddressLine2"));
					PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
					PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
					PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
					PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));
					
					PersonInfo.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
					PersonInfo.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
					PersonInfo.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
					PersonInfo.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
					PersonInfo.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
					PersonInfo.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
					PersonInfo.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
					PersonInfo.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));

					Element PersonInfo1 = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(1);
					
				PersonInfo1.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
				PersonInfo1.setAttribute("AddressLine2", "second line");
				PersonInfo1.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
				PersonInfo1.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
				PersonInfo1.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
				PersonInfo1.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));
				
				PersonInfo1.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
				PersonInfo1.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
				PersonInfo1.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
				PersonInfo1.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
				PersonInfo1.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
				PersonInfo1.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
				PersonInfo1.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
				PersonInfo1.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));

				}	
					
					
					if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("8")){
						
						
					String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"N\" TotalNumberOfRecords=\"1\"> "
							+ "<PersonInfo AVSReturnCode=\"VERIFIED\" IsAddressVerified=\"N\" AddressLine1=\"\" AddressLine2=\"\" "
							+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" "
							+ "Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\" DayPhone=\"\" "
							+ "EMailID=\"\"    EnterpriseCode=\"\"  EveningPhone=\"\"    FirstName=\"\"  "
							+ "LastName=\"\"  PersonID=\"\" PersonInfoKey=\"\" Company=\"\"/>"			
							+ "<AddressVerificationResponseMessages>"
							+ "<AddressVerificationResponseMessage MessageCode=\"205\" MessageText=\"Address Suggestion\"/>"
							+ "</AddressVerificationResponseMessages>"
							+ "</PersonInfoList>";
					responseXml= XMLUtil.getDocument(responseString);
						
						Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
						Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
						Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
						Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
						


					PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
					PersonInfo.setAttribute("AddressLine2", "second");
					PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
					PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
					PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
					PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));
					
					PersonInfo.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
					PersonInfo.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
					PersonInfo.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
					PersonInfo.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
					PersonInfo.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
					PersonInfo.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
					PersonInfo.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
					PersonInfo.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));

				}
					
					if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("9")){
						
						
						String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"Y\" TotalNumberOfRecords=\"1\"> "
								+ "<PersonInfo AVSReturnCode=\"VERIFIED\" IsAddressVerified=\"Y\" AddressLine1=\"\" AddressLine2=\"\" "
								+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" "
								+ "Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\" DayPhone=\"\" "
								+ "EMailID=\"\"    EnterpriseCode=\"\"  EveningPhone=\"\"    FirstName=\"\"  "
								+ "LastName=\"\"  PersonID=\"\" PersonInfoKey=\"\" Company=\"\"/>"			
								+ "<AddressVerificationResponseMessages>"
								+ "<AddressVerificationResponseMessage MessageCode=\"200\" MessageText=\"Address Verified\"/>"
								+ "</AddressVerificationResponseMessages>"
								+ "</PersonInfoList>";
						responseXml= XMLUtil.getDocument(responseString);
							
							Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
							Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
							Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
							Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
							


						PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
						PersonInfo.setAttribute("AddressLine2", "second");
						PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
						PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
						PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
						PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));
						
						PersonInfo.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
						PersonInfo.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
						PersonInfo.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
						PersonInfo.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
						PersonInfo.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
						PersonInfo.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
						PersonInfo.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
						PersonInfo.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));

					}
					
					if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("10")){
						
						
						String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"N\" TotalNumberOfRecords=\"1\"> "
								+ "<PersonInfo AVSReturnCode=\"FAILED\" IsAddressVerified=\"N\" AddressLine1=\"\" AddressLine2=\"\" "
								+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" "
								+ "Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\" DayPhone=\"\" "
								+ "EMailID=\"\"    EnterpriseCode=\"\"  EveningPhone=\"\"    FirstName=\"\"  "
								+ "LastName=\"\"  PersonID=\"\" PersonInfoKey=\"\" Company=\"\"/>"			
								+ "<AddressVerificationResponseMessages>"
								+ "<AddressVerificationResponseMessage MessageCode=\"205\" MessageText=\"Address Suggestion\"/>"
								+ "</AddressVerificationResponseMessages>"
								+ "</PersonInfoList>";
						responseXml= XMLUtil.getDocument(responseString);
							
							Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
							Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
							Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
							Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
							


						PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
						PersonInfo.setAttribute("AddressLine2", "second");
						PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
						PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
						PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
						PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));
						
						PersonInfo.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
						PersonInfo.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
						PersonInfo.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
						PersonInfo.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
						PersonInfo.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
						PersonInfo.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
						PersonInfo.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
						PersonInfo.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));

					}
					
					if (eleInDocPersonInfo.getAttribute("AddressLine2").equals("11")){
						
						
						String responseString ="<PersonInfoList ProceedWithSingleAVSResult=\"Y\" TotalNumberOfRecords=\"1\"> "
								+ "<PersonInfo AVSReturnCode=\"UE_MISSING\" IsAddressVerified=\"N\" AddressLine1=\"\" AddressLine2=\"\" "
								+ "AddressLine3=\"\" AddressLine4=\"\" AddressLine5=\"\" AddressLine6=\"\" City=\"\" Country=\"\" "
								+ "Latitude=\"\" Longitude=\"\" State=\"\" TaxGeoCode=\"\" ZipCode=\"\" DayPhone=\"\" "
								+ "EMailID=\"\"    EnterpriseCode=\"\"  EveningPhone=\"\"    FirstName=\"\"  "
								+ "LastName=\"\"  PersonID=\"\" PersonInfoKey=\"\" Company=\"\"/>"			
								+ "<AddressVerificationResponseMessages>"
								+ "<AddressVerificationResponseMessage MessageCode=\"205\" MessageText=\"Address Suggestion\"/>"
								+ "</AddressVerificationResponseMessages>"
								+ "</PersonInfoList>";
						responseXml= XMLUtil.getDocument(responseString);
							
							Element eleCustomer = (Element) responseXml.getElementsByTagName("PersonInfoList").item(0);
							Element PersonInfo = (Element) eleCustomer.getElementsByTagName("PersonInfo").item(0);
							Element AddressVerificationResponseMessages = (Element) eleCustomer.getElementsByTagName("AddressVerificationResponseMessages").item(0);
							Element AddressVerificationResponseMessage = (Element) AddressVerificationResponseMessages.getElementsByTagName("AddressVerificationResponseMessage").item(0);
							


						PersonInfo.setAttribute("AddressLine1", eleInDocPersonInfo.getAttribute("AddressLine1"));
						PersonInfo.setAttribute("AddressLine2", "second");
						PersonInfo.setAttribute("City", eleInDocPersonInfo.getAttribute("City"));
						PersonInfo.setAttribute("Country", eleInDocPersonInfo.getAttribute("Country"));
						PersonInfo.setAttribute("State", eleInDocPersonInfo.getAttribute("State"));
						PersonInfo.setAttribute("ZipCode", eleInDocPersonInfo.getAttribute("ZipCode"));
						
						PersonInfo.setAttribute("Company", eleInDocPersonInfo.getAttribute("Company"));
						PersonInfo.setAttribute("DayPhone", eleInDocPersonInfo.getAttribute("DayPhone"));
						PersonInfo.setAttribute("EMailID", eleInDocPersonInfo.getAttribute("EMailID"));
						PersonInfo.setAttribute("EnterpriseCode", eleInDocPersonInfo.getAttribute("EnterpriseCode"));
						PersonInfo.setAttribute("EveningPhone", eleInDocPersonInfo.getAttribute("EveningPhone"));
						PersonInfo.setAttribute("PersonInfoKey", eleInDocPersonInfo.getAttribute("PersonInfoKey"));
						PersonInfo.setAttribute("FirstName", eleInDocPersonInfo.getAttribute("FirstName"));
						PersonInfo.setAttribute("LastName", eleInDocPersonInfo.getAttribute("LastName"));

					}
				
return responseXml;
			
			
		
	}

}
