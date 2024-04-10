package liaison.linkit.profile.presentation;

import jakarta.validation.Valid;
import liaison.linkit.auth.Auth;
import liaison.linkit.auth.MemberOnly;
import liaison.linkit.auth.domain.Accessor;
import liaison.linkit.profile.dto.request.AntecedentsCreateRequest;
import liaison.linkit.profile.service.AntecedentsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/antecedents")
public class AntecedentsController {

    private final AntecedentsService antecedentsService;

    @PostMapping
    @MemberOnly
    public ResponseEntity<Void> createAntecedents(
            @Auth final Accessor accessor,
            @RequestBody @Valid AntecedentsCreateRequest antecedentsCreateRequest
            ){
        final Long antecedentsId = antecedentsService.save(accessor.getMemberId(), antecedentsCreateRequest);
        return ResponseEntity.created(URI.create("/antecedents/" + antecedentsId)).build();
    }
}
