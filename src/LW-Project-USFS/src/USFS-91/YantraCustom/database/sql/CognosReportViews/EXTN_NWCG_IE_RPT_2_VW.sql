CREATE OR REPLACE FORCE VIEW EXTN_NWCG_IE_RPT_2_VW ("LOCATION_ID", "SHIP_BY_DATE", "CMP_QTY", "CHILD_ITEM_ID", "CHILD_DESCRIPTION", "NODE_KEY")
AS
  SELECT chd22.location_id,
    chd22.ship_by_date,
    SUM(chd22.quantity * chd21.child_qty) AS cmp_qty,
    chd21.child_item_id,
    chd21.child_description,
    chd22.node_key
  FROM extn_nwcg_ie_chd_21_vw chd21,
    extn_nwcg_ie_chd_22_vw chd22
  WHERE chd21.parent_item_id = chd22.item_id
  GROUP BY chd22.location_id,
    chd22.ship_by_date,
    chd21.child_item_id,
    chd21.child_description,
    chd22.node_key
  ORDER BY chd21.child_item_id,
    chd22.location_id;