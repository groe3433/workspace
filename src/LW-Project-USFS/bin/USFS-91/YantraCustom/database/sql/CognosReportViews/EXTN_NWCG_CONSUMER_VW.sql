CREATE OR REPLACE FORCE VIEW EXTN_NWCG_CONSUMER_VW ("CUSTOMER_ID", "EXTN_CUSTOMER_NAME", "EXTN_CUSTOMER_TYPE", "CITY", "STATE", "ZIP_CODE", "COUNTRY", "DAY_PHONE", "DAY_FAX_NO", "DEPARTMENT", "ADDRESS")
AS
  SELECT a.customer_id,
    a.extn_customer_name,
    a.extn_customer_type,
    b.city,
    b.state,
    b.zip_code,
    b.country,
    b.day_phone,
    b.day_fax_no,
    b.department,
    b.address_line1
    || ' '
    || b.address_line2
    || ' '
    || b.address_line3 "ADDRESS"
  FROM YFS_CUSTOMER a,
    YFS_PERSON_INFO b,
    YFS_CONSUMER c
  WHERE a.customer_key  = c.customer_key
  AND b.person_info_key = c.billing_address_key;