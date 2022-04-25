package com.software.triviabot.bot;


public enum BotState {
    START, // start of the session
    SENDQUESTION, // game in process
    GETANSWER, // user answers a question
    SCORE, // displaying the tally
}
