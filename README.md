## How to run

Go to the project folder and execute
```
sbt clean run
```

## Endpoints

`GET /products`
* Retrieves every product in the database

`GET /products?vendor=abc`
* Retrieves a list of products grouped by vendor

`GET /products?priceGT=3`
* Retrieves a list of products with price greater than 3

`POST /products/xyz`

<details><summary>Body</summary>
<p>

```json
{
  "name": String,
  "vendor": String,
  "price": Double,
  "expirationDate": "yyyy-MM-dd" [Optional]
}
```
</p></details>  

* Saves a new product with ID xyz 
