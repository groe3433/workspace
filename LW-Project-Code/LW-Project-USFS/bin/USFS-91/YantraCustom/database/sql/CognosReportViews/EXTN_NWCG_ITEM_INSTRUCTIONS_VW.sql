CREATE OR REPLACE FORCE VIEW EXTN_NWCG_ITEM_INSTRUCTIONS_VW ("ITEM_KEY", "INSTRUCTION_TYPE", "INSTRUCTIONS")
AS
  SELECT item_key,
    instruction_type,
    ltrim(MAX(sys_connect_by_path(instruction_text,'~')),'~') AS instructions
  FROM
    (SELECT item_key,
      instruction_type,
      instruction_text,
      row_number() over (partition BY item_key
      ||instruction_type order by instruction_text) AS curr,
      row_number() over (partition BY item_key
      ||instruction_type order by instruction_text) -1 AS prev
    FROM yfs_item_instruction
    ORDER BY item_key,
      instruction_type
    )
  GROUP BY item_key,
    instruction_type
    CONNECT BY prev = prior curr
  AND item_key      = prior item_key
    START WITH curr = 1;