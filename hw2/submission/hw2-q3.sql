-- (1 row produced)
.open hw2.db

-- (10 points) Find the day of the week with the longest average arrival delay.
-- Return the name of the day and the average delay.
-- Name the output columns day_of_week and delay, in that order. (Hint: consider using LIMIT. Look up what it does!)
-- [Output relation cardinality: 1 row]

SELECT W.day_of_week AS day_of_week,
AVG(F.arrival_delay) AS delay
FROM Flights AS F, Weekdays AS W 
WHERE F.day_of_week_id = W.did
GROUP BY W.day_of_week
ORDER BY AVG(F.arrival_delay) DESC LIMIT 1;