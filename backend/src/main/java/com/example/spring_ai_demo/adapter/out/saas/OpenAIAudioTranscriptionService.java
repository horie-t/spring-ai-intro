package com.example.spring_ai_demo.adapter.out.saas;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

@Service
public class OpenAIAudioTranscriptionService {

    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public OpenAIAudioTranscriptionService(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }


    public String transcribe(ByteArrayResource audioResource) throws Exception {

        var options = OpenAiAudioTranscriptionOptions.builder()
                .model(OpenAiAudioApi.WhisperModel.WHISPER_1.getValue())
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.JSON)
                .build();

        var prompt = new AudioTranscriptionPrompt(audioResource, options);
        return transcriptionModel.call(prompt).getResult().getOutput();
    }
}
