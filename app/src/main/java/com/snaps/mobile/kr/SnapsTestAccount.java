package com.snaps.mobile.kr;

/**
 * Created by ysjeong on 2018. 1. 5..
 */

public class SnapsTestAccount {
    private String id, pw, userNo;

    public String getId() {
        return id;
    }

    String getPw() {
        return pw;
    }

    String getUserNo() {
        return userNo;
    }

    private SnapsTestAccount(Builder builder) {
        this.id = builder.id;
        this.pw = builder.pw;
        this.userNo = builder.userNo;
    }

    public static class Builder {
        private String id, pw, userNo;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        Builder setPw(String pw) {
            this.pw = pw;
            return this;
        }

        Builder setUserNo(String userNo) {
            this.userNo = userNo;
            return this;
        }

        public SnapsTestAccount create() {
            return new SnapsTestAccount(this);
        }
    }
}
