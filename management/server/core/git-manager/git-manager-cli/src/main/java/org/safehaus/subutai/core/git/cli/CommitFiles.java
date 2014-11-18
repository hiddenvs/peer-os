package org.safehaus.subutai.core.git.cli;


import java.util.List;

import org.safehaus.subutai.core.git.api.GitException;
import org.safehaus.subutai.core.git.api.GitManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.console.OsgiCommandSupport;

import com.google.common.base.Preconditions;


/**
 * Commits file(s)
 */
@Command( scope = "git", name = "commit-files", description = "Commit files" )
public class CommitFiles extends OsgiCommandSupport
{

    private static final Logger LOG = LoggerFactory.getLogger( CommitFiles.class.getName() );

    @Argument( index = 0, name = "repoPath", required = true, multiValued = false, description = "path to git repo" )
    String repoPath;
    @Argument( index = 1, name = "message", required = true, multiValued = false, description = "commit message" )
    String message;
    @Argument( index = 2, name = "file(s)", required = true, multiValued = true, description = "file(s) to commit" )
    List<String> files;
    @Argument( index = 3, name = "conflict resolution", required = false, multiValued = false,
            description = "commit after conflict resolution (true/false = default)" )
    boolean afterConflictResolved;

    private final GitManager gitManager;


    public CommitFiles( final GitManager gitManager )
    {
        Preconditions.checkNotNull( gitManager, "Git Manager is null" );

        this.gitManager = gitManager;
    }


    protected Object doExecute()
    {
        try
        {
            String commitId = gitManager.commit( repoPath, files, message, afterConflictResolved );

            System.out.println( String.format( "Commit ID : %s", commitId ) );
        }
        catch ( GitException e )
        {
            LOG.error( "Error in doExecute", e );
            System.out.println( e.getMessage() );
        }
        return null;
    }
}
