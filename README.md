# **[Darkstore](https://cs.wikipedia.org/wiki/Internetov%C3%A9_distribu%C4%8Dn%C3%AD_centrum) delivery API service**

  

## REST API service for Darkstore:

  
  

  - Management of couriers and orders
   
 ## Courier budget and courier rating calculation:
   - Retrieving assigned orders for a specific courier within a given time
   frame
   - Calculating courier ratings based on the number of completed orders
   and the time frame
   
  ## Order distribution among couriers:
   
   - Creating a method to distribute orders among available couriers before the start of the workday
   
- Retrieving information about already distributed orders for couriers
   
## Rate limiting:
   
- Implementing a rate limiter to limit the number of requests to API



# Who the final system will be intended for:

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
- The documentation page will be available at: http://localhost:8080/swagger-ui/index.html
