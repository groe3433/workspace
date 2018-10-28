BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'ClearMasterWorkOrderLineJob',
   JOB_TYPE             => 'STORED_PROCEDURE', 
   JOB_ACTION           => 'CLEARMASTERWORKORDERLINE', 
   start_date           => to_date('30-APR-14 1.00.00','DD-MON-YY HH.MI.SS'),
   REPEAT_INTERVAL      => 'FREQ=DAILY', 
   end_date             => to_date('30-DEC-20 1.00.00','DD-MON-YY HH.MI.SS'),
   ENABLED              =>  true,
   AUTO_DROP		=>  false,
   comments             => 'Delete YFS_AUDIT records when Master Work Order is moved to history table.');
END;


