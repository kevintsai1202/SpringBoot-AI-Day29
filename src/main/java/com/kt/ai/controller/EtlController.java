package com.kt.ai.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kt.ai.service.EtlService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/ai/etl")
@RequiredArgsConstructor
public class EtlController {
	
	private final EtlService etlService;
	
	@GetMapping("readjson")
	public List<Document> readJsonFile(){
		return etlService.loadJsonAsDocuments();
	}
	
	@GetMapping("importjson")
	public void importJson(){
		etlService.importStudentsAsJson();
	}

	@GetMapping("readtext")
	public List<Document> readTextFile(){
		return etlService.loadTextAsDocuments();
	}
	
	@GetMapping("importtext")
	public void importText(){
		etlService.importText();
	}
	
	@GetMapping("readpdf")
	public List<Document> readPdfFile() throws IOException{
		return etlService.loadPdfAsDocuments();
	}
	
	@GetMapping("importpdf")
	public void importPdf() throws IOException{
		etlService.importPdf();
	}
	
	@GetMapping("importpdf1")
	public void importPdf1() throws IOException{
		etlService.importPdf1();
	}
	
	@GetMapping("readpptx")
	public List<Document> readPptxFile() throws IOException{
		return etlService.loadPptxAsDocuments();
	}
	
	@GetMapping("importpptx")
	public void importPptx() throws IOException{
		etlService.importPptx();
	}
	
	@GetMapping("search")
	public ResponseEntity<Resource> search(String query) throws IOException {
		Path path = Paths.get("./result.md");
		Files.write(path, etlService.search(query).getBytes());
		
        Resource resource = new UrlResource(path.toUri());
		
		return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"result.md\"")
                .body(resource);
	}
	
	@GetMapping("readissue")
	public List<Document> readIssue() throws IOException{
		return etlService.getAllIssue();
	}
	
	@GetMapping("importissue")
	public String importIssue() throws IOException{
		etlService.importIssue();
		return "OK";
	}
	
	@GetMapping("issuesearch")
	public List<Document> issueSearch(String query) throws IOException {
		return etlService.issueSearch(query);
	}
	
}
