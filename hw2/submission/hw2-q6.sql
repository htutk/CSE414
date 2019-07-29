-- (3 rows produced)
.open hw2.db

-- (10 points) Find the maximum price of tickets between Seattle and New York, NY
-- (i.e. Seattle to New York or New York to Seattle).
-- Show the maximum price for each airline separately.
-- Name the output columns carrier and max_price, in that order.
-- [Output relation cardinality: 3 rows]

SELECT C.name AS carrier, MAX(F.price) AS max_price
FROM Flights AS F, Carriers AS C
WHERE F.carrier_id = C.cid
AND ((F.origin_city = "Seattle WA"
	AND F.dest_city = "New York NY")
OR (F.origin_city = "New York NY"
	AND F.dest_city = "Seattle WA"))
GROUP BY C.name;