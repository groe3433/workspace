BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'ClearRossAccountCodesJob',
   JOB_TYPE             => 'STORED_PROCEDURE', 
   JOB_ACTION           => 'CLEARROSSACCOUNTCODES', 
   start_date           => to_date('30-APR-14 1.00.00','DD-MON-YY HH.MI.SS'),
   REPEAT_INTERVAL      => 'FREQ=DAILY', 
   end_date             => to_date('30-DEC-20 1.00.00','DD-MON-YY HH.MI.SS'),
   ENABLED              =>  true,
   AUTO_DROP		=>  false,
   comments             => 'purged when "TABLE_KEY" is not in table NWCG_ROSS_ACCOUNT_CODE');
END;


