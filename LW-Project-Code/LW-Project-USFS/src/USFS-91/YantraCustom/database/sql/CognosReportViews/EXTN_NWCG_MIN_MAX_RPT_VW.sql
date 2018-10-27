CREATE OR REPLACE FORCE VIEW EXTN_NWCG_MIN_MAX_RPT_VW ("CACHE ID", "ITEM ID", "DESCRIPTION", "GLOBAL_ITEM_ID", "QTY_RFI", "QTY_REQ", "QTY_PO", "QTY_DUE", "QTY_BACKORDER", "QTY_WO", "QTY_MIN", "QTY_MAX", "QTY_ISSUED", "QTY_RETURNED", "QTY_RESD", "SUPPLIER CODE")
AS
  SELECT DISTINCT x.node_key "CACHE ID",
    x.item_id "ITEM ID",
    yi.description,
    yi.global_item_id,
    (SELECT SUM((a1.quantity+a1.pend_in_qty) - (a1.hard_alloc_qty+a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND b1.item_id              = x.item_id
    AND a1.inventory_status     = 'RFI'
    AND a1.node_key             = x.node_key
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    ) "QTY_RFI",
    (SELECT SUM(yl.ordered_qty)
    FROM yfs_order_line yl,
      yfs_order_header yh,
      yfs_organization yo
    WHERE yh.order_header_key = yl.order_header_key
    AND yh.document_type      = '0005'
    AND yl.item_id            = x.item_id
    AND yh.receiving_node     = x.node_key
    AND yo.organization_key   = x.node_key
    AND yh.order_date        >= NVL(yo.extn_cache_start_date,to_date('01-JAN-2007','DD-MON-YYYY'))
    ) "QTY_REQ",
    (SELECT SUM(yl.received_quantity)
    FROM yfs_order_line yl,
      yfs_order_header yh,
      yfs_organization yo
    WHERE yh.order_header_key = yl.order_header_key
    AND yh.document_type      = '0005'
    AND yl.item_id            = x.item_id
    AND yh.receiving_node     = x.node_key
    AND yo.organization_key   = x.node_key
    AND yh.order_date        >= NVL(yo.extn_cache_start_date,to_date('01-JAN-2007','DD-MON-YYYY'))
    ) "QTY_PO",
    (SELECT
      (SELECT SUM (l.ordered_qty) - SUM (l.received_quantity)
      FROM yfs_order_line l,
        yfs_order_header h
      WHERE l.item_id        = x.item_id
      AND l.order_header_key = h.order_header_key
      AND h.document_type   IN ('0005','0006')
      AND l.receiving_node   = x.node_key
      AND l.ordered_qty      > l.received_quantity
      )-
      (SELECT NVL(SUM (r.status_quantity),0)
      FROM yfs_order_line l,
        yfs_order_header h,
        yfs_order_release_status r
      WHERE l.item_id        = x.item_id
      AND l.order_header_key = h.order_header_key
      AND l.order_line_key   = r.order_line_key
      AND l.order_header_key = r.order_header_key
      AND h.document_type   IN ('0005','0006')
      AND l.receiving_node   = x.node_key
      AND l.ordered_qty      > l.received_quantity
      AND r.status          IN ('9020')
      )
    FROM dual
    ) "QTY_DUE",
    (SELECT SUM(yl.extn_backordered_qty)
    FROM yfs_order_line yl,
      yfs_order_header yh,
      yfs_organization yo
    WHERE yh.order_header_key = yl.order_header_key
    AND yh.document_type     IN ('0001','0007.ex')
    AND yl.item_id            = x.item_id
    AND yh.ship_node          = x.node_key
    AND yo.organization_key   = x.node_key
    AND yh.order_date        >= NVL(yo.extn_cache_start_date,to_date('01-JAN-2007','DD-MON-YYYY'))
    ) "QTY_BACKORDER",
    (SELECT SUM(wo.quantity_requested)
    FROM yfs_work_order wo,
      yfs_organization yo
    WHERE wo.document_type  = '7001'
    AND wo.item_id          = x.item_id
    AND wo.node_key         = x.node_key
    AND yo.organization_key = x.node_key
    AND wo.createts        >= NVL(yo.extn_cache_start_date,to_date('01-JAN-2007','DD-MON-YYYY'))
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
    (SELECT SUM(yl.shipped_quantity)
    FROM yfs_order_line yl,
      yfs_order_header yh,
      yfs_organization yo
    WHERE yh.order_header_key = yl.order_header_key
    AND yh.document_type     IN ('0001','0007.ex')
    AND yl.item_id            = x.item_id
    AND yh.ship_node          = x.node_key
    AND yo.organization_key   = x.node_key
    AND yh.order_date        >= NVL(yo.extn_cache_start_date,to_date('01-JAN-2007','DD-MON-YYYY'))
    ) "QTY_ISSUED",
    (
    (SELECT SUM(rl.quantity)
    FROM yfs_receipt_line rl,
      yfs_receipt_header rh,
      yfs_organization yo
    WHERE rh.receipt_header_key = rl.receipt_header_key
    AND rh.document_type       IN ('0010','0011')
    AND rl.item_id              = x.item_id
    AND rh.receivingnode_key    = x.node_key
    AND yo.organization_key     = x.node_key
    AND rh.receipt_date        >= NVL(yo.extn_cache_start_date,to_date('01-JAN-2007','DD-MON-YYYY'))
    ) +
    (SELECT NVL(SUM(lpn.quantity),0)
    FROM nwcg_lpn_return lpn,
      yfs_organization yo
    WHERE lpn.item_id       = x.item_id
    AND lpn.cache_id        = x.node_key
    AND yo.organization_key = x.node_key
    AND lpn.receipt_date   >= NVL(yo.extn_cache_start_date,to_date('01-JAN-2007','DD-MON-YYYY'))
    )) "QTY_RETURNED",
    (SELECT SUM(ir.quantity)
    FROM yfs_inventory_reservation ir,
      yfs_inventory_item yii,
      yfs_item i,
      yfs_organization yo
    WHERE i.item_id           = x.item_id
    AND yii.item_id           = x.item_id
    AND ir.inventory_item_key = yii.inventory_item_key
    AND ir.shipnode_key       = x.node_key
    AND yo.organization_key   = x.node_key
    AND ir.createts          >= NVL(yo.extn_cache_start_date,to_date('01-JAN-2007','DD-MON-YYYY'))
    ) "QTY_RESD",
    (SELECT MAX(supplier_id)
    FROM nwcg_supplier_item
    WHERE preferred = 'Y'
    AND item_id     = x.item_id
    ) "SUPPLIER CODE"
  FROM
    (SELECT DISTINCT b.node_key,
      a.item_id
    FROM yfs_inventory_item a,
      yfs_locn_inventory_audit b
    WHERE a.inventory_item_key = b.inventory_item_key
    AND b.inventory_status    IN ('RFI','NRFI','NRFI-RFB')
    UNION
    SELECT a1.organization_code,
      b1.item_id
    FROM yfs_organization a1,
      yfs_item b1
    WHERE a1.is_node              = 'Y'
    AND a1.organization_code NOT IN ('HIGH_VOLUME_TEMPLATE','LOW_VOLUME_TEMPLATE')
    UNION
    SELECT DISTINCT b.node_key,
      a.item_id
    FROM yfs_inventory_item a,
      yfs_location_inventory b
    WHERE a.inventory_item_key = b.inventory_item_key
    AND b.inventory_status    IN ('RFI','NRFI','NRFI-RFB')
    ) x,
    yfs_item yi
  WHERE x.item_id        = yi.item_id
  AND yi.item_group_code = 'PROD'
  AND yi.status          = '3000'
  GROUP BY x.node_key,
    x.item_id,
    yi.description,
    yi.global_item_id;