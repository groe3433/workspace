CREATE OR REPLACE FORCE VIEW EXTN_NWCG_TRACKABLE_LOC_VW ("Item ID", "Storage Location", "Trackable ID", "Cache ID", "Kit Trackable ID", "Status")
                       AS
  SELECT yfsii.item_id AS "Item ID",
    CASE
      WHEN ti.serial_status = 'K'
      THEN
        (SELECT location_id
        FROM YFS_GLOBAL_SERIAL_NUM
        WHERE global_Serial_key = yfsgsn.parent_serial_key
        )
      ELSE yfsgsn.location_id
    END                 AS "Storage Location",
    YFSGSN.SERIAL_NO    AS "Trackable ID",
    yfsgsn.shipnode_key AS "Cache ID",
    CASE
      WHEN ti.serial_status = 'K'
      THEN ti.kit_serial_no
      ELSE ' '
    END                     AS "Kit Trackable ID",
    yfsgsn.inventory_status AS "Status"
  FROM yfs_global_serial_num yfsgsn,
    yfs_inventory_item yfsii,
    nwcg_trackable_item ti
  WHERE rtrim(ti.serial_no)     = rtrim(yfsgsn.serial_no)
  AND yfsgsn.inventory_item_key = yfsii.inventory_item_key
  AND rtrim(yfsii.item_id)      = rtrim(ti.item_id)
  AND
    --yfsii.item_id in ('004115','004161', '004404', '004535', '004603', '004466') and
    RTRIM(YFSGSN.SHIPNODE_KEY) = RTRIM(TI.STATUS_CACHE_ID)
  AND YFSGSN.SHIPNODE_KEY NOT IN ('LOW_VOLUME_TEMPLATE','HIGH_VOLUME_TEMPLATE')
    --ORDER BY yfsii.item_id ASC;;;;
    --AND ti.serial_status not in ('N', 'D');;;;