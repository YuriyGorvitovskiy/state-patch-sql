# state-patch-sql

# Architecture Overview

![Example](./architecture-overview.svg)

<a href="https://app.diagrams.net/#HYuriyGorvitovskiy%2Fstate-patch-sql%2Fmaster%2Farchitecture-overview.svg" target="_blank">Edit</a>

# Persistence

Should contains following packages:

* Functions to Manipulate with DB schemas, tables, columns and indexes,
* Functions to Manipulate with DB single record: INSERT, UPDATE, UPSERT, DELETE
* Functions to Manipulate with DB multiple (all in table, List of PK, and by Query.where condition) record,
* Functions to process patches,
* Functions to distribute patch processing.

# Query Processor

# Action Processor

# Triggers

# Uniqueness

# Import

# Export

# Cache

Requirements:

* Should have 2 levels:
  * **Strong Level** of changed data, that didn't reached DB yet. 
  * **Weak Level** of data the is already in DB. Data from this level can be removed any time.
* At Startup, Cache should post **ping** message to the **Patch Topic**.
* Cache should hold any access to data until it recieves **pong** message to the **Notification Topic**.
* Requesting data from Cache, should look at the following order until it finds data:
  * **Strong Level** cache
  * **Weak Level** cache
  * Queried from DB with putting result into **Weak Level** cache
* Once patch is published to the **Patch Topic** the following steps should be happening:
  * Related records should be requested from the cache
  * Patch should be applied to the requested records
  * Changed records should be placed into the **Strong Level** cache
  * Changed records should be associated with the patch id.
  * When **Notification Topic** states that patch with id get processed by Persistence, the records associated with the patch id can be moved from **Strong Level** to **Weak Level** cache.
  
# To Think About

- [ ] How to process by cache multiple record patches?
- [ ] Patch per record or per transaction?
  

