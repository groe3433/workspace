BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'ClearYFSResourcePermissionJob',
   JOB_TYPE             => 'STORED_PROCEDURE', 
   JOB_ACTION           => 'CLEARYFSRESOURCEPERMISSION', 
   start_date           => to_date('30-APR-14 1.00.00','DD-MON-YY HH.MI.SS'),
   REPEAT_INTERVAL      => 'FREQ=DAILY', 
   end_date             => to_date('30-DEC-20 1.00.00','DD-MON-YY HH.MI.SS'),
   ENABLED              =>  true,
   AUTO_DROP		=>  false,
   comments             => 'purged after 90 days');
END;


