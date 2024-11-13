package com.education.flashEng.service;

import com.education.flashEng.entity.ClassEntity;
import com.education.flashEng.payload.request.CreateClassRequest;
import com.education.flashEng.payload.response.ApiResponse;
import com.education.flashEng.payload.response.ClassInformationResponse;
import com.education.flashEng.payload.response.ClassMemberListReponse;

import java.util.List;
import java.util.Optional;

public interface ClassService {
    ClassMemberListReponse createClass(CreateClassRequest createClassRequest);

    ClassEntity getClassById(Long id);
    boolean updateClassName(Long classId, String name);

    ClassInformationResponse getClassInformation(Long classId);

    List<ClassInformationResponse> getAllCurrentUserClasses();

    List<ClassInformationResponse> findClassByName(String name);

    boolean deleteClassByEntity(ClassEntity classEntity);
}
