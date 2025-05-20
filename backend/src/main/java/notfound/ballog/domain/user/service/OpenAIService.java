package notfound.ballog.domain.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import notfound.ballog.common.response.BaseResponseStatus;
import notfound.ballog.exception.InternalServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAIService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    @Value("${OPENAI_API_URL}")
    private String apiUrl;

    @Value("${OPENAI_MODEL}")
    private String model;

    public Map<String, Object> getCompletionFromGPT(String prompt) {
        // 1. http 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);

        // 2. 요청 바디 생성
        Map<String, Object> requestBody = createRequestBody(createStructuredPrompt(prompt));
        log.info("프롬프트: {}", createStructuredPrompt(prompt));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // api 호출
            Map<String, Object> response = restTemplate.postForObject(apiUrl, requestEntity, Map.class);
            return extractContentFromResponse(response);
        } catch (Exception e) {
            log.error("OpenAI API 호출 중 오류 발생", e);
            throw new InternalServerException(BaseResponseStatus.RECOMMAND_PLAYER_GPT_ERROR);
        }
    }

    private String createStructuredPrompt(String prompt) {
        return prompt + "\n\n다음과 같은 JSON 구조로 응답해주세요:\n" +
                "{\n" +
                "  \"analysis\": \"플레이 스타일 분석 내용 (유저 이름을 사용하여 작성, 이름 뒤에 님을 붙여서 작성 {예 : 플레이어님})\",\n" +
                "  \"recommendedPlayer\": \n" +
                "    {\n" +
                "      \"name\": \"선수 이름 (해외 선수 포함 이름을 한글로 번역해서 보여줌)\",\n" +
                "      \"position\": \"포지션 (영어로 {예 : FW, CM, WB, CB, LW, RW 등등})\",\n" +
                "      \"style\": \"플레이 스타일\",\n" +
                "      \"reason\": \"유사한 이유 (유저 이름을 사용하여 작성, 이름 뒤에 님을 붙여서 작성 {예 : 플레이어님})\"\n" +
                "      \"naver\": \"네이버에 선수 이름으로 검색한 링크\"\n" +
                "    }\n" +
                "  ,\n" +
                "  \"conclusion\": \"한 줄로 짧고 간결하게 작성 (예: 활동량 기반 전술 미드필더형 선수 등등)\"\n" +
                "}";
    }

    private Map<String, Object> createRequestBody(String structuredPrompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        
        Map<String, String> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", structuredPrompt);
        
        requestBody.put("messages", Collections.singletonList(message));
        requestBody.put("temperature", 0.7);
        
        Map<String, String> responseFormat = new HashMap<>();
        responseFormat.put("type", "json_object");
        requestBody.put("response_format", responseFormat);
        
        return requestBody;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> extractContentFromResponse(Map<String, Object> response) {
        if (response == null) {
            return Map.of("error", "API 응답이 null입니다");
        }

        try {
            log.info("gpt 응답: {}", response);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                throw new InternalServerException(BaseResponseStatus.RECOMMAND_PLAYER_GPT_ERROR);
            }

            Map<String, Object> messageResponse = (Map<String, Object>) choices.get(0).get("message");
            if (messageResponse == null) {
                throw new InternalServerException(BaseResponseStatus.RECOMMAND_PLAYER_GPT_ERROR);
            }

            String content = Optional.ofNullable(messageResponse.get("content"))
                .map(Object::toString)
                .orElse("{\"error\": \"API 응답에 내용이 없습니다\"}");

            try {
                return objectMapper.readValue(content, Map.class);
            } catch (Exception e) {
                log.error("JSON 파싱 중 오류 발생", e);
                throw new InternalServerException(BaseResponseStatus.RECOMMAND_PLAYER_GPT_ERROR);
            }
        } catch (ClassCastException e) {
            log.error("API 응답 파싱 중 형변환 오류", e);
            throw new InternalServerException(BaseResponseStatus.RECOMMAND_PLAYER_GPT_ERROR);
        }
    }

}