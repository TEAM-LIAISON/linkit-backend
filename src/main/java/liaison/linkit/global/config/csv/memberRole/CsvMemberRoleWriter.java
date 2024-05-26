package liaison.linkit.global.config.csv.memberRole;

import liaison.linkit.member.domain.MemberRole;
import liaison.linkit.member.domain.repository.MemberRoleRepository;
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
    private final MemberRoleRepository memberRoleRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends MemberRoleCsvData> chunk) throws Exception {

        Chunk<MemberRole> memberRoles = new Chunk<>();

        chunk.forEach(memberRoleCsvData -> {
            MemberRole memberRole = MemberRole.of(memberRoleCsvData.getRoleName());
            memberRoles.add(memberRole);
        });

        memberRoleRepository.saveAll(memberRoles);
    }
}
