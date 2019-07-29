-- List all cities that cannot be reached from Seattle though a direct flight but can be reached with
-- one stop (i.e., with any two flights that go through an intermediate city).
-- Do not include Seattle as one of these destinations (even though you could get back with two flights).
-- (15 points)
-- Name the output column city. Order the output ascending by city.
-- [Output relation cardinality: 256]

-- [2019-04-21 20:47:38] 256 rows retrieved starting from 1 in 22 s 356 ms (execution: 22 s 310 ms, fetching: 46 ms)

-- city
-----------
-- Aberdeen SD
-- Abilene TX
-- Adak Island AK
-- Aguadilla PR
-- Akron OH
-- Albany GA
-- Albany NY
-- Alexandria LA
-- Allentown/Bethlehem/Easton PA
-- Alpena MI
-- Amarillo TX
-- Appleton WI
-- Arcata/Eureka CA
-- Asheville NC
-- Ashland WV
-- Aspen CO
-- Atlantic City NJ
-- Augusta GA
-- Bakersfield CA
-- Bangor ME


SELECT G1.dest_city as city
FROM (
SELECT DISTINCT F2.dest_city
FROM Flights F1, Flights F2
WHERE F1.dest_city = F2.origin_city
AND F1.origin_city = 'Seattle WA'
AND F2.dest_city <> 'Seattle WA'
) G1

LEFT JOIN (

SELECT DISTINCT F3.dest_city
FROM Flights F3
WHERE F3.origin_city = 'Seattle WA'
) G2

ON G1.dest_city = G2.dest_city

WHERE G2.dest_city IS NULL
ORDER BY G1.dest_city;