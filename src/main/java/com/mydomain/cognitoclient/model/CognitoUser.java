package com.mydomain.cognitoclient.model;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CognitoUser {

    private String username;

    private String userStatus;

    private Date createdDate;

    private Date modifiedDate;

    List<AttributeType>attributes;
}
