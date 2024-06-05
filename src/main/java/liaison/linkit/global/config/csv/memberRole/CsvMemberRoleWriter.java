package liaison.linkit.global.config.csv.memberRole;

import liaison.linkit.member.domain.Role;
import liaison.linkit.member.domain.repository.RoleRepository;
import liaison.linkit.member.dto.csv.MemberRoleCsvData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CsvMemberRoleWriter implements ItemWriter<MemberRoleCsvData> {
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends MemberRoleCsvData> chunk) throws Exception {

        Chunk<Role> memberRoles = new Chunk<>();

        chunk.forEach(memberRoleCsvData -> {
            Role role = Role.of(memberRoleCsvData.getRoleName());
            memberRoles.add(role);
        });

        roleRepository.saveAll(memberRoles);
    }
}
