
package com.crio.warmup.stock.quotes;

import org.springframework.web.client.RestTemplate;

public enum StockQuoteServiceFactory {

  INSTANCE;


  public StockQuotesService getService(String provider,  RestTemplate restTemplate) {

  }
}
