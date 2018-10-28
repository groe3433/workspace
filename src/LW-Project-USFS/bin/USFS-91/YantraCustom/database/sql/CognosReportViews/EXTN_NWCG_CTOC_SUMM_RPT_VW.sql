CREATE OR REPLACE FORCE VIEW EXTN_NWCG_CTOC_SUMM_RPT_VW ("CACHE ID", "TRANS_DATE", "TRANSACTION_NO", "ITEM_ID", "DESCRIPTION", "UOM", "TO CACHE ID", "TO_QTY", "FROM CACHE ID", "FROM_QTY", "UNIT_COST", "FROM_VALUE", "TO_VALUE", "QTY_RFI")
AS
  SELECT z."CACHE ID",
    z.trans_date,
    z.transaction_no,
    z.item_id,
    z.description,
    z.uom,
    z."TO CACHE ID",
    z.to_qty,
    z."FROM CACHE ID",
    z.from_qty,
    z.unit_cost,
    z."FROM_VALUE",
    z."TO_VALUE",
    (SELECT SUM( (a1.quantity + a1.pend_in_qty) - (a1.hard_alloc_qty + a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND trim(b1.item_id)        = trim(z.item_id)
    AND trim(a1.node_key)       = trim(z."CACHE ID")
    AND a1.inventory_status     = 'RFI'
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    ) "QTY_RFI"
  FROM
    (SELECT a.organization_key "CACHE ID",
      to_date(TO_CHAR(c.trans_date,'DD-MON-YYYY'),'DD-MON-YYYY') "TRANS_DATE",
      c.transaction_no,
      c.item_id,
      c.description,
      c.uom,
      b.organization_key "TO CACHE ID",
      c.to_qty,
      b.organization_key "FROM CACHE ID",
      c.from_qty,
      c.unit_cost,
      c.from_qty*c.unit_cost "FROM_VALUE",
      c.to_qty  *c.unit_cost "TO_VALUE"
    FROM yfs_organization a,
      yfs_organization b,
      extn_nwcg_ctoc_base_rpt_vw c
    WHERE a.organization_key     != b.organization_key
    AND a.is_node                 = 'Y'
    AND b.is_node                 = 'Y'
    AND a.organization_key NOT   IN ('HIGH_VOLUME_TEMPLATE','LOW_VOLUME_TEMPLATE')
    AND b.organization_key NOT   IN ('HIGH_VOLUME_TEMPLATE','LOW_VOLUME_TEMPLATE')
    AND trim(a.organization_key)  = trim(c."CACHE ID")
    AND (trim(b.organization_key) = trim(c."FROM CACHE ID")
    OR trim(b.organization_key)   = trim(c."TO CACHE ID"))
    GROUP BY a.organization_key,
      to_date(TO_CHAR(c.trans_date,'DD-MON-YYYY'),'DD-MON-YYYY'),
      c.transaction_no,
      c.item_id,
      c.description,
      c.uom,
      b.organization_key,
      c.to_qty,
      b.organization_key,
      c.from_qty,
      c.unit_cost
    ) z;