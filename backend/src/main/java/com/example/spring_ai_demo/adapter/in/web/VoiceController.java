package com.example.spring_ai_demo.adapter.in.web;

import com.example.spring_ai_demo.adapter.in.web.dto.TranscriptionResult;
import com.example.spring_ai_demo.adapter.out.saas.OpenAIAudioTranscriptionService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class VoiceController {

    private final OpenAIAudioTranscriptionService transcriptionService;

    public VoiceController(OpenAIAudioTranscriptionService transcriptionService) {
        this.transcriptionService = transcriptionService;
    }


    @PostMapping(value = "/api/voice/transcribe", consumes = "multipart/form-data")
    public TranscriptionResult transcribe(@RequestParam("audio") MultipartFile file) throws Exception {
        var resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        return new TranscriptionResult(transcriptionService.transcribe(resource));
    }
}
