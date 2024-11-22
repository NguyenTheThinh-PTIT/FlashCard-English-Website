package com.education.flashEng.service.Impl;

import com.education.flashEng.entity.*;
import com.education.flashEng.enums.AccessModifierType;
import com.education.flashEng.exception.EntityNotFoundWithIdException;
import com.education.flashEng.exception.ResourceAlreadyExistsException;
import com.education.flashEng.payload.response.SetResponse;
import com.education.flashEng.payload.response.WordResponse;
import com.education.flashEng.repository.ClassMemberRepository;
import com.education.flashEng.repository.ClassSetRequestRepository;
import com.education.flashEng.repository.SetRepository;
import com.education.flashEng.service.ClassSetRequestService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ClassSetRequestServiceImpl implements ClassSetRequestService {

    @Autowired
    private ClassSetRequestRepository classSetRequestRepository;
    @Autowired
    private UserServiceImpl userService;
    @Autowired
    private SetRepository setRepository;
    @Autowired
    private ClassMemberRepository classMemberRepository;
    @Autowired
    private NotificationServiceImpl notificationService;
    @Autowired
    private NotificationServiceImpl notificationServiceImpl;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    @Override
    public ClassSetRequestEntity createSetRequest(SetEntity setEntity, UserEntity userEntity, ClassEntity classEntity) {
        ClassSetRequestEntity classSetRequestEntity = new ClassSetRequestEntity();
        classSetRequestEntity.setTitle(setEntity.getName());
        classSetRequestEntity.setClassEntity(classEntity);
        classSetRequestEntity.setUserEntity(userEntity);
        classSetRequestEntity.setSetEntity(setEntity);
        classSetRequestRepository.save(classSetRequestEntity);
        return classSetRequestEntity;
    }

    @Transactional
    @Override
    public boolean acceptSetRequest(Long SetRequestId) {
        UserEntity userEntity = userService.getUserFromSecurityContext();
        ClassSetRequestEntity classSetRequestEntity = classSetRequestRepository.findById(SetRequestId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("Set Request", SetRequestId.toString()));
        ClassEntity classEntity = classSetRequestEntity.getClassEntity();
        boolean isAdmin = false;
        for (ClassMemberEntity memberEntity : classEntity.getClassMemberEntityList()){
            if(memberEntity.getRoleClassEntity().getName().equals("ADMIN")){
                UserEntity user = memberEntity.getUserEntity();;
                if(userEntity.equals(user)){
                    isAdmin = true;
                    break;
                }
            }
        }
        if(!isAdmin){
            throw new AccessDeniedException("You do not permission to accept this request.");
        }
        SetEntity setEntity = classSetRequestEntity.getSetEntity();
        if(setEntity.getPrivacyStatus().equals(AccessModifierType.getKeyfromValue("Class"))){
            throw new ResourceAlreadyExistsException("Set is already in this class.");
        }else{
            setEntity.setClassEntity(classEntity);
            setEntity.setPrivacyStatus(AccessModifierType.getKeyfromValue("Class"));
            setRepository.save(setEntity);
            classSetRequestRepository.delete(classSetRequestEntity);
            notificationService.deleteAllRelatedNotificationsByNotificationMetaData("classSetRequestId", SetRequestId.toString());
            notificationService.createAcceptRequestNotification(setEntity);
            return true;
        }
    }

    @Transactional
    @Override
    public boolean rejectSetRequest(Long SetRequestId) {
        UserEntity userEntity = userService.getUserFromSecurityContext();
        ClassSetRequestEntity classSetRequestEntity = classSetRequestRepository.findById(SetRequestId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("Set Request", SetRequestId.toString()));
        ClassEntity classEntity = classSetRequestEntity.getClassEntity();
        boolean isAdmin = false;
        for (ClassMemberEntity memberEntity : classEntity.getClassMemberEntityList()){
            if(memberEntity.getRoleClassEntity().getName().equals("ADMIN")){
                UserEntity user = memberEntity.getUserEntity();;
                if(userEntity.equals(user)){
                    isAdmin = true;
                    break;
                }
            }
        }
        if(!isAdmin){
            throw new AccessDeniedException("You do not permission to accept this request.");
        }
        SetEntity setEntity = classSetRequestEntity.getSetEntity();
        if(setEntity.getPrivacyStatus().equals(AccessModifierType.getKeyfromValue("Class"))){
            throw new ResourceAlreadyExistsException("Set is already in this class.");
        }else{
            setEntity.setPrivacyStatus(AccessModifierType.getKeyfromValue("Private"));
            setRepository.save(setEntity);
            classSetRequestRepository.delete(classSetRequestEntity);
            notificationService.deleteAllRelatedNotificationsByNotificationMetaData("classSetRequestId", SetRequestId.toString());
            notificationService.createRejectRequestNotification(setEntity, classEntity);
            return true;
        }
    }

    @Override
    public SetResponse getSetRequest(Long setRequestId) {
        ClassSetRequestEntity classSetRequestEntity = classSetRequestRepository.findById(setRequestId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("Class Set Request", setRequestId.toString()));
        UserEntity user = userService.getUserFromSecurityContext();
        ClassEntity classEntity = classSetRequestEntity.getClassEntity();
        for(ClassMemberEntity memberEntity : classEntity.getClassMemberEntityList()){
            if(memberEntity.getRoleClassEntity().getName().equals("ADMIN")){
                if(memberEntity.getUserEntity().equals(user)){
                    SetResponse setResponse = new SetResponse();
                    SetEntity setEntity = classSetRequestEntity.getSetEntity();
                    modelMapper.map(setEntity, setResponse);
                    List<WordResponse> wordResponses = new ArrayList<>();
                    for(WordEntity wordEntity : setEntity.getWordsEntityList()){
                        WordResponse wordResponse = new WordResponse();
                        modelMapper.map(wordEntity, wordResponse);
                        wordResponses.add(wordResponse);
                    }
                    setResponse.setWordResponses(wordResponses);
                    setResponse.setNumberOfWords((long) wordResponses.size());
                    UserEntity userEntity = classSetRequestEntity.getUserEntity();
                    setResponse.setUserDetailResponse(userEntity.getFullName(),
                            userEntity.getUsername(),
                            userEntity.getEmail(),
                            userEntity.getCountry());
                    return setResponse;
                }
            }
        }
        throw new AccessDeniedException("You do not permission to retrieve information of this request.");
    }

    @Override
    public boolean deleteClassSetRequestByEntity(ClassSetRequestEntity classSetRequestEntity) {
        classSetRequestRepository.delete(classSetRequestEntity);
        return true;
    }
}
