package com.fanatics.sterling.ue;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import com.fanatics.sterling.constants.CreateOrderConstants;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.dblayer.YFCContext;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetOrderNoUE;

/**
 * 
 * This class provides the Use Exit implementation for the YFSGetOrderNoUE.
 * 
 * @(#) FANYFSGetOrderNoUEImpl.java Created on Apr 12, 2016 11:30:22 AM
 * 
 * Package Declaration: File Name: FANYFSGetOrderNoUEImpl.java Package Name:
 * com.fanatics.sterling.ue Project name: Fanatics Type Declaration: Class
 * Name: FANYFSGetOrderNoUEImpl Type Comment:
 * @author kNtagkas
 * @version 1.0
 * @history
 * 
 * 
 * 
 * (C) Copyright 2016 by owner. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of the owner.
 * ("Confidential Information"). Redistribution of the source code or binary
 * form is not permitted without prior authorization from the owner.
 * 
 */

public class FANYFSGetOrderNoUEImpl implements YFSGetOrderNoUE {

	private static YFCLogCategory log = YFCLogCategory.instance("com.yantra.yfc.log.YFCLogCategory");

	@Override
	@SuppressWarnings({ "unused", "rawtypes" })
	public String getOrderNo(YFSEnvironment env, Map map) throws YFSUserExitException {

		log.verbose("FANYFSGetOrderNoUEImpl -> Begin");

		String documentType = (String) map.get(CreateOrderConstants.ATT_DOCUMENT_TYPE);
		String seqNoStr     = CreateOrderConstants.NO_VALUE;
		
		try {
			
			YFCContext ctxt = (YFCContext) env;
			Statement stmt  = ctxt.getConnection().createStatement();
			ResultSet rs    = stmt.executeQuery(CreateOrderConstants.SEQ_EXISTS_SQL);

			while (rs.next()) {
				if (rs.getInt(1) < 1) {
					log.verbose("DB Sequence does not exist - Creating Default Order No Sequence ...");
					Statement createOrderSeqStmt = ctxt.getConnection().createStatement();
					int createSeq = createOrderSeqStmt.executeUpdate(CreateOrderConstants.SEQ_CREATE_SQL);
				} else {
					log.verbose("DB Sequence found - extracting Sequence number ...");
				}
			}

			long seqNo = ((YCPContext) env).getNextDBSeqNo(CreateOrderConstants.SEQ_FANATICS_ORDER_NO);
			seqNoStr   = Long.toString(seqNo);

		} catch (Exception e) {
			log.error("FANYFSGetOrderNoUEImpl --> ERROR: " + e.getMessage(), e);
			throw new YFSUserExitException("Error_SEQ_FANATICS_ORDER_NO");
		}
		
		log.verbose("Returning Order no:" + seqNoStr + "for DocumentType::" + documentType);

		log.verbose("Leaving FANYFSGetOrderNoUEImpl -> getOrderNo");

		return seqNoStr;

	}

}
