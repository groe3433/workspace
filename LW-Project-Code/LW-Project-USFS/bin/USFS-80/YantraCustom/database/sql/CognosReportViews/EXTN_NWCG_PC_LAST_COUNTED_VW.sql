CREATE OR REPLACE FORCE VIEW EXTN_NWCG_PC_LAST_COUNTED_VW ("CACHE ID", "ZONE ID", "LOCATION_ID", "AISLE_NUMBER", "BAY_NUMBER", "LEVEL_NUMBER", "LAST_COUNTED", "LAST_REQUEST")
AS
  SELECT l.node_key "CACHE ID",
    l.zone_id "ZONE ID",
    l.location_id,
    l.aisle_number,
    l.bay_number,
    l.level_number,
    MAX (t.createts) last_counted,
    SUBSTR (MAX (TO_CHAR (t.createts, 'yyyymmdd')
    || t.count_request_no), 9 ) last_request
  FROM yfs_location l,
    yfs_task t
  WHERE l.node_key                  = t.organization_code(+)
  AND l.location_id                 = t.source_location_id(+)
  AND SUBSTR (t.task_type(+), 1, 3) = 'PC-'
  GROUP BY l.node_key,
    l.zone_id,
    l.location_id,
    l.aisle_number,
    l.bay_number,
    l.level_number;