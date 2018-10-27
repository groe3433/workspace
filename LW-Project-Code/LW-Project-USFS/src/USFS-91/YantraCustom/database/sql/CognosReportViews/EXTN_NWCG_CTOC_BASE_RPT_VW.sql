CREATE OR REPLACE FORCE VIEW EXTN_NWCG_CTOC_BASE_RPT_VW ("CACHE ID", "TRANS_DATE", "TRANSACTION_NO", "ITEM_ID", "DESCRIPTION", "UOM", "TO CACHE ID", "TO_QTY", "FROM CACHE ID", "FROM_QTY", "UNIT_COST", "RECEIVE_TRANS_TYPE", "SHIP_TRANS_TYPE")
AS
  SELECT z.cache_id "CACHE ID",
    z.trans_date,
    z.transaction_no,
    z.item_id,
    yi.description,
    yi.uom,
    z.receive_cache_id "TO CACHE ID",
    z.receive_qty "TO_QTY",
    z.ship_cache_id "FROM CACHE ID",
    z.ship_qty "FROM_QTY",
    z.unit_cost,
    z.receive_trans_type,
    z.ship_trans_type
  FROM
    (SELECT x.cache_id,
      ' ' "RECEIVE_CACHE_ID",
      x.transaction_no,
      x.trans_date,
      y.cache_id "SHIP_CACHE_ID",
      y.item_id,
      0 "RECEIVE_QTY",
      SUM(y.trans_qty) "SHIP_QTY",
      y.unit_cost,
      ' ' "RECEIVE_TRANS_TYPE",
      y.trans_type "SHIP_TRANS_TYPE"
    FROM
      (SELECT a.cache_id,
        a.transaction_no,
        a.trans_date,
        a.item_id,
        a.uom
      FROM nwcg_billing_transaction a
      WHERE a.trans_type IN ('RECEIVE CACHE TO')
      GROUP BY a.cache_id,
        a.transaction_no,
        a.trans_date,
        a.item_id,
        a.uom
      )x,
      nwcg_billing_transaction y
    WHERE x.item_id      = y.item_id
    AND y.trans_type     = 'SHIP CACHE TO'
    AND x.transaction_no = y.transaction_no
    GROUP BY x.cache_id,
      x.transaction_no,
      x.trans_date,
      y.cache_id,
      y.item_id,
      y.unit_cost,
      y.trans_type
    UNION
    SELECT x.cache_id,
      y.cache_id "RECEIVE_CACHE_ID",
      x.transaction_no,
      x.trans_date,
      ' ' "SHIP_CACHE_ID",
      y.item_id,
      SUM(y.trans_qty) "RECEIVE_QTY",
      0 "SHIP_QTY",
      y.unit_cost,
      y.trans_type "RECEIVE_TRANS_TYPE",
      ' ' "SHIP_TRANS_TYPE"
    FROM
      (SELECT a.cache_id,
        a.transaction_no,
        a.trans_date,
        a.item_id,
        a.uom
      FROM nwcg_billing_transaction a
      WHERE a.trans_type IN ('SHIP CACHE TO')
      GROUP BY a.cache_id,
        a.transaction_no,
        a.trans_date,
        a.item_id,
        a.uom
      )x,
      nwcg_billing_transaction y
    WHERE x.item_id      = y.item_id
    AND y.trans_type     = 'RECEIVE CACHE TO'
    AND x.transaction_no = y.transaction_no
    GROUP BY x.cache_id,
      x.transaction_no,
      x.trans_date,
      y.cache_id,
      y.item_id,
      y.unit_cost,
      y.trans_type
    ) z,
    yfs_item yi
  WHERE trim(z.item_id) = trim(yi.item_id);