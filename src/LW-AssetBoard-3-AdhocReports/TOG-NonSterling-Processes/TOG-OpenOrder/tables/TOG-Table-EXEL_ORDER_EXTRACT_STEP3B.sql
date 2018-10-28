CREATE TABLE OMCTMETA.EXEL_ORDER_EXTRACT_STEP3B
  (
    "PRIME_LINE_NO"           DECIMAL(5,0)    NOT NULL,
    "ORDER_HEADER_KEY"        CHAR(24)        NOT NULL,
    "ITEM_ID"                 CHAR(40)        NOT NULL,
    "STATUS"                  CHAR(15),
    "EXTN_LTOOS_FLAG"         CHAR(21)        NOT NULL,
    "STATUS_QUANTITY"         DECIMAL(14,4),
    "ORDER_RELEASE_KEY"       CHAR(24),
    "ORDER_LINE_SCHEDULE_KEY" CHAR(24)
  )