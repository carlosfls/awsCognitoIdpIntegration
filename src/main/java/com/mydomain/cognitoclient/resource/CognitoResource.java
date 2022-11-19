package com.mydomain.cognitoclient.resource;

import com.amazonaws.services.cognitoidp.model.UserPoolDescriptionType;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.mydomain.cognitoclient.cognito.CognitoService;
import com.mydomain.cognitoclient.model.SimpleCognitoUser;
import com.mydomain.cognitoclient.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cognito")
@RequiredArgsConstructor
public class CognitoResource {

    private final CognitoService cognitoService;

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody User user) throws Exception {
        var result = cognitoService.loginAdmin(
                user.getEmail(),
                user.getPassword(),
                user.getNuevaPassword());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/user/info/")
    public ResponseEntity<SimpleCognitoUser>getUserInfo(@RequestParam(name = "accessToken") String accessToken) throws Exception {
        return ResponseEntity.ok(cognitoService.getUserInfo(accessToken));
    }

    @GetMapping("/users/")
    public ResponseEntity<List<UserType>>listUsers(){
        var users = cognitoService.listUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/usersPool")
    public ResponseEntity<List<UserPoolDescriptionType>>listsUsersPools(){
        return ResponseEntity.ok(cognitoService.listUserPools());
    }


}
