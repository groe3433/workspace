CREATE OR REPLACE FORCE VIEW EXTN_NWCG_PO_BY_ITEM_RPT_VW ("ENTERPRISE_KEY", "DOCUMENT_TYPE", "PURCHASE ORDER NO", "ORDER_NAME", "EXTN_REQUISITION_NO", "ORDER_DATE", "SUPPLIER CODE", "ORGANIZATION_NAME", "PO_DATE", "CARRIER_SERVICE_CODE", "CARRIER_SERVICE", "REQ_DELIVERY_DATE", "VENDOR_ID", "FIRST_NAME", "MIDDLE_NAME", "LAST_NAME", "ADDRESS_LINE1", "ADDRESS_LINE2", "ADDRESS_LINE3", "ADDRESS_LINE4", "ADDRESS_LINE5", "ADDRESS_LINE6", "CITY", "STATE", "COUNTRY", "DAY_FAX_NO", "DAY_PHONE", "ZIP_CODE", "SHIP_TO_FIRST_NAME", "SHIP_TO_MIDDLE_NAME", "SHIP_TO_LAST_NAME", "SHIP_TO_ADDRESS1", "SHIP_TO_ADDRESS2", "SHIP_TO_ADDRESS3", "SHIP_TO_ADDRESS4", "SHIP_TO_ADDRESS5", "SHIP_TO_ADDRESS6", "SHIP_TO_CITY", "SHIP_TO_STATE", "SHIP_TO_COUNTRY", "SHIP_TO_DAY_FAX_NO", "SHIP_TO_DAY_PHONE", "SHIP_TO_ZIP_CODE", "CACHE ID", "PRIME_LINE_NO", "ITEM ID", "ITEM_DESCRIPTION", "UOM", "UNIT_COST", "GLOBAL_ITEM_ID", "EXTN_DOCUMENT_IDENTIFIER_CODE", "EXTN_ROUTING_IDENTIFIER_CODE",
  "EXTN_MEDIA_AND_STATUS_CODE", "EXTN_ACTIVITY_ADDRESS_CODE", "EXTN_JDATE", "EXTN_SUPPLEMENTARY_ADDRESS", "EXTN_SIGNAL_CODE", "EXTN_FUND_CODE", "EXTN_DIST_CODE", "EXTN_PRIORITY_DESIGNATOR_CODE", "EXTN_REQUIRED_DELIVERY_DATE", "EXTN_ADVICE_CODE", "EXTN_CNAME", "EXTN_CNO", "EXTN_PROJ_CODE", "EXTN_DRN_CODE", "EXTN_OVERRIDE_CODE", "EXTN_PO_SHIP_ACCT", "EXTN_FS_ACCT_CODE", "EXTN_CACHE_SHIP_ACCT_CODE", "EXTN_BLM_ACCT_CODE", "EXTN_PRIORITY_CODE", "EXTN_CACHE_RECEIVE_ACCT_CODE", "EXTN_CUST_ACCT_CODE", "EXTN_INCIDENT_NO", "EXTN_INCIDENT_NAME", "EXTN_PRICE", "EXTN_SA_OVERRIDE_CODE", "EXTN_RA_OVERRIDE_CODE", "EXTN_ORDER_PURPOSE", "EXTN_TRANSPORTATION_METHOD", "EXTN_GSA_NO", "EXTN_GSA_QTY", "SUPPLIER_STD_PACK", "SUPPLIER_UOM", "SUPPLIER_PART_NO", "ORDER_STATUS", "PO_QTY", "RECD_QTY", "ORDER_INSTRUCTIONS", "ORDER_COMMENTS", "ORDER_LINE_COMMENTS")
