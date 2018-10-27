CREATE OR REPLACE FORCE VIEW EXTN_NWCG_CITEM_REPOS_RPT_VW ("ITEM ID", "ITEM DESCRIPTION", "UOM", "UNIT_COST", "NSN", "NODE_KEY", "INVENTORY STATUS", "QUANTITY", "CLASS")
AS
  SELECT DISTINCT b.item_id "ITEM ID",
    b.description "ITEM DESCRIPTION",
    b.uom,
    b.unit_cost,
    b.global_item_id "NSN",
    a.node_key,
    a.inventory_status "INVENTORY STATUS",
    SUM(a.quantity) "QUANTITY",
    NVL(b.tax_product_code,'NO CLASS CODE') "CLASS"
  FROM yfs_location_inventory a,
    yfs_item b,
    yfs_inventory_item c
  WHERE a.inventory_item_key = c.inventory_item_key
  AND b.item_id              = c.item_id
  GROUP BY b.item_id,
    b.description,
    b.uom,
    b.unit_cost,
    b.global_item_id,
    a.node_key,
    a.inventory_status,
    b.tax_product_code;