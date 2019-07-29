-- (1472 rows produced)
.open hw2.db

-- Find all itineraries from Seattle to Boston on July 15th. Search only for itineraries that have one stop (i.e., flight 1: Seattle -> [somewhere], flight2: [somewhere] -> Boston).
-- Both flights must depart on the same day (same day here means the date of flight) and must be with the same carrier. It's fine if the landing date is different from the departing date (i.e., in the case of an overnight flight). You don't need to check whether the first flight overlaps with the second one since the departing and arriving time of the flights are not provided.
-- The total flight time (actual_time) of the entire itinerary should be fewer than 7 hours
-- (but notice that actual_time is in minutes).
-- For each itinerary, the query should return the name of the carrier, the first flight number,
-- the origin and destination of that first flight, the flight time, the second flight number,
-- the origin and destination of the second flight, the second flight time, and finally the total flight time.
-- Only count flight times here; do not include any layover time.
-- Name the output columns name as the name of the carrier, f1_flight_num, f1_origin_city, f1_dest_city, f1_actual_time, f2_flight_num, f2_origin_city, f2_dest_city, f2_actual_time, and actual_time as the total flight time. List the output columns in this order.
-- [Output relation cardinality: 1472 rows]

SELECT C.name AS name,	
F1.flight_num AS f1_flight_num,
F1.origin_city AS f1_origin_city,
F1.dest_city AS f1_dest_city,
F1.actual_time AS f1_actual_time,
F2.flight_num AS f2_flight_num,
F2.origin_city AS f2_origin_city,
F2.dest_city AS f2_dest_city,
F2.actual_time AS f2_actual_time,
(F1.actual_time + F2.actual_time) AS actual_time
FROM Flights AS F1, Flights AS F2, 
Months AS M, Carriers AS C
WHERE M.month = "July"
AND F1.month_id = M.mid 
AND F1.month_id = F2.month_id
AND F1.day_of_month = 15
AND F2.day_of_month = F1.day_of_month
AND F1.carrier_id = C.cid
AND F1.carrier_id = F2.carrier_id
AND F1.origin_city = "Seattle WA"
AND F1.dest_city = F2.origin_city
AND F2.dest_city = "Boston MA"
AND (F1.actual_time + F2.actual_time) / 60 < 7;