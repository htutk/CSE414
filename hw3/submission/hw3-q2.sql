-- Find all origin cities that only serve flights shorter than 3 hours.
-- You can assume that flights with NULL actual_time are not 3 hours or more. (15 points)
-- Name the output column city and sort them. List each city only once in the result.
-- [Output relation cardinality: 109]
--

-- [2019-04-21 18:33:51] 109 rows retrieved starting from 1 in 12 s 570 ms (execution: 12 s 561 ms, fetching: 9 ms)

-- city
--------------------------
-- Aberdeen SD
-- Abilene TX
-- Alpena MI
-- Ashland WV
-- Augusta GA
-- Barrow AK
-- Beaumont/Port Arthur TX
-- Bemidji MN
-- Bethel AK
-- Binghamton NY
-- Brainerd MN
-- Bristol/Johnson City/Kingsport TN
-- Butte MT
-- Carlsbad CA
-- Casper WY
-- Cedar City UT
-- Chico CA
-- College Station/Bryan TX
-- Columbia MO
-- Columbus GA

SELECT G1.origin_city AS city
FROM (
SELECT DISTINCT F1.origin_city
FROM Flights F1
) G1

LEFT JOIN

(SELECT DISTINCT F2.origin_city
FROM Flights F2
WHERE F2.actual_time IS NULL
OR F2.actual_time >= (3 * 60)
) G2

ON G1.origin_city = G2.origin_city

WHERE G2.origin_city IS NULL
ORDER BY G1.origin_city;