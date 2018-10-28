CREATE OR REPLACE FORCE VIEW EXTN_NWCG_SERIAL_LIST_VW ("ITEM_ID", "SERIAL_NO", "EXTN_INCIDENT_NO", "EXTN_INCIDENT_YEAR", "ORDER_NO")
AS
  SELECT trim(NVL(a.kit_item_id,a.item_id)) "ITEM_ID",
    a.serial_no "SERIAL_NO",
    a.status_incident_no "EXTN_INCIDENT_NO",
    a.status_incident_year "EXTN_INCIDENT_YEAR",
    a.last_document_number "ORDER_NO"
  FROM nwcg_trackable_item a
  WHERE a.serial_status    = 'I'
  AND a.serial_status_desc = 'Issue'
  AND (a.kit_item_id      IS NULL
  OR a.kit_item_id         = a.item_id)
  UNION
  SELECT trim(d.item_id) "ITEM_ID",
    x.serial_no,
    x.extn_incident_no,
    x.extn_incident_year,
    x.order_no
  FROM
    (SELECT a.kit_item_id "ITEM_ID",
      a.kit_serial_no,
      (SELECT MAX(c.parent_serial_key)
      FROM yfs_global_serial_num c
      WHERE c.serial_no = a.serial_no
      ) "PARENT_KEY",
      a.serial_no "SERIAL_NO",
      a.status_incident_no "EXTN_INCIDENT_NO",
      a.status_incident_year "EXTN_INCIDENT_YEAR",
      a.last_document_number "ORDER_NO"
    FROM nwcg_trackable_item a
    WHERE a.serial_status    = 'I'
    AND a.serial_status_desc = 'Issued in Kit'
    ) x,
    yfs_global_serial_num b,
    yfs_inventory_item d,
    nwcg_trackable_item e
  WHERE x.parent_key       = b.global_serial_key
  AND b.inventory_item_key = d.inventory_item_key
  AND d.product_class      = 'Supply'
  AND x.extn_incident_no   = e.status_incident_no
  AND x.extn_incident_year = e.status_incident_year
  AND x.kit_serial_no      = e.serial_no
  AND e.serial_status_desc = 'Issue'
  UNION
  SELECT trim(d.item_id) "ITEM_ID",
    y.serial_no,
    y.extn_incident_no,
    y.extn_incident_year,
    y.order_no
  FROM
    (SELECT x.item_id,
      x.parent_key,
      x.serial_no,
      x.extn_incident_no,
      x.extn_incident_year,
      x.order_no,
      b.serial_no "SERIAL1",
      b.parent_serial_key "PARENT_SERIAL1"
    FROM
      (SELECT a.kit_item_id "ITEM_ID",
        (SELECT MAX(c.parent_serial_key)
        FROM yfs_global_serial_num c
        WHERE c.serial_no = a.serial_no
        ) "PARENT_KEY",
        a.serial_no "SERIAL_NO",
        a.status_incident_no "EXTN_INCIDENT_NO",
        a.status_incident_year "EXTN_INCIDENT_YEAR",
        a.last_document_number "ORDER_NO"
      FROM nwcg_trackable_item a
      WHERE a.serial_status    = 'I'
      AND a.serial_status_desc = 'Issued in Kit'
      ) x,
      yfs_global_serial_num b
    WHERE x.parent_key = b.global_serial_key
    ) y,
    yfs_global_serial_num c,
    yfs_inventory_item d
  WHERE y.parent_serial1   = c.global_serial_key
  AND c.inventory_item_key = d.inventory_item_key
  AND d.product_class      = 'Supply' ;