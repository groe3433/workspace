CREATE OR REPLACE FORCE VIEW EXTN_NWCG_PMS_SUMMARY_RPT_VW ("CACHE_ID", "TRANS_DATE", "ISSUE_AMOUNT", "YTD_ISSUE_AMOUNT", "RETURN_AMOUNT", "YTD_RETURN_AMOUNT")
AS
  SELECT a.cache_id,
    a.trans_date,
    (SELECT SUM(b.trans_amount)
    FROM nwcg_billing_transaction b
    WHERE a.cache_id        = b.cache_id
    AND a.trans_date        = b.trans_date
    AND b.document_type    IN ('0001','0006','0007.ex','0008.ex')
    AND b.item_product_line = 'PMS Publications'
    ) "ISSUE_AMOUNT",
    (SELECT SUM(b.trans_amount)
    FROM nwcg_billing_transaction b
    WHERE a.cache_id = b.cache_id
    AND a.trans_date >
      (SELECT NVL(extn_cache_start_date,to_date('01/01/2007','MM/DD/YYYY'))
      FROM yfs_organization
      WHERE trim(organization_code) = a.cache_id
      )
    AND b.document_type    IN ('0001','0006','0007.ex','0008.ex')
    AND b.item_product_line = 'PMS Publications'
    ) "YTD_ISSUE_AMOUNT",
    (SELECT SUM(b.trans_amount)
    FROM nwcg_billing_transaction b
    WHERE a.cache_id        = b.cache_id
    AND a.trans_date        = b.trans_date
    AND b.document_type    IN ('0010')
    AND b.item_product_line = 'PMS Publications'
    ) "RETURN_AMOUNT",
    (SELECT SUM(b.trans_amount)
    FROM nwcg_billing_transaction b
    WHERE a.cache_id = b.cache_id
    AND a.trans_date >
      (SELECT NVL(extn_cache_start_date,to_date('01/01/2007','MM/DD/YYYY'))
      FROM yfs_organization
      WHERE trim(organization_code) = a.cache_id
      )
    AND b.document_type    IN ('0010')
    AND b.item_product_line = 'PMS Publications'
    ) "YTD_RETURN_AMOUNT"
  FROM extn_nwcg_pms_summary_base_vw a;