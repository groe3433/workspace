BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'ArchiveBillingTransactionJob',
   JOB_TYPE             => 'STORED_PROCEDURE', 
   JOB_ACTION           => 'ARCHIVEBILLINGTRANSACTION', 
   start_date           => to_date('30-APR-14 1.00.00','DD-MON-YY HH.MI.SS'),
   REPEAT_INTERVAL      => 'FREQ=DAILY', 
   end_date             => to_date('21-FEB-20 1.00.00','DD-MON-YY HH.MI.SS'),
   ENABLED              =>  true,
   AUTO_DROP		=>  false,
   comments             => 'Archive into history table messages in NWCG_BILLING_TRANSACTION that are 3 years old');
END;


