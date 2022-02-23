package com.ashleyb24.stockeventproducer;

import com.ashleyb24.jalphavantage.AlphaVantage;
import com.ashleyb24.jalphavantage.model.exchange.CurrencyExchange;
import com.ashleyb24.jalphavantage.model.quote.GlobalQuote;
import com.ashleyb24.jalphavantage.query.CurrencyExchangeQuery;
import com.ashleyb24.jalphavantage.query.GlobalQuoteQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.LinkedList;

@Component
public class StockSchedule {

    Logger log = LoggerFactory.getLogger(StockSchedule.class);

    @Value("${producer.stock.topic}")
    private String stockTopic;
    @Value("${producer.currency.topic}")
    private String currencyTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AlphaVantage alphaVantage;
    private final LinkedList<String> stockSymbols;
    private final LinkedList<String> currencyCodes;

    public StockSchedule(KafkaTemplate<String, Object> kafkaTemplate, AlphaVantage alphaVantage,
                         @Value("${producer.stock.symbols}") String[] stockSymbols,
                         @Value("${producer.currency.codes}") String[] currencyCodes) {
        this.kafkaTemplate = kafkaTemplate;
        this.alphaVantage = alphaVantage;
        this.stockSymbols = new LinkedList<>(Arrays.asList(stockSymbols));
        this.currencyCodes = new LinkedList<>(Arrays.asList(currencyCodes));
    }

    @Scheduled(fixedDelay = 30000, initialDelay = 5000)
    public CurrencyExchange addCurrencyExchangeToTopic() {
        String currencyCode = moveFirstToEnd(this.currencyCodes);
        log.info("Schedule start - Adding currency exchange \"{}\" to \"USD\" to kafka topic", currencyCode);
        CurrencyExchange currencyExchange = alphaVantage.execute(new CurrencyExchangeQuery(currencyCode, "USD"));
        kafkaTemplate.send(currencyTopic, currencyCode, currencyExchange);
        log.info("Schedule complete");
        return currencyExchange;
    }

    @Scheduled(fixedDelay = 60000, initialDelay = 15000)
    public GlobalQuote addGlobalQuoteToTopic() {
        String stockSymbol = moveFirstToEnd(this.stockSymbols);
        log.info("Schedule start - Adding Global Quote \"{}\" to kafka topic", stockSymbol);
        GlobalQuote globalQuote = alphaVantage.execute(new GlobalQuoteQuery(stockSymbol));
        kafkaTemplate.send(stockTopic, stockSymbol, globalQuote);
        log.info("Schedule complete");
        return globalQuote;
    }

    private String moveFirstToEnd(LinkedList<String> list) {
        String first = list.removeFirst();
        list.addLast(first);
        return first;
    }
}
