package com.software.triviabot.enums;


public enum BotState {
    START, // start of the session
    ENTERNAME, // accepting name input
    GAMESTART, // picking the topic

    SENDQUESTION, // game in process
    GIVEHINT, // accepting hint request
    SENDQUESTION_AFTER_HINT, // resending question after a hint was shown
    DOUBLE_HINT_REQUEST,

    SCORE, // displaying the score
    REMINDRULES, // resending the rules message
    GETSTATS, // display statistics
    DELETEDATA, // delete user data

    IGNORE // ignore non-command user input
}
