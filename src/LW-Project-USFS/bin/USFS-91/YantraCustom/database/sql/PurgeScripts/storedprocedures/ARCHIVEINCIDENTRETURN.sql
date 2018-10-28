create or replace
PROCEDURE ARCHIVEINCIDENTRETURN AS

   i_count INTEGER := 0;
   d_count INTEGER := 0;
   emesg VARCHAR2(250);
   
CURSOR del_record_cur is 

SELECT
      DISTINCT(NWCG_INCIDENT_RETURN.INCIDENT_RET_KEY)
      FROM NWCG_INCIDENT_RETURN, NWCG_INCIDENT_ORDER, YFS_ORDER_HEADER_H
      WHERE TO_CHAR (NWCG_INCIDENT_RETURN.MODIFYTS, 'YYYYMMDD') < TO_CHAR(SYSDATE-(3*365),'YYYYMMDD')
      AND YFS_ORDER_HEADER_H.extn_incident_no = NWCG_INCIDENT_ORDER.incident_no
      AND ((NWCG_INCIDENT_RETURN.ISSUE_NO is null AND NWCG_INCIDENT_RETURN.incident_no = NWCG_INCIDENT_ORDER.incident_no)
      or (NWCG_INCIDENT_RETURN.ISSUE_NO is not null AND NWCG_INCIDENT_RETURN.ISSUE_NO = YFS_ORDER_HEADER_H.order_no)); 
      
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting INSERT INTO NWCG_INCIDENT_RETURN_H for incident orders older than 3 years at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;
  
  FOR rec in del_record_cur
  LOOP
 
 insert into NWCG_INCIDENT_RETURN_H
  select 
    INCIDENT_RET_KEY,
    ITEM_ID,
    QUANTITY_SHIPPED,
    UNIT_PRICE,
    INCIDENT_NO,
    TRACKABLE_ID,
    CACHE_ID,
    QUANTITY_RETURNED,
    QUANTITY_RFI,
    QUANTITY_NRFI,
    QUANTITY_UNS_RETURN,
    QUANTITY_UNS_NWT_RETURN,
    DATE_ISSUED,
    OVER_RECEIPT,
    RECEIVED_AS_COMPONENT,
    CREATETS,
    SYSDATE,
    CREATEUSERID,
    MODIFYUSERID,
    CREATEPROGID,
    'PURGE',
    LOCKID,
    ISSUE_NO,
    QUANTITY_RFI_REFURB,
    QUANTITY_UNS_REFURB,
    QUANTITY_UNS_NWT_REFURB,
    INCIDENT_YEAR
    from NWCG_INCIDENT_RETURN
  where  NWCG_INCIDENT_RETURN.INCIDENT_RET_KEY = rec.INCIDENT_RET_KEY;
  i_count := i_count + sql%rowcount;
    
  delete from NWCG_INCIDENT_RETURN
  where  NWCG_INCIDENT_RETURN.INCIDENT_RET_KEY = rec.INCIDENT_RET_KEY;
  
  d_count := d_count + sql%rowcount;
    
  commit;
  END LOOP;
  
  dbms_output.put_line('# ROWS INSERTED TO NWCG_INCIDENT_RETURN_H '||TO_CHAR(i_count) );
  dbms_output.put_line('# ROWS DELETED FROM NWCG_INCIDENT_RETURN '||TO_CHAR(d_count));
  
   DBMS_OUTPUT.PUT_LINE('Completed archive of NWCG_INCIDENT_RETURN at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
       
        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE
              ('Duplicate Key Value found when INSERTING into NWCG_INCIDENT_RETURN_H table.  Rolling back changes for messages');
             ROLLBACK TO SAVEPOINT SP1;
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when archiving NWCG_INCIDENT_RETURN.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during NWCG_INCIDENT_RETURN archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
          emesg := SQLERRM;
    dbms_output.put_line(emesg);
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during NWCG_INCIDENT_RETURN archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
END ARCHIVEINCIDENTRETURN;
