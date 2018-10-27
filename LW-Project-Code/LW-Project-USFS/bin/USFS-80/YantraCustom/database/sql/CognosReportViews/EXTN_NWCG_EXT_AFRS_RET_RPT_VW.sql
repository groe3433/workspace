CREATE OR REPLACE FORCE VIEW EXTN_NWCG_EXT_AFRS_RET_RPT_VW ("CACHE_ID", "RECEIPT_DATE", "TOT_RETURNED", "TOT_POUNDS", "TOT_CUBES", "TOT_RETURNED_VALUE")
AS
  SELECT x.receivingnode_key "CACHE_ID",
    to_date(TO_CHAR(x.receipt_date,'DD-MON-YYYY')) "RECEIPT_DATE",
    SUM(x.ret_count) "TOT_RETURNED",
    SUM(x.pounds) "TOT_POUNDS",
    SUM(x.cubes) "TOT_CUBES",
    SUM(x.returned_value) "TOT_RETURNED_VALUE"
  FROM
    (SELECT a.receivingnode_key,
      a.receipt_date,
      COUNT(DISTINCT b.receipt_header_key) "RET_COUNT",
      SUM(b.quantity)       +SUM(d.quantity) "RETURNED",
      SUM((NVL(b.quantity,0)+NVL(d.quantity,0)) * c.unit_weight) "POUNDS",
      SUM((NVL(b.quantity,0)+NVL(d.quantity,0))*c.unit_height*unit_length*unit_width) "CUBES",
      SUM((NVL(b.quantity,0)+NVL(d.quantity,0)) *
      (SELECT a1.list_price
      FROM yfs_item_price_set a1,
        yfs_price_set b1
      WHERE a1.item_id     = b.item_id
      AND a1.price_set_key = b1.price_set_key
      AND b1.active_flag   = 'Y'
      )) "RETURNED_VALUE",
      b.item_id
    FROM yfs_receipt_header a,
      yfs_receipt_line b,
      yfs_item c,
      nwcg_lpn_return d
    WHERE a.document_type   IN ('0003','0010','0011')
    AND a.receipt_header_key = b.receipt_header_key
    AND b.item_id            = c.item_id
    AND b.receipt_header_key = d.return_header_key (+)
    AND b.item_id            = d.item_id (+)
    GROUP BY a.receivingnode_key,
      a.receipt_date,
      b.item_id
    ) x
  GROUP BY x.receivingnode_key,
    x.receipt_date;