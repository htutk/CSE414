-- (22 rows produced)
.open hw2.db

-- (10 points) Compute the total departure delay of each airline
-- across all flights.
-- Name the output columns name and delay, in that order.
-- [Output relation cardinality: 22 rows]

SELECT C.name AS NAME, SUM(F.departure_delay) AS delay
FROM Flights AS F, Carriers AS C
WHERE F.carrier_id = C.cid 
GROUP BY C.name;