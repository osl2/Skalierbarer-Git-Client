package commands;

import git.GitFacade;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

class ConfigTest extends AbstractCommandTest {

    @Test
    void executeTest() {

        MockedConstruction<GitFacade> gitFacadeMockedConstruction = mockConstruction(GitFacade.class, (mock, context) ->
        {
            when(mock.setConfigValue(anyString(), anyString())).thenThrow(new AssertionError());
        });

        Config config = new Config();
        assertTrue(config.execute());

        gitFacadeMockedConstruction.close();

        gitFacadeMockedConstruction = mockConstruction(GitFacade.class, (mock, context) ->
        {
            when(mock.setConfigValue(eq("user.name"), anyString())).thenReturn(true);
            when(mock.setConfigValue(eq("user.email"), anyString())).thenReturn(true);
        });

        config.setName("Name");
        config.setEMail("Email");
        assertTrue(config.execute());

        gitFacadeMockedConstruction.close();
    }

    @Test
    void failOnNullTest() {
        Config config = new Config();
        assertTrue(config.execute());

        config.setName(null);
        assertFalse(config.execute());

        config.setName("");
        assertTrue(config.execute());
        config.setEMail(null);
        assertFalse(config.execute());

    }

}
