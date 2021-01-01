package Git;

/**
 * Provides a central point to obtain git objects
 */
public class Facade {

    /**
     * Get all commits of the current Repository
     *
     * @return Array of all commits without stashes
     */
    public GitCommit[] getCommits() {
        /* Welche Klasse diese Funktionalitäten dann Erzeugt ist noch unklar, passiert aber im Git package. */
        return null;
    }

    /**
     * Get all stashes of the current Repository
     *
     * @return Array of all Commits which are Stashes
     */
    public GitCommit[] getStashes() {
        return null;
    }
}
