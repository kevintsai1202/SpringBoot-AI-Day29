package com.kt.ai.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

	@Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.build();
    }
	
//	@Bean
//    public SummaryMetadataEnricher summaryMetadata(OpenAiChatModel chatModel) {
//        return new SummaryMetadataEnricher(chatModel,
//            List.of(SummaryType.PREVIOUS, SummaryType.CURRENT, SummaryType.NEXT));
//    }
	
}
