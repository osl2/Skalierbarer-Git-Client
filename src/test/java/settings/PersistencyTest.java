package settings;

import git.AbstractGitTest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class PersistencyTest extends AbstractGitTest {
    @TempDir
    static File config;
    Data data;

    @Override
    public void init() throws IOException, GitAPIException {
        super.init();
        data = Data.getInstance();
        settings.setLevel(data.getLevels().get(0));
        settings.setUser(gitData.getCommits().next().getAuthor());
        settings.setShowTreeView(false);
        settings.setUseTooltips(true);
        FileUtils.deleteDirectory(config);
        FileUtils.forceMkdir(config);
        new Persistency().save(config);
    }

    @Test
    public void singletonsAreCreatedCorrectlyTest() throws IllegalAccessException, IOException {
        // The Instance fields are private, so we should not be able to set them. We use reflection to still do that.
        // This should create new instances when we try to get another Settings or Data instance.
        FieldUtils.writeField(settings, "INSTANCE", null, true);
        FieldUtils.writeField(data, "INSTANCE", null, true);

        new Persistency().load(config);
        Data newData = Data.getInstance();
        Settings newSettings = Settings.getInstance();

        // If those fail, we did not get a new instance.
        assertNotSame(data, newData);
        assertNotSame(settings, newSettings);

        assertEquals(settings.getLevel(), newSettings.getLevel());
        assertEquals(settings.getUser(), newSettings.getUser());
        assertEquals(settings.getActiveRepositoryPath(), newSettings.getActiveRepositoryPath());

        assertEquals(data.getLevels(), newData.getLevels());
        assertEquals(data.getRecentlyOpenedRepositories(), newData.getRecentlyOpenedRepositories());
    }

}
