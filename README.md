Simple program for managing expenses with CLI

# Set up
1. Install JRE SE [Download](http://www.oracle.com/technetwork/java/javase/downloads/server-jre8-downloads-2133154.html).  
2. Install Gradle [From here](https://gradle.org/install). 
3. Run Gradle command ```gradle fatJar``` inside the ```expenses-cli``` folder
4. The generated jar file is located in ```expenses-cli/build/libs/expenses.jar```
5. Configure mysql server.
6. Set up the following environment variables:
 - DB_HOST (e.g.: 127.0.0.1:3306)
 - DB_USER
 - DB_PASS
 - DB_DATABASE 
7. Done!

# Usage
Creating new expense:

When creating new expense option 'n' should be used,
to define that this is going to be a new expense.
Options 'k', 'd', 'a' and 'D' means 'kind', 'description',
'amount' and 'date' respectively. 'k' and 'd' takes string as
argument, 'a' takes double and 'D' accepts date format. When
creating new expense only 'k' and 'a' options are mandatory.

Examples:

```
expenses -n -k Kind -d 'String with spaces needs quotes' -a 5.1 
expenses -n -k Kind -D 18.4.23 -a 5,1
```  

Updating existing expense:

When editing expense option 'e' should be used, which also takes
the id of the expense as argument. Options 'k', 'd', 'a' and 'D'
are used to update the existing data of the expense and each of
them is not required.

Examples:

```
expenses -e 15 -k 'new kind' 
expenses -e 15 -D '2018/05/17'
expenses -e 15 -k 'new kind' -d 'new description' -a 5.9 -D 18.5.16
```  

Deleting expense:

When deleting expense option 'delete' should be used which takes
the id of the expense as argument.

Examples:

```expenses --delete 15```  

Prints expenses:

To print the expenses, option 'p' should be used. Although there are two
exceptions. When using the options 'printKindsOnly' and 'printMonthsAmount'
option 'p' should be omitted.

Examples:

```expenses -p``` (*prints all expenses*)  
```expenses -p -k 'kind1, kind2'``` (*prints all expenses with kinds 'kind1' and 'kind2'*)  
```expenses -p --startDate 18.1.1 --endDate 18.7.12``` (*prints all expenses within the provided dates including them*)  
```expenses --printKindsOnly``` (*prints all kinds*)  
```expenses --printMonthsAmount``` (*prints the total amount for each month*)  
```expenses -k kind1 --startDate 18.5.1 --endDate 18.6.1``` (*prints all expenses from kind 'kind1' within the chosen date*)

## Valid data formats:

### double:
```
1.5
1,5
.5
,5
``` 
### date:
```
2018-05-03
18.5.3
2018/05/03
18/5/3
2018.05.03
18.5.3
```

With '?' or 'help' this message is printed

# Note
The examples above are made by using alias. Without alias the full command name is ```java -jar expenses.jar [OPTIONS]```

# Examples
```
$ exp -p
+===========================================================================+
|                             Expenses for JULY                             |
+===========================================================================+
+=====+==========+=========+================+====================+==========+
| ID  |   DATE   |   DAY   |      KIND      |    DESCRIPTION     |  AMOUNT  |
+=====+==========+=========+================+====================+==========+
|  4  |2018-07-18|WEDNESDAY|      Fuel      |                    |  20.00   |
+-----+----------+---------+----------------+--------------------+----------+
|  5  |2018-07-18|WEDNESDAY|      Food      |       Lunch        |  06.00   |
+-----+----------+---------+----------------+--------------------+----------+
|  6  |2018-07-18|WEDNESDAY|     Taxes      |    Electricity     |  75.50   |
+-----+----------+---------+----------------+--------------------+----------+
|  7  |2018-07-18|WEDNESDAY|      Food      |       Pizza        |  02.00   |
+-----+----------+---------+----------------+--------------------+----------+
+===========================================================================+
|                          Total for JULY: 103.50                           |
+===========================================================================+
```
```
$ exp -p -k food
+===========================================================================+
|                             Expenses for JULY                             |
+===========================================================================+
+=====+==========+=========+================+====================+==========+
| ID  |   DATE   |   DAY   |      KIND      |    DESCRIPTION     |  AMOUNT  |
+=====+==========+=========+================+====================+==========+
|  5  |2018-07-18|WEDNESDAY|      Food      |       Lunch        |  06.00   |
+-----+----------+---------+----------------+--------------------+----------+
|  7  |2018-07-18|WEDNESDAY|      Food      |       Pizza        |  02.00   |
+-----+----------+---------+----------------+--------------------+----------+
+===========================================================================+
|                           Total for JULY: 08.00                           |
+===========================================================================+
```
```
$ exp -p --startDate 18.7.18 --endDate 18.7.18
+===========================================================================+
|                      FROM 2018-07-18, TO 2018-07-18                       |
+===========================================================================+
+=====+==========+=========+================+====================+==========+
| ID  |   DATE   |   DAY   |      KIND      |    DESCRIPTION     |  AMOUNT  |
+=====+==========+=========+================+====================+==========+
|  4  |2018-07-18|WEDNESDAY|      Fuel      |                    |  20.00   |
+-----+----------+---------+----------------+--------------------+----------+
|  5  |2018-07-18|WEDNESDAY|      Food      |       Lunch        |  06.00   |
+-----+----------+---------+----------------+--------------------+----------+
|  6  |2018-07-18|WEDNESDAY|     Taxes      |    Electricity     |  75.50   |
+-----+----------+---------+----------------+--------------------+----------+
|  7  |2018-07-18|WEDNESDAY|      Food      |       Pizza        |  02.00   |
+-----+----------+---------+----------------+--------------------+----------+
+===========================================================================+
|           TOTAL AMOUNT FROM 2018-07-18, TO 2018-07-18 = 103.50            |
+===========================================================================+
```
```
$ exp -p -k food --startDate 18.7.18 --endDate 18.7.18
+===========================================================================+
|                      FROM 2018-07-18, TO 2018-07-18                       |
+===========================================================================+
+=====+==========+=========+================+====================+==========+
| ID  |   DATE   |   DAY   |      KIND      |    DESCRIPTION     |  AMOUNT  |
+=====+==========+=========+================+====================+==========+
|  5  |2018-07-18|WEDNESDAY|      Food      |       Lunch        |  06.00   |
+-----+----------+---------+----------------+--------------------+----------+
|  7  |2018-07-18|WEDNESDAY|      Food      |       Pizza        |  02.00   |
+-----+----------+---------+----------------+--------------------+----------+
+===========================================================================+
|            TOTAL AMOUNT FROM 2018-07-18, TO 2018-07-18 = 08.00            |
+===========================================================================+
```
```
$ exp --printKindsOnly
+========================================+
|               ALL KINDS                |
+========================================+
|                  Fuel                  |
+----------------------------------------+
|                  Food                  |
+----------------------------------------+
|                 Taxes                  |
+----------------------------------------+
```
```
$ exp --printMonthsAmount
+===============+===============+
|     MONTH     | TOTAL AMOUNT  |
+===============+===============+
|     JULY      |    103.50     |
+---------------+---------------+
```