# Spring Dynamic Commons

Spring Dynamic Commons provides SQL Directives and Utils for other Spring Dynamic libraries.
The Spring Dynamic libraries will be classified into two types, Spring Dynamic Data and Dynamic Query Template Provider.

## Spring Dynamic Data

Spring Dynamic Data provides `@DynamicQuery` annotation, so you can write dynamic query.
Each Spring Dynamic Data library is an extension to the corresponding [Spring Data](https://spring.io/projects/spring-data) package.

- [Spring Dynamic JPA](https://github.com/joutvhu/spring-dynamic-jpa)
- [Spring Dynamic JDBC](https://github.com/joutvhu/spring-dynamic-jdbc)
- [Spring Dynamic R2DBC](https://github.com/joutvhu/spring-dynamic-r2dbc)

To use it, you need to choose a Dynamic Query Template Provider to combine with Spring Dynamic Data.

## Dynamic Query Template Provider

Each Dynamic Query Template Provider will give you a different way of writing dynamic query templates.

- [Spring Dynamic Freemarker](https://github.com/joutvhu/spring-dynamic-freemarker)
- [Spring Dynamic Velocity](https://github.com/joutvhu/spring-dynamic-velocity)

You can also create your own Dynamic Query Template Provider based on a template engine.
