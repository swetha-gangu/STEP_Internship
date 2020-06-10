// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/data")
public class DataServlet extends HttpServlet {
    private static final String COMMENT = "comment";
    private static final String EMAIL = "email";
    private static final String ENTITY_TYPE = "Comment";
    private static final String MAX_COMMENTS = "max_comments";

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int maxComments = getMaxComments(request);
        Query query = new Query(ENTITY_TYPE); 
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        List<Entity> results = datastore.prepare(query).
            asList(FetchOptions.Builder.withLimit(maxComments));
        List<Comment> comments = new ArrayList<>();

        for (Entity entity : results) {
            long id = entity.getKey().getId();
            String message = (String) entity.getProperty(COMMENT);
            String email = (String) entity.getProperty(EMAIL); 
            comments.add(new Comment(id, message, email));
        }

        response.setContentType("application/json;");
        response.getWriter().println(new Gson().toJson(comments));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
        UserService userService = UserServiceFactory.getUserService();
        String userEmail = ""; 
        if (userService.isUserLoggedIn()) {
            userEmail = userService.getCurrentUser().getEmail();
        } else {
            System.err.println("User is not logged in");
            return;
        }

        Entity commentEntity = new Entity(ENTITY_TYPE);
        commentEntity.setProperty(COMMENT, request.getParameter(COMMENT));
        commentEntity.setProperty(EMAIL, userEmail);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(commentEntity);
        response.sendRedirect("/comments.html");
    }

    private int getMaxComments(HttpServletRequest request) {
        String maxCommentsString = request.getParameter("max_comments");
        // Convert the input to an int.
        int maxComments;
        try {
            maxComments = Integer.parseInt(maxCommentsString);
        } catch (NumberFormatException e) {
            System.err.println("Could not convert to int: " + maxCommentsString);
            return -1;
        }

        // Check that the input is non negative.
        if (maxComments < 0) {
            System.err.println("Number of comments is out of range: " + maxCommentsString);
            return -1;
        }

        return maxComments;
    }
}
