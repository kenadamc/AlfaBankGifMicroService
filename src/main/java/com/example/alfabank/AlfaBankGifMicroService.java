package com.example.alfabank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Controller
class ExchangeController {

    @Autowired
    private Config config;
    @Autowired
    public ExchangeClient exchangeClient;
    @Autowired
    public GifClient gifClient;

    //С параметров extradata=yes получаем расширенный вывод информации
    @GetMapping("/exchange")
    public String exchange(@RequestParam(value = "extradata", defaultValue = "No", required = false) String extraData, Model model) throws IOException, InterruptedException, URISyntaxException {
        //Загружаем данные из конфига. Формируем строку запроса с серверу валют
        String cur = config.getCurrency();
        String exID = config.getOpenexchangeratesid();
        String exServer = config.getOpenexchangeratesServer();
        String exCurrent = exServer + "latest.json?app_id=" + exID;

        URI uri;
        uri = URI.create(exCurrent);

        //Через feign получаем курс требуемой валюты и курс рубля.
        Exchange currentExchange = exchangeClient.getExchange(uri);

        double curRub = currentExchange.getRates().get("RUB");
        double curEx = currentExchange.getRates().get(cur);
        //Вычисляем курс валюты к рублю
        double curToRub = curRub / curEx;

        model.addAttribute("currency", cur);
        model.addAttribute("course", curToRub);

        //Получаем вчерашнюю дату в формате yyyy-MM-dd
        LocalDateTime date = LocalDateTime.now().minusDays(1);
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
        String yesterday = format.format(date);


        String exYesterday = exServer + "historical/" + yesterday + ".json?app_id=" + exID;
        uri = URI.create(exYesterday);

        //Получаем вчерашние курсы с сервера валют
        Exchange yesterdayExchange = exchangeClient.getExchange(uri);

        curRub = yesterdayExchange.getRates().get("RUB");
        curEx = yesterdayExchange.getRates().get(cur);
        Double yesToRub = curRub / curEx;

        model.addAttribute("yesterday", yesToRub);

        //Получаем параметры сервера Гифок. Формируем строку запроса к нему.
        String gifApiKey = config.getGiphyId();
        String gifServer = config.getGiphyServer();
        String gifTag;
        //Если курс меньше вчерашнего, то выбираем рандомную гифку из "broken", в иновм случае "rich"
        if (curToRub < yesToRub) {
            gifTag = "broken";
        }
        else {
            gifTag = "rich";
        }
        String gifRequest = gifServer + "v1/gifs/random?api_key=" + gifApiKey + "&tag=" + gifTag;
        uri = URI.create(gifRequest);

        //Получаем ID выданной гифки
        String gifID = gifClient.getGif(uri).data.getId();

        //На основе ID формируем полный путь к ней на сервере giphy
        String gifPath = "https://i.giphy.com/media/" + gifID + "/giphy.gif";
        model.addAttribute("gifPath", gifPath);
        model.addAttribute("extraData", extraData);

        return "exchange";
    }



}

@FeignClient(name="ExchangeClient", url = "https://ru")
interface ExchangeClient {
    @GetMapping(consumes= MediaType.APPLICATION_JSON_VALUE)
    Exchange getExchange(URI baseUrl);
}

@FeignClient(name="GifClient", url = "https://ru")
interface GifClient {
    @GetMapping(consumes= MediaType.APPLICATION_JSON_VALUE)
    Gif getGif(URI baseUrl);
}