package git;

import commands.Push;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PushTest extends AbstractGitTest {
    private Push push;

    @BeforeEach
    void setPush() {
        push = new Push();
    }

    @Test
    void getNameTest() {
        assertEquals(0, push.getName().compareTo("Push"));
    }

    @Test
    void getCommandLineTest() {
        GitRemote remote = new GitRemote("url", "user", "testRemote");
        GitBranch branch = new GitBranch("testBranch");
        push.setRemote(remote);
        push.setLocalBranch(branch);
        assertEquals(0, push.getCommandLine().compareTo("git push testRemote testBranch"));

        push.setRemoteBranch("remoteBranch");
        String commandLine = push.getCommandLine();
        assertEquals(0, push.getCommandLine().compareTo("git push testRemote testBranch:remoteBranch"));

    }

    @Test
    void getDescriptionTest() {
        String description = "Lädt die lokalen Einbuchungen aus dem aktuellen Branch in das Online-Verzeichnis hoch";
        assertEquals(0, push.getDescription().compareTo(description));
    }


}
