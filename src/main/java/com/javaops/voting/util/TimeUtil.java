package com.javaops.voting.util;

import com.javaops.voting.util.exception.LateToVoteException;

import java.time.LocalTime;

public class TimeUtil {

    public static int hourLimitForVote = 10; // condition check + 1 hour

    public static void checkTime() {
        if (LocalTime.now().getHour() > hourLimitForVote) throw new LateToVoteException("You can't vote after 11am");
    }
}
