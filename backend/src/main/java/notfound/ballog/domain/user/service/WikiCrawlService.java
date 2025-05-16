package notfound.ballog.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class WikiCrawlService {

    public String getPlayerImageUrl(String wikiUrl) {
        try {
            // 나무위키 페이지 가져오기
            Document doc = Jsoup.connect(wikiUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .get();
            
            // 정확한 위치의 이미지 선택 (테이블 > tbody > tr > td > div > span > span > img.Bt0LH91h)
            Element img = doc.selectFirst("table tbody tr td div span span img.Bt0LH91h");
            
            if (img != null) {
                // data-src 먼저 확인 (지연 로딩)
                String dataSrc = img.attr("data-src");
                if (!dataSrc.isEmpty()) {
                    return dataSrc.startsWith("//") ? "https:" + dataSrc : dataSrc;
                }
                
                // 그 다음 src 확인
                String src = img.attr("src");
                if (!src.isEmpty()) {
                    return src.startsWith("//") ? "https:" + src : src;
                }
            }

            log.warn("선수 이미지를 찾을 수 없습니다: {}", wikiUrl);
            return null;
        } catch (IOException e) {
            log.error("나무위키 이미지 크롤링 오류: {}", e.getMessage());
            return null;
        }
    }
}