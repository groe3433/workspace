BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'ArchiveIssueTrackableListJob',
   JOB_TYPE             => 'STORED_PROCEDURE', 
   JOB_ACTION           => 'ARCHIVEISSUETRACKABLELIST', 
   start_date           => to_date('30-APR-14 1.00.00','DD-MON-YY HH.MI.SS'),
   REPEAT_INTERVAL      => 'FREQ=DAILY', 
   end_date             => to_date('30-DEC-20 1.00.00','DD-MON-YY HH.MI.SS'),
   ENABLED              =>  true,
   AUTO_DROP		=>  false,
   comments             => 'Archive into history table messages in NWCG_ISSUE_TRACKABLE_LIST FOR ORDERS NOT FOUND IN YFS_ORDER_HEADER');
END;


