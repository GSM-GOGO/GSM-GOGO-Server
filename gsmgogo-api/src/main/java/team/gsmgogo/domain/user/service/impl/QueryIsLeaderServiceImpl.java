package team.gsmgogo.domain.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.gsmgogo.domain.user.dto.response.UserIsLeaderResponse;
import team.gsmgogo.domain.user.enums.Role;
import team.gsmgogo.domain.user.service.QueryIsLeaderService;
import team.gsmgogo.global.facade.UserFacade;

@Service
@RequiredArgsConstructor
public class QueryIsLeaderServiceImpl implements QueryIsLeaderService {

    private final UserFacade userFacade;

    @Override
    @Transactional(readOnly = true)
    public UserIsLeaderResponse queryIsLeader() {
        return new UserIsLeaderResponse(userFacade.getCurrentUser().getRole().equals(Role.LEADER));
    }
}
