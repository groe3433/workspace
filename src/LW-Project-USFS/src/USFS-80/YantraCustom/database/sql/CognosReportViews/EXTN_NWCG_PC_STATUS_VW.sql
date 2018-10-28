CREATE OR REPLACE FORCE VIEW EXTN_NWCG_PC_STATUS_VW ("CACHE ID", "COUNT REQUEST", "REQUEST_CREATED", "ZONE ID", "LOCATION_CRITERIA", "AISLE_CRITERIA", "BAY_CRITERIA", "LEVEL_CRITERIA", "ITEM ID", "FIRST_COUNTER", "FIRST_COMPLETE", "FIRST_TOTAL", "SECOND_COUNTER", "SECOND_COMPLETE", "SECOND_TOTAL", "THIRD_COUNTER", "THIRD_COMPLETE", "THIRD_TOTAL", "STATUS", "ITERATION", "FINAL")
AS
  SELECT TRIM (cr.node_key) "CACHE ID",
    TRIM (cr.count_request_no) "COUNT REQUEST",
    cr.createts request_created,
    NVL (l.zone_id, cr.zone_id) "ZONE ID",
    cr.location_id location_criteria,
    NVL (cr.aisle_number, l.aisle_number) aisle_criteria,
    NVL (cr.bay_number, l.bay_number) bay_criteria,
    NVL (cr.level_number, l.level_number) level_criteria,
    TRIM (cr.item_id) "ITEM ID",
    MAX(
    CASE
      WHEN t.task_type = 'PC-1ST'
      THEN TRIM (assigned_to_user_id)
    END) first_counter,
    SUM(
    CASE
      WHEN t.task_type  = 'PC-1ST'
      AND t.task_status = '2000'
      THEN 1
      ELSE 0
    END) first_complete,
    SUM (
    CASE
      WHEN t.task_type = 'PC-1ST'
      THEN 1
      ELSE 0
    END) first_total,
    MAX(
    CASE
      WHEN t.task_type = 'PC-2ND'
      THEN TRIM (assigned_to_user_id)
    END) second_counter,
    SUM(
    CASE
      WHEN t.task_type  = 'PC-2ND'
      AND t.task_status = '2000'
      THEN 1
      ELSE 0
    END) second_complete,
    SUM (
    CASE
      WHEN t.task_type = 'PC-2ND'
      THEN 1
      ELSE 0
    END) second_total,
    MAX(
    CASE
      WHEN t.task_type = 'PC-3RD'
      THEN TRIM (assigned_to_user_id)
    END) third_counter,
    SUM(
    CASE
      WHEN t.task_type  = 'PC-3RD'
      AND t.task_status = '2000'
      THEN 1
      ELSE 0
    END) third_complete,
    SUM (
    CASE
      WHEN t.task_type = 'PC-3RD'
      THEN 1
      ELSE 0
    END) third_total,
    s.description status,
    DECODE (TRIM (MAX (t.task_type)), 'PC-1ST', '1st Counts', 'PC-2ND', '2nd Counts', 'PC-3RD', '3rd Counts' ) iteration,
    (
    CASE
      WHEN s.description LIKE 'Count Complete%'
      THEN 'Y'
      ELSE 'N'
    END) final
  FROM yfs_count_request cr,
    yfs_task t,
    yfs_location l,
    yfs_status s
  WHERE cr.count_request_key = t.count_request_key(+)
  AND cr.location_id         = l.location_id(+)
  AND cr.node_key            = l.node_key(+)
  AND cr.status NOT LIKE '9000%'
  AND t.task_status(+)  != '9000'
  AND cr.status          = s.status
  AND s.process_type_key = 'COUNT_EXECUTION'
  GROUP BY cr.node_key,
    cr.count_request_no,
    cr.createts,
    l.zone_id,
    cr.zone_id,
    cr.location_id,
    cr.aisle_number,
    cr.bay_number,
    cr.level_number,
    cr.item_id,
    s.description,
    l.aisle_number,
    l.bay_number,
    l.level_number;