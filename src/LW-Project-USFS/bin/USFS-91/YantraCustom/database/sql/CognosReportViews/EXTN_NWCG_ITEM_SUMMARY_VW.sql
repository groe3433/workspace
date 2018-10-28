CREATE OR REPLACE FORCE VIEW EXTN_NWCG_ITEM_SUMMARY_VW ("INCIDENT_NO", "INCIDENT_NAME", "CACHE_ID", "YEAR", "CACHE_ITEM", "DESCRIPTION", "UOM", "ITEM_CLASSIFICATION", "ITEM_GROUP", "QTY_ISSUED", "QTY_TRANSFERRED_IN", "TOTAL_ISSUED", "TOTAL_ISSUED_VALUE", "QTY_RETURNED", "TOTAL_RETURNED_VALUE", "QTY_TRANSFERRED_OUT", "TOTAL_RETURNED", "TOTAL_QTY_WO", "TOTAL_QTY_UNS", "TOTAL_OUTSTANDING", "UNIT_PRICE", "COST_OUTSTANDING", "LOSS_USE_ACTUAL", "ORDER_NO", "FS_ACCT_CODE", "BLM_ACCT_CODE", "OVERRIDE_CODE", "OTHER_ACCT_CODE")
AS
  SELECT incident_no,
    INCIDENT_NAME,
    cache_id,
    YEAR,
    CACHE_ITEM,
    DESCRIPTION,
    UOM,
    ITEM_CLASSIFICATION,
    ITEM_GROUP,
    QTY_ISSUED,
    QTY_TRANSFERRED_IN,
    TOTAL_ISSUED,
    TOTAL_ISSUED_VALUE,
    QTY_RETURNED,
    TOTAL_RETURNED_VALUE,
    QTY_TRANSFERRED_OUT,
    TOTAL_RETURNED,
    TOTAL_QTY_WO,
    TOTAL_QTY_UNS,
    TOTAL_OUTSTANDING,
    unit_price,
    (TOTAL_OUTSTANDING * unit_price) "COST_OUTSTANDING",
    LOSS_USE_ACTUAL,
    ORDER_NO,
    FS_ACCT_CODE,
    BLM_ACCT_CODE,
    OVERRIDE_CODE,
    OTHER_ACCT_CODE
  FROM
    (SELECT incident_no,
      INCIDENT_NAME,
      cache_id,
      YEAR,
      CACHE_ITEM,
      DESCRIPTION,
      UOM,
      ITEM_CLASSIFICATION,
      CASE
        WHEN ITEM_CLASSIFICATION LIKE 'Durable%'
        THEN 'Durable'
        ELSE ITEM_CLASSIFICATION
      END AS "ITEM_GROUP",
      QTY_ISSUED,
      QTY_TRANSFERRED_IN,
      TOTAL_ISSUED,
      (TOTAL_ISSUED*unit_price) "TOTAL_ISSUED_VALUE",
      -- TOTAL_ISSUED*unit_price,
      QTY_RETURNED,
      ((TOTAL_RETURNED+TOTAL_QTY_WO)*unit_price) "TOTAL_RETURNED_VALUE",
      -- ((QTY_RETURNED+TOTAL_QTY_WO)*unit_price) "TOTAL_RETURNED_VALUE",  commented on 10/28/2013 by JayP to include QTY_TRANSFERRED_OUT in TOTAL_RETURNED_VALUE
      -- QTY_RETURNED*unit_price,
      QTY_TRANSFERRED_OUT,
      TOTAL_RETURNED,
      TOTAL_QTY_WO,
      TOTAL_QTY_UNS,
      -- (TOTAL_ISSUED - TOTAL_RETURNED - TOTAL_QTY_WO) "TOTAL_OUTSTANDING", commented by JayP for PI-1050
      (TOTAL_ISSUED - TOTAL_RETURNED - TOTAL_QTY_WO - TOTAL_QTY_UNS) "TOTAL_OUTSTANDING",
      unit_price,
      CASE
        WHEN TOTAL_RETURNED = 0
        THEN 100
        WHEN TOTAL_ISSUED = 0
        THEN 0
        WHEN (NULLIF( TOTAL_ISSUED,0 ) - TOTAL_RETURNED ) = 0
        THEN 0
        ELSE NVL( ROUND( ( ( (NULLIF((TOTAL_ISSUED),0 )) - TOTAL_RETURNED ) / NULLIF( TOTAL_ISSUED,0 ) ) * 100, 2), 0)
      END AS "LOSS_USE_ACTUAL",
      ORDER_NO,
      FS_ACCT_CODE,
      BLM_ACCT_CODE,
      OVERRIDE_CODE,
      OTHER_ACCT_CODE
    FROM
      (SELECT incident_no,
        INCIDENT_NAME,
        cache_id,
        YEAR,
        CACHE_ITEM,
        DESCRIPTION,
        UOM,
        ITEM_CLASSIFICATION,
        QTY_ISSUED,
        QTY_TRANSFERRED_IN,
        TOTAL_ISSUED,
        QTY_RETURNED,
        QTY_TRANSFERRED_OUT,
        (QTY_RETURNED + QTY_TRANSFERRED_OUT) "TOTAL_RETURNED",
        TOTAL_QTY_WO,
        TOTAL_QTY_UNS,
        unit_price,
        ORDER_NO,
        FS_ACCT_CODE,
        BLM_ACCT_CODE,
        OVERRIDE_CODE,
        OTHER_ACCT_CODE
      FROM
        (SELECT incident_no "INCIDENT_NO",
          incident_name "INCIDENT_NAME", ---Added by Vishy on 08/12/2013
          cache_id "CACHE_ID",
          YEAR,
          Item_id "CACHE_ITEM",
          description "DESCRIPTION",
          uom "UOM",
          tax_product_code "ITEM_CLASSIFICATION",
          QtyIssued "QTY_ISSUED",
          QtyTransferredIn "QTY_TRANSFERRED_IN",
          --(QtyIssued       +QtyTransferredIn+QTY_REFURB) "TOTAL_ISSUED",
          (QtyIssued       +QtyTransferredIn) "TOTAL_ISSUED",
          ((QTY_RFI_REFURB +QTY_RFI+QTY_UNS_NWT_RETURN+QTY_UNS_NWT_REFURB)-QTY_TRANSFERRED_OUT) "QTY_RETURNED",
          QTY_TRANSFERRED_OUT,
          (QTY_WORK_ORDERED + RFBXFER_IN_AS_ITEMS - RFBXFER_OUT_AS_ITEMS - QTY_RFI_REFURB - QTY_UNS_NWT_REFURB - QTY_UNSREFURB) "TOTAL_QTY_WO",
          -- "QTY_UNSREFURB" is subtracted from QTY_WORK_ORDERED as it is doing double count.  Added by JayP on 11/13/2013
          (QTY_UNS + QTY_UNSREFURB) "TOTAL_QTY_UNS",
          unit_price,
          ORDER_NO,
          FS_ACCT_CODE,
          BLM_ACCT_CODE,
          OVERRIDE_CODE,
          OTHER_ACCT_CODE
        FROM
          (SELECT rpt.item_id,
            item.uom,
            item.description,
            item.tax_product_code,
            m.incident_no,
            m.incident_name, ---Added by Vishy on 08/12/2013
            rpt.cache_id,
            rpt.unit_price,
            m.YEAR,
            --(SELECT SUM(ol.shipped_quantity)
            --FROM yfs_order_line ol,
            --yfs_order_header oh
            --WHERE oh.extn_incident_no          = rpt.incident_no
            --AND NVL(oh.extn_incident_year,' ') = NVL(rpt.year,' ')
            --AND oh.order_header_key            = ol.order_header_key
            --AND oh.document_type              IN ('0001','0007.ex')
            --AND oh.order_type NOT LIKE '%Refurb%'
            --AND rtrim(oh.ship_node) = rtrim(rpt."CACHE_ID")
            --AND ol.item_id          = rpt.item_id
            --AND ol.unit_price       = rpt.unit_price
            --) AS QtyIssued,
            (
            SELECT NVL(SUM(billing.trans_qty) ,0)
            FROM nwcg_billing_transaction billing
            WHERE billing.trans_type ='ISSUE CONFIRM SHIPMENT'
            AND billing.incident_no  = rpt.incident_no
            AND rtrim(rpt.cache_id)  = rtrim(billing.cache_id)
            AND billing.item_id      = rtrim(rpt.item_id)
            AND billing.unit_cost    = rpt.unit_price
            ) +
            (SELECT NVL(ABS(SUM(billing.trans_qty)) ,0)
            FROM nwcg_billing_transaction billing
            WHERE billing.trans_type IN ( 'WO-REFURB' )
            AND billing.incident_no   = rpt.incident_no
            AND rtrim(rpt.cache_id)   = rtrim(billing.cache_id)
            AND billing.item_id       = rtrim(rpt.item_id)
            AND billing.unit_cost     = rpt.unit_price
            AND billing.trans_qty     < 0
            ) AS QtyIssued,
            (SELECT NVL( ABS( SUM(billing.trans_qty) ) ,0)
            FROM nwcg_billing_transaction billing
            WHERE billing.trans_type IN ( 'CONFIRM INCIDENT TO')
            AND billing.incident_no   = rpt.incident_no
            AND rtrim(rpt.cache_id)   = rtrim(billing.cache_id)
            AND billing.item_id       = rtrim(rpt.item_id)
            AND billing.unit_cost     = rpt.unit_price
            ) AS QtyTransferredIn,
            --(SELECT NVL(SUM(ol.ordered_qty), 0)
            --FROM yfs_order_line ol,
            --yfs_order_header oh
            --WHERE oh.extn_to_incident_no       = rpt.incident_no
            --AND NVL(oh.extn_incident_year,' ') = NVL(rpt.year,' ')
            --AND oh.order_header_key            = ol.order_header_key
            --AND oh.document_type LIKE '0008%'
            --AND rtrim(oh.ship_node) = rtrim(rpt."CACHE_ID")
            --AND ol.item_id          = rpt.item_id
            --AND ol.unit_price       = rpt.unit_price
            --) AS QtyTransferredIn,
            /*  (
            SELECT NVL(SUM(ol.ordered_qty), 0)
            FROM yfs_order_line ol,
            yfs_order_header oh
            WHERE oh.extn_incident_no          = rpt.incident_no
            AND NVL(oh.extn_incident_year,' ') = NVL(rpt.year,' ')
            AND oh.order_header_key            = ol.order_header_key
            AND oh.document_type              IN ('0001','0007.ex')
            AND oh.order_type LIKE '%Refurb%'
            AND rtrim(oh.ship_node) = rtrim(rpt.CACHE_ID)
            AND ol.item_id          = rpt.item_id
            AND ol.unit_price       = rpt.unit_price
            ) AS QTY_REFURB,
            */
            --(select NVL(sum(billing.trans_qty) ,0) from nwcg_billing_transaction billing where
            --billing.trans_type  in ( 'WO-REFURB' )
            --and billing.incident_no = rpt.incident_no
            --and rtrim(rpt.cache_id)= rtrim(billing.cache_id) and
            --billing.item_id = rtrim(rpt.item_id) and
            --billing.unit_cost = rpt.unit_price) as QTY_REFURB,
            --(SELECT NVL(SUM(ol.ordered_qty), 0)
            --FROM yfs_order_line ol,
            --yfs_order_header oh
            --WHERE oh.extn_incident_no = rpt.incident_no
            --AND oh.extn_incident_year = rpt.year
            --AND oh.order_header_key   = ol.order_header_key
            --AND rtrim(oh.ship_node)   = rtrim(rpt."CACHE_ID")
            --AND oh.document_type LIKE '0008%'
            --AND ol.item_id    = rpt.item_id
            --AND ol.unit_price = rpt.unit_price
            --) AS QTY_TRANSFERRED_OUT,
            (
            SELECT NVL( SUM(billing.trans_qty) ,0)
            FROM nwcg_billing_transaction billing
            WHERE billing.trans_type IN ( 'CONFIRM INCIDENT FROM')
            AND billing.incident_no   = rpt.incident_no
            AND rtrim(rpt.cache_id)   = rtrim(billing.cache_id)
            AND billing.item_id       = rtrim(rpt.item_id)
            AND billing.unit_cost     = rpt.unit_price
            ) AS QTY_TRANSFERRED_OUT,
            (SELECT NVL(SUM(c1.quantity_rfi_refurb),0)
            FROM nwcg_incident_return c1
            WHERE c1.incident_no   = rpt.incident_no
            AND c1.incident_year   = rpt.year
            AND rtrim(c1.cache_id) = rtrim(rpt."CACHE_ID")
            AND c1.item_id         = rpt.item_id
            AND c1.unit_price      = rpt.unit_price
            ) AS QTY_RFI_REFURB,
            (SELECT NVL(SUM(c1.quantity_rfi),0)
            FROM nwcg_incident_return c1
            WHERE c1.incident_no   = rpt.incident_no
            AND c1.incident_year   = rpt.year
            AND rtrim(c1.cache_id) = rtrim(rpt."CACHE_ID")
            AND c1.item_id         = rpt.item_id
            AND c1.unit_price      = rpt.unit_price
            ) AS QTY_RFI,
            (SELECT NVL(SUM(c1.quantity_uns_nwt_return),0)
            FROM nwcg_incident_return c1
            WHERE c1.incident_no   = rpt.incident_no
            AND c1.incident_year   = rpt.year
            AND rtrim(c1.cache_id) = rtrim(rpt."CACHE_ID")
            AND c1.item_id         = rpt.item_id
            AND c1.unit_price      = rpt.unit_price
            ) AS QTY_UNS_NWT_RETURN,
            (SELECT NVL(SUM(c1.quantity_uns_nwt_refurb),0)
            FROM nwcg_incident_return c1
            WHERE c1.incident_no   = rpt.incident_no
            AND c1.incident_year   = rpt.year
            AND rtrim(c1.cache_id) = rtrim(rpt."CACHE_ID")
            AND c1.item_id         = rpt.item_id
            AND c1.unit_price      = rpt.unit_price
            ) AS QTY_UNS_NWT_REFURB,
            --(SELECT NVL(SUM(wol.actual_quantity)-SUM(wol.transfer_qty) , 0)
            --FROM nwcg_master_work_order wo,
            --nwcg_master_work_order_line wol
            --WHERE wo.incident_no                = rpt.incident_no
            --AND NVL(wo.incident_year,' ')       = NVL(rpt.year,' ')
            --AND rtrim(wo.node)                  = rtrim(rpt."CACHE_ID")
            --AND wol.item_id                     = rtrim(rpt.item_id)
            --AND rtrim(wo.master_work_order_key) = rtrim(wol.master_work_order_key)
            --) AS QTY_WORK_ORDERED,
            (
            SELECT NVL(SUM(wol.actual_quantity) , 0)
            FROM nwcg_master_work_order wo,
              nwcg_master_work_order_line wol
            WHERE wo.incident_no                = rpt.incident_no
            AND NVL(wo.incident_year,' ')       = NVL(rpt.year,' ')
            AND rtrim(wo.node)                  = rtrim(rpt."CACHE_ID")
            AND wol.item_id                     = rtrim(rpt.item_id)
            AND rtrim(wo.master_work_order_key) = rtrim(wol.master_work_order_key)
            ) AS QTY_WORK_ORDERED,
            --(SELECT NVL(SUM(ol.ordered_qty),0)
            --FROM yfs_order_line ol,
            --yfs_order_header oh
            --WHERE oh.extn_incident_no    = rpt.incident_no
            --AND oh.extn_incident_year    = rpt.year
            --AND oh.order_header_key      = ol.order_header_key
            --AND oh.document_type         = '0006'
            --AND oh.order_type            = 'Refurb Transfer'
            --AND rtrim(oh.receiving_node) = rtrim(rpt."CACHE_ID")
            --AND ol.item_id               = rpt.item_id
            --) AS RFBXFER_IN_AS_ITEMS,
            (
            SELECT NVL(SUM(ol.ordered_qty),0)
            FROM yfs_order_line ol,
              yfs_order_header oh,
              yfs_order_release_status ors
            WHERE oh.order_header_key = ol.order_header_key
            AND oh.order_header_key   =ors.order_header_key
            AND ol.order_header_key   = ors.order_header_key
            AND ol.order_line_key     = ors.order_line_key
            AND oh.extn_incident_no   = rpt.incident_no
            AND oh.extn_incident_year = NVL(rpt.year, ' ')
            AND ors.status_quantity   > 0
            AND ors.status            <'3900'
            AND oh.document_type      = '0006'
            AND oh.order_type         = 'Refurb Transfer'
            AND rtrim(oh.ship_node)   = rtrim(rpt."CACHE_ID")
            AND ol.item_id            = rtrim(rpt.item_id)
            ) AS RFBXFER_IN_AS_ITEMS,
            --(select NVL(sum(billing.trans_qty) ,0) from nwcg_billing_transaction billing where
            --billing.trans_type  in ( 'SHIP CACHE TO' )
            --and billing.incident_no = rpt.incident_no
            --and rtrim(rpt.cache_id)= rtrim(billing.cache_id) and
            --billing.item_id = rtrim(rpt.item_id) and
            --billing.unit_cost = rpt.unit_price) as RFBXFER_OUT_AS_ITEMS,
            --(select NVL(sum(billing.trans_qty) ,0) from nwcg_billing_transaction billing where
            --billing.trans_type  in ( 'RECEIVE CACHE TO' )
            --and billing.incident_no = rpt.incident_no
            --and rtrim(rpt.cache_id)= rtrim(billing.cache_id) and
            --billing.item_id = rtrim(rpt.item_id) and
            --billing.unit_cost = rpt.unit_price) as RFBXFER_IN_AS_ITEMS,
            (
            SELECT NVL(SUM(ol.ordered_qty),0)
            FROM yfs_order_line ol,
              yfs_order_header oh
            WHERE oh.extn_incident_no = rpt.incident_no
            AND oh.extn_incident_year = rpt.year
            AND oh.order_header_key   = ol.order_header_key
            AND oh.document_type      = '0006'
            AND oh.order_type         = 'Refurb Transfer'
            AND rtrim(oh.ship_node)   = rtrim(rpt."CACHE_ID")
            AND ol.item_id            = rpt.item_id
            ) AS RFBXFER_OUT_AS_ITEMS,
            (SELECT NVL(SUM(c1.quantity),0)
            FROM yfs_receipt_line c1,
              yfs_receipt_header c2
            WHERE c1.receipt_header_key     = c2.receipt_header_key
            AND c2.extn_incident_no         = rpt.incident_no
            AND c2.extn_incident_year       = rpt.year
            AND rtrim(c2.receivingnode_key) = rtrim(rpt."CACHE_ID")
            AND c1.item_id                  = rpt.item_id
            AND c1.extn_receiving_price     = rpt.unit_price
              --and c1.extn_received_as_component = 'N'
            AND c1.disposition_code = 'UNSERVICE'
            ) AS QTY_UNS,
            (SELECT NVL(SUM(c1.quantity_uns_refurb),0)
            FROM nwcg_incident_return c1
            WHERE c1.incident_no   = rpt.incident_no
            AND c1.incident_year   = rpt.year
            AND rtrim(c1.cache_id) = rtrim(rpt."CACHE_ID")
            AND c1.item_id         = rpt.item_id
            AND c1.unit_price      = rpt.unit_price
            ) AS QTY_UNSREFURB,
            (SELECT MAX(order_no)
            FROM yfs_order_header od
            WHERE od.extn_incident_no = rpt.incident_no
            AND rtrim(od.ship_node)   = rtrim(rpt."CACHE_ID")
            )AS ORDER_NO,
            m.incident_fs_acct_code "FS_ACCT_CODE",
            NVL(m.incident_blm_acct_code, 0) "BLM_ACCT_CODE",
            m.override_code "OVERRIDE_CODE",
            m.incident_other_acct_code "OTHER_ACCT_CODE"
          FROM EXTN_NWCG_INCIDENT_RPT_SUM_VW rpt,
            yfs_item item,
            nwcg_incident_order m
          WHERE rpt.item_id      = item.item_id
          AND ( (rpt.incident_no = m.incident_no
          AND rpt.year           = m.year)
          OR (rpt.incident_no    = m.replaced_incident_no
          AND rpt.year           = m.replaced_incident_year)
          OR (rpt.incident_no    = m.replaced_incident_no_2
          AND rpt.year           = m.replaced_incident_year_2) )
            -- ORDER BY Item_id ASC
          )
        )
      )
    );