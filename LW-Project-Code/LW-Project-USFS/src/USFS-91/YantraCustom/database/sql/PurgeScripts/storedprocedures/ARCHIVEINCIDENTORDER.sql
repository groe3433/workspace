create or replace
PROCEDURE ARCHIVEINCIDENTORDER AS 
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting INSERT INTO NWCG_INCIDENT_ORDER_H for incident orders older than 3 years at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;
 
 insert into NWCG_INCIDENT_ORDER_H
  select 
    INCIDENT_KEY,
    INCIDENT_NO,
    INCIDENT_NAME,
    INCIDENT_HOST,
    INCIDENT_TYPE,
    INCIDENT_SOURCE,
    INCIDENT_FS_ACCT_CODE,
    INCIDENT_BLM_ACCT_CODE,
    INCIDENT_OTHER_ACCT_CODE,
    YEAR,
    CUSTOMER_ID,
    PRIMARY_CACHE_ID,
    DATE_STARTED,
    DATE_CLOSED,
    INCIDENT_TEAM_TYPE,
    REPLACED_INCIDENT_NO,
    IS_OTHER_ORDER,
    CUSTOMER_PO_NO,
    OTHER_ORDER_TYPE,
    ADDITIONAL_INFO,
    PHONE_NO,
    PERSON_INFO_SHIPTO_KEY,
    PERSON_INFO_BILLTO_KEY,
    PERSON_INFO_DELIVERTO_KEY,
    CREATETS,
    SYSDATE,
    CREATEUSERID,
    MODIFYUSERID,
    CREATEPROGID,
    'PURGE',
    LOCKID,
    IS_ACTIVE,
    IS_COST_SHARED,
    OVERRIDE_CODE,
    REPLACED_INCIDENT_NO_2,
    INCIDENT_TEAM_NAME,
    GACC,
    CUSTOMER_NAME,
    MODIFICATION_CODE,
    MODIFICATION_DESC,
    UNIT_TYPE,
    AGENCY,
    DEPARTMENT,
    REPLACED_INCIDENT_YEAR,
    REPLACED_INCIDENT_YEAR_2,
    REGISTER_INTEREST_IN_ROSS,
    INCIDENT_LOCKED,
    ROSS_INCIDENT_STATUS,
    ROSS_DISPATCH_ID,
    IS_COMPLEX_INDICATOR,
    ROSS_FINANCIAL_CODE,
    ROSS_FINANCIAL_FISCAL_YEAR,
    REQUEST_NO_BLOCK_START,
    REQUEST_NO_BLOCK_END,
    GEOGRAPHIC_COORD_LATITUDE,
    GEOGRAPHIC_COORD_LONGITUDE,
    LAST_UPDATED_FROM_ROSS,
    IS_SUPPRESSIVE,
    ICBS_FISCAL_YEAR,
    LOCK_REASON,
    COST_CENTER,
    WBS,
    FUNCTIONAL_AREA,
    REPLACED_INCIDENT_NO_3,
    REPLACED_INCIDENT_YEAR_3,
    SHIPPING_INSTRUCTIONS,
    SHIP_INSTR_CITY,
    SHIP_INSTR_STATE,
    INCIDENT_ID
  from NWCG_INCIDENT_ORDER
  where  TO_CHAR (NWCG_INCIDENT_ORDER.MODIFYTS, 'YYYYMMDD') < TO_CHAR(SYSDATE-(3*365),'YYYYMMDD')
      and    NWCG_INCIDENT_ORDER.IS_OTHER_ORDER='N';
  dbms_output.put_line('# ROWS INSERTED TO NWCG_INCIDENT_ORDER_H '||TO_CHAR(sql%rowcount) );

  delete from NWCG_INCIDENT_ORDER
  where TO_CHAR (NWCG_INCIDENT_ORDER.MODIFYTS, 'YYYYMMDD') < TO_CHAR(SYSDATE-(3*365),'YYYYMMDD')
      and    NWCG_INCIDENT_ORDER.IS_OTHER_ORDER='N';
  dbms_output.put_line('# ROWS DELETED FROM NWCG_INCIDENT_ORDER '||TO_CHAR(SQL%rowcount));
  commit;
       
  DBMS_OUTPUT.PUT_LINE('Completed archive of NWCG_INCIDENT_ORDER at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
       
        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE
              ('Duplicate Key Value found when INSERTING into NWCG_INCIDENT_ORDER_H table.  Rolling back changes for messages');
             ROLLBACK TO SAVEPOINT SP1;
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when archiving NWCG_INCIDENT_ORDER.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during NWCG_INCIDENT_ORDER archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during NWCG_INCIDENT_ORDER archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
END ARCHIVEINCIDENTORDER;
