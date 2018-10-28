CREATE OR REPLACE FORCE VIEW EXTN_NWCG_OUT_SURPLUS_RPT_VW ("ITEM_ID", "CACHE ID", "INCIDENT NO", "INCIDENT_NAME", "DESCRIPTION", "UOM", "CLASS", "FS ACCT CODE", "BLM ACCT CODE", "OTHER ACCT CODE", "OVERRIDE_CODE", "YEAR", "QTY_OUT", "UNIT_PRICE")
AS
  SELECT a.item_id,
    a.cache_id "CACHE ID",
    a.incident_no "INCIDENT NO",
    c.incident_name,
    b.description,
    b.uom,
    b.tax_product_code "CLASS",
    c.incident_fs_acct_code "FS ACCT CODE",
    c.incident_blm_acct_code "BLM ACCT CODE",
    c.incident_other_acct_code "OTHER ACCT CODE",
    c.override_code,
    c.year,
    (SUM (a.quantity_shipped) - SUM (a.quantity_returned)) "QTY_OUT",
    TO_NUMBER (a.unit_price)
  FROM nwcg_incident_return a,
    yfs_item b,
    nwcg_incident_order c
  WHERE a.incident_no = c.incident_no
  AND a.incident_year = c.year
  AND a.item_id       = b.item_id
  GROUP BY a.item_id,
    a.cache_id,
    a.incident_no,
    c.incident_name,
    b.description,
    b.uom,
    b.tax_product_code,
    c.incident_fs_acct_code,
    c.incident_blm_acct_code,
    c.incident_other_acct_code,
    c.override_code,
    c.year,
    TO_NUMBER (A.UNIT_PRICE)
    --  UNION
    --  SELECT a.item_id,
    --    a.cache_id "CACHE ID",
    --  a.incident_no "INCIDENT NO",
    --    c.incident_name,
    --    b.description,
    --    b.uom,
    --    b.tax_product_code "CLASS",
    --    c.incident_fs_acct_code "FS ACCT CODE",
    --    c.incident_blm_acct_code "BLM ACCT CODE",
    --    c.incident_other_acct_code "OTHER ACCT CODE",
    --    c.override_code,
    --    c.year,
    --    (ABS (SUM (a.trans_qty))) "QTY_OUT",
    --    a.unit_cost
    --  FROM nwcg_billing_transaction a,
    --    yfs_item b,
    --    nwcg_incident_order c
    --  WHERE a.incident_no         = c.incident_no
    --  AND RTRIM (a.incident_year) = c.year
    --  AND RTRIM (a.cache_id)      = RTRIM (c.primary_cache_id)
    -- AND a.item_classification LIKE 'Consumable%'  commented because they can use any type of item in REFURB workorder
    --  AND a.trans_type      = 'WO-REFURB'
    --  AND RTRIM (a.item_id) = RTRIM (b.item_id)
    --  AND a.trans_qty       < 0
    --  GROUP BY a.item_id,
    --    a.cache_id,
    --    a.incident_no,
    --    c.incident_name,
    --    b.description,
    --    b.uom,
    --    b.tax_product_code,
    --    c.incident_fs_acct_code,
    --    c.incident_blm_acct_code,
    --    c.incident_other_acct_code,
    --    c.override_code,
    --    C.YEAR,
    --    a.unit_cost;;;