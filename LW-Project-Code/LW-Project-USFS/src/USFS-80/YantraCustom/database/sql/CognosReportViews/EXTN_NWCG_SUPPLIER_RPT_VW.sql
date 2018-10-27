CREATE OR REPLACE FORCE VIEW EXTN_NWCG_SUPPLIER_RPT_VW ("SUPPLIER CODE", "ORGANIZATION_NAME", "ITEM ID", "SUPPLIER_ITEM_DESCRIPTION", "GLOBAL_ITEM_ID", "SUPPLIER_STANDARD_PACK", "SUPPLIER_PART_NO", "PREFERRED", "UNIT_COST", "UNIT_OF_MEASURE", "FIRST_NAME", "MIDDLE_NAME", "LAST_NAME", "ADDRESS_LINE1", "ADDRESS_LINE2", "ADDRESS_LINE3", "ADDRESS_LINE4", "ADDRESS_LINE5", "ADDRESS_LINE6", "CITY", "COUNTRY", "DAY_FAX_NO", "DAY_PHONE", "ZIP_CODE", "ORDERED_QTY", "PO_DATE", "DOCUMENT_TYPE")
AS
  SELECT a.supplier_id "SUPPLIER CODE",
    e.organization_name,
    a.item_id "ITEM ID",
    b.description "SUPPLIER_ITEM_DESCRIPTION",
    b.global_item_id,
    a.supplier_standard_pack,
    a.supplier_part_no,
    a.preferred,
    a.unit_cost,
    a.unit_of_measure,
    d.first_name,
    d.middle_name,
    d.last_name,
    d.address_line1,
    d.address_line2,
    d.address_line3,
    d.address_line4,
    d.address_line5,
    d.address_line6,
    d.city,
    d.country,
    d.day_fax_no,
    d.day_phone,
    d.zip_code,
    (SELECT ordered_qty
    FROM yfs_order_line yol
    WHERE yol.supplier_code = a.supplier_id
    AND yol.item_id         = a.item_id
    ) "ORDERED_QTY",
    (SELECT to_date(TO_CHAR(yol.modifyts,'DD-MON-YYYY'))
    FROM yfs_order_line yol
    WHERE yol.supplier_code = a.supplier_id
    AND yol.item_id         = a.item_id
    ) "PO_DATE",
    (SELECT yoh.document_type
    FROM yfs_order_header yoh,
      yfs_order_line yol
    WHERE yol.supplier_code  = a.supplier_id
    AND yol.item_id          = a.item_id
    AND yoh.order_header_key = yol.order_header_key
    ) "DOCUMENT_TYPE"
  FROM nwcg_supplier_item a,
    yfs_item b,
    yfs_person_info d,
    yfs_organization e
  WHERE a.supplier_id       = e.organization_code
  AND a.item_id             = b.item_id
  AND b.status              = '3000'
  AND e.contact_address_key = d.person_info_key;