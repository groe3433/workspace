CREATE OR REPLACE FORCE VIEW EXTN_NWCG_RSD_CITEMS_RPT_VW ("CACHE ID", "CUSTOMER ID", "RESD_QTY", "ITEM_ID", "ENTERPRISE_KEY", "UOM", "DESCRIPTION", "CUSTOMER NAME")
AS
  SELECT x.shipnode_key "CACHE ID",
    x.reservation_id "CUSTOMER ID",
    x.quantity "RESD_QTY",
    x.item_id,
    x.organization_code "ENTERPRISE_KEY",
    x.uom,
    x.description,
    (SELECT extn_customer_name
    FROM yfs_customer
    WHERE rtrim(customer_id) = rtrim(x.reservation_id)
    ) "CUSTOMER NAME"
  FROM
    (SELECT a.shipnode_key,
      a.reservation_id,
      a.quantity,
      b.item_id,
      b.organization_code,
      b.uom,
      c.description
    FROM yfs_inventory_reservation a,
      yfs_inventory_item b,
      yfs_item c
    WHERE b.inventory_item_key = a.inventory_item_key (+)
    AND b.item_id              = c.item_id (+)
    ) x;