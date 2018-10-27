CREATE OR REPLACE FORCE VIEW EXTN_NWCG_AVAIL_TASKS_VW ("CACHE ID", "ACTIVITY_CODE", "TASK_TYPE_NAME", "ITEM ID", "QUANTITY", "TASK_STATUS", "SOURCE_LOCATION_ID", "SOURCE_ZONE_ID", "TARGET_LOCATION_ID", "TARGET_ZONE_ID", "ASSIGNED_TO_USER_ID")
AS
  SELECT t.organization_code "CACHE ID",
    tt.activity_code,
    tt.task_type_name,
    TRIM (t.item_id) "ITEM ID",
    t.quantity,
    DECODE (TRIM (t.task_status), '1000', 'Draft', '1100', 'Open', '1200', 'Suggested', '1300', 'In Progress', '1400', 'Held', '2000', 'Completed', '9000', 'Canceled' ) task_status,
    t.source_location_id,
    t.source_zone_id,
    t.target_location_id,
    t.target_zone_id,
    t.assigned_to_user_id
  FROM yfs_task t,
    yfs_task_type tt
  WHERE t.task_type       = tt.task_type
  AND t.organization_code = tt.organization_code
  AND t.task_status      IN ('1100', '1200', '1300', '1400');