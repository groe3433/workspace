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

import java.util.*;
import java.io.*;

import com.yantra.yfc.log.YFCLogCategory;
import com.nwcg.icbs.yantra.agents.NWCGReadNoResultMessageAgent;
import com.nwcg.icbs.yantra.util.common.NWCGROSSLogger;

public class NWCGCodeTemplate extends Hashtable {
	
	private static YFCLogCategory logger = NWCGROSSLogger.instance(NWCGCodeTemplate.class);

	private static final long serialVersionUID = -1824085386351483128L;
	
	String str;
	
	String name;

	public NWCGCodeTemplate() {

	}

	public NWCGCodeTemplate(String str) {
		this.str = str;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Vector getSlots() {
		Vector v = new Vector();
		int cIndex = str.indexOf('^');
		while (cIndex != -1) {
			int cIndexPrime = str.indexOf('^', cIndex + 1);
			if (cIndexPrime < 0) {
				cIndex = -1;
				continue;
			}
			String slot = str.substring(cIndex + 1, cIndexPrime);
			v.addElement(slot);
			cIndex = str.indexOf('^', cIndexPrime + 1);
		}
		return v;
	}

	public String concatenateVars(Vector v) {
		Enumeration e = v.elements();
		String s = "";
		while (e.hasMoreElements()) {
			String so = (String) e.nextElement();
			s = s + so + "\n";
		}
		return s;
	}

	public void setSlot(String slotname, String value) {
		put(slotname, value);
	}

	String subparse(String s, String slotname, String value, Boolean on) {
		String line = "";
		StringTokenizer tok2 = new StringTokenizer(s, "^");
		String sC = tok2.nextToken();
		if (sC.equals(slotname)) {
			String sD = "";
			if (tok2.hasMoreTokens())
				sD = tok2.nextToken();
			line = value + sD;
			return "\\" + line;
		}
		else if (on.booleanValue()) {
			on = new Boolean(false);
			return s;
		} else {
			return "^" + s;
		}
	}

	public void replaceSlot(String slotname, String value) {
		String strout = "";
		try {
			StringReader sr = new StringReader(str);
			LineNumberReader lnr = new LineNumberReader(sr);
			while (lnr.ready()) {
				String line = lnr.readLine();
				if (line == null)
					break;
				String lineout = "";
				if (line.startsWith("!"))
					continue;
				if (line.startsWith("^"))
					line = "\\" + line;
				StringTokenizer tok = new StringTokenizer(line, "^");
				if (!tok.hasMoreTokens()) {
					strout = strout + line + "\n";
					continue;
				}
				String sA1 = tok.nextToken();
				lineout = lineout + sA1;
				Boolean on = new Boolean(false);
				while (tok.hasMoreTokens()) {
					sA1 = tok.nextToken();
					String x = subparse(sA1, slotname, value, on);
					if (x.startsWith("\\")) {
						on = new Boolean(true);
						lineout = lineout + x.substring(1);
					} else {
						on = new Boolean(false);
						lineout = lineout + x;
					}
				}
				if (lineout.equals("null"))
					lineout = "";
				if (lineout.startsWith("\\")) {
					strout = strout + lineout.substring(1) + "\n";
				}
				else
					strout = strout + lineout + "\n";
			}
		} catch (Exception e) {
			logger.error("!!!!! Caught General Exception :: " + e.toString());
		}
		str = strout;
	}

	public String generateCode() {
		Enumeration e = keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value = (String) get(key);
			replaceSlot(key, value);
		}
		return str;
	}
}