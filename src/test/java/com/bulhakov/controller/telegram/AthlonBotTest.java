package com.bulhakov.controller.telegram;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AthlonBotTest {

    private static final String[] validCommands = {
            "/hello@bot",
            "/hello my friend",
            "/hello"
    };

    private static final String invalidCommand = "Not a command";

    private AthlonBot controller;

    @Before
    public void setUp() {
        controller = new AthlonBot("test", "test", null);
    }

    @Test
    public void shouldFindCommandUsingRegex() {
        String commandRepresentation = controller.getCommandRepresentation(validCommands[0]);
        assertNotNull(commandRepresentation);
    }

    @Test
    public void shouldFindAllCommandsUsingRegex() {
        for (String message : validCommands) {
            String commandRepresentation = controller.getCommandRepresentation(message);
            assertNotNull(commandRepresentation);
        }
    }

    @Test
    public void shouldReturnNullAfterParsingInvalidCommand() {
        String commandRepresentation = controller.getCommandRepresentation(invalidCommand);
        assertNull(commandRepresentation);
    }
}