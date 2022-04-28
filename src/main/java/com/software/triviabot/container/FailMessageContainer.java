package com.software.triviabot.container;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class FailMessageContainer {
    private FailMessageContainer(){}

    private static final List<String> messages = Arrays.asList(
        " рублей сгорели в камине.\uD83D\uDD25 \uD83D\uDCB8", //fire emoji, dollar stack with wings emoji
        " рублей ушли на благотворительность.\uD83C\uDF1F", // glowing star emoji
        " рублей распределены рабочему классу.☭",
        " рублей разлетелись по ветру.\uD83D\uDCA8", // wind gust emoji
        " рублей ушли разработчику этого бота.❤",
        " рублей изъяты налоговой инспекцией.\uD83D\uDD8A" // pen emoji
    );

    public static String getRandomFailMessage(){
        Random rnd = new Random();
        int max = messages.size() + 1;
        int min = 1;
        return messages.get(rnd.nextInt(max - min) + min);
    }
}
