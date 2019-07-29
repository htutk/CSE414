-- (6 rows produced)
.open hw2.db

-- (10 points) Find all airlines that had more than 0.5 percent of their flights out of Seattle be canceled.
-- Return the name of the airline and the percentage of canceled flight out of Seattle.
-- Order the results by the percentage of canceled flights in ascending order.
-- Name the output columns name and percent, in that order.
-- [Output relation cardinality: 6 rows]

SELECT DISTINCT G1.name AS name,
(G2.canceled / G1.canceled * 100) AS percent
FROM 
(SELECT C1.name AS name, CAST(COUNT(F1.canceled) AS FLOAT) AS canceled
FROM Flights AS F1, Carriers C1
WHERE F1.carrier_id = C1.cid 
AND F1.origin_city = "Seattle WA"
GROUP BY C1.name) AS G1
LEFT JOIN
(SELECT C2.name AS name, CAST(COUNT(F2.canceled) AS FLOAT) AS canceled
FROM Flights AS F2, Carriers C2
WHERE F2.carrier_id = C2.cid 
AND F2.origin_city = "Seattle WA"
AND F2.canceled = 1
GROUP BY C2.name) AS G2
ON G1.name = G2.name 
WHERE G2.canceled IS NOT NULL
GROUP BY G1.name
HAVING percent > 0.5
ORDER BY percent;	