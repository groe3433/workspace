/* Copyright 2010, Sterling Commerce, Inc. All rights reserved. */
/*
 LIMITATION OF LIABILITY
 THIS SOFTWARE SAMPLE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED 
 WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
 IN NO EVENT SHALL STERLING COMMERCE, Inc. BE LIABLE UNDER ANY THEORY OF 
 LIABILITY (INCLUDING, BUT NOT LIMITED TO, BREACH OF CONTRACT, BREACH 
 OF WARRANTY, TORT, NEGLIGENCE, STRICT LIABILITY, OR ANY OTHER THEORY 
 OF LIABILITY) FOR (i) DIRECT DAMAGES OR INDIRECT, SPECIAL, INCIDENTAL, 
 OR CONSEQUENTIAL DAMAGES SUCH AS, BUT NOT LIMITED TO, EXEMPLARY OR 
 PUNITIVE DAMAGES, OR ANY OTHER SIMILAR DAMAGES, WHETHER OR NOT 
 FORESEEABLE AND WHETHER OR NOT STERLING OR ITS REPRESENTATIVES HAVE 
 BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES, OR (ii) ANY OTHER 
 CLAIM, DEMAND OR DAMAGES WHATSOEVER RESULTING FROM OR ARISING OUT OF
 OR IN CONNECTION THE DELIVERY OR USE OF THIS INFORMATION.
 */

package com.nwcg.icbs.yantra.commCore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;

public class NWCGSoapMessageThread extends Thread {

	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGSoapMessageThread.class);

	public String xmldata;
	public String ip;
	public int port;
	public String result;

	public void run() {
		try {
			logger.verbose("@@@@@ Entering NWCGSoapMessageThread::run");

			// Create socket
			String hostname = ip;
			String path = "";
			InetAddress addr = InetAddress.getByName(hostname);
			Socket sock = new Socket(addr, port);

			// Send header
			// String path = "/rcx-ws/rcx";
			BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
			
			// You can use "UTF8" for compatibility with the Microsoft virtual machine.
			wr.write("POST " + path + " HTTP/1.0\r\n");
			wr.write("Host:" + hostname + "\r\n");
			wr.write("Content-Length: " + xmldata.length() + "\r\n");
			wr.write("Content-Type: text/xml; charset=\"utf-8\"\r\n");
			wr.write("\r\n");

			// Send data
			wr.write(xmldata);
			wr.flush();

			// Response
			BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			String lineTotal = "";
			String line;
			while ((line = rd.readLine()) != null) {
				lineTotal += line;
			}
			sock.close();
			result = lineTotal;
		} catch (Exception e) {
			result = "Error:" + e.toString();
			logger.error("!!!!!  result :: " + result);
		}
		logger.verbose("@@@@@ Exiting NWCGSoapMessageThread::run");
	}
}