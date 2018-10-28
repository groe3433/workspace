CREATE OR REPLACE FORCE VIEW EXTN_NWCG_SURPLUS_RPT_TEST_VW ("ITEM_ID", "CACHE_ID", "INCIDENT NO", "INCIDENT_NAME", "DESCRIPTION", "UOM", "CLASS", "FS ACCT CODE", "BLM ACCT CODE", "OTHER ACCT CODE", "OVERRIDE_CODE", "YEAR", "QTY_OUT", "UNIT_PRICE")
AS
  SELECT a.item_id,
    a.cache_id "CACHE_ID",
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
    (SUM(a.quantity_shipped)-SUM(a.quantity_returned))"QTY_OUT",
    b.unit_cost
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
    b.unit_cost
  UNION
  SELECT a.item_id,
    a.cache_id "CACHE_ID",
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
    (ABS(SUM(a.trans_qty))) "QTY_OUT",
    b.unit_cost
  FROM nwcg_billing_transaction a,
    yfs_item b,
    nwcg_incident_order c
  WHERE a.incident_no        = c.incident_no
  AND rtrim(a.incident_year) = c.year
  AND rtrim(a.cache_id)      = rtrim(c.primary_cache_id)
  AND a.item_classification LIKE 'Consumable%'
  AND a.trans_type     = 'WO-REFURB'
  AND rtrim(a.item_id) = rtrim(b.item_id)
  AND a.trans_qty      < 0
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
    b.unit_cost;