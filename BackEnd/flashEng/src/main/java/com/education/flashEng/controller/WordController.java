package com.education.flashEng.controller;

import com.education.flashEng.payload.request.CreateWordRequest;
import com.education.flashEng.payload.request.UpdateWordRequest;
import com.education.flashEng.payload.response.ApiResponse;
import com.education.flashEng.service.WordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/word")
public class WordController {
    @Autowired
    WordService wordService;

    @PostMapping
    public ResponseEntity<?> createWord(@Valid @ModelAttribute CreateWordRequest createWordRequest){
        ApiResponse<?> response = new ApiResponse<>(true, "Create Word Successfully", wordService.createWord(createWordRequest));
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{setId}")
    public ResponseEntity<?> getWordBySetId(@PathVariable Long setId){
        ApiResponse<?> response = new ApiResponse<>(true, "Get Word Successfully", wordService.getWordBySetId(setId));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/userCurrent")
    public ResponseEntity<?> getCurrentUserWord(){
        ApiResponse<?> response = new ApiResponse<>(true, "Get Current Reminder Words Successfully", wordService.getCurrentUserWord());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<?> updateWord(@Valid @ModelAttribute UpdateWordRequest updateWordRequest){
        ApiResponse<?> response = new ApiResponse<>(wordService.updateWord(updateWordRequest), "Update Word Successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteWord(@PathVariable Long id){
        ApiResponse<?> response = new ApiResponse<>(wordService.deleteWordById(id), "Delete Word Successfully", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
