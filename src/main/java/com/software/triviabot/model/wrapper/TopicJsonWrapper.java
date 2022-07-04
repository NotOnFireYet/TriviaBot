package com.software.triviabot.model.wrapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// Class to map topics from json file
// during topic and question creation
public class TopicJsonWrapper {
    private String topicName;
    List<QuestionJsonWrapper> questions;
}
