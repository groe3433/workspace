SELECT
  '"","OMS Open Order Report","Date: '
  || TO_CHAR( sysdate, 'yyyy-mm-dd hh24:mi:ss' )
  || '"'
  || 'newLine'
FROM
  dual;
SELECT
  ''
  || 'newLine'
FROM
  dual;
SELECT
  '"ORDER_TYPE","BRAND_CODE","ORDERDATE","STATUS","RELEASE MESSAGE","SITE","LTOOS","TOTAL QUANTITY","ORDER COUNT"' 
  || 'newLine'
FROM
  dual;
SELECT
  '' 
  || 'newLine'
FROM
  dual;
SELECT
  '"'
  || trim(Order_type)
  || '","'
  || trim(Brand_Code)
  || '","'
  || OrderDate
  || '","=""'
  || trim(status)
  || '""","'
  || trim(Release_Message)
  || '","'
  || Site
  || '","'
  || trim(LTOOS)
  || '",'
  || SUM( status_quantity )
  || ','
  || COUNT( DISTINCT order_no ) 
  || 'newLine'
FROM
  OMCTMETA.exel_order_extract_step4
GROUP BY
  Order_type,
  Brand_Code,
  OrderDate,
  status,
  Release_Message,
  Site,
  LTOOS;
SELECT
  '"<-----------------------------------End of Report----------------------------------->"'
FROM
  dual;