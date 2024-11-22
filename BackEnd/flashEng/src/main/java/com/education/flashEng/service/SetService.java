package com.education.flashEng.service;

import com.education.flashEng.entity.SetEntity;
import com.education.flashEng.payload.request.CreateSetRequest;
import com.education.flashEng.payload.request.UpdateSetRequest;
import com.education.flashEng.payload.response.SetResponse;

import java.util.List;

public interface SetService {
    SetResponse createSet(CreateSetRequest createSetRequest);
    List<SetResponse> getOwnPublicAndPrivateSet();
    List<SetResponse> getPrivateSet();
    List<SetResponse> getSetByClassID(Long classID);
    boolean updateSet(UpdateSetRequest updateSetRequest);
    boolean deleteSetById(Long setID);
    List<SetResponse> getRecentSet();
    List<SetResponse> findSetByName(String name);

    List<SetResponse> getPublicSet();
}
