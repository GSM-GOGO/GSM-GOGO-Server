package team.gsmgogo.domain.team.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team.gsmgogo.domain.team.controller.dto.request.*;
import team.gsmgogo.domain.team.controller.dto.response.TeamListResponse;
import team.gsmgogo.domain.team.controller.dto.response.TeamNormalListResponse;
import team.gsmgogo.domain.team.enums.TeamType;
import team.gsmgogo.domain.team.service.*;
import team.gsmgogo.global.exception.error.ExpectedException;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamSaveService teamSaveService;
    private final TeamNormalSaveService teamNormalSaveService;
    private final TeamDeleteService teamDeleteService;
    private final TeamBadmintonSaveService teamBadmintonSaveService;
    private final TeamFollowService teamFollowService;
    private final TeamGetService teamGetService;
    private final TeamNormalDetailGetService teamNormalDetailGetService;

    @GetMapping
    public ResponseEntity<List<TeamListResponse>> getTeamList(@RequestParam(name = "type") String type){
        return ResponseEntity.ok(teamGetService.getTeam(type));
    }

    @PostMapping
    public ResponseEntity<Void> saveTeam(@RequestBody @Valid TeamSaveRequest request) {
        teamSaveService.saveTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/normal")
    public ResponseEntity<TeamNormalListResponse> getNormalTeamDetail(@RequestParam(name = "teamId") String teamId){
        TeamNormalListResponse teamNormalListResponse = teamNormalDetailGetService.execute(teamId);
        return ResponseEntity.ok(teamNormalListResponse);
    }

    @PostMapping("/normal")
    public ResponseEntity<Void> saveNormalTeam(@RequestBody @Valid List<TeamNormalSaveRequest> request) {
        teamNormalSaveService.saveNormalTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteTeam(@RequestBody @Valid TeamDeleteRequest request) {
        teamDeleteService.deleteTeam(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/badminton")
    public ResponseEntity<Void> saveTeamBadminton(@RequestBody @Valid TeamBadmintonSaveRequest request) {
        teamBadmintonSaveService.saveBadmintonTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/follow")
    public ResponseEntity<Void> followTeam(@RequestBody @Valid TeamFollowRequest request) {
        teamFollowService.followTeam(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
