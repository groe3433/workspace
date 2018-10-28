CREATE OR REPLACE FORCE VIEW EXTN_NWCG_ISSUE_REPORT_VW ("ORDER_HEADER_KEY", "ENTERPRISE_KEY", "ORDER_NO", "DOCUMENT_TYPE", "BILL_TO_KEY", "BILL_TO_ID", "SHIP_TO_KEY", "CACHE ID", "ORDER_DATE", "TOTAL_AMOUNT", "EXTN_INCIDENT_NO", "EXTN_INCIDENT_YEAR", "EXTN_INCIDENT_NAME", "EXTN_FS_ACCT_CODE", "EXTN_BLM_ACCT_CODE", "EXTN_OTHER_ACCT_CODE", "EXTN_OVERRIDE_CODE", "EXTN_REQ_DELIVERY_DATE", "EXTN_NAV_INFO", "EXTN_SHIPPING_INSTRUCTIONS", "EXTN_SHIPPING_INFO", "EXTN_WILL_PICK_UP_NAME", "EXTN_WILL_PICK_UP_INFO", "EXTN_SHIP_INSTR_CITY", "EXTN_SHIP_INSTR_STATE", "EXTN_SHIPPING_CONT_NAME", "EXTN_SHIPPING_CONT_PHONE", "ITEM_ID", "ITEM_DESCRIPTION", "UOM", "ORDERED_QTY", "EXTN_REQUEST_NO", "EXTN_ORIG_REQ_QTY", "EXTN_QTY_RFI", "EXTN_UTF_QTY", "EXTN_FWD_QTY", "EXTN_BACKORDERED_QTY", "FIRST_NAME", "MIDDLE_NAME", "LAST_NAME", "ADDRESS_LINE1", "ADDRESS_LINE2", "ADDRESS_LINE3", "ADDRESS_LINE4", "ADDRESS_LINE5", "ADDRESS_LINE6", "CITY", "STATE", "COUNTRY", "DAY_FAX_NO", "DAY_PHONE", "ZIP_CODE",
  "BILL_TO_CUSTOMER", "SHIP_TO_FIRST_NAME", "SHIP_TO_MIDDLE_NAME", "SHIP_TO_LAST_NAME", "SHIP_TO_ADDRESS1", "SHIP_TO_ADDRESS2", "SHIP_TO_ADDRESS3", "SHIP_TO_ADDRESS4", "SHIP_TO_ADDRESS5", "SHIP_TO_ADDRESS6", "SHIP_TO_CITY", "SHIP_TO_STATE", "SHIP_TO_COUNTRY", "SHIP_TO_DAY_FAX_NO", "SHIP_TO_DAY_PHONE", "SHIP_TO_ZIP_CODE", "SHIP_TO_CUSTOMER", "CUSTOMER_NAME", "ORDER_COMMENTS", "ORDER_LINE_COMMENTS")
AS
  SELECT a.order_header_key,
    a.enterprise_key,
    a.order_no,
    a.document_type,
    a.bill_to_key,
    a.bill_to_id,
    a.ship_to_key,
    a.ship_node "CACHE ID",
    a.order_date,
    a.total_amount,
    a.extn_incident_no,
    a.extn_incident_year,
    a.extn_incident_name,
    a.extn_fs_acct_code,
    a.extn_blm_acct_code,
    a.extn_other_acct_code,
    a.extn_override_code,
    a.extn_req_delivery_date,
    (SELECT code_short_description
    FROM yfs_common_code
    WHERE code_type = 'NWCG_SHIP_MTHDS'
    AND code_value  = a.extn_nav_info
    ) "EXTN_NAV_INFO",
    a.extn_shipping_instructions,
    a.extn_shipping_info,
    a.extn_will_pick_up_name,
    a.extn_will_pick_up_info,
    a.extn_ship_instr_city,
    a.extn_ship_instr_state,
    a.extn_shipping_cont_name,
    a.extn_shipping_cont_phone,
    b.item_id,
    b.item_description,
    b.uom,
    b.ordered_qty,
    b.extn_request_no,
    b.extn_orig_req_qty,
    b.extn_qty_rfi,
    b.extn_utf_qty,
    b.extn_fwd_qty,
    b.extn_backordered_qty,
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
    e.http_url "BILL_TO_CUSTOMER",
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
    f.http_url "SHIP_TO_CUSTOMER",
    (SELECT extn_customer_name
    FROM yfs_customer
    WHERE trim(customer_id) = trim(a.bill_to_id)
    ) "CUSTOMER_NAME",
    (SELECT comments
    FROM extn_nwcg_yfs_comments_vw
    WHERE table_key = a.order_header_key
    AND table_name  = 'yfs_order_header'
    ) "ORDER_COMMENTS",
    (SELECT n1.note_text
    FROM yfs_notes n1
    WHERE n1.table_key = b.order_line_key
    AND n1.table_name  = 'yfs_order_line'
    GROUP BY n1.note_text,
      n1.sequence_no
    HAVING n1.sequence_no =
      (SELECT MAX(n2.sequence_no)
      FROM yfs_notes n2
      WHERE n2.table_key = b.order_line_key
      AND n2.table_name  = 'yfs_order_line'
      )
    ) "ORDER_LINE_COMMENTS"
  FROM yfs_order_header a,
    yfs_order_line b,
    yfs_person_info e,
    yfs_person_info f
  WHERE a.order_header_key = b.order_header_key
  AND a.bill_to_key        = e.person_info_key
  AND a.ship_to_key        = f.person_info_key;