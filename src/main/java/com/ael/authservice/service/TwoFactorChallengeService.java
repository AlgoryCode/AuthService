package com.ael.authservice.service;

import com.ael.authservice.repository.TwoFactorChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TwoFactorChallengeService {
    private final TwoFactorChallengeRepository twoFactorChallengeRepository;


//    public TwoFactorChallengeResponse createTwoFactorChallenge(){
//
//    }
}
