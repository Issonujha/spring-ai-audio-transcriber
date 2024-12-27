package com.ai.audio_transcriber.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transcribe")
public class TranscriberController {
	
	private final OpenAiAudioTranscriptionModel transcriptionModel;
	
	public TranscriberController(@Value("${spring.ai.openai.api-key}") String apiKey) {
		OpenAiAudioApi aiAudioApi = new OpenAiAudioApi(apiKey);
		this.transcriptionModel = new OpenAiAudioTranscriptionModel(aiAudioApi);
	}
	
	@PostMapping
	public ResponseEntity<String> transcribeAudio(@RequestParam("file") MultipartFile file) throws IOException {
		File tempFile = File.createTempFile("audio", ".wav");
		file.transferTo(tempFile);

		OpenAiAudioTranscriptionOptions transcriptionOption = OpenAiAudioTranscriptionOptions.builder()
				.withResponseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT).withLanguage("en").withTemperature(0f)
				.build();

		FileSystemResource audioFile = new FileSystemResource(tempFile);
		AudioTranscriptionPrompt audioTranscriptionPrompt = new AudioTranscriptionPrompt(audioFile,
				transcriptionOption);
		AudioTranscriptionResponse response = transcriptionModel.call(audioTranscriptionPrompt);
		tempFile.delete();
		return new ResponseEntity<String>(response.getResult().getOutput(), HttpStatus.OK);
	}
	
	
	
	

}
