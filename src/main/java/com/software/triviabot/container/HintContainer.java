package com.software.triviabot.container;

import com.software.triviabot.bot.enums.Hint;
import org.springframework.stereotype.Component;

import java.util.Map;

// Keeps hint values corresponding to button texts
// for easier processing and keyboard building
@Component
public class HintContainer {
    private static final Map<Hint, String> hintTextMap = Map.of(
        Hint.FIFTY_FIFTY, "\uD83C\uDFB2 50/50", // dice emoji
        Hint.AUDIENCE_HELP, "\uD83D\uDC65 Помощь зала", // two silhouettes emoji
        Hint.CALL_FRIEND, "\uD83D\uDCDE Звонок другу" // phone receiver emoji
    );

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
