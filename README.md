# Car rental

The car rental company provides information about available cars for rent (manufacturer, brand, number of seats, color, price) and fixes agreements with customers (term, car).

The minimal set of tables are:
- cars,

- customers

- agreements

1. At first create custom data type  `car_type` as enum with values 'SUV', 'Hatchback', 'Crossover', 'Convertible', 'Sedan', 'Sports Car', 'Coupe', 'Miniven', 'Pickup Truck'.
[wiki.postgresql](https://wiki.postgresql.org/wiki/Enum), 
[sqliz.com](https://www.sqliz.com/postgresql-ref/enum-datatype/)


In each table define primary key `id` as int8 type (bigserial).
[postgresql-tutorial](`https://www.postgresqltutorial.com/postgresql-tutorial/postgresql-serial/#:~:text=1)%20Basic%20PostgreSQL%20SERIAL`)

2. Table `cars` in additional to `id` should contain fields:
* required brand,
* required manufacturer,
* required unique number,
* optional type of car_type,
* required color,
* required seats_amount of int2 type,
* optional cost_per_day.

3. Table `customers` in addition to `id` should contain fields:
* required first_name   varchar not null,
* required last_name    varchar not null,
* unique phone_number with check using regular expression `[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}`.

4. Table `agreements` in addition to `id` should contain fields:
* foreign key car_id,
* foreign key customer_id,
* required start_date,
* optional end_date,
* optional total_price.

**Note**: use 'simple' types: varchar for text fields, numeric for money representation, date for date.

# What needs to be done to complete the assignment?

____Use just github actions____
1. Write to file `src/test/resources/init.sql` script that creates database structure (avoid using extra spaces, after each sql command put semicolon, it is advisable to type each statement on one line).
2. Push changes to github

_**Use docker**_
1. Install docker desktop.
2. Clone repo.
3. Write to file `src/test/resources/init.sql` script that creates database structure (avoid using extra spaces, after each sql command put semicolon, it is advisable to type each statement on one line)
3. Run docker-compose up.
4. Analyze execution log.
5. Push changes to github

**_Use maven and local database server_**
1. Create database and change file `src/test/resources/db.properties` with your data.
2. Clone repo.
3. Install java.
4. install maven.
5. Run mvn surefire-report:report in your project directory.
6. View report from file `target/site/surefire-report.html`
7. Write to file `src/test/resources/init.sql` script that creates database structure (avoid using extra spaces, after each sql command puts semicolon).
8. Push changes to github