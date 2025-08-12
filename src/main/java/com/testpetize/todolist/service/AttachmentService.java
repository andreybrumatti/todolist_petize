package com.testpetize.todolist.service;

import com.testpetize.todolist.domain.model.Attachment;
import com.testpetize.todolist.exception.AttachmentException;
import com.testpetize.todolist.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;

    @Transactional
    public void saveAttachment(MultipartFile file) {
        try {
            Attachment attachment = new Attachment();
            attachment.setName(file.getOriginalFilename());
            attachment.setType(file.getContentType());
            attachment.setData(file.getBytes());

            attachmentRepository.save(attachment);
        } catch (IOException e) {
            throw AttachmentException.fileProcessingError();
        }
    }
}