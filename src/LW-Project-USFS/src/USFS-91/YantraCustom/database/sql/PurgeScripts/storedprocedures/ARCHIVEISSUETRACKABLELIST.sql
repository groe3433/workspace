create or replace
PROCEDURE ARCHIVEISSUETRACKABLELIST AS
   i_count INTEGER := 0;
   d_count INTEGER := 0;
   
   CURSOR c_del_record_cur IS
    select tl.sequence_key 
    from nwcg_issue_trackable_list tl
    join yfs_order_header_h ohh
      on tl.incident_no = ohh.extn_incident_no
    where TRIM(tl.issue_no) = TRIM(ohh.order_no);
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting INSERT INTO NWCG_ISSUE_TRACKABLE_LIST_H for orders not found in YFS_ORDER_HEADER at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;
 
  FOR rec IN c_del_record_cur 
  LOOP
    insert into nwcg_issue_trackable_list_h
    select 
      SEQUENCE_KEY,
      ISSUE_NO,
      DOCUMENT_TYPE,
      INCIDENT_NO,
      INCIDENT_YEAR,
      CACHE_ID,
      ITEM_ID,
      ITEM_DESCRIPTION,
      SERIAL_NO,
      ORDER_HEADER_KEY,
      ORDER_LINE_KEY,
      PARENT_ITEM_ID_1,
      PARENT_SERIAL_NO_1,
      PARENT_ITEM_ID_2,
      PARENT_SERIAL_NO_2,
      PARENT_ITEM_ID_3,
      PARENT_SERIAL_NO_3,
      NODE_LEVEL,
      CREATETS,
      SYSDATE,
      CREATEUSERID,
      MODIFYUSERID,
      CREATEPROGID,
      'PURGE',
      LOCKID,
      ISSUED_XFER
    from nwcg_issue_trackable_list
    where sequence_key = rec.sequence_key;
    
    i_count := i_count + sql%rowcount;
    
    delete from nwcg_issue_trackable_list  
    where sequence_key = rec.sequence_key;
    
    d_count := d_count + sql%rowcount;
    
    commit;
  END LOOP;
  
  dbms_output.put_line('# ROWS INSERTED TO NWCG_ISSUE_TRACKABLE_LIST_H '||TO_CHAR(i_count) );
  dbms_output.put_line('# ROWS DELETED FROM NWCG_ISSUE_TRACKABLE_LIST '||TO_CHAR(d_count) );
       
  DBMS_OUTPUT.PUT_LINE('Completed archive of NWCG_ISSUE_TRACKABLE_LIST at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
       
        EXCEPTION
          WHEN DUP_VAL_ON_INDEX THEN
            DBMS_OUTPUT.PUT_LINE
              ('Duplicate Key Value found when INSERTING into NWCG_ISSUE_TRACKABLE_LIST_H table.  Rolling back changes for messages');
             ROLLBACK TO SAVEPOINT SP1;
              
          WHEN STORAGE_ERROR THEN
            DBMS_OUTPUT.PUT_LINE
              ('PL/SQL ran out of memory when archiving NWCG_ISSUE_TRACKABLE_LIST.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
            
          WHEN TIMEOUT_ON_RESOURCE THEN
            DBMS_OUTPUT.PUT_LINE
              ('A time out occurred while the database is waiting for a resource during NWCG_ISSUE_TRACKABLE_LIST archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
              
          WHEN OTHERS THEN 
            DBMS_OUTPUT.PUT_LINE
              ('An error occurred during NWCG_ISSUE_TRACKABLE_LIST archiving process.  Rolling back changes for messages');
              ROLLBACK TO SAVEPOINT SP1;
      
END ARCHIVEISSUETRACKABLELIST;
