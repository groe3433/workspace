CREATE OR REPLACE FORCE VIEW EXTN_NWCG_PC_RESULTS_VW ("CACHE ID", "COUNT REQUEST", "REQUEST_CREATED", "ZONE ID", "LOCATION_CRITERIA", "AISLE_CRITERIA", "BAY_CRITERIA", "LEVEL_CRITERIA", "LOCATION_ID", "ITEM ID", "SHORT_DESCRIPTION", "UNIT_COST", "SERIAL_NO", "TAG_NUMBER", "SYSTEM_QUANTITY", "FIRST_COUNT", "SECOND_COUNT", "THIRD_COUNT", "LAST_COUNT_TYPE", "LAST_COUNT", "VARIANCE_ACCEPTED", "QTY_VARIANCE", "COST_VARIANCE", "TASK_STATUS")
AS
  SELECT y.node_key "CACHE ID" ,
    y.count_request_no "COUNT REQUEST",
    y.createts "REQUEST_CREATED",
    l.zone_id "ZONE ID",
    l.location_id "LOCATION_CRITERIA",
    l.aisle_number "AISLE_CRITERIA",
    l.bay_number "BAY_CRITERIA",
    l.level_number "LEVEL_CRITERIA",
    y.location_id,
    y.item_id "ITEM ID",
    y.short_description,
    y.unit_cost,
    y.serial_no,
    y.tag_number,
    y.system_quantity,
    y.first_count,
    y.second_count,
    y.third_count,
    y.last_count_type,
    (SELECT SUM(b5.count_quantity)
    FROM yfs_count_result b5
    WHERE b5.count_request_key = y.count_request_key
    AND b5.item_id             = y.item_id
    AND (b5.serial_no          = y.serial_no
    OR b5.tag_number           = y.tag_number)
    AND b5.location_id         = y.location_id
    AND b5.task_type           = y.last_count_type
    ) "LAST_COUNT",
    (SELECT MAX(variance_accepted)
    FROM yfs_count_result b7
    WHERE b7.count_request_key = y.count_request_key
    AND b7.item_id             = y.item_id
    AND b7.location_id         = y.location_id
    AND b7.task_type           = y.last_count_type
    AND (b7.serial_no          = y.serial_no
    OR b7.tag_number           = y.tag_number)
    AND b7.inventory_status    = 'RFI'
    ) "VARIANCE_ACCEPTED",
    (SELECT SUM(b8.count_quantity - b8.system_quantity)
    FROM yfs_count_result b8
    WHERE b8.count_request_key = y.count_request_key
    AND b8.item_id             = y.item_id
    AND (b8.serial_no          = y.serial_no
    OR b8.tag_number           = y.tag_number)
    AND b8.location_id         = y.location_id
    AND b8.task_type           = y.last_count_type
    ) "QTY_VARIANCE",
    (SELECT NVL((SUM(b9.count_quantity - b9.system_quantity) * y.unit_cost),0)
    FROM yfs_count_result b9
    WHERE b9.count_request_key = y.count_request_key
    AND b9.item_id             = y.item_id
    AND (b9.serial_no          = y.serial_no
    OR b9.tag_number           = y.tag_number)
    AND b9.location_id         = y.location_id
    AND b9.task_type           = y.last_count_type
    AND b9.variance_accepted  != 'I'
    ) "COST_VARIANCE",
    (SELECT NVL(MAX(task_status),'0')
    FROM yfs_task yt
    WHERE yt.count_request_key = y.count_request_key
    AND yt.item_id             = y.item_id
    ) "TASK_STATUS"
  FROM
    (SELECT x.count_request_key,
      x.node_key,
      x.count_request_no,
      x.createts,
      x.location_id,
      x.item_id,
      x.short_description,
      x.unit_cost,
      x.serial_no,
      x.tag_number,
      x.system_quantity,
      (SELECT SUM(b1.count_quantity)
      FROM yfs_count_result b1
      WHERE b1.count_request_key = x.count_request_key
      AND b1.item_id             = x.item_id
      AND (b1.serial_no          = x.serial_no
      OR b1.tag_number           = x.tag_number)
      AND b1.location_id         = x.location_id
      AND b1.task_type           = 'PC-1ST'
      ) "FIRST_COUNT",
      (SELECT SUM(b2.count_quantity)
      FROM yfs_count_result b2
      WHERE b2.count_request_key = x.count_request_key
      AND b2.item_id             = x.item_id
      AND (b2.serial_no          = x.serial_no
      OR b2.tag_number           = x.tag_number)
      AND b2.location_id         = x.location_id
      AND b2.task_type           = 'PC-2ND'
      ) "SECOND_COUNT",
      (SELECT SUM(b3.count_quantity)
      FROM yfs_count_result b3
      WHERE b3.count_request_key = x.count_request_key
      AND b3.item_id             = x.item_id
      AND (b3.serial_no          = x.serial_no
      OR b3.tag_number           = x.tag_number)
      AND b3.location_id         = x.location_id
      AND b3.task_type           = 'PC-3RD'
      ) "THIRD_COUNT",
      (SELECT MAX(b4.task_type)
      FROM yfs_count_result b4
      WHERE b4.count_request_key = x.count_request_key
      AND b4.item_id             = x.item_id
      AND (b4.serial_no          = x.serial_no
      OR b4.tag_number           = x.tag_number)
      AND b4.location_id         = x.location_id
      ) "LAST_COUNT_TYPE"
    FROM
      (SELECT DISTINCT a.count_request_key,
        a.node_key,
        a.count_request_no,
        a.createts,
        a.zone_id,
        a.aisle_number,
        a.bay_number,
        a.level_number,
        (
        CASE
          WHEN a.location_id = ' '
          THEN b.location_id
          ELSE a.location_id
        END) "LOCATION_ID",
        b.item_id,
        c.short_description,
        c.unit_cost,
        (
        CASE
          WHEN b.serial_no = ' '
          THEN trim(b.tag_number)
          ELSE b.serial_no
        END) "SERIAL_NO",
        (
        CASE
          WHEN b.tag_number = ' '
          THEN b.serial_no
          ELSE b.tag_number
        END) "TAG_NUMBER",
        b.system_quantity
      FROM yfs_count_request a,
        yfs_count_result b,
        yfs_item c
      WHERE a.count_request_key = b.count_request_key
      AND b.item_id             = c.item_id
      AND b.inventory_status    = 'RFI'
      AND a.status NOT         IN ('1200.01','9000')
      AND b.variance_accepted  != 'I'
      AND a.createts            > to_date('01-JAN-2010','DD-MON-YYYY')
      )x
    )y,
    yfs_location l
  WHERE y.location_id = l.location_id
  AND y.node_key      = l.node_key;