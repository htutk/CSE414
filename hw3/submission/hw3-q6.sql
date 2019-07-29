-- List the names of carriers that operate flights from Seattle to San Francisco, CA.
-- Return each carrier's name only once. Use a nested query to answer this question. (7 points)
-- Name the output column carrier. Order the output ascending by carrier.
-- [Output relation cardinality: 4]

-- [2019-04-21 22:19:00] 4 rows retrieved starting from 1 in 3 s 879 ms (execution: 3 s 853 ms, fetching: 26 ms)

-- carrier
----------------
-- Alaska Airlines Inc.
-- SkyWest Airlines Inc.
-- United Air Lines Inc.
-- Virgin America


SELECT C.name AS carrier
FROM Carriers C
WHERE C.cid IN (
    SELECT DISTINCT F.carrier_id
    FROM Flights F
    WHERE F.origin_city = 'Seattle WA'
    AND F.dest_city = 'San Francisco CA'
    )
ORDER BY C.name;