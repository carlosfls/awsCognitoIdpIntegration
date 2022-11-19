package com.mydomain.cognitoclient.cognito;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.mydomain.cognitoclient.model.SimpleCognitoUser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CognitoService {

    private final Log logger = LogFactory.getLog(CognitoService.class);

    private final AWSCognitoIdentityProvider client;

    private static final String NEW_PASS_REQUIRED = "NEW_PASSWORD_REQUIRED";

    @Value("${clientId}")
    private String clientId;

    @Value("${userPoolId}")
    private String userPool;

    public Map<String,String> loginAdmin(String email,String password,String nuevaPassword) throws Exception {
        AuthenticationResultType resultType = null;
        Map<String,String> authParams = new HashMap<>();
        authParams.put("USERNAME",email);
        authParams.put("PASSWORD",password);

        logger.info("Initialize the request..");
        var adminAuthRequest = new AdminInitiateAuthRequest()
                            .withAuthFlow(AuthFlowType.ADMIN_USER_PASSWORD_AUTH)
                            .withUserPoolId(userPool)
                            .withClientId(clientId)
                            .withAuthParameters(authParams);

        logger.info("Getting the result..");
        var adminAuthResult = client.adminInitiateAuth(adminAuthRequest);

        logger.info("looking for challenges...");
        if(adminAuthResult.getChallengeName() != null && !adminAuthResult.getChallengeName().isEmpty()){
            logger.info("Challenge detected!!");
            if(NEW_PASS_REQUIRED.equals(adminAuthResult.getChallengeName())){
                resultType = this.validateChallengeFirstLogin(adminAuthResult,email,password,nuevaPassword);
            }
        }else{
            resultType = adminAuthResult.getAuthenticationResult();
        }
        logger.info("Getting the tokens..");
        Map<String,String> result = new HashMap<>();
        result.put("idToken",resultType.getIdToken());
        result.put("accessToken",resultType.getAccessToken());
        result.put("refreshToken",resultType.getRefreshToken());
        result.put("message","Successfully Login");

        return result;
    }

    public SimpleCognitoUser getUserInfo(String accessToken){
        var getUserRequest = new GetUserRequest().withAccessToken(accessToken);
        var getUserResult = client.getUser(getUserRequest);

        var simpleCognitoUser = new SimpleCognitoUser();

        simpleCognitoUser.setUsername(getUserResult.getUsername());
        simpleCognitoUser.setAttributes(getUserResult.getUserAttributes());

        return simpleCognitoUser;
    }

    public List<UserType>listUsers(){
        var listUsersRequest = new ListUsersRequest()
                .withUserPoolId(userPool)
                .withLimit(10);

        var listUsersResponse = client.listUsers(listUsersRequest);

        if(listUsersResponse !=null){
            return listUsersResponse.getUsers();
        }
        return new ArrayList<>();
    }

    private AuthenticationResultType validateChallengeFirstLogin(AdminInitiateAuthResult result,String email,String password,String nuevaPassword) throws Exception {
        Map<String, String> challengeResponses = new HashMap<>();
        if(nuevaPassword == null){
            logger.info("error challenge aborted..");
            throw new Exception("The challenges required a new Password, the password cant be null");
        }
        logger.info("Executing the new Password challenge");
        challengeResponses.put("USERNAME",email);
        challengeResponses.put("PASSWORD",password);
        challengeResponses.put("NEW_PASSWORD",nuevaPassword);

        var adminChallengeRequest = new AdminRespondToAuthChallengeRequest()
                .withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                .withChallengeResponses(challengeResponses)
                .withClientId(clientId)
                .withUserPoolId(userPool)
                .withSession(result.getSession());

        var adminChallengeResult = client.adminRespondToAuthChallenge(adminChallengeRequest);

        return adminChallengeResult.getAuthenticationResult();
    }



    public SignUpResult signUp(String name,String email,String password){
        var request = new SignUpRequest()
                .withClientId(clientId)
                .withUsername(email)
                .withPassword(password);

        return client.signUp(request);
    }

    public ConfirmSignUpResult confirmSignUp(String email,String confirmationCode){
        var confirmRequest = new ConfirmSignUpRequest()
                            .withClientId(clientId)
                            .withUsername(email)
                            .withConfirmationCode(confirmationCode);

        return client.confirmSignUp(confirmRequest);
    }

    public ChangePasswordResult changePassword(String accessToken,String oldPassword,String nuevaPassword){
        var changePassRequest = new ChangePasswordRequest()
                .withAccessToken(accessToken)
                .withPreviousPassword(oldPassword)
                .withProposedPassword(nuevaPassword);

        return  client.changePassword(changePassRequest);
    }

    public ForgotPasswordResult forgotPassword(String username) {
        var request = new ForgotPasswordRequest()
                .withClientId(clientId)
                .withUsername(username);

        var result = client.forgotPassword(request);
        return result;
    }

    public ResendConfirmationCodeResult resendConfirmationCode(String username) {
        var codeRequest = new ResendConfirmationCodeRequest()
                .withClientId(clientId)
                .withUsername(username);
        var codeResponse = client.resendConfirmationCode(codeRequest);

        return codeResponse;
    }

    public List<UserPoolDescriptionType>listUserPools(){
        var request = new ListUserPoolsRequest()
                .withMaxResults(10);

        var response = client.listUserPools(request);
        return response.getUserPools();
    }

    public Map<String,String> createUserPool(String userPoolName){
        var request = new CreateUserPoolRequest()
                .withPoolName(userPoolName);

        var response = client.createUserPool(request);

        logger.info(response.getUserPool().getId());

        var userPool = response.getUserPool();

        Map<String,String>dataUserPool = new HashMap<>();
        dataUserPool.put("Id",userPool.getId());
        dataUserPool.put("status",userPool.getStatus());
        dataUserPool.put("domain",userPool.getDomain());

        return dataUserPool;
    }

    public Map<String,String> createAppClient(String clientName){
        var request = new CreateUserPoolClientRequest()
                .withUserPoolId(userPool)
                .withClientName(clientName)
                .withExplicitAuthFlows(
                        ExplicitAuthFlowsType.ADMIN_NO_SRP_AUTH,
                        ExplicitAuthFlowsType.ALLOW_REFRESH_TOKEN_AUTH,
                        ExplicitAuthFlowsType.ALLOW_ADMIN_USER_PASSWORD_AUTH,
                        ExplicitAuthFlowsType.USER_PASSWORD_AUTH,
                        ExplicitAuthFlowsType.ALLOW_USER_SRP_AUTH
                )
                .withAllowedOAuthFlows(OAuthFlowType.Client_credentials)
                .withGenerateSecret(true);

        var response = client.createUserPoolClient(request);

        var appClient = response.getUserPoolClient();

        logger.info(appClient.getClientId());

        Map<String,String> clientData = new HashMap<>();

        clientData.put("client_id",appClient.getClientId());
        clientData.put("client_secret",appClient.getClientSecret());
        clientData.put("creation_date",appClient.getCreationDate().toString());

        return clientData;
    }

    public void deleteAppClient(String clientId){
        var deleteClientRequest = new DeleteUserPoolClientRequest()
                .withClientId(clientId)
                .withUserPoolId(userPool);
        client.deleteUserPoolClient(deleteClientRequest);
    }


    public List<ResourceServerType>listResourceServers(){
        var request = new ListResourceServersRequest()
                .withMaxResults(50)
                .withUserPoolId(userPool);

        var response = client.listResourceServers(request);
        return response.getResourceServers();
    }

    public List<UserPoolClientDescription>listUserPoolClients(){
        var request = new ListUserPoolClientsRequest()
                .withMaxResults(50)
                .withUserPoolId(userPool);
        var response = client.listUserPoolClients(request);

        return response.getUserPoolClients();
    }


}
