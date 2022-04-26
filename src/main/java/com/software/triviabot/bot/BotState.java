package com.software.triviabot.bot;


public enum BotState {
    START, // start of the session
    GAMESTART, // beginning of the game before 1st question is sent
    SENDQUESTION, // game in process
    GETANSWER, // user answers a question
    SCORE, // displaying the score
}
