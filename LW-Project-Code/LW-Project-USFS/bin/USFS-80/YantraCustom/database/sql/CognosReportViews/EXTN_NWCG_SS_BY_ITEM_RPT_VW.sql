CREATE OR REPLACE FORCE VIEW EXTN_NWCG_SS_BY_ITEM_RPT_VW ("CACHE ID", "ITEM ID", "ITEM_DESCRIPTION", "UOM", "UNIT_COST", "QTY_RFI", "QTY_NRFI", "QTY_NRFI-RFB", "QTY_RESD", "QTY_KITS", "QTY_DUE", "QTY_DUE_C2C", "QTY_BACKORDER", "QTY_ALLOC", "QTY_WO", "QTY_MIN", "QTY_MAX", "QTY_ISSUED", "QTY_RETURNED", "QTY_TRANSFERRED_IN", "QTY_TRANSFERRED_OUT")
AS
  SELECT x.node_key "CACHE ID",
    x.item_id "ITEM ID",
    yi.description "ITEM_DESCRIPTION",
    yi.uom,
    yi.unit_cost,
    (SELECT SUM ( (a1.quantity + a1.pend_in_qty) - (a1.hard_alloc_qty + a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND b1.item_id              = x.item_id
    AND a1.node_key             = x.node_key
    AND a1.inventory_status     = 'RFI'
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    ) "QTY_RFI",
    (SELECT SUM (a1.quantity - (a1.hard_alloc_qty + a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND b1.item_id              = x.item_id
    AND a1.node_key             = x.node_key
    AND a1.inventory_status     = 'NRFI'
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    ) "QTY_NRFI",
    (SELECT SUM (a1.quantity - (a1.hard_alloc_qty + a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND b1.item_id              = x.item_id
    AND a1.node_key             = x.node_key
    AND a1.inventory_status     = 'NRFI-RFB'
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    ) "QTY_NRFI-RFB",
    (SELECT SUM (ir.quantity)
    FROM yfs_inventory_reservation ir,
      yfs_inventory_item yii,
      yfs_item i,
      yfs_organization yo
    WHERE i.item_id           = x.item_id
    AND yii.item_id           = x.item_id
    AND ir.inventory_item_key = yii.inventory_item_key
    AND ir.shipnode_key       = x.node_key
    AND yo.organization_key   = x.node_key
    AND ir.createts          >= NVL (yo.extn_cache_start_date, TO_DATE ('01-JAN-2007', 'DD-MON-YYYY'))
    ) "QTY_RESD",
    (
    (SELECT NVL (SUM (QTY_KITS1), 0)
    FROM extn_nwcg_kit_qty1_vw
    WHERE component_item = x.item_id
    AND cache_id         = x.node_key
    ) +
    (SELECT NVL (SUM (QTY_KITS2), 0)
    FROM extn_nwcg_kit_qty2_vw
    WHERE component_item = x.item_id
    AND cache_id         = x.node_key
    ) +
    (SELECT NVL (SUM (QTY_KITS3), 0)
    FROM extn_nwcg_kit_qty3_vw
    WHERE component_item = x.item_id
    AND cache_id         = x.node_key
    ) +
    (SELECT NVL (SUM (QTY_KITS4), 0)
    FROM extn_nwcg_kit_qty4_vw
    WHERE component_item = x.item_id
    AND cache_id         = x.node_key
    ) +
    (SELECT NVL (SUM (QTY_KITS5), 0)
    FROM extn_nwcg_kit_qty5_vw
    WHERE component_item = x.item_id
    AND cache_id         = x.node_key
    )) "QTY_KITS",
    (SELECT SUM (l.ordered_qty) - (SUM (l.received_quantity) + SUM (l.tran_discrepancy_qty * -1))
    FROM yfs_order_line l,
      yfs_order_header h
    WHERE l.item_id        = x.item_id
    AND l.order_header_key = h.order_header_key
    AND h.document_type    = '0005'
    AND l.receiving_node   = x.node_key
    AND l.ordered_qty      > l.received_quantity
    ) "QTY_DUE",
    (SELECT SUM (l.shipped_quantity) - SUM (l.received_quantity)
    FROM yfs_order_line l,
      yfs_order_header h
    WHERE l.item_id        = x.item_id
    AND l.order_header_key = h.order_header_key
    AND h.document_type    = '0006'
    AND l.receiving_node   = x.node_key
    AND l.shipped_quantity > 0
    AND l.shipped_quantity > l.received_quantity
    ) "QTY_DUE_C2C",
    (SELECT SUM (l.extn_backordered_qty)
    FROM yfs_order_line l,
      yfs_order_header h,
      yfs_organization yo
    WHERE l.item_id            = x.item_id
    AND l.extn_back_order_flag = 'N'
    AND l.order_header_key     = h.order_header_key
    AND h.document_type        = '0005'
    AND l.receiving_node       = x.node_key
    AND yo.organization_key    = x.node_key
    AND h.order_date          >= NVL (yo.extn_cache_start_date, TO_DATE ('01-JAN-2007', 'DD-MON-YYYY'))
    ) "QTY_BACKORDER",
    (SELECT SUM (yid.quantity)
    FROM yfs_inventory_demand yid,
      yfs_inventory_item yii
    WHERE yid.inventory_item_key = yii.inventory_item_key
    AND yii.item_id              = x.item_id
    AND yid.shipnode_key         = x.node_key
    AND yid.demand_type          = 'ALLOCATED'
    ) "QTY_ALLOC",
    (SELECT SUM (w.quantity_allocated)
    FROM yfs_work_order w,
      yfs_organization yo
    WHERE RTRIM (w.item_id) = RTRIM (x.item_id)
    AND w.node_key          = x.node_key
    AND yo.organization_key = x.node_key
    AND w.createts         >= NVL (yo.extn_cache_start_date, TO_DATE ('01-JAN-2007', 'DD-MON-YYYY'))
    ) "QTY_WO",
    (SELECT im.minimum_qty_1
    FROM yfs_item_status_rules im
    WHERE im.item_id    = x.item_id
    AND im.shipnode_key = x.node_key
    ) "QTY_MIN",
    (SELECT im.maximum_qty_1
    FROM yfs_item_status_rules im
    WHERE im.item_id    = x.item_id
    AND im.shipnode_key = x.node_key
    ) "QTY_MAX",
    (SELECT SUM (yl.shipped_quantity)
    FROM yfs_order_line yl,
      yfs_order_header yh,
      yfs_organization yo
    WHERE yh.order_header_key = yl.order_header_key
    AND yh.document_type     IN ('0001', '0007.ex')
    AND yl.item_id            = x.item_id
    AND yh.ship_node          = x.node_key
    AND yo.organization_key   = x.node_key
    AND yh.order_date        >= NVL (yo.extn_cache_start_date, TO_DATE ('01-JAN-2007', 'DD-MON-YYYY'))
    ) "QTY_ISSUED",
    (
    (SELECT SUM (rl.quantity)
    FROM yfs_receipt_line rl,
      yfs_receipt_header rh,
      yfs_organization yo
    WHERE rh.receipt_header_key = rl.receipt_header_key
    AND rh.document_type       IN ('0010', '0011')
    AND rl.item_id              = x.item_id
    AND rh.receivingnode_key    = x.node_key
    AND yo.organization_key     = x.node_key
    AND rh.receipt_date        >= NVL (yo.extn_cache_start_date, TO_DATE ('01-JAN-2007', 'DD-MON-YYYY'))
    ) +
    (SELECT NVL (SUM (lpn.quantity), 0)
    FROM nwcg_lpn_return lpn,
      yfs_organization yo
    WHERE TRIM (lpn.item_id) = TRIM (x.item_id)
    AND TRIM (lpn.cache_id)  = TRIM (x.node_key)
    AND yo.organization_key  = x.node_key
    AND lpn.receipt_date    >= NVL (yo.extn_cache_start_date, TO_DATE ('01-JAN-2007', 'DD-MON-YYYY'))
    )) "QTY_RETURNED",
    (SELECT NVL( ABS( SUM(billing.trans_qty) ) ,0)
    FROM nwcg_billing_transaction billing,
      yfs_organization yo
    WHERE billing.trans_type IN ( 'RECEIVE CACHE TO')
    AND rtrim(x.node_key)     = rtrim(billing.cache_id)
    AND billing.item_id       = rtrim(x.item_id)
    AND yo.organization_key   = x.node_key
    AND billing.TRANS_DATE   >= NVL (yo.extn_cache_start_date, TO_DATE ('01-JAN-2007', 'DD-MON-YYYY'))
    ) "QTY_TRANSFERRED_IN",
    (SELECT NVL( SUM(billing.trans_qty) ,0)
    FROM nwcg_billing_transaction billing,
      yfs_organization yo
    WHERE billing.trans_type IN ( 'SHIP CACHE TO')
    AND rtrim(x.node_key)     = rtrim(billing.cache_id)
    AND billing.item_id       = rtrim(x.item_id)
    AND yo.organization_key   = x.node_key
    AND billing.TRANS_DATE   >= NVL (yo.extn_cache_start_date, TO_DATE ('01-JAN-2007', 'DD-MON-YYYY'))
    ) "QTY_TRANSFERRED_OUT"
  FROM
    (SELECT DISTINCT ysk.node_key, ysk.item_id FROM yfs_sku_dedication ysk
    UNION
    SELECT DISTINCT b.node_key,
      a.item_id
    FROM yfs_inventory_item a,
      yfs_location_inventory b
    WHERE a.inventory_item_key = b.inventory_item_key
    AND b.inventory_status    IN ('RFI', 'NRFI', 'NRFI-RFB')
    ) x,
    yfs_item yi
  WHERE x.item_id        = yi.item_id
  AND yi.item_group_code = 'PROD'
  AND yi.status          = '3000'
  GROUP BY x.node_key,
    x.item_id,
    yi.description,
    yi.uom,
    yi.unit_cost;