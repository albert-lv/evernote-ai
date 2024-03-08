package org.albert.evernote.ai.service.evernote.impl;

import static org.albert.evernote.ai.service.evernote.constant.Constants.SOURCE_URL;
import static org.albert.evernote.ai.service.evernote.constant.Constants.TITLE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.albert.evernote.ai.service.evernote.EverNoteHtmlService;
import org.springframework.stereotype.Service;

@Service
public class EverNoteHtmlServiceImpl implements EverNoteHtmlService {

    public static final int MAX_LINES_TO_READ = 5;

    @Override
    public Map<String, String> parseMetaData(String htmlPath) {
        Map<String, String> metaData = new HashMap<>();
        StringBuilder content = new StringBuilder();
        int maxLinesToRead = MAX_LINES_TO_READ;
        try (BufferedReader reader = new BufferedReader(new FileReader(htmlPath))) {
            String line;
            for (int i = 0; i < maxLinesToRead && (line = reader.readLine()) != null; i++) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + htmlPath);
            return metaData;
        }
        String htmlContent = content.toString();
        // 使用正则表达式查找title和source-url
        Pattern titlePattern = Pattern.compile("<title>([^<]+)</title>");
        Matcher titleMatcher = titlePattern.matcher(htmlContent);
        if (titleMatcher.find()) {
            String title = titleMatcher.group(1);
            metaData.put(TITLE, title);
        }
        Pattern sourceUrlPattern =
                Pattern.compile("<meta name=\"source-url\" content=\"([^\"]+)\"");
        Matcher sourceUrlMatcher = sourceUrlPattern.matcher(htmlContent);
        if (sourceUrlMatcher.find()) {
            String sourceUrl = sourceUrlMatcher.group(1);
            metaData.put(SOURCE_URL, sourceUrl);
        }
        return metaData;
    }
}
