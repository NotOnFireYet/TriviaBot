package com.software.triviabot.enums;


public enum State {
    IGNORE, // pre-interaction, before /start is invoked
    START, // interaction up until topics menu
    ENTERNAME, // accepting name input

    FIRSTQUESTION, // first question is sent
    GAMEPROCESS, // game in process after first question
    GOTANSWER, // processing given answer
    GIVEHINT, // accepting hint request

    SCORE, // main menu after game
    DELETEDATA // user requested to delete their data
}
