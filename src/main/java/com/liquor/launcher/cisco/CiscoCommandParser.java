package com.liquor.launcher.cisco;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liquor.launcher.loaders.JsonLoader;
import com.liquor.launcher.model.CiscoCommand;
import lombok.SneakyThrows;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;

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

            @SneakyThrows
            @Override
            public String filePath() {
                return getClass().getResource("cisco.json").getPath().substring(1);

            }

        };
    }

}
