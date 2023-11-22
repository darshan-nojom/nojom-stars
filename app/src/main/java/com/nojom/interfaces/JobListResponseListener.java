package com.nojom.interfaces;

import com.nojom.model.Projects;

public interface JobListResponseListener {
    void onJobsResponse(Projects response,int page);
}
