package com.software.triviabot.enums;

public enum State {
    ENTERNAME, // accepting name input
    START, // interaction up until topic selection

    FIRSTQUESTION, // first question is sent
    GAMEPROCESS, // game in process after first question
    RIGHTANSWER, // reacting to correct answer

    DELETEDATA, // delete all messages including commands
    PREGAME, // before /start is invoked
    SCORE, // main menu after game
}
