CREATE OR REPLACE FORCE VIEW EXTN_NWCG_PMS_SUMMARY_BASE_VW ("CACHE_ID", "TRANS_DATE", "DOCUMENT_TYPE")
AS
  SELECT a.cache_id,
    a.trans_date,
    a.document_type
  FROM nwcg_billing_transaction a
  WHERE a.document_type IN ('0001','0006','0007.ex','0008.ex','0010')
  AND a.item_product_line LIKE 'PMS Publications%'
  GROUP BY a.cache_id,
    a.trans_date,
    a.document_type;