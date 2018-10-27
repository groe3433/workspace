CREATE OR REPLACE FORCE VIEW EXTN_NWCG_PMS_SHIPMENTS_RPT_VW ("CACHE ID", "ACTUAL_SHIPMENT_DATE", "ISSUES")
AS
  SELECT x.shipnode_key "CACHE ID",
    x.actual_shipment_date,
    SUM(x.issues1) "ISSUES"
  FROM
    (SELECT a.shipnode_key,
      a.actual_shipment_date,
      SUM(b.ordered_qty * b.unit_price) "ISSUES1",
      b.item_id
    FROM yna_shipment_vw a,
      yna_order_line_vw b,
      yfs_item yi,
      yfs_category yc,
      yfs_category_item yci
    WHERE a.document_type IN ('0001','0006','0007.ex','0008.ex')
    AND a.order_header_key = b.order_header_key
    AND b.item_id          = yi.item_id
    AND yi.item_key        = yci.item_key
    AND yci.category_key   = yc.category_key
    AND yc.category_id     = 'PMS Publications'
    GROUP BY a.shipnode_key,
      a.actual_shipment_date,
      b.item_id
    ) x
  GROUP BY x.shipnode_key,
    x.actual_shipment_date;