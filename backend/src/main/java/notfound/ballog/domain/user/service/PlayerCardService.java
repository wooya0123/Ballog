package notfound.ballog.domain.user.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import notfound.ballog.domain.user.entity.PlayerCard;
import notfound.ballog.domain.user.repository.PlayerCardRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerCardService {

    private final PlayerCardRepository playerCardRepository;

    // 플레이어 카드 생성
    @Transactional
    public void addPlayerCard(PlayerCard playerCard) {
        playerCardRepository.save(playerCard);
    }
}
