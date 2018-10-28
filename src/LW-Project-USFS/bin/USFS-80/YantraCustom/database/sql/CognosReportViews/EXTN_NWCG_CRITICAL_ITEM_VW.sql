CREATE OR REPLACE FORCE VIEW EXTN_NWCG_CRITICAL_ITEM_VW ("ITEM_ID", "UOM", "ITEM_DESCRIPTION", "CACHE ID", "QTY_RFI", "QTY_NRFI", "PREV_DAY_USAGE", "QTY_ALLOC", "QUANTITY_WORDERED", "GLOBAL_ITEM_ID", "UNIT_COST", "LOCAL CRITICAL", "NATIONAL CRITICAL", "GSA PREFERRED", "QTY_MIN", "QTY_MAX", "QTY_DUE", "QTY_RESD")
AS
  SELECT a.item_id,
    a.uom,
    a.item_description,
    a.node_key "CACHE ID",
    (SELECT SUM((a1.quantity+a1.pend_in_qty) - (a1.hard_alloc_qty+a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1,
      yfs_item c1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND b1.item_id              = c1.item_id
    AND b1.item_id              = a.item_id
    AND a1.inventory_status     = 'RFI'
    AND a1.node_key             = a.node_key
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    ) "QTY_RFI",
    (SELECT SUM(a1.quantity - (a1.hard_alloc_qty+a1.soft_alloc_qty))
    FROM yfs_location_inventory a1,
      yfs_inventory_item b1,
      yfs_item c1
    WHERE a1.inventory_item_key = b1.inventory_item_key
    AND b1.item_id              = c1.item_id
    AND b1.item_id              = a.item_id
    AND a1.inventory_status     = 'NRFI'
    AND a1.node_key             = a.node_key
    AND a1.location_id NOT     IN ('CYCLE_COUNT_BIN', 'PHYSICAL_COUNT_BIN', 'PACK_BIN')
    ) "QTY_NRFI",
    (SELECT SUM(sl.quantity)
    FROM yfs_shipment sh,
      yfs_shipment_line sl,
      yfs_order_header oh
    WHERE sl.order_header_key               =oh.order_header_key
    AND sh.shipment_key                     =sl.shipment_key
    AND oh.document_type                    = '0001'
    AND oh.ship_node                        = a.node_key
    AND sl.item_id                          = a.item_id
    AND TO_CHAR(sh.ship_date,'DD-MON-YYYY') = TO_CHAR(sysdate-1,'DD-MON-YYYY')
    ) "PREV_DAY_USAGE",
    (SELECT SUM(yid.quantity)
    FROM yfs_inventory_demand yid,
      yfs_inventory_item yii
    WHERE yid.inventory_item_key = yii.inventory_item_key
    AND yii.item_id              = a.item_id
    AND yid.shipnode_key         = a.node_key
    AND yid.demand_type          = 'ALLOCATED'
    ) "QTY_ALLOC",
    (SELECT SUM(c3.quantity_allocated)
    FROM yfs_work_order c3
    WHERE rtrim(c3.item_id) = rtrim(a.item_id)
    AND c3.node_key         = a.node_key
    )"QUANTITY_WORDERED",
    c.global_item_id,
    c.unit_cost,
    NVL(
    (SELECT extn_local_critical_item
    FROM yfs_item_node_defn
    WHERE item_id = a.item_id
    AND node      = a.node_key
    ),'N') "LOCAL CRITICAL",
    NVL(c.extn_nat_critical_item,'N') "NATIONAL CRITICAL",
    NVL(
    (SELECT preferred
    FROM nwcg_supplier_item
    WHERE item_id   = a.item_id
    AND supplier_id = 'TX0014'
    ),'N') "GSA PREFERRED",
    (SELECT im.minimum_qty_1
    FROM yfs_item_status_rules im
    WHERE im.item_id    = a.item_id
    AND im.shipnode_key = a.node_key
    ) "QTY_MIN",
    (SELECT im.maximum_qty_1
    FROM yfs_item_status_rules im
    WHERE im.item_id    = a.item_id
    AND im.shipnode_key = a.node_key
    ) "QTY_MAX",
    (SELECT SUM(l.ordered_qty)
    FROM yfs_order_line l,
      yfs_order_header h
    WHERE l.item_id        = a.item_id
    AND l.order_header_key = h.order_header_key
    AND h.document_type    = '0005'
    AND l.receiving_node   = a.node_key
    ) "QTY_DUE",
    (SELECT SUM(ir.quantity)
    FROM yfs_inventory_reservation ir,
      yfs_inventory_item yii,
      yfs_item i
    WHERE i.item_id           = a.item_id
    AND yii.item_id           = a.item_id
    AND ir.inventory_item_key = yii.inventory_item_key
    AND ir.shipnode_key       = a.node_key
    ) "QTY_RESD"
  FROM YNA_NODE_INVENTORY_NL_VW a,
    YFS_ITEM c
  WHERE a.item_id = c.item_id
  AND c.status    = '3000'
  GROUP BY a.item_id,
    a.node_key,
    a.item_description,
    a.uom,
    c.global_item_id,
    c.unit_cost,
    c.extn_nat_critical_item;