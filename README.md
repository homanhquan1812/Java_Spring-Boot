# Spring Data JPA
## What is JPA & Hibernate?
* **JPA (Java Persistence API)** is a Java specification that defines a standard way to map Java objects to relational database tables and manage their lifecycle, so developer can interact with the database using objects instead of raw SQL.
* **Hibernate** is a JPA implementation (ORM framework) that maps Java objects to database tables and handles SQL generation.
## Workflow of JPA
* At application startup, Hibernate performs **ORM (Object-Relational Mapping)** by scanning @Entity classes and building schema-level metadata that defines the mapping between Java entities and database tables and columns.
* During runtime:
  * If a repository method is invoked, Spring Data JPA delegates the call to the EntityManager.
