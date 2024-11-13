package com.education.flashEng.service;

import com.education.flashEng.payload.request.CreateWordRequest;
import com.education.flashEng.payload.request.UpdateWordRequest;
import com.education.flashEng.payload.response.WordResponse;
import com.education.flashEng.payload.response.WordResponse;
import com.education.flashEng.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WordService {
    WordResponse createWord(CreateWordRequest createWordRequest);
    List<WordResponse> getWordBySetId(Long setId);
    boolean updateWord(UpdateWordRequest updateWordRequest);
    boolean deleteWordById(Long wordId);

    List<WordResponse> getCurrentUserWord();
}
