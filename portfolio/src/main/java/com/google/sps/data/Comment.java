package com.google.sps.data;

public final class Comment {
    private final long id;
    private final String message; 
    private final String email; 
    private final float score; 

    public Comment(long id, String message, String email, float score) {
        this.id = id;
        this.message = message; 
        this.email = email;
        this.score = score; 
    }
}
