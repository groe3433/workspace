BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'ClearIncidentOrderConInfoJob',
   JOB_TYPE             => 'STORED_PROCEDURE', 
   JOB_ACTION           => 'CLEARAUDITINCIDENTORDERCONINFO', 
   start_date           => to_date('30-APR-14 1.00.00','DD-MON-YY HH.MI.SS'),
   REPEAT_INTERVAL      => 'FREQ=DAILY', 
   end_date             => to_date('30-DEC-20 1.00.00','DD-MON-YY HH.MI.SS'),
   ENABLED              =>  true,
   AUTO_DROP		=>  false,
   comments             => 'Delete YFS_AUDIT records when INCIDENT is moved to history table.');
END;


