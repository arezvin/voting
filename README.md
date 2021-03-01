### Task:

Design and implement a REST API using Hibernate/Spring/SpringMVC (or Spring-Boot) **without frontend**.

The task is:

Build a voting system for deciding where to have lunch.

 * 2 types of users: admin and regular users
 * Admin can input a restaurant and it's lunch menu of the day (2-5 items usually, just a dish name and price)
 * Menu changes each day (admins do the updates)
 * Users can vote on which restaurant they want to have lunch at
 * Only one vote counted per user
 * If user votes again the same day:
    - If it is before 11:00 we asume that he changed his mind.
    - If it is after 11:00 then it is too late, vote can't be changed

Each restaurant provides new menu each day.

### curl samples (application deployed at application context `voting`)

#### get All Menus
`curl -s http://localhost:8080/voting/menus --user user@gmail.com:user`

#### vote (you can change field `hourLimitForVote` at `TimeUtil` class to extend the limit)
`curl -s -X POST -d '{"date":"2020-02-01"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/voting/votes/100002 --user user@gmail.com:user`

#### create Restaurant
`curl -s -X POST -d '{"name":"Created Restaurant"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/voting/admin/restaurants --user admin@gmail.com:admin`

#### create Dish
`curl -s -X POST -d '{"name":"New Dish","price":"32","date":"2020-02-01"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/voting/admin/dishes/100003 --user admin@gmail.com:admin`

#### register Users
`curl -s -X POST -d '{"name":"New User","email":"test@gmail.com","password":"test-password"}' -H 'Content-Type:application/json;charset=UTF-8' http://localhost:8080/voting/profile/register`
