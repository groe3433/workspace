CREATE OR REPLACE FORCE VIEW EXTN_NWCG_TRACK_ITEMS_VW ("PARENT_ID", "PARENT_DESCRIPTION", "PARENT_TRACKABLE_ID", "AT_NODE", "SHIPNODE_KEY", "COMP_ITEM_ID", "CHILD_DESCRIPTION", "CHILD_TRACKABLE_ID")
AS
  SELECT y.parent_id,
    yip.description "PARENT_DESCRIPTION",
    y.parent_trackable_id,
    y.at_node,
    y.shipnode_key,
    y.comp_item_id,
    yic.description "CHILD_DESCRIPTION",
    y.child_trackable_id
  FROM (
    (SELECT DISTINCT parent_id,
      ' '"PARENT_TRACKABLE_ID",
      ' '"AT_NODE",
      ' '"SHIPNODE_KEY",
      comp_item_id,
      ' '"CHILD_TRACKABLE_ID"
    FROM extn_nwcg_yfs_kit_items_vw
    MINUS
    SELECT DISTINCT x.parent_item,
      ' '"PARENT_TRACKABLE_ID",
      ' '"AT_NODE",
      ' '"SHIPNODE_KEY",
      x.child_item,
      ' '"CHILD_TRACKABLE_ID"
    FROM
      (SELECT
        (SELECT item_id
        FROM yfs_inventory_item
        WHERE inventory_item_key = a.inventory_item_key
        ) "PARENT_ITEM",
        a.serial_no "PARENT_TRACKABLE_ID",
        (SELECT item_id
        FROM yfs_inventory_item
        WHERE inventory_item_key = b.inventory_item_key
        ) "CHILD_ITEM",
        b.serial_no "CHILD_TRACKABLE_ID"
      FROM yfs_global_serial_num a,
        yfs_global_serial_num b
      WHERE b.parent_serial_key = a.global_serial_key
      ) x
    )
  UNION ALL
  SELECT
    (SELECT item_id
    FROM yfs_inventory_item
    WHERE inventory_item_key = a.inventory_item_key
    ) "PARENT_ITEM",
    a.serial_no "PARENT_TRACKABLE_ID",
    a.at_node,
    a.shipnode_key,
    (SELECT item_id
    FROM yfs_inventory_item
    WHERE inventory_item_key = b.inventory_item_key
    ) "CHILD_ITEM",
    b.serial_no "CHILD_TRACKABLE_ID"
  FROM yfs_global_serial_num a,
    yfs_global_serial_num b
  WHERE b.parent_serial_key = a.global_serial_key) y,
    yfs_item yip,
    yfs_item yic
  WHERE y.parent_id  = yip.item_id
  AND y.comp_item_id = yic.item_id;