package com.education.flashEng.service.Impl;

import com.education.flashEng.entity.SetEntity;
import com.education.flashEng.entity.StudySessionEntity;
import com.education.flashEng.entity.UserEntity;
import com.education.flashEng.entity.WordEntity;
import com.education.flashEng.enums.AccessModifierType;
import com.education.flashEng.exception.EntityNotFoundWithIdException;
import com.education.flashEng.payload.request.CreateWordRequest;
import com.education.flashEng.payload.request.UpdateWordRequest;
import com.education.flashEng.payload.response.WordResponse;
import com.education.flashEng.repository.SetRepository;
import com.education.flashEng.repository.WordRepository;
import com.education.flashEng.service.StudySessionService;
import com.education.flashEng.service.UserService;
import com.education.flashEng.service.WordService;
import com.education.flashEng.util.TimeUtil;
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
    @Autowired
    private TimeUtil timeUtil;

    @Transactional
    @Override
    public WordResponse createWord(CreateWordRequest createWordRequest) {
        UserEntity user = userService.getUserFromSecurityContext();

        SetEntity setEntity = setRepository.findById(createWordRequest.getSetId())
                .orElseThrow(() -> new EntityNotFoundWithIdException("SetEntity", createWordRequest.getSetId().toString()));
        if (!Objects.equals(setEntity.getUserEntity().getId(), user.getId())) {
            throw new IllegalArgumentException("You do not have permission to create word in this set");
        }

        try {
            WordEntity wordEntity = new WordEntity();
            modelMapper.map(createWordRequest, wordEntity);
            wordEntity.setSetEntity(setEntity);

            if (createWordRequest.getImage() != null && !createWordRequest.getImage().isEmpty()) {
                // Cho phép nhiều định dạng (png, jpg, jpeg)
                List<String> validImageTypes = Arrays.asList("image/png", "image/jpeg", "image/jpg");
                if (!validImageTypes.contains(createWordRequest.getImage().getContentType())) {
                    throw new IllegalArgumentException("Please send a valid image file (png, jpg, jpeg)");
                }
                Map<String, String> uploadResult = cloudinaryService.uploadFile(createWordRequest.getImage(), setEntity.getId().toString());
                String imageUrl = uploadResult.get("url");
                String publicId = uploadResult.get("publicId");
                wordEntity.setImage(imageUrl);
                wordEntity.setImagePublicId(publicId);
            }

            if (createWordRequest.getAudio() != null && !createWordRequest.getAudio().isEmpty()) {
                wordEntity.setAudio(createWordRequest.getAudio());
            }

            WordEntity wordSaved = wordRepository.save(wordEntity);
            WordResponse wordResponse = new WordResponse();
            modelMapper.map(wordSaved, wordResponse);
            return wordResponse;
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to upload file to Cloudinary");
        }
    }

    @Override
    public List<WordResponse> getWordBySetId(Long setId) {
        SetEntity setEntity = setRepository.findById(setId)
                .orElseThrow(() -> new EntityNotFoundWithIdException("SetEntity", setId.toString()));
        if(!setEntity.getPrivacyStatus().equals(AccessModifierType.getKeyfromValue("Public"))){
            UserEntity user = userService.getUserFromSecurityContext();
            if((user.getClassMemberEntityList().stream().noneMatch(classMemberEntity -> classMemberEntity.getClassEntity().getSetsEntityList().contains(setEntity))&&setEntity.getPrivacyStatus().equals(AccessModifierType.getKeyfromValue("Class")))||(setEntity.getPrivacyStatus().equals(AccessModifierType.getKeyfromValue("Private"))&&!Objects.equals(setEntity.getUserEntity().getId(), user.getId()))){
                throw new IllegalArgumentException("You do not have permission to get word in this set");
            }
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
            if(updateWordRequest.getImage() != null && !updateWordRequest.getImage().isEmpty()){
                String imageUrl = cloudinaryService.updateFile(wordEntity.getImagePublicId(), updateWordRequest.getImage());
                wordEntity.setImage(imageUrl);
            }
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
            if(wordEntity.getImagePublicId() != null)
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
                if(timeUtil.addFractionOfDay(studySessionEntity.getCreatedAt(),studySessionEntity.getReminderTime()).isAfter(LocalDateTime.now()))
                    continue;

                WordResponse wordResponse = new WordResponse();
                modelMapper.map(studySessionEntity.getWordEntity(), wordResponse);
                wordResponses.add(wordResponse);
            }
        }
        return wordResponses;
    }
}
