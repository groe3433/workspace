CREATE OR REPLACE FORCE VIEW EXTN_NWCG_PMS_RETURNS_RPT_VW ("CACHE ID", "RECEIPT_DATE", "RECEIPTS")
AS
  SELECT x.receivingnode_key "CACHE ID",
    x.receipt_date,
    SUM(x.receipts1) "RECEIPTS"
  FROM
    (SELECT a.receivingnode_key,
      a.receipt_date,
      SUM((NVL(b.quantity,0)+NVL(lpn.quantity,0)) * yi.unit_cost) "RECEIPTS1",
      b.item_id
    FROM yfs_receipt_header a,
      yfs_receipt_line b,
      yfs_item yi,
      nwcg_lpn_return lpn
    WHERE a.document_type   IN ('0003','0010','0011')
    AND a.receipt_header_key = b.receipt_header_key
    AND b.item_id            = yi.item_id
    AND yi.product_line      = 'PMS Publications'
    AND b.receipt_header_key = lpn.return_header_key (+)
    AND b.item_id            = lpn.item_id (+)
    GROUP BY a.receivingnode_key,
      a.receipt_date,
      b.item_id
    ) x
  GROUP BY x.receivingnode_key,
    x.receipt_date;