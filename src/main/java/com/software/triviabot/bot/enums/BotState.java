package com.software.triviabot.bot.enums;


public enum BotState {
    START, // start of the session
    ENTERNAME, // accepting name input
    GAMESTART, // beginning of the game before 1st question is sent

    SENDQUESTION, // game in process
    GETANSWER, // user answers a question
    GIVEHINT, // accepting hint request

    SCORE, // displaying the score
    REMINDRULES, // resending the rules message
    GETSTATS, // display statistics
    DELETEDATA, // delete user data

    IGNORE // ignore non-command user input
}
