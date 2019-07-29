-- List all cities that cannot be reached from Seattle through a direct flight nor with one stop
-- (i.e., with any two flights that go through an intermediate city). Warning: this query might take a while to execute.
-- We will learn about how to speed this up in lecture. (15 points)
-- Name the output column city. Order the output ascending by city.
-- (You can assume all cities to be the collection of all origin_city or all dest_city)
-- (Note: Do not worry if this query takes a while to execute. We are mostly concerned with the results)
-- [Output relation cardinality: 3 or 4, depending on what you consider to be the set of all cities]


--[2019-04-21 21:53:37] 3 rows retrieved starting from 1 in 30 s 849 ms

-- city
------------------------
-- Devils Lake ND
-- Hattiesburg/Laurel MS
-- St. Augustine FL


SELECT G1.dest_city AS city
FROM (
SELECT DISTINCT F1.dest_city
FROM Flights F1) G1

LEFT JOIN

((SELECT DISTINCT F2.dest_city
FROM Flights F2
WHERE F2.origin_city = 'Seattle WA')

UNION

(SELECT DISTINCT F4.dest_city
FROM Flights F3, Flights F4
WHERE F3.origin_city = 'Seattle Wa'
AND F3.dest_city = F4.origin_city
AND F4.dest_city <> 'Seattle WA')) G2

ON G1.dest_city = G2.dest_city

WHERE G2.dest_city IS NULL
AND G1.dest_city <> 'Seattle WA'
ORDER BY G1.dest_city;