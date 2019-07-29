-- (3 rows produced)
.open hw2.db

-- (10 points) List the distinct flight numbers of all flights from Seattle to Boston by Alaska Airlines Inc. on Mondays.
-- Also notice that, in the database, the city names include the state. So Seattle appears as
-- Seattle WA.
-- Name the output column flight_num.
-- [Hint: Output relation cardinality: 3 rows]

SELECT DISTINCT F.flight_num AS flight_num
FROM Flights AS F, Carriers AS C, Weekdays AS W
WHERE W.day_of_week = "Monday" 
AND W.did = F.day_of_week_id
AND C.name = "Alaska Airlines Inc."
AND C.cid = F.carrier_id
AND F.origin_city = "Seattle WA"
AND F.dest_city = "Boston MA";