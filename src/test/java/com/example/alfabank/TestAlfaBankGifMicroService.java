package com.example.alfabank;
        import org.junit.jupiter.api.Test;
        import static org.assertj.core.api.Assertions.assertThat;

        import static org.hamcrest.Matchers.containsString;
        import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
        import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
        import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

        import org.mockito.Mockito;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.boot.test.context.SpringBootTest;
        import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
        import org.springframework.boot.test.mock.mockito.MockBean;
        import org.springframework.boot.test.web.client.TestRestTemplate;
        import org.springframework.boot.web.server.LocalServerPort;
        import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
        import org.springframework.test.web.servlet.MockMvc;

        import java.net.URI;
        import java.time.LocalDateTime;
        import java.time.format.DateTimeFormatter;
        import java.util.HashMap;
        import java.util.Locale;
        import java.util.Map;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TestAlfaBankGifMicroService {

    @LocalServerPort
    private int port;

    @Autowired
    private ExchangeController controller;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Config config;

    @MockBean
    private ExchangeClient exchangeClient;

    @MockBean
    private GifClient gifClient;

    //Основной тест с моком внешних сервисов. Проверяет фактическое отображение требуемой страницы
    @Test
    public void mockExchange() throws Exception {

        //Setup тестовых данных
        LocalDateTime date = LocalDateTime.now().minusDays(1);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        String yesterday = format.format(date);

        String exCurrent = config.getOpenexchangeratesServer() + "latest.json?app_id=" + config.getOpenexchangeratesid();
        String exYesterday = config.getOpenexchangeratesServer() + "historical/" + yesterday + ".json?app_id=" + config.getOpenexchangeratesid();
        String gifRequest = config.getGiphyServer() + "v1/gifs/random?api_key=" + config.getGiphyId() + "&tag=rich";

        URI uriCur = URI.create(exCurrent);
        URI uriYes = URI.create(exYesterday);
        URI uriGif = URI.create(gifRequest);
        Exchange testEx = new Exchange();
        Map exMap = new HashMap<>();
        exMap.put("EUR", 80.0);
        exMap.put("RUB", 1.0);
        testEx.setRates(exMap);
        Data testData = new Data();
        Gif testGif = new Gif();
        testGif.setData(testData);
        testGif.data.setId("Ynx9CV7G7uYWpEZU2s");

        //Возвращаем мок внешних сервисов
        Mockito.when(exchangeClient.getExchange(uriCur)).thenReturn(testEx);
        Mockito.when(exchangeClient.getExchange(uriYes)).thenReturn(testEx);
        Mockito.when(gifClient.getGif(uriGif)).thenReturn(testGif);

        this.mockMvc.perform(get("/exchange")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("<img src=\"https://i.giphy.com/media/")));
    }


    @Test
    public void basicLoad() throws Exception {
        assertThat(controller).isNotNull();
    }

}