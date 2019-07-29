-- (1 row produced)
.open hw2.db

-- (10 points) Find the total capacity of all direct flights that fly between Seattle and San Francisco, CA on July 10th
-- (i.e. Seattle to San Francisco or San Francisco to Seattle).
-- Name the output column capacity.
-- [Output relation cardinality: 1 row]

SELECT SUM(F.capacity) AS capacity 
FROM Flights AS F, Months AS M
WHERE F.month_id = M.mid
AND M.month = "July"
AND F.day_of_month = 10
AND ((F.origin_city = "Seattle WA" AND F.dest_city = "San Francisco CA")
OR (F.origin_city = "San Francisco CA" AND F.dest_city = "Seattle WA"));