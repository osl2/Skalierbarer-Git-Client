package levels;


import com.fasterxml.jackson.databind.ObjectMapper;
import commands.Branch;
import commands.Checkout;
import commands.ICommand;
import commands.Init;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class LevelTest {
    @TempDir
    static File tempDir;
    private static LinkedList<ICommand> aCommands = new LinkedList<>();
    /* Different Objects then aCommands */
    private static LinkedList<ICommand> bCommands = new LinkedList<>();
    private static LinkedList<ICommand> cCommands = new LinkedList<>();
    private static Level a;
    private static Level b;
    private static Level c;

    @BeforeAll
    public static void prepare() {
        aCommands.add(new Branch());
        aCommands.add(new Checkout());
        bCommands.add(new Branch());
        bCommands.add(new Checkout());
        cCommands.add(new Init());
        cCommands.add(aCommands.get(1));
        a = new Level("A", aCommands, 1);
        b = new Level("A", bCommands, 1);
        c = new Level("C", cCommands, 2);
    }

    @Test
    public void levelEqualsSelfTest() {
        assertEquals(a, a);
    }

    @Test
    public void levelNotEqualsLevelWithDifferentCommandsTest() {
        assertNotEquals(a, c);

    }

    @Test
    public void levelEqualsDifferentLevelWithSameData() {
        assertEquals(a, b);
    }

    @Test
    public void levelSerializationNotAffectEquals() throws IOException {
        File f = new File(tempDir, "serialisation");
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(f, a);
        assertEquals(a, mapper.readValue(f, Level.class));
        mapper.writeValue(f, b);
        assertEquals(b, mapper.readValue(f, Level.class));
        mapper.writeValue(f, c);
        assertEquals(c, mapper.readValue(f, Level.class));

    }
}
