package com.education.flashEng.service.Impl;

import com.education.flashEng.entity.SetEntity;
import com.education.flashEng.entity.StudySessionEntity;
import com.education.flashEng.entity.UserEntity;
import com.education.flashEng.entity.WordEntity;
import com.education.flashEng.exception.EntityNotFoundWithIdException;
import com.education.flashEng.payload.request.CreateWordRequest;
import com.education.flashEng.payload.request.UpdateWordRequest;
import com.education.flashEng.payload.response.WordResponse;
import com.education.flashEng.repository.SetRepository;
import com.education.flashEng.repository.WordRepository;
import com.education.flashEng.service.StudySessionService;
import com.education.flashEng.service.UserService;
import com.education.flashEng.service.WordService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class WordServiceImpl implements WordService {

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private SetRepository setRepository;
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserService userService;
    @Autowired
    private StudySessionService studySessionService;

    @Transactional
    @Override
    public WordResponse createWord(CreateWordRequest createWordRequest){
        UserEntity user = userService.getUserFromSecurityContext();

        if(createWordRequest.getImage().isEmpty()){
            throw new IllegalArgumentException("Invalid File");
        }
        // Cho phép nhiều định dạng ảnh (png, jpg, jpeg)
        List<String> validImageTypes = Arrays.asList("image/png", "image/jpeg", "image/jpg");
        if (!validImageTypes.contains(createWordRequest.getImage().getContentType())) {
            throw new IllegalArgumentException("Please send a valid image file (png, jpg, jpeg)");
        }

        SetEntity setEntity = setRepository.findById(createWordRequest.getSetId())
                .orElseThrow(() -> new EntityNotFoundWithIdException("SetEntity", createWordRequest.getSetId().toString()));
        if(!Objects.equals(setEntity.getUserEntity().getId(), user.getId())){
            throw new IllegalArgumentException("You do not permission to create word in this set");
        }
        try{
            WordEntity wordEntity = new WordEntity();
            modelMapper.map(createWordRequest, wordEntity);
            wordEntity.setSetEntity(setEntity);
            // Tải ảnh lên Cloudinary và lưu URL, publicId vào cơ sở dữ liệu
            Map<String, String> uploadResult = cloudinaryService.uploadFile(createWordRequest.getImage(), setEntity.getId().toString());
            String imageUrl = uploadResult.get("url");
            String publicId = uploadResult.get("publicId");
            wordEntity.setImage(imageUrl);
            wordEntity.setImagePublicId(publicId);
            WordEntity wordSaved = wordRepository.save(wordEntity);
            WordResponse wordResponse = new WordResponse();
            modelMapper.map(wordSaved, wordResponse);
            return wordResponse;
        }catch (IOException e){
            throw new IllegalArgumentException("Failed to upload file to Cloudinary");
        }
    }

    @Override
    public List<WordResponse> getWordBySetId(Long setId) {
        UserEntity user = userService.getUserFromSecurityContext();
        SetEntity setEntity = setRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("SetEntity", setId.toString()));
        if((user.getClassMemberEntityList().stream().noneMatch(classMemberEntity -> classMemberEntity.getClassEntity().getSetsEntityList().contains(setEntity))&&setEntity.getPrivacyStatus().equals("Class"))||(setEntity.getPrivacyStatus().equals("PRIVATE")&&!Objects.equals(setEntity.getUserEntity().getId(), user.getId()))){
            throw new IllegalArgumentException("You do not permission to get word in this set");
        }
        List<WordEntity> wordEntities = wordRepository.findAllBySetEntityId(setId);
        List<WordResponse> wordListResponses = new ArrayList<>();
        for(WordEntity wordEntity : wordEntities){
            WordResponse wordListResponse = new WordResponse();
            modelMapper.map(wordEntity, wordListResponse);
            wordListResponses.add(wordListResponse);
        }
        return wordListResponses;
    }

    @Transactional
    @Override
    public boolean updateWord(UpdateWordRequest updateWordRequest) {
        UserEntity user = userService.getUserFromSecurityContext();
        WordEntity wordEntity = wordRepository.findById(updateWordRequest.getId())
                .orElseThrow(() -> new EntityNotFoundWithIdException("WordEntity", updateWordRequest.getId().toString()));

        if(!Objects.equals(wordEntity.getSetEntity().getUserEntity().getId(), user.getId())){
            throw new IllegalArgumentException("You do not permission to update word in this set");
        }
        modelMapper.map(updateWordRequest, wordEntity);
        try{
            String imageUrl = cloudinaryService.updateFile(wordEntity.getImagePublicId(), updateWordRequest.getImage());
            wordEntity.setImage(imageUrl);
            wordRepository.save(wordEntity);
        }catch (IOException e){
            throw new IllegalArgumentException("Failed to update file to Cloudinary");
        }
        return true;
    }

    @Transactional
    @Override
    public boolean deleteWordById(Long wordId) {
        WordEntity wordEntity = wordRepository.findById(wordId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("WordEntity", wordId.toString()));
        if(!Objects.equals(wordEntity.getSetEntity().getUserEntity().getId(), userService.getUserFromSecurityContext().getId())){
            throw new AccessDeniedException("You do not permission to delete word in this set");
        }
        try{
            cloudinaryService.deleteImage(wordEntity.getImagePublicId());
            wordRepository.delete(wordEntity);
        }
        catch (IOException e){
            throw new IllegalArgumentException("Failed to delete file from Cloudinary");
        }
        return true;
    }

    @Override
    public List<WordResponse> getCurrentUserWord() {
        UserEntity user = userService.getUserFromSecurityContext();
        List<StudySessionEntity> studySessionEntities = user.getStudySessionEntityList();

        studySessionEntities.sort(Comparator.comparing(StudySessionEntity::getCreatedAt).reversed());

        Set<Long> wordIds = new HashSet<>();
        List<WordResponse> wordResponses = new ArrayList<>();

        for (StudySessionEntity studySessionEntity : studySessionEntities) {
            Long wordId = studySessionEntity.getWordEntity().getId();
            if (!wordIds.contains(wordId)) {
                wordIds.add(wordId);
                if(studySessionService.getReminderTimeBasedOnLevel(studySessionEntity.getDifficulty(), studySessionEntity.getCreatedAt()).isAfter(LocalDateTime.now()))
                    continue;
                
                WordResponse wordResponse = new WordResponse();
                modelMapper.map(studySessionEntity.getWordEntity(), wordResponse);
                wordResponses.add(wordResponse);
            }
        }
        return wordResponses;
    }
}
