package notfound.ballog.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class WikiCrawlService {

    public String getPlayerImageUrl(String wikiUrl) {
        try {
            Document doc = Jsoup.connect(wikiUrl)
                    .ignoreHttpErrors(true)    // 403 에러를 던지지 않음
                    .timeout(10_000)
                    .get();

            // style="...width:XYZpx..." 에서 XYZ가 395~405 사이인 div들만 선택
            Elements imgs = doc.select(
                    "div[style~=(?i)width\\s*:\\s*(39[5-9]|40[0-5])px] img[src]:not([src^=data:])"
            );

            for (Element img : imgs) {
                String src = img.attr("src");
                log.info("▶ Found img[src] in nested parse: {}", src);
                return src.startsWith("//") ? "https:" + src : src;
            }

            // 2) OG 메타 태그로 fallback
            Element og = doc.selectFirst("meta[property=og:image]");
            if (og != null) {
                String content = og.attr("content");
                log.info("▶ Found og:image: {}", content);
                return content.startsWith("//") ? "https:" + content : content;
            }

            // 3) 데스크탑 도메인 OG 메타 태그 파싱
            Document desktopDoc = Jsoup.connect(wikiUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36")
                    .ignoreHttpErrors(true)
                    .timeout(10_000)
                    .get();
            Element desktopOg = desktopDoc.selectFirst("meta[property=og:image]");

            if (desktopOg != null) {
                String content = desktopOg.attr("content");
                log.info("▶ Found og:image on desktop domain: {}", content);
                return content.startsWith("//") ? "https:" + content : content;
            }
            log.warn("❌ 모든 크롤링 fallback 실패: {}", wikiUrl);
            return null;
        } catch (IOException e) {
            log.error("나무위키 이미지 크롤링 중 에러 발생 URL={}", wikiUrl, e);
            return null;
        }
    }
}