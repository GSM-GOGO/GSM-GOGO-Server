package team.gsmgogo.domain.team.service;

import team.gsmgogo.domain.team.controller.dto.response.TeamListResponse;
import team.gsmgogo.domain.team.controller.dto.response.TeamNormalListResponse;

import java.util.List;

public interface TeamNormalDetailGetService {
    TeamNormalListResponse execute(String type);
}
