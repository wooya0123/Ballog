package notfound.ballog.common.jwt;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class TokenBlacklistService {
    private final Set<String> blacklisted = new ConcurrentSkipListSet<>();

    public void addToBlacklist(String token) {
        blacklisted.add(token);
    }

    public boolean isBlacklisted(String token) {
        return blacklisted.contains(token);
    }
}
