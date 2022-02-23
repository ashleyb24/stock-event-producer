package com.ashleyb24.stockeventproducer;

import com.ashleyb24.jalphavantage.AlphaVantage;
import com.ashleyb24.jalphavantage.model.exchange.CurrencyExchange;
import com.ashleyb24.jalphavantage.model.quote.GlobalQuote;
import com.ashleyb24.jalphavantage.query.CurrencyExchangeQuery;
import com.ashleyb24.jalphavantage.query.GlobalQuoteQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockScheduleTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private AlphaVantage alphaVantage;
    private StockSchedule stockSchedule;

    private static final String STOCK_TOPIC = "stockTopic";
    private static final String CURRENCY_TOPIC = "currencyTopic";
    private static final String[] STOCK_SYMBOLS = {"AAPL", "TSLA"};
    private static final String[] CURRENCY_CODES = {"BTC", "ETH"};

    @BeforeEach
    public void setup() {
        stockSchedule = new StockSchedule(kafkaTemplate, alphaVantage, STOCK_SYMBOLS, CURRENCY_CODES);
        ReflectionTestUtils.setField(stockSchedule, STOCK_TOPIC, STOCK_TOPIC);
        ReflectionTestUtils.setField(stockSchedule, CURRENCY_TOPIC, CURRENCY_TOPIC);
    }

    @Test
    public void testAddCurrencyExchangeToTopic() {
        CurrencyExchange mockOne = mock(CurrencyExchange.class);
        CurrencyExchange mockTwo = mock(CurrencyExchange.class);
        doReturn(mockOne, mockTwo).when(alphaVantage).execute(any(CurrencyExchangeQuery.class));
        CurrencyExchange currencyExchangeOne = stockSchedule.addCurrencyExchangeToTopic();
        CurrencyExchange currencyExchangeTwo = stockSchedule.addCurrencyExchangeToTopic();

        verify(kafkaTemplate).send(CURRENCY_TOPIC, CURRENCY_CODES[0], currencyExchangeOne);
        verify(kafkaTemplate).send(CURRENCY_TOPIC, CURRENCY_CODES[1], currencyExchangeTwo);
    }

    @Test
    public void testAddGlobalQuoteToTopic() {
        GlobalQuote mockOne = mock(GlobalQuote.class);
        GlobalQuote mockTwo = mock(GlobalQuote.class);
        doReturn(mockOne, mockTwo).when(alphaVantage).execute(any(GlobalQuoteQuery.class));
        GlobalQuote globalQuoteOne = stockSchedule.addGlobalQuoteToTopic();
        GlobalQuote globalQuoteTwo = stockSchedule.addGlobalQuoteToTopic();

        verify(kafkaTemplate).send(STOCK_TOPIC, STOCK_SYMBOLS[0], globalQuoteOne);
        verify(kafkaTemplate).send(STOCK_TOPIC, STOCK_SYMBOLS[1], globalQuoteTwo);
    }
}
