CREATE OR REPLACE FORCE VIEW EXTN_NWCG_SUPPLIER_VW ("SUPPLIER ID", "SUPPLIER NAME", "ADDRESS_LINE1", "ADDRESS_LINE2", "ADDRESS_LINE3", "ADDRESS_LINE4", "ADDRESS_LINE5", "CITY", "STATE", "ZIP_CODE", "COUNTRY", "DAY_PHONE", "DAY_FAX_NO", "HTTP_URL")
AS
  SELECT a.organization_code "SUPPLIER ID",
    a.organization_name "SUPPLIER NAME",
    b.address_line1,
    b.address_line2,
    b.address_line3,
    b.address_line4,
    b.address_line5,
    b.city,
    b.state,
    b.zip_code,
    b.country,
    b.day_phone,
    b.day_fax_no,
    b.http_url
  FROM yfs_organization a,
    yfs_person_info b
  WHERE a.is_seller         = 'Y'
  AND a.contact_address_key = b.person_info_key;