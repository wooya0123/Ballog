//package notfound.ballog.domain.user.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class OpenAIService {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${openai.api.key}")
//    private String apiKey;
//
//    @Value("${openai.api.url}")
//    private String apiUrl;
//
//    @Value("${openai.model}")
//    private String model;
//
//    public String getCompletionFromGPT(String prompt) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("Authorization", "Bearer " + apiKey);
//
//        Map<String, Object> requestBody = new HashMap<>();
//        requestBody.put("model", model);
//
//        Map<String, String> message = new HashMap<>();
//        message.put("role", "user");
//        message.put("content", prompt);
//
//        requestBody.put("messages", List.of(message));
//        requestBody.put("temperature", 0.7);
//
//        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
//
//        try {
//            Map<String, Object> response = restTemplate.postForObject(apiUrl, requestEntity, Map.class);
//            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
//            Map<String, Object> messageResponse = (Map<String, Object>) choices.get(0).get("message");
//            return (String) messageResponse.get("content");
//        } catch (Exception e) {
//            return "선수 추천 중 오류가 발생했습니다: " + e.getMessage();
//        }
//    }
//}