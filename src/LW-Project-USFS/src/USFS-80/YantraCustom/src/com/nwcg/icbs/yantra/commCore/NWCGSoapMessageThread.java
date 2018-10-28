package com.nwcg.icbs.yantra.commCore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import com.nwcg.icbs.yantra.util.common.NWCGLoggerUtil;
public class NWCGSoapMessageThread extends Thread{

	public String xmldata;
	public String ip;
	public int port;
	public String result;
	
	public void run(){
		try{
        	
  	      //Create socket
  	      String hostname = ip;
  	      
  	      String path = "";
  	   
  	      InetAddress  addr = InetAddress.getByName(hostname);
  	      Socket sock = new Socket(addr, port);
  				
  	      //Send header
  	      //String path = "/rcx-ws/rcx";
  	      BufferedWriter  wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(),"UTF-8"));
  	      // You can use "UTF8" for compatibility with the Microsoft virtual machine.
  	      wr.write("POST " + path + " HTTP/1.0\r\n");
  	      wr.write("Host:"+hostname+"\r\n");
  	      wr.write("Content-Length: " + xmldata.length() + "\r\n");
  	      wr.write("Content-Type: text/xml; charset=\"utf-8\"\r\n");
  	      wr.write("\r\n");
  				
  	      //Send data
  	      wr.write(xmldata);
  	      wr.flush();
  				
  	      // Response
  	      BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
  	      String lineTotal = "";
  	      String line;
  	      
  	      while((line = rd.readLine()) != null){
  	    	  
  	    	  lineTotal+= line;
  	      }
  	      
  	      sock.close();
  	      
  	      result = lineTotal;
	         
	          
		}catch(Exception e){
			result = "Error:" + e.toString();
			
	        NWCGLoggerUtil.Log.warning(result);
	        	
		}
		
	}
}
