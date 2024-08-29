根據上下文信息，這裡是一個使用Spring AI構建的RAG應用的範例，涵蓋了ETL（提取、轉換和載入）過程以及搜尋和生成資料的部分。以下是程式範例：

### 1. ETL過程

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EtlService {

    @Value("file:/path/to/pdf/directory/*") // 指定PDF檔案的目錄
    private Resource pdfResource;

    private final VectorStore vectorStore;

    public EtlService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public List<Document> loadPdfAsDocuments() {
        PdfReader pdfReader = new PdfReader(pdfResource); // 自定義PDF讀取器
        // 這裡假設PdfReader能將PDF內容讀取為Document列表
        return pdfReader.getDocuments();
    }

    public void importPdf() {
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> documents = loadPdfAsDocuments();
        // 將Document拆分為小塊並寫入向量資料庫
        vectorStore.write(splitter.split(documents));
    }

    public List<Document> searchTextData(String queryStr) {
        return vectorStore.similaritySearch(SearchRequest.query(queryStr));
    }
}
```

### 2. 搜尋與生成資料

```java
@Service
public class RAGService {

    private final EtlService etlService;

    public RAGService(EtlService etlService) {
        this.etlService = etlService;
    }

    public String generateResponse(String userQuery) {
        // 使用ETL服務進行資料搜尋
        List<Document> similarDocuments = etlService.searchTextData(userQuery);
        
        // 假設有一個生成模型來生成回覆
        String response = generateFromSimilarDocuments(similarDocuments, userQuery);
        return response;
    }

    private String generateFromSimilarDocuments(List<Document> documents, String userQuery) {
        // 這裡實現生成回覆的邏輯，根據相似文檔與用戶查詢生成回覆
        StringBuilder sb = new StringBuilder();
        for (Document doc : documents) {
            sb.append(doc.getContent()).append("\n");
        }
        // 這裡簡單地將相似內容拼接起來作為回覆
        return sb.toString();
    }
}
```

### 3. 使用示例

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RAGController {

    private final RAGService ragService;

    @Autowired
    public RAGController(RAGService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/generate-response")
    public String generateResponse(@RequestParam String query) {
        return ragService.generateResponse(query);
    }
}
```

### 總結
這個範例展示了如何使用Spring AI來構建一個RAG應用，從PDF檔案中提取資料，轉換為可用於向量資料庫的格式，並能夠根據用戶查詢生成相應的回覆。請根據實際需求調整路徑和類的實現。