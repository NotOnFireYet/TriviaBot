package com.software.triviabot.container;

import com.software.triviabot.enums.Hint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// Keeps hint values corresponding to button texts
@Component
public class HintContainer {
    private static final Map<Hint, String> hintTextMap = Map.of(
        Hint.FIFTY_FIFTY, "\uD83C\uDFB2 50/50", // dice emoji
        Hint.AUDIENCE_HELP, "\uD83D\uDC65 Помощь зала", // two silhouettes emoji
        Hint.CALL_FRIEND, "\uD83D\uDCDE Звонок другу" // phone receiver emoji
    );

    public static String getText(Hint hint){
        return hintTextMap.get(hint);
    }

    public static Hint getHintByText(String text){ // get hint object from button text
        for (Map.Entry<Hint, String> entry : hintTextMap.entrySet()) {
            if (entry.getValue().equals(text)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static List<String> getAllHintTexts() {
        List<String> result = new ArrayList<>();
        for (Hint hint : Hint.values()){
            result.add(getText(hint));
        }
        return result;
    }
}
