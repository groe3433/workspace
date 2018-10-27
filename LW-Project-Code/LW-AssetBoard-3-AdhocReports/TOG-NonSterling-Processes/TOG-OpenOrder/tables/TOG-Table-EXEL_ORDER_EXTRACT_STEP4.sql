CREATE TABLE OMCTMETA.EXEL_ORDER_EXTRACT_STEP4
  (
    "ORDER_NO"        VARCHAR(40)   NOT NULL,
    "BRAND_CODE"      VARCHAR(24),
    "ORDER_TYPE"      VARCHAR(20)   NOT NULL,
    "ORDERDATE"       VARCHAR(10),
    "ITEM_ID"         CHAR(40)      NOT NULL,
    "PRIME_LINE_NO"   DECIMAL(5,0)  NOT NULL,
    "STATUS"          CHAR(15),
    "RELEASE_NO"      DECIMAL(5,0),
    "RELEASE_MESSAGE" VARCHAR(17),
    "LTOOS"           VARCHAR(3),
    "SITE"            VARCHAR(24),
    "STATUS_QUANTITY" DECIMAL(14,4)
  )