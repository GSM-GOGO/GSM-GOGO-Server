package team.gsmgogo.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.gsmgogo.domain.user.dto.response.*;
import team.gsmgogo.domain.user.service.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final QueryUserInfoService queryUserInfoService;
    private final QueryUserIdService queryUserIdService;
    private final QueryUserFollowTeamService queryUserFollowTeamService;
    private final QueryUserPointService queryUserPointService;
    private final QueryIsLeaderService queryIsLeaderService;

    @GetMapping
    public ResponseEntity<List<UserInfoResponse>> queryUser(@RequestParam(name = "name") String name) {
        return ResponseEntity.ok(queryUserInfoService.queryUserInfo(name));
    }

    @GetMapping("/my-id")
    public ResponseEntity<UserIdResponse> queryUserId() {
        return ResponseEntity.ok(queryUserIdService.queryUserId());
    }

    @GetMapping("/follow-team-id")
    public ResponseEntity<UserFollowTeamIdResponse> queryFollowTeam() {
        return ResponseEntity.ok(queryUserFollowTeamService.queryUserFollowTeam());
    }

    @GetMapping("/my-point")
    public ResponseEntity<UserPointResponse> queryPoint() {
        return ResponseEntity.ok(queryUserPointService.queryUserPoint());
    }

    @GetMapping("/is-leader")
    public ResponseEntity<UserIsLeaderResponse> queryIsLeader() {
        return ResponseEntity.ok(queryIsLeaderService.queryIsLeader());
    }

}
