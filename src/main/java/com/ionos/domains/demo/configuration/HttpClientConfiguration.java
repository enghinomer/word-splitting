package com.ionos.domains.demo.configuration;

import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
public class HttpClientConfiguration {

    @Bean
    public WebClient googleNgramsWebClient() {
        // @formatter:off
        return WebClient
                .builder()
                .baseUrl("https://books.google.com/ngrams/json?content=grand, engineering&year_start=1800&year_end=2019&corpus=26&smoothing=3")
                .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
                .build();
        // @formatter:on
    }

    private HttpClient getHttpClient() {
        // @formatter:off
        return HttpClient
                .create()
                .tcpConfiguration(tcpClient -> {
                    tcpClient = tcpClient.option(CONNECT_TIMEOUT_MILLIS, 20000);
                    tcpClient = tcpClient.doOnConnected(conn -> conn
                            .addHandlerLast(new ReadTimeoutHandler(20000, MILLISECONDS)))
                            .doOnDisconnected(conn -> {
                                if (!conn.isDisposed()) {
                                    conn.disposeNow();
                                }
                            });
                    return tcpClient;
                });
        // @formatter:on
    }
}
