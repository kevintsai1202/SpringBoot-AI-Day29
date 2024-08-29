package com.kt.ai.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import lombok.Data;

@Data
@Service
public class SearchService {
	private final VectorStore vectorStore;
	private final ChatClient chatClient;

	public Resource search(String query) throws IOException {
		Path pathResult = Paths.get("./result.md");
		String result = chatClient.prompt().advisors(new QuestionAnswerAdvisor(vectorStore)).user(query).call().content();
		Files.writeString(pathResult, result);
        Resource resource = new UrlResource(pathResult.toUri());
		return resource;
	}
}
