package com.mydomain.cognitoclient.model;

import com.amazonaws.services.cognitoidp.model.AttributeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SimpleCognitoUser {

    private String username;

    List<AttributeType>attributes;
}
