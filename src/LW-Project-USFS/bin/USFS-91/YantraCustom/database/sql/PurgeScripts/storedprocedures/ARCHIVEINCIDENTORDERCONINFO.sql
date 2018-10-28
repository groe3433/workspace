create or replace
PROCEDURE ARCHIVEINCIDENTORDERCONINFO AS

   i_count INTEGER := 0;
   d_count INTEGER := 0;
   
  CURSOR c_coninfo IS
      select  NWCG_INCIDENT_ORDER_CON_INFO.CONTACT_INFO_KEY FROM NWCG_INCIDENT_ORDER_CON_INFO, NWCG_INCIDENT_ORDER_H 
      WHERE TRIM(NWCG_INCIDENT_ORDER_CON_INFO.INCIDENT_KEY) = TRIM(NWCG_INCIDENT_ORDER_H.INCIDENT_KEY)
      AND   TO_CHAR (NWCG_INCIDENT_ORDER_CON_INFO.MODIFYTS, 'YYYYMMDD') < TO_CHAR(SYSDATE-(2*365),'YYYYMMDD');  
    
BEGIN

  DBMS_OUTPUT.PUT_LINE('Starting INSERT INTO NWCG_INCIDENT_ORDER_CON_INFO_H for orders not found in  NWCG_INCIDENT_ORDER at '||to_char(sysdate,'MM-DD-YYYY HH24:MI:SS'));
  DBMS_OUTPUT.PUT_LINE('Creating savepoint, for exception rollbacks');
  savepoint SP1;
  
  FOR rec IN c_coninfo 
    LOOP
      insert into NWCG_INCIDENT_ORDER_CON_INFO_H
        select 
        CONTACT_INFO_KEY,
        INCIDENT_KEY,
        CONTACT,
        CONTACT_INFO,
        CONTACT_TYPE,
        CREATETS,
        SYSDATE,
        CREATEUSERID,
        MODIFYUSERID,
        CREATEPROGID,
        'PURGE',
        LOCKID
      from NWCG_INCIDENT_ORDER_CON_INFO
      where CONTACT_INFO_KEY = rec.CONTACT_INFO_KEY;
      
      i_count := i_count + sql%rowcount;
    
      delete from NWCG_INCIDENT_ORDER_CON_INFO 
      where CONTACT_INFO_KEY = rec.CONTACT_INFO_KEY;
      
      d_count := d_count + sql%rowcount;
    
    commit;
  END LOOP;
  
   dbms_output.put_line('# ROWS INSERTED TO NWCG_INCIDENT_ORDER_CON_INFO_H '||TO_CHAR(i_count) );
   dbms_output.put_line('# ROWS DELETED FROM NWCG_INCIDENT_ORDER_CON_INFO '||TO_CHAR(d_count));
  
END ARCHIVEINCIDENTORDERCONINFO;
