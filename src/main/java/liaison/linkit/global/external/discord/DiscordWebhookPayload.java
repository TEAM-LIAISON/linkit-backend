package liaison.linkit.global.external.discord;

import lombok.NonNull;

public record DiscordWebhookPayload(
    @NonNull String content
) {

}
