/*
package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.dto.TotalReturnsDto;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Read the json file provided in the argument[0]. The file will be avilable in the classpath.
  //  1. Use #resolveFileFromResources to get actual file from classpath.
  //  2. parse the json file using ObjectMapper provided with #getObjectMapper,
  //  and extract symbols provided in every trade.
  //  return the list of all symbols in the same order as provided in json.
  //  Test the function using gradle commands below
  //   ./gradlew run --args="trades.json"
  //  Make sure that it prints below String on the console -
  //  ["AAPL","MSFT","GOOGL"]
  //  Now, run
  //  ./gradlew build and make sure that the build passes successfully
  //  There can be few unused imports, you will need to fix them to make the build pass.
  
  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File f = resolveFileFromResources(args[0]);

    ArrayList<String> arr = new ArrayList<String>();
    ObjectMapper m = getObjectMapper();
    Ob[] o = m.readValue(f, Ob[].class);
    for (int i = 0;i < o.length;i++) {
      arr.add(o[i].getSymbol());
    }
    System.out.println(m.writeValueAsString(arr));
    return arr;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  //  Follow the instructions provided in the task documentation and fill up the correct values for
  //  the variables provided. First value is provided for your reference.
  //  A. Put a breakpoint on the first line inside mainReadFile() which says
  //    return Collections.emptyList();
  //  B. Then Debug the test #mainReadFile provided in PortfoliomanagerApplicationTest.java
  //  following the instructions to run the test.
  //  Once you are able to run the test, perform following tasks and record the output as a
  //  String in the function below.
  //  Use this link to see how to evaluate expressions -
  //  https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  //  1. evaluate the value of "args[0]" and set the value
  //     to the variable named valueOfArgument0 (This is implemented for your reference.)
  //  2. In the same window, evaluate the value of expression below and set it
  //  to resultOfResolveFilePathArgs0
  //     expression ==> resolveFileFromResources(args[0])
  //  3. In the same window, evaluate the value of expression below and set it
  //  to toStringOfObjectMapper.
  //  You might see some garbage numbers in the output. Dont worry, its expected.
  //    expression ==> getObjectMapper().toString()
  //  4. Now Go to the debug window and open stack trace. Put the name of the function you see at
  //  second place from top to variable functionNameFromTestFileInStackTrace
  //  5. In the same window, you will see the line number of the function in the stack trace window.
  //  assign the same to lineNumberFromTestFileInStackTrace
  //  Once you are done with above, just run the corresponding test and
  //  make sure its working as expected. use below command to do the same.
  //  ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = 
        "/home/crio-user/workspace/gauravrmsc-ME_QMONEY/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@f001896";
    String functionNameFromTestFileInStackTrace = "main";
    String lineNumberFromTestFileInStackTrace = "128";


    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }








  // TODO: CRIO_TASK_MODULE_REST_API
  //  Copy the relavent code from #mainReadFile to parse the Json into PortfolioTrade list.
  //  Now That you have the list of PortfolioTrade already populated in module#1
  //  For each stock symbol in the portfolio trades,
  //  Call Tiingo api (https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=&endDate=&token=)
  //  with
  //   1. ticker = symbol in portfolio_trade
  //   2. startDate = purchaseDate in portfolio_trade.
  //   3. endD//ate = args[1]
  //  Use RestTemplate#getForObject in order to call the API,
  //  and deserialize the results in List<Candle>
  //  Note - You may have to register on Tiingo to get the api_token.
  //    Please refer the the module documentation for the steps.
  //  Find out the closing price of the stock on the end_date and
  //  return the list of all symbols in ascending order by its close value on endDate
  //  Test the function using gradle commands below
  //   ./gradlew run --args="trades.json 2020-01-01"
  //   ./gradlew run --args="trades.json 2019-07-01"
  //   ./gradlew run --args="trades.json 2019-12-03"
  //  And make sure that its printing correct results.

  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    File f = resolveFileFromResources(args[0]);
    RestTemplate rest = new RestTemplate();
    ArrayList<TiingoCandle> candleArr = new ArrayList<TiingoCandle>();
    ArrayList<String> arr = new ArrayList<String>();
    String token = "4db1af6c86d3834c49e4ff3f2f073e2859aa4663"; 
    String endDate = args[1];
    ObjectMapper m = getObjectMapper();
    Ob[] o = m.readValue(f, Ob[].class);
    for (Ob obj:o) {
      arr.add(obj.getSymbol());
      String query = "https://api.tiingo.com/tiingo/daily/" + obj.symbol + "/prices?startDate=" + obj.purchaseDate + "&endDate=" + endDate + "&token=" + token;
      String json = rest.getForObject(query, String.class);
      TiingoCandle[] ting = m.readValue(json,TiingoCandle[].class);
      TiingoCandle last = ting[ting.length - 1];
      candleArr.add(last);
    }
    for (int i = 0;i < candleArr.size();i++) {
      for (int j = 0;j < candleArr.size() - i - 1;j++) {
        if (candleArr.get(j).getClose() > candleArr.get(j + 1).getClose()) {
          TiingoCandle temp = candleArr.get(j);
          candleArr.set(j,candleArr.get(j + 1));
          candleArr.set(j + 1,temp);
          String temp1 = arr.get(j);
          arr.set(j,arr.get(j + 1));
          arr.set(j + 1,temp1);

        }
      }
    }
   
    return arr;
     
     
  }









  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    // printJsonObject(mainReadFile(args));
    printJsonObject(mainReadQuotes(args));


  }

  static class Ob {

    private String symbol;
    private int quantity;
    private String tradeType;
    private String purchaseDate;
    
    public String getSymbol() {
      return symbol;
    }

    public void setSymbol(String symbol) {
      this.symbol = symbol;
    }

    public int getQuantity() {
      return quantity;
    }

    public void setQuantity(int quantity) {
      this.quantity = quantity;
    }

    public String getTradeType() {
      return tradeType;
    }

    public void setTradeType(String tradeType) {
      this.tradeType = tradeType;
    }

    public String getPurchaseDate() {
      return purchaseDate;
    }
    
    public void setPurchaseDate(String purchaseDate) {
      this.purchaseDate = purchaseDate;
    }
  }
}

*/