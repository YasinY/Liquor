package com.liquor.launcher.cisco;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.liquor.launcher.Liquor;
import com.liquor.launcher.loaders.JsonLoader;
import com.liquor.launcher.model.CiscoCommand;
import com.liquor.resourcemanagement.ResourceLoader;
import lombok.SneakyThrows;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CiscoCommandParser {

    public static List<CiscoCommand> CISCO_COMMANDS = new ArrayList<>();

    public static JsonLoader initialize() {
        return new JsonLoader() {
            @Override
            public void load(JsonObject reader, Gson builder) {
                String commandName = reader.get("name").getAsString();
                String description = reader.get("description").getAsString();
                CISCO_COMMANDS.add(CiscoCommand.builder().name(commandName).description(description).build());
            }

            @Override
            public String filePath() {
                return getClass().getResource("cisco.json").getPath().substring(1);
            }

        };
    }

}
