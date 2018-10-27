package com.fanatics.sterling.util;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.fanatics.sterling.constants.OrderReleaseConstants;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.dblayer.YFCContext;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

public class FANDBUtil {

	private static YFCLogCategory logger= YFCLogCategory.instance(YFCLogCategory.class);
	
	public static ArrayList<Object[]> getDBResult(YFSEnvironment env, String sqlQuery,int noOfColumnus) throws Exception
	{
		YFCContext ctxt = (YFCContext) env;
		Statement stmt = null;
		ResultSet rs = null;
		ArrayList<Object[]> queryRecords = new ArrayList<Object[]>();
			try {
				stmt = ctxt.getConnection().createStatement();
				rs = stmt.executeQuery(sqlQuery);
				logger.verbose("Executed SQL statement");
				
				while (rs.next()) {
					Object[] resultRow = new Object[noOfColumnus]; 
					for( int i=0;i<noOfColumnus;i++)
					{
						resultRow[i]=rs.getObject(i+1);
					}
					queryRecords.add(resultRow);
				}
			} catch (Exception e) {
				logger.verbose("Exception in  :: getDBResult :: DSWDBUtil "	+ e);
				throw new YFSException(e.getMessage());
			} finally {
				// Close Resultset
				try {
					rs.close();
				} catch (Exception e) {
					logger.verbose("Exception while closing resultset  :: getDBResult :: DSWDBUtil "	+ e);
					throw new YFSException(e.getMessage());
				}

				// Close Statement
				try {
					stmt.close();
				} catch (Exception e) {
					logger.verbose("Exception while closing statement  :: getDBResult :: DSWDBUtil "	+ e);
					throw new YFSException(e.getMessage());
				}
			}
			return queryRecords;
	}
	
	@SuppressWarnings({ "unused" })
	public static String getNexSeqNo(YFSEnvironment env, String serviceName, String seqName) throws YFSUserExitException {

		logger.verbose("getNexSeqNo for " + serviceName + " -> Begin");
		
		String seqNoStr = OrderReleaseConstants.NO_VALUE;
		
		try {

			YFCContext ctxt = (YFCContext) env;
			Statement stmt  = ctxt.getConnection().createStatement();
			ResultSet rs    = stmt.executeQuery(
					"SELECT COUNT(*) FROM user_sequences WHERE sequence_name = '"+seqName+"'");

			while (rs.next()) {
				if (rs.getInt(1) < 1) {

					logger.verbose("DB Sequence does not exist - Creating Default Order No Sequence ...");

					Statement createOrderSeqStmt = ctxt.getConnection().createStatement();
					int createSeq 				 = createOrderSeqStmt.executeUpdate("CREATE SEQUENCE "+ seqName +" START WITH 1 INCREMENT BY 1 MINVALUE 1 MAXVALUE 999999999");

				} else {
					logger.verbose("DB Sequence found - extracting Sequence number ...");
				}
			}

			long seqNo = ((YCPContext) env).getNextDBSeqNo(seqName);
			seqNoStr   = Long.toString(seqNo);

		} catch (Exception e) {
			logger.error(serviceName + " --> ERROR: " + e.getMessage(), e);
			throw new YFSUserExitException("Error_"+serviceName);
		}

		logger.verbose("Returning Sequence no:" + seqNoStr);

		return seqNoStr;

	}
}
