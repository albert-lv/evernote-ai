package org.albert.evernote.ai.service.evernote;

import java.util.Map;

public interface EverNoteHtmlService {

    Map<String, String> parseMetaData(String htmlPath);
}
