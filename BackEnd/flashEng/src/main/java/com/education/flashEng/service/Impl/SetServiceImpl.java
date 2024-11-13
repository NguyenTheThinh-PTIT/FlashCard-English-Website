package com.education.flashEng.service.Impl;

import com.education.flashEng.entity.*;
import com.education.flashEng.enums.AccessModifierType;
import com.education.flashEng.exception.EntityNotFoundWithIdException;
import com.education.flashEng.payload.request.CreateSetRequest;
import com.education.flashEng.payload.request.UpdateSetRequest;
import com.education.flashEng.payload.response.SetResponse;
import com.education.flashEng.payload.response.WordResponse;
import com.education.flashEng.repository.ClassMemberRepository;
import com.education.flashEng.repository.ClassRepository;
import com.education.flashEng.repository.ClassSetRequestRepository;
import com.education.flashEng.repository.SetRepository;
import com.education.flashEng.service.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SetServiceImpl implements SetService {

    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private SetRepository setRepository;
    @Autowired
    private ClassSetRequestService classSetRequestService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private ClassRepository classRepository;
    @Autowired
    private ClassMemberRepository classMemberRepository;
    @Autowired
    private ClassSetRequestRepository classSetRequestRepository;
    @Autowired
    private WordService wordService;

    @Transactional
    @Override
    public SetResponse createSet(CreateSetRequest createSetRequest) {
        UserEntity user = userService.getUserFromSecurityContext();

        SetEntity setEntity = modelMapper.map(createSetRequest, SetEntity.class);
        setEntity.setUserEntity(user);
        SetEntity savedSetEntity = new SetEntity();
        //Kiểm tra privacy của set
        if(setEntity.getPrivacyStatus().equals(AccessModifierType.getKeyfromValue("Class"))){
            ClassEntity classEntity = classRepository.findById(createSetRequest.getClassId())
                    .orElseThrow(() -> new EntityNotFoundWithIdException("ClassEntity", createSetRequest.getClassId().toString()));
            ClassMemberEntity classMemberEntity = classMemberRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundWithIdException("ClassMemberEntity", user.getId().toString()));

            if(classMemberEntity.getRoleClassEntity().getName().equals("ADMIN")){
                setEntity.setClassEntity(classEntity);
                savedSetEntity = setRepository.save(setEntity);
            }
            else{
                setEntity.setPrivacyStatus(String.valueOf(AccessModifierType.getKeyfromValue("Private")));
                savedSetEntity = setRepository.save(setEntity);
                ClassSetRequestEntity classSetRequestEntity = classSetRequestService.createSetRequest(setEntity, user, classEntity);
                notificationService.createClassSetRequestNotification(classSetRequestEntity);
            }
        }
        else{
            savedSetEntity = setRepository.save(setEntity);
        }
        SetResponse setResponse = modelMapper.map(savedSetEntity, SetResponse.class);
        setResponse.setUserDetailResponse(
                user.getFullName(),
                user.getUsername(),
                user.getEmail(),
                user.getCountry()
        );
        return setResponse;
    }

    @Override
    public List<SetResponse> getPublicAndPrivateSet() {
        List<SetEntity> publicSetEntities = setRepository.findAllByPrivacyStatus(AccessModifierType.getKeyfromValue("Public"));
        UserEntity user = userService.getUserFromSecurityContext();
        List<SetEntity> privateSetEntities = setRepository.findAllByPrivacyStatusAndUserEntityId(
                AccessModifierType.getKeyfromValue("Private"), user.getId());
        List<SetEntity> combinedSetEntities = new ArrayList<>();
        combinedSetEntities.addAll(publicSetEntities);
        combinedSetEntities.addAll(privateSetEntities);
        List<SetResponse> setResponses = new ArrayList<>();
        for(SetEntity setEntity : combinedSetEntities){
            SetResponse s = new SetResponse();
            modelMapper.map(setEntity, s);
            s.setUserDetailResponse(setEntity.getUserEntity().getFullName(),
                    setEntity.getUserEntity().getUsername(),
                    setEntity.getUserEntity().getEmail(),
                    setEntity.getUserEntity().getCountry());

            List<WordResponse> wordListResponses = wordService.getWordBySetId(setEntity.getId());
            s.setWordResponses(wordListResponses);
            s.setNumberOfWords((long) wordListResponses.size());
            setResponses.add(s);
        }
        return setResponses;
    }

    @Override
    public List<SetResponse> getPrivateSet() {
        UserEntity user = userService.getUserFromSecurityContext();
        List<SetEntity> setEntities = setRepository.findAllByPrivacyStatusAndUserEntityId(
                AccessModifierType.getKeyfromValue("Private"), user.getId());
        List<SetResponse> setResponses = new ArrayList<>();
        for(SetEntity setEntity : setEntities){
            SetResponse s = new SetResponse();
            modelMapper.map(setEntity, s);
            // Thêm numberOfWords
            s.setUserDetailResponse(
                    setEntity.getUserEntity().getFullName(),
                    setEntity.getUserEntity().getUsername(),
                    setEntity.getUserEntity().getEmail(),
                    setEntity.getUserEntity().getCountry());
            List<WordResponse> wordListResponses = wordService.getWordBySetId(setEntity.getId());
            s.setWordResponses(wordListResponses);
            setResponses.add(s);
        }
        return setResponses;
    }

    @Override
    public List<SetResponse> getSetByClassID(Long classID) {
        UserEntity user = userService.getUserFromSecurityContext();
        ClassEntity classEntity = classRepository.findById(classID)
                .orElseThrow(() -> new EntityNotFoundWithIdException("ClassEntity", classID.toString()));
        if(user.getClassMemberEntityList().stream().noneMatch(classMemberEntity -> classMemberEntity.getClassEntity().getId().equals(classID))){
            throw new IllegalArgumentException("You do not belong to the class " + classEntity.getName());
        }
        List<SetEntity> setEntities = setRepository.findAllByPrivacyStatusAndClassEntityId(
                AccessModifierType.getKeyfromValue("Class"), classID);
        List<SetResponse> setResponses = new ArrayList<>();
        for(SetEntity setEntity : setEntities){
            SetResponse setResponse = new SetResponse();
            modelMapper.map(setEntity, setResponse);
            setResponse.setUserDetailResponse(
                    setEntity.getUserEntity().getFullName(),
                    setEntity.getUserEntity().getUsername(),
                    setEntity.getUserEntity().getEmail(),
                    setEntity.getUserEntity().getCountry());
            List<WordResponse> wordListResponses = wordService.getWordBySetId(setEntity.getId());
            setResponse.setWordResponses(wordListResponses);
            setResponse.setNumberOfWords((long) wordListResponses.size());
            setResponses.add(setResponse);
        }
        return setResponses;
    }

    @Transactional
    @Override
    public boolean updateSet(UpdateSetRequest updateSetRequest) {
        UserEntity user = userService.getUserFromSecurityContext();
        SetEntity setEntity = setRepository.findById(updateSetRequest.getSetId())
                .orElseThrow(() -> new EntityNotFoundWithIdException("SetEntity", updateSetRequest.getSetId().toString()));
        if(!setEntity.getUserEntity().getId().equals(user.getId())){
            throw new IllegalArgumentException("You are not the owner of the set");
        }
        if(AccessModifierType.getKeyfromValue("Class").equals(updateSetRequest.getPrivacyStatus())){
            ClassEntity classEntity = classRepository.findById(updateSetRequest.getClassId())
                    .orElseThrow(() -> new EntityNotFoundWithIdException("ClassEntity", updateSetRequest.getClassId().toString()));
            if(classEntity.getSetsEntityList().stream().anyMatch(setEntity1 -> setEntity1.getId().equals(setEntity.getId()))){
                throw new IllegalArgumentException("Your set is already in class " + classEntity.getName());
            }
            if(classSetRequestRepository.findBySetEntityId(setEntity.getId()) != null){
                throw new IllegalArgumentException("Your set is already pending in class " + classEntity.getName());
            }
            modelMapper.map(updateSetRequest, setEntity);
            List<ClassMemberEntity> classMemberEntity  = classEntity.getClassMemberEntityList();
            for(ClassMemberEntity memberEntity : classMemberEntity){
                if(memberEntity.getRoleClassEntity().getName().equals("ADMIN") && memberEntity.getUserEntity().getId().equals(user.getId())){
                    setEntity.setClassEntity(classEntity);
                    setRepository.save(setEntity);
                    return true;
                }
            }
            setEntity.setPrivacyStatus(String.valueOf(AccessModifierType.getKeyfromValue("Private")));
            setRepository.save(setEntity);
            ClassSetRequestEntity classSetRequestEntity = classSetRequestService.createSetRequest(setEntity, user, classEntity);
            notificationService.createClassSetRequestNotification(classSetRequestEntity);
        }
        else{
            modelMapper.map(updateSetRequest, setEntity);
            setEntity.setClassEntity(null);
            setRepository.save(setEntity);
        }
        return true;
    }

    @Transactional
    @Override
    public boolean deleteSetById(Long setID) {
        UserEntity user = userService.getUserFromSecurityContext();
        SetEntity setEntity = setRepository.findById(setID).
                orElseThrow(() -> new EntityNotFoundWithIdException("SetEntity", setID.toString()));
        if(!setEntity.getUserEntity().getId().equals(user.getId())){
            throw new IllegalArgumentException("You are not the owner of the set");
        }
        setRepository.delete(setEntity);
        return true;
    }

    @Override
    public List<SetResponse> getRecentSet() {
        UserEntity user = userService.getUserFromSecurityContext();
        List<StudySessionEntity> studySessionEntities = user.getStudySessionEntityList();
        studySessionEntities.sort(Comparator.comparing(StudySessionEntity::getCreatedAt).reversed());
        Set<SetEntity> processedSets = new LinkedHashSet<>();
        studySessionEntities.stream().map(StudySessionEntity::getWordEntity).forEach(wordEntity -> processedSets.add(wordEntity.getSetEntity()));
        List<SetResponse> setResponses = new ArrayList<>();
        for(SetEntity setEntity : processedSets){
            SetResponse s = new SetResponse();
            modelMapper.map(setEntity, s);
            s.setUserDetailResponse(
                    setEntity.getUserEntity().getFullName(),
                    setEntity.getUserEntity().getUsername(),
                    setEntity.getUserEntity().getEmail(),
                    setEntity.getUserEntity().getCountry());
            s.setNumberOfWords((long) setEntity.getWordsEntityList().size());
            s.setDescription(setEntity.getDescription());
            List<WordResponse> wordListResponses = wordService.getWordBySetId(setEntity.getId());
            s.setWordResponses(wordListResponses);
            setResponses.add(s);
        }
        return setResponses;
    }


}
