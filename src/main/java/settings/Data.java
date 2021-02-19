package settings;

import com.fasterxml.jackson.annotation.JsonProperty;
import commands.*;
import levels.Level;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * This class holds general purpose data which needs to be persisted.
 */
public class Data extends DataObservable {
    private static Data INSTANCE;

    // Modify settings.PersistencyTest if you add or remove a field!
    private LinkedList<Level> levels = new LinkedList<>();
    @JsonProperty("recentlyOpenedRepositories")
    private LinkedList<File> repoList = new LinkedList<>();

    // This layout is necessary so that Jackson can create a correctly instantiated class.
    private Data() {
        if (INSTANCE == null)
            INSTANCE = this;
    }

    public static Data getInstance() {
        if (INSTANCE == null)
            new Data();
        return INSTANCE;
    }

    private static LinkedList<ICommandGUI> getCommandList(int id) {
        Add add = new Add();
        Commit commit = new Commit();
        Revert revert = new Revert();
        Init init = new Init();

        Branch branch = new Branch();
        Checkout checkout = new Checkout();
        Merge merge = new Merge();

        Clone clone = new Clone();
        Pull pull = new Pull();
        Push push = new Push();

        Rebase rebase = new Rebase(null, null);
        Remote remote = new Remote();
        Stash stash = new Stash();

        LinkedList<ICommandGUI> commands = new LinkedList<>();

        switch (id) {
            case 1:
                commands.add(add);
                commands.add(commit);
                commands.add(init);
                commands.add(revert);
                break;
            case 2:
                commands.add(add);
                commands.add(commit);
                commands.add(init);
                commands.add(revert);
                commands.add(branch);
                commands.add(checkout);
                commands.add(merge);
                break;
            case 3:
                commands.add(add);
                commands.add(commit);
                commands.add(init);
                commands.add(revert);
                commands.add(branch);
                commands.add(checkout);
                commands.add(merge);
                commands.add(clone);
                commands.add(pull);
                commands.add(push);
                break;
            case 4:
                commands.add(add);
                commands.add(commit);
                commands.add(init);
                commands.add(revert);
                commands.add(branch);
                commands.add(checkout);
                commands.add(merge);
                commands.add(clone);
                commands.add(pull);
                commands.add(push);
                commands.add(stash);
                commands.add(remote);
                commands.add(rebase);
                break;
            default:

                break;

        }
        return commands;

    }

    /**
     * Method to get A list of all repositories that have been opened before.
     *
     * @return list of paths ordered by time last opened ascending
     */
    @SuppressWarnings("unchecked")
    public List<File> getRecentlyOpenedRepositories() {
        return (List<File>) repoList.clone();
    }

    /**
     * Stores a new path to a repository.
     *
     * @param path path to a new created git repository;
     */
    public void storeNewRepositoryPath(File path) {
        if (!repoList.contains(path))
            repoList.add(path);
    }

    /**
     * Method to get a list of all possible levels in the current configuration.
     *
     * @return immutable ordered List of all possible levels in the current configuration
     */
    public LinkedList<Level> getLevels() {
        LinkedList<Level> levelLinkedList = new LinkedList<>();
        LinkedList<ICommandGUI> commands1 = getCommandList(1);
        LinkedList<ICommandGUI> commands2 = getCommandList(2);
        LinkedList<ICommandGUI> commands3 = getCommandList(3);
        LinkedList<ICommandGUI> commands4 = getCommandList(4);
        Level levelOne = new Level("Level 1", commands1, 1);
        Level levelTwo = new Level("Level 2", commands2, 2);
        Level levelThree = new Level("Level 3", commands3, 3);
        Level levelFour = new Level("Level 4", commands4, 4);
        levelLinkedList.add(levelOne);
        levelLinkedList.add(levelTwo);
        levelLinkedList.add(levelThree);
        levelLinkedList.add(levelFour);
        levels = levelLinkedList;
        return levelLinkedList;
    }
}
