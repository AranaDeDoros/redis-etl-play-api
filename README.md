# Play + Redis Transactions API

A lightweight test REST API built with **Play Framework** that retrieves transaction data stored in **Redis**, previously processed by a Spark ETL job. 

---

##  Overview

This API exposes endpoints to query retail transactions stored in Redis by `CustomerID`.  
It is designed to connect directly to the output of the [`spark-redis-etl`](https://github.com/AranaDeDoros/spark-redis-etl) project.

**Architecture**
```text
Redis (from Spark ETL) → Play REST API → JSON Response
```
**Conf file example**
```text
redis {
url = "redis://user:passwd@host:port"
}
```


# ETL # 
You can find the ETL here [a link](https://github.com/AranaDeDoros/spark-redis-etl)