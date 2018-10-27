BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'ClearInboundMessageJob',
   JOB_TYPE             => 'STORED_PROCEDURE', 
   JOB_ACTION           => 'CLEARINBOUNDMESSAGE', 
   start_date           => to_date('30-APR-14 1.00.00','DD-MON-YY HH.MI.SS'),
   REPEAT_INTERVAL      => 'FREQ=DAILY', 
   end_date             => to_date('30-DEC-20 1.00.00','DD-MON-YY HH.MI.SS'),
   ENABLED              =>  true,
   AUTO_DROP		=>  false,
   comments             => 'Clear YFS_AUDIT INBOUNDMESSAGE records over 90 days old');
END;


