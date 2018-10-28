create or replace
PROCEDURE ARCHIVEBILLINGTRANSACTION AS 
BEGIN
  DBMS_OUTPUT.PUT_LINE('Starting INSERT INTO NWCG_BILLING_TRANSACTION_H for records older than 3 years at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;
 
 insert into NWCG_BILLING_TRANSACTION_H
  select 
    SEQUENCE_KEY,
    INCIDENT_NO,
    INCIDENT_NAME,
    ENTERPRISE_CODE,
    DOCUMENT_TYPE,
    DOCUMENT_NO,
    TRANS_TYPE,
    TRANSACTION_NO,
    TRANS_DATE,
    TRANS_QTY,
    TRANSACTION_HEADER_KEY,
    TRANSACTION_LINE_KEY,
    TRANSACTION_FISCAL_YEAR,
    ITEM_ID,
    ITEM_CLASSIFICATION,
    ITEM_DESCRIPTION,
    UOM,
    UNIT_COST,
    INCIDENT_FS_ACCT_CODE,
    INCIDENT_BLM_ACCT_CODE,
    INCIDENT_OTHER_ACCT_CODE,
    CACHE_ID,
    DISPOSITION_CODE,
    LAST_EXTRACT_DATE,
    IS_EXTRACTED,
    CREATETS,
    SYSDATE,
    CREATEUSERID,
    MODIFYUSERID,
    CREATEPROGID,
    'PURGE',
    LOCKID,
    IS_REVIEWED,
    TRANS_AMOUNT,
    INCIDENT_YEAR,
    ITEM_PRODUCT_LINE,
    INCIDENT_FS_OVERRIDE_CODE,
    IS_ACCOUNT_SPLIT,
    SPLIT_AMT_NUMBER,
    LAST_FS_ACCT_CODE,
    LAST_FS_OVERRIDE_CODE,
    LAST_BLM_ACCT_CODE,
    LAST_OTHER_ACCT_CODE,
    LOCATION_ID,
    EXTRACT_TRANS_NO,
    REASON_CODE,
    REASON_CODE_TEXT,
    TRANS_CREATEUSERID,
    TRANS_MODIFYUSERID
  from NWCG_BILLING_TRANSACTION
  where  TO_CHAR (NWCG_BILLING_TRANSACTION.MODIFYTS, 'YYYYMMDD') < TO_CHAR(SYSDATE-(3*365),'YYYYMMDD');
  dbms_output.put_line('# ROWS INSERTED TO NWCG_BILLING_TRANSACTION '||to_char(sql%rowcount) );

  delete from NWCG_BILLING_TRANSACTION
  where TO_CHAR (NWCG_BILLING_TRANSACTION.MODIFYTS, 'YYYYMMDD') < TO_CHAR(SYSDATE-(3*365),'YYYYMMDD');
  dbms_output.put_line('# ROWS DELETED FROM NWCG_BILLING_TRANSACTION '||to_char(SQL%rowcount));
  commit;
       
  DBMS_OUTPUT.PUT_LINE('Completed archive of NWCG_BILLING_TRANSACTION at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
       
        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE
              ('Duplicate Key Value found when INSERTING into NWCG_BILLING_TRANSACTION_H table.  Rolling back changes for messages');
             ROLLBACK TO SAVEPOINT SP1;
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when archiving NWCG_BILLING_TRANSACTION.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during NWCG_BILLING_TRANSACTION archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during NWCG_BILLING_TRANSACTION archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
END ARCHIVEBILLINGTRANSACTION;
