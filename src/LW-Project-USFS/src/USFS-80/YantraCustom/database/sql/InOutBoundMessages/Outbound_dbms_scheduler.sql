BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'NWCG_Archive_Outbound_Messages',
   JOB_TYPE             => 'STORED_PROCEDURE', 
   JOB_ACTION           => 'EXECUTE ARCHIVEOUTBOUNDMESSAGES;', 
   start_date           => '05-DEC-13 1.00.00AM US/Pacific',
   REPEAT_INTERVAL      => 'FREQ=DAILY', 
   end_date             => '30-DEC-20 1.00.00AM US/Pacific',
   ENABLED              =>  true,
   comments             => 'Archive into history table messages in NWCG_OUTBOUND_MESSAGE that are 90days old');
END;


