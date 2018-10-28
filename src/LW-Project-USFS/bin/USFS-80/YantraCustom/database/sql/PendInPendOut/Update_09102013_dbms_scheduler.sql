BEGIN
DBMS_SCHEDULER.CREATE_JOB (
   job_name             => 'NWCG_Clear_Rogue_PendIN_and_PendOUT',
   job_type             => 'STORED_PROCEDURE',
   job_action           => "YFS80"."CLEARROGUEPENDINPENDOUT",
   start_date           => '09-OCT-13 1.00.00AM US/Pacific',
   repeat_interval      => 'FREQ=DAILY', 
   end_date             => '31-DEC-20 1.00.00AM US/Pacific',
   enabled              =>  TRUE,
   comments             => 'Clear the Rogue Pend IN and Pend OUT Move Requests');
END;
/

