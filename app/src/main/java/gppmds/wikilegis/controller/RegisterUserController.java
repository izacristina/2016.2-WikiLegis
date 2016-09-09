package gppmds.wikilegis.controller;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import gppmds.wikilegis.dao.GetRequest;
import gppmds.wikilegis.exception.UserException;
import gppmds.wikilegis.model.User;


public class RegisterUserController {
    private static RegisterUserController instance = null;
    private final Context context;

    private RegisterUserController(Context context) {
        this.context = context;
    }

    public static RegisterUserController getInstance(Context context) {
        if (instance == null) {
            instance = new RegisterUserController(context);
        } else {
			/* ! Nothing To Do. */
        }
        return instance;
    }

    public String registerUser(String firstName,
                               String lastName,
                               String email,
                               String password,
                               String passwordConfirmation) {

        try {

            User user = new User(firstName, lastName, email, password, passwordConfirmation);

            return "SUCESS";

        } catch (UserException e) {
            String exceptionMessage = e.getMessage();
            return exceptionMessage;

        }
    }


    /**
     * Log D the users
     */
    public static String getUsersExemple() {
        final String URL = "http://wikilegis.labhackercd.net/api/segment_types/";
        String getApi = null;

        GetRequest request = new GetRequest();

        request.execute(URL);

        try {
            getApi = request.get().toString();
        } catch (ExecutionException e){
            //Não faço ideia do que fazer
        } catch (InterruptedException e){
            //Não faço ideia do que fazer
        }
        return getApi;
    }
}
