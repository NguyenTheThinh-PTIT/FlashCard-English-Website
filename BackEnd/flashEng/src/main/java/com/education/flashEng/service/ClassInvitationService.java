package com.education.flashEng.service;

import com.education.flashEng.payload.response.AllClassesInvitationResponse;
import com.education.flashEng.payload.response.ClassInvitationResponse;

import java.util.List;

public interface ClassInvitationService {

     boolean inviteToClass(Long classId, String inviteeUsername);

     boolean acceptInvitation(Long invitationId);

     boolean rejectInvitation(Long invitationId);

     boolean deleteAllInviteeInvitationsOfAClass(Long classId, Long inviteeId);

     ClassInvitationResponse getClassInvitation(Long invitationId);

     List<ClassInvitationResponse> getAllCurrentUserClassInvitations();

     List<AllClassesInvitationResponse> getAllClassInvitations(Long classId);

     String checkExistance(Long classId, String inviteeUsername);

     boolean revokeInvitation(Long invitationId);
}

