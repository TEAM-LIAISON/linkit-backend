package liaison.linkit.profile.exception.skill;

import liaison.linkit.common.exception.BaseCodeException;

public class SkillNotFoundException extends BaseCodeException {
    public static BaseCodeException EXCEPTION = new SkillNotFoundException();

    private SkillNotFoundException() {
        super(SkillErrorCode.SKILL_NOT_FOUND);
    }
}
