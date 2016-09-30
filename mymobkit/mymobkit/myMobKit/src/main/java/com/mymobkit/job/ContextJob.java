package com.mymobkit.job;

import android.content.Context;

import org.whispersystems.jobqueue.Job;
import org.whispersystems.jobqueue.JobParameters;
import org.whispersystems.jobqueue.dependencies.ContextDependent;

/**
 * Base job class.
 *
 * Created by MEKOH on 3/7/2016.
 */
public abstract class ContextJob extends Job implements ContextDependent {

    protected transient Context context;

    protected ContextJob(Context context, JobParameters parameters) {
        super(parameters);
        this.context = context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    protected Context getContext() {
        return context;
    }

}