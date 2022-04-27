package com.software.triviabot.config;

import com.software.triviabot.bot.enums.Hint;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HintConfig {
    private static Map<Hint, String> hintTextMap = new HashMap<>();

    public HintConfig() {
        hintTextMap.put(Hint.FIFTY_FIFTY, "\uD83C\uDFB2 50/50"); // dice emoji
        hintTextMap.put(Hint.AUDIENCE_HELP, "\uD83D\uDC65 Помощь зала"); // two silhouettes emoji
        hintTextMap.put(Hint.CALL_FRIEND, "\uD83D\uDCDE Звонок другу"); // phone receiver emoji
    }

    public static String getHintText(Hint hint){
        return hintTextMap.get(hint);
    }

    public static Hint getHintByText(String text){
        for (Map.Entry<Hint, String> entry : hintTextMap.entrySet()) {
            if (entry.getValue().equals(text)) {
                return entry.getKey();
            }
        }
        return null;
    }
}
