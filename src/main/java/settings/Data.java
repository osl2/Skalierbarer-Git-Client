package settings;

import java.io.File;
import java.util.*;

import commands.*;
import git.GitRemote;
import levels.Level;

/**
 * This class holds general purpose data which needs to be persisted.
 */
public class Data {
    private static Data INSTANCE;
    private LinkedList<Level> levels = new LinkedList<Level>();
    private LinkedList<File> repoList = new LinkedList<File>();


    private Data() {
    }

    /**
     * Method to get A list of all repositories that have been opened before.
     *
     * @return list of paths ordered by time last opened ascending
     */
    public List<File> getRecentlyOpenedRepositories() {
        return repoList;
    }

    /**
     * Stores a new path to a repository.
     *
     * @param path path to a new created git repository;
     */
    public void storeNewRepositoryPath(File path) {
        repoList.add(path);
    }

    /**
     * Method to get a list of all possible levels in the current configuration.
     *
     * @return immutable ordered List of all possible levels in the current configuration
     */
    public LinkedList<Level> getLevels() {
        LinkedList<Level> levelLinkedList = new LinkedList<Level>();
        LinkedList<ICommand> commands1 = getCommandList(1);
        LinkedList<ICommand> commands2 = getCommandList(2);
        LinkedList<ICommand> commands3 = getCommandList(3);
        LinkedList<ICommand> commands4 = getCommandList(4);
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

    public static Data getInstance() {
        if (INSTANCE == null)
            INSTANCE = new Data();
        return INSTANCE;
    }
    private static LinkedList<ICommand> getCommandList(int id){
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

        GitignoreAdd gitignoreAdd = new GitignoreAdd(null);
        GitignoreRemove gitignoreRemove = new GitignoreRemove();
        Rebase rebase = new Rebase(null, null);
        Remote remote = new Remote();
        Stash stash = new Stash();

        LinkedList<ICommand> commands = new LinkedList<ICommand>();

        switch (id) {
            case 1:
                commands = null;
                commands.add(add);
                commands.add(commit);
                commands.add(init);
                commands.add(revert);
                commands.add(gitignoreRemove);
                commands.add(gitignoreAdd);
            break;
            case 2:
                commands = null;
                commands.add(add);
                commands.add(commit);
                commands.add(init);
                commands.add(revert);
                commands.add(branch);
                commands.add(checkout);
                commands.add(merge);
            break;
            case 3:
                commands = null;
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
                commands = null;
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
}
