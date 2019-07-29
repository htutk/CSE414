-- For each origin city, find the percentage of departing flights shorter than 3 hours.
-- -- For this question, treat flights with NULL actual_time values as longer than 3 hours. (15 points)
-- -- Name the output columns origin_city and percentage
-- -- Order by percentage value, ascending. Be careful to handle cities without any flights shorter than 3 hours.
-- -- We will accept either 0 or NULL as the result for those cities.
-- -- Report percentages as percentages not decimals (e.g., report 75.25 rather than 0.7525).
-- -- [Output relation cardinality: 327]

--[2019-04-21 19:51:44] 327 rows retrieved starting from 1 in 13 s 577 ms (execution: 13 s 561 ms, fetching: 16 ms)

-- NULL values will be ordered first (thus the first 109 rows
-- are omitted)

-- origin_city      percentage
----------------------------------
-- Guam TT	0
-- Pago Pago TT	0
-- Aguadilla PR	29.43
-- Anchorage AK	32.15
-- San Juan PR	33.89
-- Charlotte Amalie VI	40
-- Ponce PR	41.94
-- Fairbanks AK	50.69
-- Kahului HI	53.66
-- Honolulu HI	54.91
-- San Francisco CA	56.31
-- Los Angeles CA	56.6
-- Seattle WA	57.76
-- Long Beach CA	62.45
-- Kona HI	63.28
-- New York NY	63.48
-- Las Vegas NV	65.16
-- Christiansted VI	65.33
-- Newark NJ	67.14
-- Worcester MA	67.74


SELECT G1.origin_city AS origin_city,
       ROUND((1 - (G2.c / G1.c)) * 100, 2) AS percentage
FROM (
SELECT F1.origin_city,
  CAST(COUNT(*) AS FLOAT) AS c  --c for count
FROM Flights AS F1
GROUP BY F1.origin_city) G1

LEFT JOIN

(SELECT F2.origin_city,
  CAST(COUNT(*) AS FLOAT) AS c
FROM Flights AS F2
WHERE F2.actual_time IS NULL
  OR F2.actual_time >= (3 * 60)
GROUP BY F2.origin_city) G2

ON G1.origin_city = G2.origin_city
ORDER BY percentage;