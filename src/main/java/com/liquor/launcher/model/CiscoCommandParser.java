package com.liquor.launcher.model;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.liquor.launcher.Liquor;
import com.liquor.resourcemanagement.ResourceLoader;
import lombok.SneakyThrows;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CiscoCommandParser {


    @SneakyThrows
    public static void parse() {
        JsonParser jsonParser = new JsonParser();
        Optional<URL> jsonPath = ResourceLoader.getJSON("cisco_commands");
        if(jsonPath.isPresent()) {
            URL json = jsonPath.get();
            List<String> result = Files.readAllLines(Paths.get(Liquor.class.getResource("cisco_commands.json").toExternalForm().trim()));
            jsonParser.parse(Files.lines(Paths.get(json.getPath())).collect(Collectors.joining()));
        }
    }

}
