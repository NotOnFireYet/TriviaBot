package com.software.triviabot.enums;


public enum State {
    IGNORE, // pre-interaction, before /start is invoked
    START, // interaction up until topics menu
    ENTERNAME, // accepting name input

    GAMEPROCESS, // game in process
    GIVEHINT, // accepting hint request

    SCORE // ignore non-command user input
}
