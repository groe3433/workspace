SELECT
    'ORDER_NO,BRAND_CODE,ORDER_TYPE,ORDERDATE,STATUS,RELEASE_NO,SITE,ITEM_ID,LINE_NUMBER,UNITS,RELEASE_MESSAGEnewLine'
FROM
    dual;
SELECT
    NVL(Order_No, '')||','||
    NVL(Brand_Code, '')||','||
    NVL(Order_type, '')||','||
    NVL(Orderdate, '')||','||
    NVL(status, '')||','||
    NVL((TRIM(TRAILING ' ' FROM release_no)), '')||','||
    NVL(site, '')||','||
    NVL(item_id, '')||','||
    NVL(prime_line_no, '')||','||
    NVL(status_quantity, '')||','||
    NVL(Release_message, '')||'newLine'  
FROM
    OMCTMETA.exel_order_extract_step4; 