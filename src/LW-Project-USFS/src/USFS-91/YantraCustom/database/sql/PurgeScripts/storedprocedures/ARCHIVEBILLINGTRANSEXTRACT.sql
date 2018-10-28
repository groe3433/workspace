create or replace
PROCEDURE ARCHIVEBILLINGTRANSEXTRACT AS 
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting INSERT INTO NWCG_BILLING_TRANS_EXTRACT_H for records older than 3 years at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;
 
 insert into NWCG_BILLING_TRANS_EXTRACT_H
  select 
    EXTRACT_SEQUENCE_KEY,
    DOCUMENT_NO,
    FISCAL_YEAR1,
    CALENDAR_MONTH,
    SEQUENCE_NO,
    STATE,
    OFFICE,
    FISCAL_YEAR2,
    FUND_CODE,
    ACTIVITY_CODE,
    PROGRAM_ELEMENT,
    PROJECT_CODE,
    OBJECT,
    TRANS_AMOUNT,
    EXTRACT_FILE_NAME,
    CREATETS,
    SYSDATE,
    CREATEUSERID,
    MODIFYUSERID,
    CREATEPROGID,
    MODIFYPROGID,
    LOCKID,
    ACCT_CODE,
    TOTAL_RECORDS,
    PMS_TRANS_AMOUNT,
    EXTRACT_TRANS_NO,
    CACHE_ID,
    BUSINESS_AREA,
    INTERFACE_TYPE,
    DOCUMENT_DATE,
    POSTING_DATE,
    FISCAL_PERIOD,
    REFERENCE_DOCUMENT_NUMBER,
    DOCUMENT_HEADER_TEXT,
    POSTING_KEY,
    GL_ACCOUNT_CODE,
    ITEM_TEXT,
    TRADING_PARTNER_COMPANY_ID,
    COST_CENTER,
    WBS,
    FUNCTIONAL_AREA,
    ORDER_NUMBER,
    COMMITMENT_ITEM,
    FUNDS_CENTER,
    FUND,
    DOC_NUM_FOR_EARMARKED_FUNDS,
    EARMARKED_FUNDS_DOC_ITEM,
    AMOUNT_IN_DOC_CURRENCY,
    INCIDENT_BLM_ACCT_CODE,
    TRANSACTION_NO,
    INTERFACE_POSTING_TYPE
  from NWCG_BILLING_TRANS_EXTRACT
  where  TO_CHAR (NWCG_BILLING_TRANS_EXTRACT.MODIFYTS, 'YYYYMMDD') < TO_CHAR(SYSDATE-(3*365),'YYYYMMDD');
  dbms_output.put_line('# ROWS INSERTED TO NWCG_BILLING_TRANS_EXTRACT '||to_char(sql%rowcount) );

  delete from NWCG_BILLING_TRANS_EXTRACT
  where TO_CHAR (NWCG_BILLING_TRANS_EXTRACT.MODIFYTS, 'YYYYMMDD') < TO_CHAR(SYSDATE-(3*365),'YYYYMMDD');
  dbms_output.put_line('# ROWS DELETED FROM NWCG_BILLING_TRANS_EXTRACT '||to_char(SQL%rowcount));
  commit;
       
  DBMS_OUTPUT.PUT_LINE('Completed archive of NWCG_BILLING_TRANS_EXTRACT at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
       
        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE
              ('Duplicate Key Value found when INSERTING into NWCG_BILLING_TRANS_EXTRACT_H table.  Rolling back changes for messages');
             ROLLBACK TO SAVEPOINT SP1;
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when archiving NWCG_BILLING_TRANS_EXTRACT.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during NWCG_BILLING_TRANS_EXTRACT archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during NWCG_BILLING_TRANS_EXTRACT archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
END ARCHIVEBILLINGTRANSEXTRACT;
