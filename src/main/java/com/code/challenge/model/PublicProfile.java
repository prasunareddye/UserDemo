package com.code.challenge.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PublicProfile {
    private String bio;
    private String nickname;


    public static PublicProfile convert(Profile profile)
    {
        PublicProfile publicProfile=new PublicProfile();
        publicProfile.setNickname(profile.getNickname());
        publicProfile.setBio(profile.getBio());
        return publicProfile;
    }
}
