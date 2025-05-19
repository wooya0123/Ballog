package notfound.ballog.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class WikiCrawlService {

    public String getPlayerImageUrl(String wikiUrl) {
        try {
            Document doc = Jsoup.connect(wikiUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                            "(KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .referrer("https://namu.wiki/")
                    .timeout(10000)
                    .get();

            // style="...width:XYZpx..." 에서 XYZ가 395~405 사이인 div들만 선택
            Elements containers = doc.select(
                    "div[style~=(?i)width\\s*:\\s*(39[5-9]|40[0-5])px]"
            );

            for (Element container : containers) {
                // 그 안의 table > tbody > tr > td > div > span > span > img[src] 중
                // data: URL이 아닌 실제 src를 가진 첫 번째 이미지를 찾는다
                Element img = container.selectFirst(
                        "table > tbody > tr > td > div > span > span img[src]:not([src^=data:])"
                );
                if (img != null) {
                    String src = img.attr("src");
                    return src.startsWith("//") ? "https:" + src : src;
                }
            }

            log.warn("style=width:400px div 내에 img[src]를 찾을 수 없습니다: {}", wikiUrl);
            return null;
        } catch (IOException e) {
            log.error("나무위키 이미지 크롤링 오류: {}", e.getMessage());
            return null;
        }
    }
}