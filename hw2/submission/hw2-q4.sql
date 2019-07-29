-- (12 rows produced)
.open hw2.db

-- (10 points) Find the names of all airlines that ever flew more than 1000 flights in one day
-- (i.e., a specific day/month, but not any 24-hour period).
-- Return only the names of the airlines. Do not return any duplicates
-- (i.e., airlines with the exact same name).
-- Name the output column name.
-- [Output relation cardinality: 12 rows]

SELECT DISTINCT C.name as name
FROM Flights AS F, Carriers AS C
WHERE F.carrier_id = C.cid
GROUP BY C.name, F.day_of_month
HAVING COUNT(F.fid) > 1000;