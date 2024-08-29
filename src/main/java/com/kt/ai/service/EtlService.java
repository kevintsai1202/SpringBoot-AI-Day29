package com.kt.ai.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonMetadataGenerator;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.KeywordMetadataEnricher;
import org.springframework.ai.transformer.SummaryMetadataEnricher;
import org.springframework.ai.transformer.SummaryMetadataEnricher.SummaryType;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import com.kt.ai.issue.Issue;
import com.kt.ai.issue.IssueReposotory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EtlService {
	private final VectorStore vectorStore;
	private final ChatModel chatModel;
	private final ChatClient chatClient;
	private final IssueReposotory issueReposotory;
	
	@Value("classpath:students.json")
	private Resource jsonResource;
	
	@Value("classpath:springai.txt")
	private Resource textResource;
	
	public List<Document> loadJsonAsDocuments() {
        JsonReader jsonReader = new JsonReader(jsonResource, new JsonMetadataGenerator() {
			@Override
			public Map<String, Object> generate(Map<String, Object> jsonMap) {
				return Map.of("Type", "Student grades");
			}
		}, "name","grade");
        return jsonReader.get();
	}
	
	public void importStudentsAsJson() {
		vectorStore.write(loadJsonAsDocuments());
	}
	
	public List<Document> loadTextAsDocuments() {
		TextReader textReader = new TextReader(textResource);
		textReader.getCustomMetadata().put("ITHOME", "16th");
        return textReader.get();
	}
	
	public void importText() {
		TokenTextSplitter splitter = new TokenTextSplitter();
		vectorStore.write(splitter.split(loadTextAsDocuments()));
	}

	public List<Document> loadPdfAsDocuments() throws IOException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = new Resource[0];
		resources = resolver.getResources("./pdf/*.pdf");
		List<Document> allDocs = new ArrayList<>();
        for (Resource pdfResource : resources) {
        	log.info("Process File:{}", pdfResource.getFilename());
        	List<Document> docs = new ArrayList<>();
        	try {
        		ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader(pdfResource);
        		docs.addAll(pdfReader.read());
        	} catch (IllegalArgumentException e) {
    			PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource);
    			docs.addAll(pdfReader.read());
    		}
        	if (docs.size()>0) {
        		docs = summaryDocuments(keywordDocuments(docs));
        		allDocs.addAll(docs);
        	}
        }
        return allDocs;
	}
	
	List<Document> summaryDocuments(List<Document> documents) {
		log.info("Process summary");
		SummaryMetadataEnricher summaryEnricher =  new SummaryMetadataEnricher(chatModel,
	            List.of(SummaryType.CURRENT)
				);
		return summaryEnricher.apply(documents);
    }
	
	List<Document> keywordDocuments(List<Document> documents) {
		log.info("Process keyword");
        KeywordMetadataEnricher keywordEnricher = new KeywordMetadataEnricher(chatModel, 3);
        return keywordEnricher.apply(documents);
    }
	
	public void importPdf() throws IOException {
		TokenTextSplitter splitter = new TokenTextSplitter(800,350,5,10000,true);
		vectorStore.write(splitter.split(loadPdfAsDocuments()));
	}
	
	public void importPdf1() throws IOException {
		TokenTextSplitter splitter = new TokenTextSplitter();
		vectorStore.write(splitter.split(loadPdfAsDocuments()));
	}
	
	public List<Document> loadPptxAsDocuments() throws IOException {
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = new Resource[0];
		resources = resolver.getResources("./pptx/*.pptx");
        List<Document> docs = new ArrayList<>();
        for (Resource pptxResource : resources) {
    		TikaDocumentReader  pptxReader = new TikaDocumentReader(pptxResource);
    		docs.addAll(pptxReader.read());
        }
        return docs;
	}
	
	public void importPptx() throws IOException {
		TokenTextSplitter splitter = new TokenTextSplitter(600,350,5,10000,true);
		vectorStore.write(splitter.split(loadPptxAsDocuments()));
	}

	
	public String search(String query) {
		return chatClient.prompt().advisors(new QuestionAnswerAdvisor(vectorStore)).user(query).call().content();
	}
	
	
	public List<Document> issueSearch(String query) {
		return vectorStore.similaritySearch(query);
	}
	
	public List<Document> getAllIssue(){
		List<Issue> issues = issueReposotory.findAll();
		Map<String, Object> metadata = new HashMap<>();
		metadata.put("type", "issue");
		List<Document> issueDocs = issues.stream().map(issue -> 
		                  new Document(issue.getId().toString(), 
		                		       issue.toString(), 
		                		       metadata)).toList();
		
		
		return issueDocs;
	}
	
	public void importIssue() {
		vectorStore.write(keywordDocuments(getAllIssue()));
	}
}
