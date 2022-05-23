package com.software.triviabot.enums;


public enum State {
    START, // interaction up until topics menu
    ENTERNAME, // accepting name input

    FIRSTQUESTION, // first question is sent
    GAMEPROCESS, // game in process after first question
    GOTANSWER, // processing given answer
    GIVEHINT, // accepting hint request

    DELETEALL, // delete all messages including commands
    PREGAME, // before /start is invoked & after user data deletion
    SCORE, // main menu after game
}
