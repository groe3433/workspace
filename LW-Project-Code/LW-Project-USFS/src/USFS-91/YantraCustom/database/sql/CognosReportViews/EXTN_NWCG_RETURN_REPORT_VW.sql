CREATE OR REPLACE FORCE VIEW EXTN_NWCG_RETURN_REPORT_VW ("RECEIPT NO", "ITEM_ID", "ITEM_DESCRIPTION", "UOM", "SERIAL_NO", "QUANTITY_RETURNED", "QUANTITY_RFI", "QUANTITY_NRFI", "QUANTITY_UNSERVICED", "QUANTITY_UNSERVICED_NWT", "QUANTITY_WORDERED", "QUANTITY_REFURBED", "QUANTITY_REFURBED_UNS", "QUANTITY_REFURBED_UNS_NWT", "REFURB_COST", "INCIDENT NO", "INCIDENT_NAME", "RETURN_DATE", "EXTN_RETURN_COMMENTS", "INSPECTION_COMMENTS", "INCIDENT_OTHER_ACCT_CODE", "INCIDENT_BLM_ACCT_CODE", "INCIDENT_FS_ACCT_CODE", "OVERRIDE_CODE", "UNIT_PRICE", "YEAR", "CACHE ID", "COMPANY", "RETURN_TO_ADDRESS1", "RETURN_TO_ADDRESS2", "RETURN_TO_CITY", "RETURN_TO_STATE", "RETURN_TO_ZIP", "SHIP_TO_COMPANY", "SHIP_TO_ADDRESS1", "SHIP_TO_ADDRESS2", "SHIP_TO_CITY", "SHIP_TO_STATE", "SHIP_TO_ZIP")
AS
  SELECT rh.receipt_no "RECEIPT NO",
    rl.item_id,
    yi.description "ITEM_DESCRIPTION",
    yi.uom,
    rl.serial_no,
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    ) "QUANTITY_RETURNED",
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    AND c1.disposition_code   = 'RFI'
    ) "QUANTITY_RFI",
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    AND c1.disposition_code   = 'NRFI'
    ) "QUANTITY_NRFI",
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    AND c1.disposition_code   = 'UNSERVICE'
    ) "QUANTITY_UNSERVICED",
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    AND c1.disposition_code   = 'UNSRV-NWT'
    ) "QUANTITY_UNSERVICED_NWT",
    (SELECT SUM (c4.quantity_completed)
    FROM yfs_work_order c3,
      yfs_work_order_acty_dtl c4
    WHERE RTRIM (c3.item_id)  = RTRIM (rl.item_id)
    AND c3.extn_incident_no   = nio.incident_no
    AND c3.extn_incident_year = nio.year
    AND c3.extn_is_refurb     = 'N'
    AND c3.work_order_key     = c4.work_order_key
    ) "QUANTITY_WORDERED",
    (SELECT SUM (c3.trans_qty)
    FROM nwcg_billing_transaction c3
    WHERE RTRIM (c3.item_id)      = RTRIM (rl.item_id)
    AND c3.incident_no            = nio.incident_no
    AND c3.incident_year          = nio.year
    AND rl.disposition_code       = 'NRFI'
    AND c3.trans_type             = 'WO-REFURB'
    AND c3.disposition_code      != 'UNSERVICE'
    AND c3.disposition_code      != 'UNSRV-NWT'
    AND RTRIM (c3.transaction_no) = RTRIM (rh.receipt_no)
    ) "QUANTITY_REFURBED",
    (SELECT SUM (c3.trans_qty)
    FROM nwcg_billing_transaction c3
    WHERE RTRIM (c3.item_id)      = RTRIM (rl.item_id)
    AND c3.incident_no            = nio.incident_no
    AND c3.incident_year          = nio.year
    AND rl.disposition_code       = 'NRFI'
    AND c3.trans_type             = 'WO-REFURB'
    AND c3.disposition_code       = 'UNSERVICE'
    AND RTRIM (c3.transaction_no) = RTRIM (rh.receipt_no)
    ) "QUANTITY_REFURBED_UNS",
    (SELECT SUM (c3.trans_qty)
    FROM nwcg_billing_transaction c3
    WHERE RTRIM (c3.item_id)      = RTRIM (rl.item_id)
    AND c3.incident_no            = nio.incident_no
    AND c3.incident_year          = nio.year
    AND rl.disposition_code       = 'NRFI'
    AND c3.trans_type             = 'WO-REFURB'
    AND c3.disposition_code       = 'UNSRV-NWT'
    AND RTRIM (c3.transaction_no) = RTRIM (rh.receipt_no)
    ) "QUANTITY_REFURBED_UNS_NWT",
    (
    (SELECT AVG (c3.refurb_cost)
    FROM nwcg_master_work_order_line c3,
      nwcg_master_work_order c4
    WHERE RTRIM (c3.item_id)             = RTRIM (rl.item_id)
    AND c4.incident_no                   = nio.incident_no
    AND c4.incident_year                 = nio.year
    AND rl.disposition_code              = 'NRFI'
    AND RTRIM (c3.master_work_order_key) = RTRIM (c4.master_work_order_key)
    AND RTRIM (c4.master_work_order_no)  = RTRIM (rh.receipt_no)
    ) + NVL (
    (SELECT SUM (c3.trans_amount)
    FROM nwcg_billing_transaction c3,
      nwcg_master_work_order_line c4,
      nwcg_master_work_order c5
    WHERE RTRIM (c3.item_id)             = RTRIM (c4.item_id)
    AND c3.incident_no                   = nio.incident_no
    AND c3.incident_year                 = nio.year
    AND c3.trans_amount                  < 0
    AND c3.trans_type                    = 'WO-REFURB'
    AND RTRIM (c3.transaction_no)        = RTRIM (rh.receipt_no)
    AND c4.is_replaced_item              = 'Y'
    AND c4.status                        = 'Work Order Completed'
    AND RTRIM (c4.master_work_order_key) = RTRIM (c5.master_work_order_key)
    AND RTRIM (c5.master_work_order_no)  = RTRIM (rh.receipt_no)
    ), 0)) "REFURB_COST",
    nio.incident_no "INCIDENT NO",
    nio.incident_name,
    rh.receipt_date "RETURN_DATE",
    rh.extn_return_comments,
    rl.inspection_comments,
    nio.incident_other_acct_code,
    nio.incident_blm_acct_code,
    nio.incident_fs_acct_code,
    nio.override_code,
    rl.extn_receiving_price "UNIT_PRICE",
    nio.year,
    rh.receivingnode_key "CACHE ID",
    d.company,
    d.address_line1 "RETURN_TO_ADDRESS1",
    d.address_line2 "RETURN_TO_ADDRESS2",
    d.city "RETURN_TO_CITY",
    d.state "RETURN_TO_STATE",
    d.zip_code "RETURN_TO_ZIP",
    nio.customer_name "SHIP_TO_COMPANY",
    f.address_line1 "SHIP_TO_ADDRESS1",
    f.address_line2 "SHIP_TO_ADDRESS2",
    f.city "SHIP_TO_CITY",
    f.state "SHIP_TO_STATE",
    f.zip_code "SHIP_TO_ZIP"
  FROM yfs_receipt_header rh,
    yfs_receipt_line rl,
    nwcg_incident_order nio,
    yfs_item yi,
    yfs_person_info d,
    yfs_organization e,
    yfs_person_info f
  WHERE nio.incident_no     = rh.extn_incident_no
  AND nio.year              = rh.extn_incident_year
  AND rh.receipt_header_key = rl.receipt_header_key
  AND rl.item_id            = yi.item_id
    --Added by Vishy
  AND rl.uom                       =yi.uom
  AND RTRIM (rh.receivingnode_key) = RTRIM (e.organization_code)
  AND e.corporate_address_key      = d.person_info_key
  AND nio.person_info_shipto_key   = f.person_info_key
  AND rh.extn_is_return_receipt    = 'Y'
  AND yi.is_serial_tracked         = 'N'
  UNION ALL
  SELECT rh.receipt_no "RECEIPT NO",
    rl.item_id,
    yi.description "ITEM_DESCRIPTION",
    yi.uom,
    rl.serial_no,
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    AND c1.serial_no          = rl.serial_no
    ) "QUANTITY_RETURNED",
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    AND c1.serial_no          = rl.serial_no
    AND c1.disposition_code   = 'RFI'
    ) "QUANTITY_RFI",
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    AND c1.serial_no          = rl.serial_no
    AND c1.disposition_code   = 'NRFI'
    ) "QUANTITY_NRFI",
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    AND c1.serial_no          = rl.serial_no
    AND c1.disposition_code   = 'UNSERVICE'
    ) "QUANTITY_UNSERVICED",
    (SELECT SUM (c1.quantity)
    FROM yfs_receipt_line c1,
      yfs_receipt_header c2
    WHERE c1.receipt_line_key = rl.receipt_line_key
    AND c1.receipt_header_key = c2.receipt_header_key
    AND c2.receipt_no         = rh.receipt_no
    AND c1.serial_no          = rl.serial_no
    AND c1.disposition_code   = 'UNSRV-NWT'
    ) "QUANTITY_UNSERVICED_NWT",
    (SELECT SUM (c4.quantity_completed)
    FROM yfs_work_order c3,
      yfs_work_order_acty_dtl c4
    WHERE RTRIM (c3.item_id)  = RTRIM (rl.item_id)
    AND c3.extn_incident_no   = nio.incident_no
    AND c3.extn_incident_year = nio.year
    AND c3.extn_is_refurb     = 'N'
    AND c3.work_order_key     = c4.work_order_key
    AND c4.serial_no          = rl.serial_no
    ) "QUANTITY_WORDERED",
    (SELECT SUM (c5.trans_qty)
    FROM nwcg_master_work_order_line c3,
      nwcg_billing_transaction c5
    WHERE RTRIM (c3.item_id)              = RTRIM (rl.item_id)
    AND rl.disposition_code               = 'NRFI'
    AND RTRIM (c3.primay_serial_no)       = RTRIM (rl.serial_no)
    AND RTRIM (c5.item_id)                = RTRIM (rl.item_id)
    AND c5.incident_no                    = nio.incident_no
    AND c5.incident_year                  = nio.year
    AND RTRIM (c5.transaction_header_key) = RTRIM (c3.master_work_order_key)
    AND RTRIM (c5.transaction_no)         = RTRIM (rh.receipt_no)
    AND RTRIM (c5.transaction_line_key)   = RTRIM (c3.master_work_order_line_key)
    AND c5.trans_type                     = 'WO-REFURB'
    AND c5.disposition_code              != 'UNSERVICE'
    AND c5.disposition_code              != 'UNSRV-NWT'
    ) "QUANTITY_REFURBED",
    (SELECT SUM (c5.trans_qty)
    FROM nwcg_master_work_order_line c3,
      nwcg_billing_transaction c5
    WHERE RTRIM (c3.item_id)              = RTRIM (rl.item_id)
    AND rl.disposition_code               = 'NRFI'
    AND RTRIM (c3.primay_serial_no)       = RTRIM (rl.serial_no)
    AND RTRIM (c5.item_id)                = RTRIM (rl.item_id)
    AND c5.incident_no                    = nio.incident_no
    AND c5.incident_year                  = nio.year
    AND RTRIM (c5.transaction_header_key) = RTRIM (c3.master_work_order_key)
    AND RTRIM (c5.transaction_no)         = RTRIM (rh.receipt_no)
    AND RTRIM (c5.transaction_line_key)   = RTRIM (c3.master_work_order_line_key)
    AND c5.trans_type                     = 'WO-REFURB'
    AND c5.disposition_code               = 'UNSERVICE'
    ) "QUANTITY_REFURBED_UNS",
    (SELECT SUM (c5.trans_qty)
    FROM nwcg_master_work_order_line c3,
      nwcg_billing_transaction c5
    WHERE RTRIM (c3.item_id)              = RTRIM (rl.item_id)
    AND rl.disposition_code               = 'NRFI'
    AND RTRIM (c3.primay_serial_no)       = RTRIM (rl.serial_no)
    AND RTRIM (c5.item_id)                = RTRIM (rl.item_id)
    AND c5.incident_no                    = nio.incident_no
    AND c5.incident_year                  = nio.year
    AND RTRIM (c5.transaction_header_key) = RTRIM (c3.master_work_order_key)
    AND RTRIM (c5.transaction_no)         = RTRIM (rh.receipt_no)
    AND RTRIM (c5.transaction_line_key)   = RTRIM (c3.master_work_order_line_key)
    AND c5.trans_type                     = 'WO-REFURB'
    AND c5.disposition_code               = 'UNSRV-NWT'
    ) "QUANTITY_REFURBED_UNS_NWT",
    (
    (SELECT AVG (c3.refurb_cost)
    FROM nwcg_master_work_order_line c3,
      nwcg_master_work_order c4
    WHERE RTRIM (c3.item_id)             = RTRIM (rl.item_id)
    AND c4.incident_no                   = nio.incident_no
    AND c4.incident_year                 = nio.year
    AND rl.disposition_code              = 'NRFI'
    AND RTRIM (c3.master_work_order_key) = RTRIM (c4.master_work_order_key)
    AND RTRIM (c4.master_work_order_no)  = RTRIM (rh.receipt_no)
    AND c3.primay_serial_no              = rl.serial_no
    ) + NVL (
    (SELECT SUM (c3.trans_amount)
    FROM nwcg_billing_transaction c3,
      nwcg_master_work_order_line c4,
      nwcg_master_work_order c5
    WHERE RTRIM (c3.item_id)             = RTRIM (c4.item_id)
    AND c3.incident_no                   = nio.incident_no
    AND c3.incident_year                 = nio.year
    AND c3.trans_amount                  < 0
    AND c3.trans_type                    = 'WO-REFURB'
    AND RTRIM (c3.transaction_no)        = RTRIM (rh.receipt_no)
    AND c4.is_replaced_item              = 'Y'
    AND c4.status                        = 'Work Order Completed'
    AND RTRIM (c4.master_work_order_key) = RTRIM (c5.master_work_order_key)
    AND RTRIM (c5.master_work_order_no)  = RTRIM (rh.receipt_no)
    ), 0)) "REFURB_COST",
    nio.incident_no "INCIDENT NO",
    nio.incident_name,
    rh.receipt_date "RETURN_DATE",
    rh.extn_return_comments,
    rl.inspection_comments,
    nio.incident_other_acct_code,
    nio.incident_blm_acct_code,
    nio.incident_fs_acct_code,
    nio.override_code,
    rl.extn_receiving_price "UNIT_PRICE",
    nio.year,
    rh.receivingnode_key "CACHE ID",
    d.company,
    d.address_line1 "RETURN_TO_ADDRESS1",
    d.address_line2 "RETURN_TO_ADDRESS2",
    d.city "RETURN_TO_CITY",
    d.state "RETURN_TO_STATE",
    d.zip_code "RETURN_TO_ZIP",
    nio.customer_name "SHIP_TO_COMPANY",
    f.address_line1 "SHIP_TO_ADDRESS1",
    f.address_line2 "SHIP_TO_ADDRESS2",
    f.city "SHIP_TO_CITY",
    f.state "SHIP_TO_STATE",
    f.zip_code "SHIP_TO_ZIP"
  FROM yfs_receipt_header rh,
    yfs_receipt_line rl,
    nwcg_incident_order nio,
    yfs_item yi,
    yfs_person_info d,
    yfs_organization e,
    yfs_person_info f
  WHERE nio.incident_no     = rh.extn_incident_no
  AND nio.year              = rh.extn_incident_year
  AND rh.receipt_header_key = rl.receipt_header_key
  AND rl.item_id            = yi.item_id
    --Added by Vishy
  AND rl.uom                       =yi.uom
  AND RTRIM (rh.receivingnode_key) = RTRIM (e.organization_code)
  AND e.corporate_address_key      = d.person_info_key
  AND nio.person_info_shipto_key   = f.person_info_key
  AND rh.extn_is_return_receipt    = 'Y'
  AND yi.is_serial_tracked         = 'Y'
  UNION ALL
  SELECT lpn.return_no "RECEIPT NO",
    lpn.item_id,
    yi.description "ITEM_DESCRIPTION",
    yi.uom,
    lpn.serial_no,
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    ) "QUANTITY_RETURNED",
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    AND c1.disposition_code    = 'RFI'
    ) "QUANTITY_RFI",
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    AND c1.disposition_code    = 'NRFI'
    ) "QUANTITY_NRFI",
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    AND c1.disposition_code    = 'UNSERVICE'
    ) "QUANTITY_UNSERVICED",
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    AND c1.disposition_code    = 'UNSRV-NWT'
    ) "QUANTITY_UNSERVICED_NWT",
    (SELECT SUM (c4.quantity_completed)
    FROM yfs_work_order c3,
      yfs_work_order_acty_dtl c4
    WHERE RTRIM (c3.item_id)  = RTRIM (lpn.item_id)
    AND c3.extn_incident_no   = nio.incident_no
    AND c3.extn_incident_year = nio.year
    AND c3.extn_is_refurb     = 'N'
    AND c3.work_order_key     = c4.work_order_key
    ) "QUANTITY_WORDERED",
    (SELECT SUM (c3.trans_qty)
    FROM nwcg_billing_transaction c3
    WHERE RTRIM (c3.item_id)      = RTRIM (lpn.item_id)
    AND c3.incident_no            = nio.incident_no
    AND c3.incident_year          = nio.year
    AND lpn.disposition_code      = 'NRFI'
    AND c3.trans_type             = 'WO-REFURB'
    AND c3.disposition_code      != 'UNSERVICE'
    AND c3.disposition_code      != 'UNSRV-NWT'
    AND RTRIM (c3.transaction_no) = RTRIM (rh.receipt_no)
    ) "QUANTITY_REFURBED",
    (SELECT SUM (c3.trans_qty)
    FROM nwcg_billing_transaction c3
    WHERE RTRIM (c3.item_id)      = RTRIM (lpn.item_id)
    AND c3.incident_no            = nio.incident_no
    AND c3.incident_year          = nio.year
    AND lpn.disposition_code      = 'NRFI'
    AND c3.trans_type             = 'WO-REFURB'
    AND c3.disposition_code       = 'UNSERVICE'
    AND RTRIM (c3.transaction_no) = RTRIM (rh.receipt_no)
    ) "QUANTITY_REFURBED_UNS",
    (SELECT SUM (c3.trans_qty)
    FROM nwcg_billing_transaction c3
    WHERE RTRIM (c3.item_id)      = RTRIM (lpn.item_id)
    AND c3.incident_no            = nio.incident_no
    AND c3.incident_year          = nio.year
    AND lpn.disposition_code      = 'NRFI'
    AND c3.trans_type             = 'WO-REFURB'
    AND c3.disposition_code       = 'UNSRV-NWT'
    AND RTRIM (c3.transaction_no) = RTRIM (rh.receipt_no)
    ) "QUANTITY_REFURBED_UNS_NWT",
    (
    (SELECT AVG (c3.refurb_cost)
    FROM nwcg_master_work_order_line c3,
      nwcg_master_work_order c4
    WHERE RTRIM (c3.item_id)             = RTRIM (lpn.item_id)
    AND c4.incident_no                   = nio.incident_no
    AND c4.incident_year                 = nio.year
    AND lpn.disposition_code             = 'NRFI'
    AND RTRIM (c3.master_work_order_key) = RTRIM (c4.master_work_order_key)
    AND RTRIM (c4.master_work_order_no)  = RTRIM (rh.receipt_no)
    ) + NVL (
    (SELECT SUM (c3.trans_amount)
    FROM nwcg_billing_transaction c3,
      nwcg_master_work_order_line c4,
      nwcg_master_work_order c5
    WHERE RTRIM (c3.item_id)             = RTRIM (c4.item_id)
    AND c3.incident_no                   = nio.incident_no
    AND c3.incident_year                 = nio.year
    AND c3.trans_amount                  < 0
    AND c3.trans_type                    = 'WO-REFURB'
    AND RTRIM (c3.transaction_no)        = RTRIM (rh.receipt_no)
    AND c4.is_replaced_item              = 'Y'
    AND c4.status                        = 'Work Order Completed'
    AND RTRIM (c4.master_work_order_key) = RTRIM (c5.master_work_order_key)
    AND RTRIM (c5.master_work_order_no)  = RTRIM (rh.receipt_no)
    ), 0)) "REFURB_COST",
    nio.incident_no "INCIDENT NO",
    nio.incident_name,
    lpn.receipt_date "RETURN_DATE",
    rh.extn_return_comments,
    ' ' "INSPECTION_COMMENTS",
    nio.incident_other_acct_code,
    nio.incident_blm_acct_code,
    nio.incident_fs_acct_code,
    nio.override_code,
    lpn.unit_price "UNIT_PRICE",
    nio.year,
    rh.receivingnode_key "CACHE ID",
    d.company,
    d.address_line1 "RETURN_TO_ADDRESS1",
    d.address_line2 "RETURN_TO_ADDRESS2",
    d.city "RETURN_TO_CITY",
    d.state "RETURN_TO_STATE",
    d.zip_code "RETURN_TO_ZIP",
    nio.customer_name "SHIP_TO_COMPANY",
    f.address_line1 "SHIP_TO_ADDRESS1",
    f.address_line2 "SHIP_TO_ADDRESS2",
    f.city "SHIP_TO_CITY",
    f.state "SHIP_TO_STATE",
    f.zip_code "SHIP_TO_ZIP"
  FROM yfs_receipt_header rh,
    nwcg_lpn_return lpn,
    nwcg_incident_order nio,
    yfs_item yi,
    yfs_person_info d,
    yfs_organization e,
    yfs_person_info f
  WHERE TRIM (nio.incident_no)   = TRIM (lpn.incident_no)
  AND nio.year                   = lpn.incident_year
  AND TRIM (lpn.item_id)         = TRIM (yi.item_id)
  AND rh.receipt_header_key      = lpn.return_header_key
  AND RTRIM (lpn.cache_id)       = RTRIM (e.organization_code)
  AND e.corporate_address_key    = d.person_info_key
  AND nio.person_info_shipto_key = f.person_info_key
  AND rh.extn_is_return_receipt  = 'Y'
  AND yi.is_serial_tracked       = 'N'
  UNION ALL
  SELECT lpn.return_no "RECEIPT NO",
    lpn.item_id,
    yi.description "ITEM_DESCRIPTION",
    yi.uom,
    lpn.serial_no,
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    AND c1.serial_no           = lpn.serial_no
    ) "QUANTITY_RETURNED",
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    AND c1.serial_no           = lpn.serial_no
    AND c1.disposition_code    = 'RFI'
    ) "QUANTITY_RFI",
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    AND c1.serial_no           = lpn.serial_no
    AND c1.disposition_code    = 'NRFI'
    ) "QUANTITY_NRFI",
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    AND c1.serial_no           = lpn.serial_no
    AND c1.disposition_code    = 'UNSERVICE'
    ) "QUANTITY_UNSERVICED",
    (SELECT SUM (c1.quantity)
    FROM nwcg_lpn_return c1
    WHERE c1.return_header_key = lpn.return_header_key
    AND c1.return_no           = lpn.return_no
    AND c1.item_id             = lpn.item_id
    AND c1.serial_no           = lpn.serial_no
    AND c1.disposition_code    = 'UNSRV-NWT'
    ) "QUANTITY_UNSERVICED_NWT",
    (SELECT SUM (c4.quantity_completed)
    FROM yfs_work_order c3,
      yfs_work_order_acty_dtl c4
    WHERE RTRIM (c3.item_id)  = RTRIM (lpn.item_id)
    AND c3.extn_incident_no   = nio.incident_no
    AND c3.extn_incident_year = nio.year
    AND c3.extn_is_refurb     = 'N'
    AND c3.work_order_key     = c4.work_order_key
    AND c4.serial_no          = lpn.serial_no
    ) "QUANTITY_WORDERED",
    (SELECT SUM (c5.trans_qty)
    FROM nwcg_master_work_order_line c3,
      nwcg_billing_transaction c5
    WHERE RTRIM (c3.item_id)              = RTRIM (lpn.item_id)
    AND lpn.disposition_code              = 'NRFI'
    AND RTRIM (c3.primay_serial_no)       = RTRIM (lpn.serial_no)
    AND RTRIM (c5.item_id)                = RTRIM (lpn.item_id)
    AND c5.incident_no                    = nio.incident_no
    AND c5.incident_year                  = nio.year
    AND RTRIM (c5.transaction_header_key) = RTRIM (c3.master_work_order_key)
    AND RTRIM (c5.transaction_no)         = RTRIM (rh.receipt_no)
    AND RTRIM (c5.transaction_line_key)   = RTRIM (c3.master_work_order_line_key)
    AND c5.trans_type                     = 'WO-REFURB'
    AND c5.disposition_code              != 'UNSERVICE'
    AND c5.disposition_code              != 'UNSRV-NWT'
    ) "QUANTITY_REFURBED",
    (SELECT SUM (c5.trans_qty)
    FROM nwcg_master_work_order_line c3,
      nwcg_billing_transaction c5
    WHERE RTRIM (c3.item_id)              = RTRIM (lpn.item_id)
    AND lpn.disposition_code              = 'NRFI'
    AND RTRIM (c3.primay_serial_no)       = RTRIM (lpn.serial_no)
    AND RTRIM (c5.item_id)                = RTRIM (lpn.item_id)
    AND c5.incident_no                    = nio.incident_no
    AND c5.incident_year                  = nio.year
    AND RTRIM (c5.transaction_header_key) = RTRIM (c3.master_work_order_key)
    AND RTRIM (c5.transaction_no)         = RTRIM (rh.receipt_no)
    AND RTRIM (c5.transaction_line_key)   = RTRIM (c3.master_work_order_line_key)
    AND c5.trans_type                     = 'WO-REFURB'
    AND c5.disposition_code               = 'UNSERVICE'
    ) "QUANTITY_REFURBED_UNS",
    (SELECT SUM (c5.trans_qty)
    FROM nwcg_master_work_order_line c3,
      nwcg_billing_transaction c5
    WHERE RTRIM (c3.item_id)              = RTRIM (lpn.item_id)
    AND lpn.disposition_code              = 'NRFI'
    AND RTRIM (c3.primay_serial_no)       = RTRIM (lpn.serial_no)
    AND RTRIM (c5.item_id)                = RTRIM (lpn.item_id)
    AND c5.incident_no                    = nio.incident_no
    AND c5.incident_year                  = nio.year
    AND RTRIM (c5.transaction_header_key) = RTRIM (c3.master_work_order_key)
    AND RTRIM (c5.transaction_no)         = RTRIM (rh.receipt_no)
    AND RTRIM (c5.transaction_line_key)   = RTRIM (c3.master_work_order_line_key)
    AND c5.trans_type                     = 'WO-REFURB'
    AND c5.disposition_code               = 'UNSRV-NWT'
    ) "QUANTITY_REFURBED_UNS_NWT",
    (
    (SELECT AVG (c3.refurb_cost)
    FROM nwcg_master_work_order_line c3,
      nwcg_master_work_order c4
    WHERE RTRIM (c3.item_id)             = RTRIM (lpn.item_id)
    AND c4.incident_no                   = nio.incident_no
    AND c4.incident_year                 = nio.year
    AND lpn.disposition_code             = 'NRFI'
    AND RTRIM (c3.master_work_order_key) = RTRIM (c4.master_work_order_key)
    AND RTRIM (c4.master_work_order_no)  = RTRIM (rh.receipt_no)
    AND c3.primay_serial_no              = lpn.serial_no
    ) + NVL (
    (SELECT SUM (c3.trans_amount)
    FROM nwcg_billing_transaction c3,
      nwcg_master_work_order_line c4,
      nwcg_master_work_order c5
    WHERE RTRIM (c3.item_id)             = RTRIM (c4.item_id)
    AND c3.incident_no                   = nio.incident_no
    AND c3.incident_year                 = nio.year
    AND c3.trans_amount                  < 0
    AND c3.trans_type                    = 'WO-REFURB'
    AND RTRIM (c3.transaction_no)        = RTRIM (rh.receipt_no)
    AND c4.is_replaced_item              = 'Y'
    AND c4.status                        = 'Work Order Completed'
    AND RTRIM (c4.master_work_order_key) = RTRIM (c5.master_work_order_key)
    AND RTRIM (c5.master_work_order_no)  = RTRIM (rh.receipt_no)
    ), 0)) "REFURB_COST",
    nio.incident_no "INCIDENT NO",
    nio.incident_name,
    lpn.receipt_date "RETURN_DATE",
    rh.extn_return_comments,
    ' ' "INSPECTION_COMMENTS",
    nio.incident_other_acct_code,
    nio.incident_blm_acct_code,
    nio.incident_fs_acct_code,
    nio.override_code,
    lpn.unit_price "UNIT_PRICE",
    nio.year,
    rh.receivingnode_key "CACHE ID",
    d.company,
    d.address_line1 "RETURN_TO_ADDRESS1",
    d.address_line2 "RETURN_TO_ADDRESS2",
    d.city "RETURN_TO_CITY",
    d.state "RETURN_TO_STATE",
    d.zip_code "RETURN_TO_ZIP",
    nio.customer_name "SHIP_TO_COMPANY",
    f.address_line1 "SHIP_TO_ADDRESS1",
    f.address_line2 "SHIP_TO_ADDRESS2",
    f.city "SHIP_TO_CITY",
    f.state "SHIP_TO_STATE",
    f.zip_code "SHIP_TO_ZIP"
  FROM yfs_receipt_header rh,
    nwcg_lpn_return lpn,
    nwcg_incident_order nio,
    yfs_item yi,
    yfs_person_info d,
    yfs_organization e,
    yfs_person_info f
  WHERE TRIM (nio.incident_no)   = TRIM (lpn.incident_no)
  AND nio.year                   = lpn.incident_year
  AND TRIM (lpn.item_id)         = TRIM (yi.item_id)
  AND rh.receipt_header_key      = lpn.return_header_key
  AND RTRIM (lpn.cache_id)       = RTRIM (e.organization_code)
  AND e.corporate_address_key    = d.person_info_key
  AND nio.person_info_shipto_key = f.person_info_key
  AND RH.EXTN_IS_RETURN_RECEIPT  = 'Y'
  AND yi.is_serial_tracked       = 'Y';