AS
  SELECT DISTINCT a.enterprise_key,
    a.document_type,
    a.order_no "PURCHASE ORDER NO",
    a.order_name,
    a.extn_requisition_no,
    a.order_date,
    a.seller_organization_code "SUPPLIER CODE",
    c.organization_name,
    to_date(TO_CHAR(a.createts,'DD-MON-YYYY')) "PO_DATE",
    a.carrier_service_code,
    (SELECT s1.scac_and_service_desc
    FROM yfs_scac_and_service s1,
      yfs_carrier_service s2
    WHERE trim(a.scac)          = trim(s1.scac_key)
    AND s2.carrier_service_code = a.carrier_service_code
    AND s1.carrier_service_key  = s2.carrier_service_key
    ) "CARRIER_SERVICE",
    a.extn_req_delivery_date "REQ_DELIVERY_DATE",
    a.vendor_id,
    e.first_name,
    e.middle_name,
    e.last_name,
    e.address_line1,
    e.address_line2,
    e.address_line3,
    e.address_line4,
    e.address_line5,
    e.address_line6,
    e.city,
    e.state,
    e.country,
    e.day_fax_no,
    e.day_phone,
    e.zip_code,
    f.first_name "SHIP_TO_FIRST_NAME",
    f.middle_name "SHIP_TO_MIDDLE_NAME",
    f.last_name "SHIP_TO_LAST_NAME",
    f.address_line1 "SHIP_TO_ADDRESS1",
    f.address_line2 "SHIP_TO_ADDRESS2",
    f.address_line3 "SHIP_TO_ADDRESS3",
    f.address_line4 "SHIP_TO_ADDRESS4",
    f.address_line5 "SHIP_TO_ADDRESS5",
    f.address_line6 "SHIP_TO_ADDRESS6",
    f.city "SHIP_TO_CITY",
    f.state "SHIP_TO_STATE",
    f.country "SHIP_TO_COUNTRY",
    f.day_fax_no "SHIP_TO_DAY_FAX_NO",
    f.day_phone "SHIP_TO_DAY_PHONE",
    f.zip_code "SHIP_TO_ZIP_CODE",
    a.receiving_node "CACHE ID",
    b.prime_line_no,
    b.item_id "ITEM ID",
    d.short_description "ITEM_DESCRIPTION",
    b.uom,
    b.unit_price,
    d.global_item_id,
    a.extn_document_identifier_code,
    a.extn_routing_identifier_code,
    a.extn_media_and_status_code,
    a.extn_activity_address_code,
    a.extn_jdate,
    a.extn_supplementary_address,
    a.extn_signal_code,
    a.extn_fund_code,
    a.extn_dist_code,
    a.extn_priority_designator_code,
    a.extn_required_delivery_date,
    a.extn_advice_code,
    a.extn_cname,
    a.extn_cno,
    a.extn_proj_code,
    a.extn_drn_code,
    a.extn_override_code,
    a.extn_po_ship_acct,
    a.extn_fs_acct_code,
    a.extn_cache_ship_acct_code,
    a.extn_blm_acct_code,
    a.extn_priority_code,
    a.extn_cache_receive_acct_code,
    a.extn_cust_acct_code,
    a.extn_incident_no,
    a.extn_incident_name,
    a.extn_other_amount "EXTN_PRICE",
    a.extn_sa_override_code,
    a.extn_ra_override_code,
    a.extn_order_purpose,
    a.extn_transportation_method,
    b.extn_gsa_no,
    b.extn_gsa_qty,
    (SELECT DISTINCT supplier_standard_pack
    FROM nwcg_supplier_item
    WHERE item_id   = b.item_id
    AND supplier_id = a.seller_organization_code
    ) "SUPPLIER_STD_PACK",
    (SELECT DISTINCT supplier_uom
    FROM nwcg_supplier_item
    WHERE item_id   = b.item_id
    AND supplier_id = a.seller_organization_code
    ) "SUPPLIER_UOM",
    (SELECT DISTINCT supplier_part_no
    FROM nwcg_supplier_item
    WHERE item_id   = b.item_id
    AND supplier_id = a.seller_organization_code
    ) "SUPPLIER_PART_NO",
    (SELECT MAX(status)
    FROM yfs_order_release_status
    WHERE order_header_key = a.order_header_key
    ) "ORDER_STATUS",
    (SELECT SUM(ordered_qty)
    FROM yfs_order_line yl
    WHERE yl.order_header_key = a.order_header_key
    AND yl.item_id            = b.item_id
    AND yl.order_line_key     = b.order_line_key
    ) "PO_QTY",
    (SELECT SUM(received_quantity) + SUM(tran_discrepancy_qty*-1)
    FROM yfs_order_line yl
    WHERE yl.order_header_key = a.order_header_key
    AND yl.item_id            = b.item_id
    AND yl.order_line_key     = b.order_line_key
    ) "RECD_QTY",
    (SELECT instructions
    FROM extn_nwcg_yfs_instructions_vw
    WHERE reference_key = a.order_header_key
    AND table_name      = 'YFS_ORDER_HEADER'
    ) "ORDER_INSTRUCTIONS",
    (SELECT comments
    FROM extn_nwcg_yfs_comments_vw
    WHERE table_key = a.order_header_key
    AND table_name  = 'YFS_ORDER_HEADER'
    ) "ORDER_COMMENTS",
    (SELECT n1.note_text
    FROM yfs_notes n1
    WHERE n1.table_key = b.order_line_key
    AND n1.table_name  = 'YFS_ORDER_LINE'
    GROUP BY n1.note_text,
      n1.sequence_no
    HAVING n1.sequence_no =
      (SELECT MAX(n2.sequence_no)
      FROM yfs_notes n2
      WHERE n2.table_key = b.order_line_key
      AND n2.table_name  = 'YFS_ORDER_LINE'
      )
    ) "ORDER_LINE_COMMENTS"
  FROM yfs_order_header a,
    yfs_order_line b,
    yfs_organization c,
    yfs_item d,
    yfs_person_info e,
    yfs_person_info f
  WHERE a.order_header_key       = b.order_header_key
  AND a.seller_organization_code = c.organization_code
  AND b.item_id                  = d.item_id
  AND c.contact_address_key      = e.person_info_key
  AND a.ship_to_key              = f.person_info_key
  AND a.document_type            = '0005';