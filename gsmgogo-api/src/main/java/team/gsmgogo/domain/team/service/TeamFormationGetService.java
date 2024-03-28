package team.gsmgogo.domain.team.service;

import team.gsmgogo.domain.team.controller.dto.response.TeamFormationResponse;

public interface TeamFormationGetService {
    TeamFormationResponse execute(String teamId);
}
