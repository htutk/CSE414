-- List the names of carriers that operate flights from Seattle to San Francisco, CA.
-- Return each carrier's name only once. Use a nested query to answer this question. (7 points)
-- Name the output column carrier. Order the output ascending by carrier.
-- [Output relation cardinality: 4]

-- [2019-04-21 22:08:18] 4 rows retrieved starting from 1 in 5 s 10 ms (execution: 5 s, fetching: 10 ms)

-- carrier
----------------
-- Alaska Airlines Inc.
-- SkyWest Airlines Inc.
-- United Air Lines Inc.
-- Virgin America


SELECT DISTINCT C.name AS carrier
FROM Flights F, Carriers C 
WHERE F.carrier_id = C.cid
AND F.origin_city = 'Seattle WA'
AND F.dest_city = 'San Francisco CA'
ORDER BY C.name;