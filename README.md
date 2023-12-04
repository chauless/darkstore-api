# **[Darkstore](https://cs.wikipedia.org/wiki/Internetov%C3%A9_distribu%C4%8Dn%C3%AD_centrum) delivery API service**

  

# REST API service for Darkstore
   - Management of couriers and orders
   
 # Courier budget and courier rating calculation
   - Retrieving assigned orders for a specific courier within a given time
   frame
   - Calculating courier ratings based on the number of completed orders
   and the time frame
   
  # Order distribution among couriers
   
   - Creating a method to distribute orders among available couriers before the start of the workday
    -  Retrieving information about already distributed orders for couriers
   
# Rate limiting
   
  - Implementing a rate limiter to limit the number of requests to API


# Specification

## Basic CRUD

### `POST /couriers`

The handler takes as input a list in json format with data about couriers and their work schedule.

Couriers work only in predefined neighborhoods, and are differentiated by type: foot courier, bicycle courier, and 
car courier. The type determines the volume of orders the courier carries.
Districts are specified by positive integers. Work schedule is specified by a list of lines in `HH:MM-HH:MM` format.

### `GET /couriers/{courier_id}`

Returns information about the courier.

### `GET /couriers`

Returns information about all couriers.

The method has `offset` and `limit` parameters to provide page-by-page output.
If:
* `offset` or `limit` are not passed, the default should be `offset = 0`, `limit = 1`;
* if no offerers are found for the specified `offset` and `limit`, an empty list of `couriers` should be returned.

### `POST /orders`

Takes as input a list with data about orders in json format. The characteristics of the order are displayed - weight, region, 
delivery time and price.
Delivery time is a string in the format `HH:MM-HH:MM`, where HH - hours (from 0 to 23) and MM - minutes (from 0 to 59).
Examples: "09:00-11:00", "00:00-23:59".


### `GET /orders/{order_id}`

Returns information about the order by its ID, as well as additional information: order weight, delivery area, 
time intervals in which it is convenient to take the order.

### `GET /orders`

Returns information about all orders.

The method has `offset` and `limit` parameters to provide page-by-page output.
If:
* `offset` or `limit` are not passed, the default should be `offset = 0`, `limit = 1`;
* no offenders were found for the given `offset` and `limit`, you should return an empty list of `orders`.

### `POST /orders/complete`

Accepts an array of objects consisting of three fields: courier id, order id and order fulfillment time, after noting that the order is complete.

## Business logic

### `GET /couriers/meta-info/{courier_id}`

Method returns the money earned by the courier for the orders and his rating.
* `start_date` - start date of the rating countdown
* `end_date` - the date of rating countdown end

Earnings are calculated as the sum of payment for each completed delivery: 

`sum = ∑(cost * C)`.

`C` is a coefficient depending on the type of courier:
* foot courier - 2
* bicycle courier - 3
* car courier - 4

Rating is calculated as follows:

((Number of all completed orders from `start_date` to `end_date`) / (Number of hours between `start_date` and `end_date`)) * C, where
C is a coefficient depending on the type of courier:
* foot courier = 3
* bicycle courier = 2
* auto - 1

### `POST /orders/assign`
Before the start of the working day, we take a list of orders and assign them to available couriers

The following parameters are taken into account:
* order weight
* delivery region
* delivery cost

**Weight of orders**


Each of the courier categories has a limit on the weight and number of orders that can be carried.


| Courier type | Maximum weight | Maximum quantity |
|---|---|---|
| walking | 10 | 2 |
| bicycle | 20 | 4 |
| auto | 40 | 7 |


**Delivery Region**


The type of transportation used affects the number of regions a courier can visit when delivering orders.


| Courier type | Number of regions | Comment |
|---|---|---|
| walking | 1 | delivery in 1 region only |
| bicycle courier | 2 | exactly 2 regions |
| auto | 3 | up to 3 regions |


**Delivery Time**


Time to visit all points in one region:


| Courier type | 1st order | Next orders |
|---|---|---|
| walking | 25 | 10 |
| bicycle courier | 12 | 8 |
| auto | 8 | 4 |



### `GET /couriers/assignments`
Receiving information about already distributed orders.


# Who the final system will be intended for

Imagine, that we’ve got an order from Rohlik.cz to create a new REST API service for their store. The final system will be intended for their operators to efficiently manage couriers and orders. This system will allow the registration of new couriers, management of their work schedules, and distribution of orders among them.

# Instructions for running the project on a local machine
- Install Docker - https://docs.docker.com/get-docker/
- Clone the Darkstore API repository
```bash
git clone git@gitlab.fel.cvut.cz:B231_B6B36EAR/morenmat.git
```
- Fill in the database configuration in .env file.
```bash
# Example of filling the configuration:
POSTGRES_USER=username
POSTGRES_PASSWORD=password
```
- Run the docker-compose file
```bash
docker-compose up
```
- Test data is available in a Postman workspace: [link](https://www.postman.com/restless-station-469785/workspace/darkstoreapi/collection/28422079-98b9cf47-0e20-44e6-b739-5f2f87449403?action=share&creator=28422079)
