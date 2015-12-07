package com.plan_it.mobile.plan_it;

/**
 * Created by Kristian on 06/12/2015.
 */

enum MemberStatus {INVITED, ATTENDING, DECLINED, LEFT, OWNER}
public class Members {

    int memberId;
    String memberName;
    MemberStatus status;
    boolean itemList;
    boolean messageBoard;


    Members(int memberId, String memberName,MemberStatus status, boolean itemList, boolean messageBoard){
        this.memberId = memberId;
        this.memberName = memberName;
        this.status = status;
        this.itemList = itemList;
        this.messageBoard = messageBoard;
    }

}
