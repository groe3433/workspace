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

package com.nwcg.icbs.yantra.handler;

import com.nwcg.icbs.yantra.soap.NWCGSOAPMsg;

public class NWCGIBAuthMessageHandler extends NWCGMessageHandler {

	public NWCGSOAPMsg process(NWCGSOAPMsg msg) {
		System.out.println("@@@@@ In NWCGIBAuthMessageHandler::process @@@@@");
		System.out.println("@@@@@ Note: this class is NOT inplemented...");
		throw new UnsupportedOperationException();
	}
}