package liaison.linkit.global.presentation.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SubscribeEvent {
    private final Long memberId;
    private final String emailId;
}
