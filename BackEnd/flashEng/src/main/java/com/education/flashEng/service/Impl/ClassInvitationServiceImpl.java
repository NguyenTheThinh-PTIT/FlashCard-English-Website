package com.education.flashEng.service.Impl;

import com.education.flashEng.entity.*;
import com.education.flashEng.exception.EntityNotFoundWithIdException;
import com.education.flashEng.payload.response.AllClassesInvitationResponse;
import com.education.flashEng.payload.response.ClassInformationResponse;
import com.education.flashEng.payload.response.ClassInvitationResponse;
import com.education.flashEng.repository.ClassInvitationRepository;
import com.education.flashEng.service.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClassInvitationServiceImpl implements ClassInvitationService {

    @Autowired
    private UserService userService;

    @Autowired
    private ClassService classService;

    @Autowired
    private RoleClassService roleClassService;

    @Autowired
    private ClassInvitationRepository classInvitationRepository;

    @Autowired
    private ClassMemberService classMemberService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ClassJoinRequestService classJoinRequestService;

    @Transactional
    @Override
    public boolean inviteToClass(Long classId, String inviteeUsername) {
        UserEntity invitee = userService.getUserByUsername(inviteeUsername);
        UserEntity inviter = userService.getUserFromSecurityContext();
        ClassEntity classEntity = classService.getClassById(classId);
        if (classEntity.getClassMemberEntityList().stream().noneMatch(classMemberEntity -> classMemberEntity.getUserEntity() == inviter))
            throw new AccessDeniedException("You are not authorized to invite to this class.");
        if (classEntity.getClassMemberEntityList().stream().anyMatch(classMemberEntity -> classMemberEntity.getUserEntity() == invitee))
            throw new AccessDeniedException("User is already a member of this class.");
        if (classInvitationRepository.findByClassEntityIdAndInviteeEntityIdAndInviterEntityId(classId, invitee.getId(), inviter.getId()).isPresent())
            throw new AccessDeniedException("You have already invited this user to this class.");
        if (classJoinRequestService.getClassJoinRequestByClassIdAndUserId(classId, invitee.getId()).isPresent())
            throw new AccessDeniedException("User has already requested to join this class.");
        ClassInvitationEntity classInvitationEntity = ClassInvitationEntity.builder()
                .classEntity(classEntity)
                .inviteeEntity(invitee)
                .inviterEntity(inviter)
                .build();
        classInvitationRepository.save(classInvitationEntity);
        notificationService.createClassInvitationNotification(classInvitationEntity);
        return true;
    }

    @Transactional
    @Override
    public boolean acceptInvitation(Long invitationId) {
        ClassInvitationEntity classInvitationEntity = classInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("Invitation", invitationId.toString()));
        UserEntity invitee = userService.getUserFromSecurityContext();
        if (invitee != classInvitationEntity.getInviteeEntity())
            throw new AccessDeniedException("You are not authorized to accept this invitation.");
        ClassMemberEntity classMemberEntity = classMemberService.getClassMemberByClassIdAndUserId(classInvitationEntity.getClassEntity().getId(), classInvitationEntity.getInviterEntity().getId());

        if (classMemberEntity.getRoleClassEntity().getName().equals("ADMIN")){
            classMemberService.saveClassMember(ClassMemberEntity.builder()
                    .classEntity(classInvitationEntity.getClassEntity())
                    .userEntity(invitee)
                    .roleClassEntity(roleClassService.getRoleClassByName("MEMBER"))
                    .build());
            deleteAllInviteeInvitationsOfAClass(classInvitationEntity.getClassEntity().getId(), invitee.getId());
        }
        else
            classJoinRequestService.createClassJoinRequest(classInvitationEntity.getClassEntity().getId(), invitee.getId());
        notificationService.createAcceptedClassInvitationNotification(classInvitationEntity);
        return true;
    }

    @Transactional
    @Override
    public boolean rejectInvitation(Long invitationId) {
        ClassInvitationEntity classInvitationEntity = classInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("Invitation", invitationId.toString()));
        UserEntity invitee = userService.getUserFromSecurityContext();
        if (invitee != classInvitationEntity.getInviteeEntity())
            throw new AccessDeniedException("You are not authorized to reject this invitation.");
        classInvitationRepository.delete(classInvitationEntity);
        notificationService.deleteAllRelatedNotificationsByNotificationMetaData("classInvitationId", invitationId.toString());
        notificationService.createRejectedClassInvitationNotification(classInvitationEntity);
        return true;
    }

    @Transactional
    @Override
    public boolean deleteAllInviteeInvitationsOfAClass(Long classId, Long inviteeId){
        Optional<List<ClassInvitationEntity>> classInvitationEntities = classInvitationRepository.findByInviteeEntityIdAndClassEntityId(inviteeId, classId);
        if(classInvitationEntities.isPresent()){
            for(ClassInvitationEntity classInvitationEntity : classInvitationEntities.get()){
                notificationService.deleteAllRelatedNotificationsByNotificationMetaData("classInvitationId", classInvitationEntity.getId().toString());
                classInvitationRepository.delete(classInvitationEntity);
            }
        }
        return true;
    }

    @Override
    public ClassInvitationResponse getClassInvitation(Long invitationId) {
        ClassInvitationEntity classInvitationEntity = classInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("Invitation", invitationId.toString()));
        UserEntity invitee = userService.getUserFromSecurityContext();
        if (invitee != classInvitationEntity.getInviteeEntity())
            throw new AccessDeniedException("You are not authorized to view this invitation.");
        return ClassInvitationResponse.builder()
                .classInformationResponse(classService.getClassInformation(classInvitationEntity.getClassEntity().getId()))
                .inviterUsername(classInvitationEntity.getInviterEntity().getUsername())
                .invitationId(classInvitationEntity.getId())
                .build();
    }

    @Override
    public List<ClassInvitationResponse> getAllCurrentUserClassInvitations() {
        UserEntity invitee = userService.getUserFromSecurityContext();
        List<ClassInvitationEntity> classInvitationEntities = classInvitationRepository.findAllByInviteeEntityId(invitee.getId());
        return classInvitationEntities.stream()
                .map(classInvitationEntity -> ClassInvitationResponse.builder()
                        .classInformationResponse(classService.getClassInformation(classInvitationEntity.getClassEntity().getId()))
                        .inviterUsername(classInvitationEntity.getInviterEntity().getUsername())
                        .invitationId(classInvitationEntity.getId())
                        .build())
                .toList();
    }

    @Override
    public List<AllClassesInvitationResponse> getAllClassInvitations(Long classId) {
        UserEntity user = userService.getUserFromSecurityContext();
        ClassEntity classEntity = classService.getClassById(classId);
        if (classEntity.getClassMemberEntityList().stream().noneMatch(classMemberEntity -> classMemberEntity.getUserEntity() == user && classMemberEntity.getRoleClassEntity().getName().equals("ADMIN")))
            throw new AccessDeniedException("You are not authorized to view invitations of this class.");
        return classEntity.getClassInvitationEntityList().stream()
                .map(classInvitationEntity -> AllClassesInvitationResponse.builder()
                        .inviterUsername(classInvitationEntity.getInviterEntity().getUsername())
                        .invitationId(classInvitationEntity.getId())
                        .inviteeUsername(classInvitationEntity.getInviteeEntity().getUsername())
                        .message(classInvitationEntity.getInviterEntity().getUsername() + " has invited " + classInvitationEntity.getInviteeEntity().getUsername() + " to this class.")
                        .build())
                .toList();
    }

    @Override
    public String checkExistance(Long classId, String inviteeUsername) {
        UserEntity invitee = userService.getUserByUsername(inviteeUsername);
        UserEntity inviter = userService.getUserFromSecurityContext();
        ClassEntity classEntity = classService.getClassById(classId);
        if (classEntity.getClassMemberEntityList().stream().noneMatch(classMemberEntity -> classMemberEntity.getUserEntity() == inviter))
            throw new AccessDeniedException("You are not authorized to invite to this class.");
        Optional<ClassInvitationEntity> classInvitationEntity = classInvitationRepository.findByClassEntityIdAndInviteeEntityIdAndInviterEntityId(classId, invitee.getId(), inviter.getId());
        if (classInvitationEntity.isPresent())
            return "InvitationId: "+classInvitationEntity.get().getId();
        return null;
    }

    @Override
    public boolean revokeInvitation(Long invitationId) {
        ClassInvitationEntity classInvitationEntity = classInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("Invitation", invitationId.toString()));
        UserEntity inviter = userService.getUserFromSecurityContext();
        if (inviter != classInvitationEntity.getInviterEntity())
            throw new AccessDeniedException("You are not authorized to revoke this invitation.");
        classInvitationRepository.delete(classInvitationEntity);
        notificationService.deleteAllRelatedNotificationsByNotificationMetaData("classInvitationId", invitationId.toString());
        return true;
    }


}
