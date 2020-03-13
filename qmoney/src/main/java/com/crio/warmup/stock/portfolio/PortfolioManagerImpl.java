
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.PortfolioManagerApplication;
import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {
  RestTemplate restTemplate;
  StockQuotesService stockQuoteService;
  static String token = "4db1af6c86d3834c49e4ff3f2f073e2859aa4663";

  protected PortfolioManagerImpl(StockQuotesService stockQuotesService) {
    this.stockQuoteService = stockQuotesService;
  }


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo thirdparty APIs to a separate function.
  //  It should be split into fto parts.
  //  Part#1 - Prepare the Url to call Tiingo based on a template constant,
  //  by replacing the placeholders.
  //  Constant should look like
  //  https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  //  Where ? are replaced with something similar to <ticker> and then actual url produced by
  //  replacing the placeholders with actual parameters.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException {
        if (stockQuoteService != null) {
          return stockQuoteService.getStockQuote(symbol, from, to);
        } 
        String uri=buildUri(symbol, from, to);
        String json = restTemplate.getForObject(uri, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); 
        TiingoCandle[] ting= objectMapper.readValue(json, TiingoCandle[].class);
        
     return Arrays.asList(ting);
      }
     

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+ symbol +"/prices?"
            + "startDate="+startDate+"&endDate="+endDate+"&token="
            + token;
    return uriTemplate;        

  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(
        List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
        ArrayList<AnnualizedReturn> annualRetArr = new ArrayList<AnnualizedReturn>();  
    for ( PortfolioTrade obj: portfolioTrades) {  
      try {
        List<Candle> ting1 = getStockQuote(obj.getSymbol(), obj.getPurchaseDate(), endDate);
        TiingoCandle last = (TiingoCandle)ting1.get(ting1.size()-1);
        double buyPrice = ting1.get(0).getOpen();
        double sellPrice = last.getClose();
        PortfolioTrade portfolioTrade = 
            new PortfolioTrade(obj.getSymbol(), obj.getQuantity(),
            obj.getPurchaseDate());
        annualRetArr.add(PortfolioManagerApplication.calculateAnnualizedReturns(endDate, 
           portfolioTrade, buyPrice, sellPrice));
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }

    } 
    for (int i = 0; i < annualRetArr.size(); i++) {
      for (int j = 0; j < annualRetArr.size() - i - 1; j++) {
        if (annualRetArr.get(j).getAnnualizedReturn() < annualRetArr.get(j + 1)
            .getAnnualizedReturn()) {
          AnnualizedReturn temp = annualRetArr.get(j);
          annualRetArr.set(j, annualRetArr.get(j + 1));
          annualRetArr.set(j + 1, temp);
        }
      }
    }
    return annualRetArr;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
