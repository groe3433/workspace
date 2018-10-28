CREATE OR REPLACE FORCE VIEW EXTN_NWCG_IE_CHD_22_VW ("LOCATION_ID", "SHIP_BY_DATE", "QUANTITY", "NODE_KEY", "ITEM_ID")
AS
  SELECT li.location_id,
    li.ship_by_date,
    li.quantity,
    li.node_key,
    ii.item_id
  FROM yfs_location_inventory li,
    yfs_inventory_item ii
  WHERE li.inventory_item_key = ii.inventory_item_key;