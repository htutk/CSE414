-- For each origin city, find the destination city (or cities) with the longest direct flight.
-- By direct flight, we mean a flight with no intermediate stops. Judge the longest flight in
-- time, not distance. (15 points)
-- Name the output columns origin_city, dest_city,
-- and time representing the the flight time between them.
-- Do not include duplicates of the same origin/destination city pair.
-- Order the result by origin_city and then dest_city (ascending, i.e. alphabetically).
-- [Output relation cardinality: 334 rows]

-- [2019-04-21 23:11:41] 334 rows retrieved starting from 1 in 15 s 570 ms (execution: 15 s 540 ms, fetching: 30 ms)

-- origin_city      dest_city       time
-- --------------------------------------------------
-- Aberdeen SD	Minneapolis MN	106
-- Abilene TX	Dallas/Fort Worth TX	111
-- Adak Island AK	Anchorage AK	471
-- Aguadilla PR	New York NY	368
-- Akron OH	Atlanta GA	408
-- Albany GA	Atlanta GA	243
-- Albany NY	Atlanta GA	390
-- Albuquerque NM	Houston TX	492
-- Alexandria LA	Atlanta GA	391
-- Allentown/Bethlehem/Easton PA	Atlanta GA	456
-- Alpena MI	Detroit MI	80
-- Amarillo TX	Houston TX	390
-- Anchorage AK	Barrow AK	490
-- Appleton WI	Atlanta GA	405
-- Arcata/Eureka CA	San Francisco CA	476
-- Asheville NC	Chicago IL	279
-- Ashland WV	Cincinnati OH	84
-- Aspen CO	Los Angeles CA	304
-- Atlanta GA	Honolulu HI	649
-- Atlantic City NJ	Fort Lauderdale FL	212


SELECT G1.origin_city, G2.dest_city, MAX(G1.time) AS time
FROM
(SELECT F1.origin_city, MAX(F1.actual_time) AS time
FROM Flights F1
GROUP BY F1.origin_city) G1

JOIN

(SELECT *
FROM Flights F2) G2

ON G1.origin_city = G2.origin_city
AND G1.time = G2.actual_time

GROUP BY G1.origin_city, G2.dest_city
ORDER BY G1.origin_city, G2.dest_city;


