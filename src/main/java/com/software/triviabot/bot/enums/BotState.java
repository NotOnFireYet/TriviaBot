package com.software.triviabot.bot.enums;


public enum BotState {
    START, // start of the session
    ENTERNAME,
    GAMESTART, // beginning of the game before 1st question is sent

    SENDQUESTION, // game in process
    GETANSWER, // user answers a question
    GIVEHINT,

    SCORE, // displaying the score
}
