CREATE OR REPLACE FORCE VIEW EXTN_NWCG_YFS_INSTRUCTIONS_VW ("TABLE_NAME", "REFERENCE_KEY", "INSTRUCTIONS")
AS
  SELECT table_name,
    reference_key,
    ltrim(MAX(sys_connect_by_path(instruction_text,'^')),'^') AS instructions
  FROM
    (SELECT table_name,
      reference_key,
      instruction_text,
      row_number() over (partition BY reference_key order by instruction_text)    AS curr,
      row_number() over (partition BY reference_key order by instruction_text) -1 AS prev
    FROM yfs_instruction_detail
    )
  GROUP BY table_name,
    reference_key
    CONNECT BY prev = prior curr
  AND reference_key = prior reference_key
    START WITH curr = 1;