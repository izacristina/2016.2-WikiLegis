package gppmds.wikilegis.controller;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

import gppmds.wikilegis.dao.PostRequest;
import gppmds.wikilegis.exception.UserException;
import gppmds.wikilegis.model.User;


public class RegisterUserController {

    private static RegisterUserController instance = null;
    private final Context context;

    private RegisterUserController(final Context contextParameter) {
        this.context = contextParameter;
    }

    public static RegisterUserController getInstance(final Context context) {
        if (instance == null) {
            instance = new RegisterUserController(context);
        } else {
			/* ! Nothing To Do. */
        }
        return instance;
    }

    public String registerUser(final String firstName,
                               final String lastName,
                               final String email,
                               final String password,
                               final String passwordConfirmation) {

        try {

            User user = new User(firstName, lastName, email, password, passwordConfirmation);
            JSONObject userJson = setJSON(user);
            PostRequest postRequest = new PostRequest(context,"http://wikilegis-staging.labhackercd.net/api/user/create/");

            try {
                String responseInformation = postRequest.execute(userJson.toString(),
                        "application/json").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            Log.d("Response", postRequest.getResponse() + "");

            return String.valueOf(postRequest.getResponse());

        } catch (UserException e) {
            String exceptionMessage = e.getMessage();
            return exceptionMessage;
        } catch (JSONException e) {
            String exceptionMessage = e.getMessage();
            return exceptionMessage;
        }
    }
    private JSONObject setJSON(User user) throws JSONException {
        JSONObject jsonParam = new JSONObject();
        jsonParam.put("email", user.getEmail());
        jsonParam.put("first_name", user.getFirstName());
        jsonParam.put("last_name", user.getLastName());
        jsonParam.put("password", user.getPassword());
        return jsonParam;
    }
}
