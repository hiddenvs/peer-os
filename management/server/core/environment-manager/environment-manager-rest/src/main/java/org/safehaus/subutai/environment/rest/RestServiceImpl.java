package org.safehaus.subutai.environment.rest;


import org.safehaus.subutai.core.environment.api.EnvironmentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * Created by bahadyr on 5/6/14.
 */

public class RestServiceImpl implements RestService
{

    public final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final static Logger LOG = LoggerFactory.getLogger( RestServiceImpl.class.getName() );
    private EnvironmentManager environmentManager;


    public RestServiceImpl()
    {
    }


    public EnvironmentManager getEnvironmentManager()
    {
        return environmentManager;
    }


    public void setEnvironmentManager( final EnvironmentManager environmentManager )
    {
        this.environmentManager = environmentManager;
    }


    @Override
    public String buildNodeGroup( final String peer )
    {
        return null;
    }
}