package com.education.flashEng.controller;


import com.education.flashEng.payload.request.ClassRoleChangeRequest;
import com.education.flashEng.payload.response.ApiResponse;
import com.education.flashEng.service.ClassMemberService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/class/member")
public class ClassMemberController {

    @Autowired
    private ClassMemberService classMemberService;

    @GetMapping("/list")
    public ResponseEntity<?> getMembers(@RequestParam @NotNull(message = "classId is required") Long classId) {
        ApiResponse<?> response = new ApiResponse<>(true, "Members Fetched Successfully", classMemberService.getAllMembers(classId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteMember(@RequestParam @NotNull(message = "userId is required") Long userId,
                                          @RequestParam @NotNull(message = "classId is required") Long classId) {
        ApiResponse<String> response = new ApiResponse<>(classMemberService.deleteClassMember(userId, classId), "Member Deleted Successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/leave")
    public ResponseEntity<?> leaveClass(@RequestParam @NotNull(message = "classId is required") Long classId) {
        ApiResponse<String> response = new ApiResponse<>(classMemberService.leaveClass(classId), "Left Class Successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/role")
    public ResponseEntity<?> changeRole(@Valid @RequestBody ClassRoleChangeRequest classRoleChangeRequest) {
        ApiResponse<String> response = new ApiResponse<>(classMemberService.changeRole(classRoleChangeRequest.getUserId(), classRoleChangeRequest.getClassId(), classRoleChangeRequest.getRole()), "Role Changed Successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
