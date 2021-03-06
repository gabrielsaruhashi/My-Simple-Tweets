package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by gabesaruhashi on 6/26/17.
 */

@Parcel
public class User {

    // list the attributes
    public String name;
    public long uid;
    public String screenName;
    public String profileImageUrl;

    // deserialize the JSON
    public static User fromJSON(JSONObject jsonObject) throws JSONException {
        User user = new User();

        user.name=jsonObject.getString("name");
        user.uid = jsonObject.getLong("id");
        user.screenName = jsonObject.getString("screen_name");
        String biggerImage = "bigger";
        // user.profileImageUrl = jsonObject.getString("profile_image_url")
        String tempo = jsonObject.getString("profile_image_url");
        user.profileImageUrl = tempo.replace("normal","bigger");


        return user;
    }

    User() {}

}
