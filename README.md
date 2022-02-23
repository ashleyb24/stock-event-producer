# stock-event-producer
A Spring Boot application that periodically publishes stock and currency data to Kafka topics. The data source is
the Alpha Vantage API using a [Java wrapper](https://github.com/ashleyb24/jAlphaVantage).

The stock and currency data that is collected as well as the topic names can be customised in the application configuration.
```yaml
producer:
  stock:
    symbols: AAPL,TSLA #this can be any list of stock symbols
    topic: global-quote
  currency:
    codes: BTC,ETH #this can be any list of currency codes
    topic: currency-exchange
```