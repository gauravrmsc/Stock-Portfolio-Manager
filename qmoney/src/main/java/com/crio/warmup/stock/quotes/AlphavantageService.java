
package com.crio.warmup.stock.quotes;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AlphavantageCandle;
import com.crio.warmup.stock.dto.AlphavantageDailyResponse;
import com.crio.warmup.stock.dto.Candle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class AlphavantageService implements StockQuotesService {
  private String apiKey = "LBEXXF00X1I6W8O";
  private RestTemplate restTemplate;

  public AlphavantageService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private List<Candle> sortCandles(List<Candle> candles) {
    Comparator c = new Comparator<Candle>() {

      @Override
      public int compare(Candle o1, Candle o2) {
        return o1.getDate().compareTo(o2.getDate());
      }
    };
    Collections.sort(candles,c);
    return candles;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonMappingException, JsonProcessingException {
    List<Candle> stockQuotes = new ArrayList<Candle>();
    String uri = buildURl(symbol, from, to);
    String json = restTemplate.getForObject(uri, String.class);
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    JsonNode node = objectMapper.readValue(json, JsonNode.class);
    JsonNode node1 = node.get("Time Series (Daily)");
    try {
      HashMap<String,AlphavantageCandle> hm = 
          objectMapper.readValue(node1.toString(), 
          new TypeReference<HashMap<String,AlphavantageCandle>>(){});
      Set<Map.Entry<String,AlphavantageCandle>> set = hm.entrySet();
      for (Map.Entry<String,AlphavantageCandle> n:set) {
        LocalDate date = LocalDate.parse(n.getKey());
        if (date.compareTo(from) >= 0 && to.compareTo(date) >= 0) {
          AlphavantageCandle c = n.getValue();
          c.setDate(date);
          stockQuotes.add(c);
        }
      }
      stockQuotes = sortCandles(stockQuotes);
    } catch (NullPointerException e) {
      System.out.println("Error Fetching Data");
    }
    return stockQuotes;
  }

  private String buildURl(String symbol,LocalDate from,LocalDate to) {
    String url = String.format("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=%s&apikey=%s&outputsize=full",symbol,apiKey);
    return url;
  }
  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Inplement the StockQuoteService interface as per the contracts.
  //  The implementation of this functions will be doing following tasks
  //  1. Build the appropriate url to communicate with thirdparty.
  //  The url should consider startDate and endDate if it is supported by the provider.
  //  2. Perform thirdparty communication with the Url prepared in step#1
  //  3. Map the response and convert the same to List<Candle>
  //  4. If the provider does not support startDate and endDate, then the implementation
  //  should also filter the dates based on startDate and endDate.
  //  Make sure that result contains the records for for startDate and endDate after filtering.
  //  5. return a sorted List<Candle> sorted ascending based on Candle#getDate
  //  Call alphavantage service to fetch daily adjusted data for last 20 years. Refer to
  //  documentation here - https://www.alphavantage.co/documentation/
  //  Make sure you use {RestTemplate#getForObject(URI, String)} else the test will fail.
  //  Run the tests using command below and make sure it passes
  //  ./gradlew test --tests AlphavantageServiceTest
  //CHECKSTYLE:OFF
    //CHECKSTYLE:ON
  //TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  // Write a method to create appropriate url to call alphavantage service. Method should
  // be using configurations provided in the {@link @application.properties}.
  // Use thie method in #getStockQuote.

  

  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  Update the method signature to match the signature change in the interface.
  //  Start throwing new StockQuoteServiceException when you get some invalid response from
  //  Alphavangate, or you encounter a runtime exception during Json parsing.
  //  Make sure that the exception propagates all the way from PortfolioManager,
  //  so that the external user's of our API are able to explicitly handle this exception upfront.
  //CHECKSTYLE:OFF

}

