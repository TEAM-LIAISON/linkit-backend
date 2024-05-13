package liaison.linkit.admin.service;

import liaison.linkit.admin.domain.AdminMember;
import liaison.linkit.admin.domain.repository.AdminMemberRepository;
import liaison.linkit.admin.domain.type.AdminType;
import liaison.linkit.admin.dto.request.AdminMemberCreateRequest;
import liaison.linkit.admin.dto.request.PasswordUpdateRequest;
import liaison.linkit.admin.dto.response.AdminMemberResponse;
import liaison.linkit.admin.infrastructure.PasswordEncoder;
import liaison.linkit.global.exception.AdminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static liaison.linkit.global.exception.ExceptionCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminMemberService {
    private final AdminMemberRepository adminMemberRepository;
    private final PasswordEncoder passwordEncoder;

    public List<AdminMemberResponse> getAdminMembers() {
        final List<AdminMember> adminMembers = adminMemberRepository.findAll();
        return adminMembers.stream()
                .map(AdminMemberResponse::from)
                .toList();
    }

    public Long createAdminMember(final AdminMemberCreateRequest request) {
        if (adminMemberRepository.existsByUsername(request.getUsername())) {
            throw new AdminException(DUPLICATED_ADMIN_USERNAME);
        }

        return adminMemberRepository.save(
                new AdminMember(
                        request.getUsername(),
                        passwordEncoder.encode(request.getPassword()),
                        AdminType.getMappedAdminType(request.getAdminType())
                )
        ).getId();
    }

    public void updatePassword(final Long adminMemberId, final PasswordUpdateRequest request) {
        final AdminMember adminMember = adminMemberRepository.findById(adminMemberId)
                .orElseThrow(() -> new AdminException(NOT_FOUND_ADMIN_ID));

        if (!passwordEncoder.matches(request.getCurrentPassword(), adminMember.getPassword())) {
            throw new AdminException(INVALID_CURRENT_PASSWORD);
        }

        final AdminMember updatedAdminMember = new AdminMember(
                adminMember.getId(),
                adminMember.getUsername(),
                passwordEncoder.encode(request.getNewPassword()),
                adminMember.getAdminType()
        );
        adminMemberRepository.save(updatedAdminMember);
    }
}
