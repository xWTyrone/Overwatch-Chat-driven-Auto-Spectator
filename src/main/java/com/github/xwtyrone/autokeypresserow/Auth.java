package com.github.xwtyrone.autokeypresserow;


import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.youtube.YouTubeScopes;


import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Created by pauloafecto on 28/02/2017.
 */
public class Auth {

    // Base template for this class is the Youtube API samples class with the same name
    // All rights reserved to its owners under their licenses

    // Do not instantiate this class
    private Auth() {

    }

    public static final NetHttpTransport HTTP_TRANSPORT = getTrustedTransport();

    public static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";

    private static GoogleClientSecrets clientSecrets = getClientSecrets();

    public static final FileDataStoreFactory dataStoreFactory = getDataStoreFactoryDefault();

    private static NetHttpTransport getTrustedTransport() {
        try {
            return GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;

    }

    private static FileDataStoreFactory getDataStoreFactoryDefault() {
        try {
            return new FileDataStoreFactory(new File(System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        throw new InternalError();

    }

    private static GoogleClientSecrets getClientSecrets() {
        try {
            return GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(Auth.class.getResourceAsStream("/client_secrets.json")));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static Credential authorize() {
        GoogleAuthorizationCodeFlow flow = null;
        try {
            flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Collections.singleton(YouTubeScopes.YOUTUBE)).setDataStoreFactory(dataStoreFactory).build();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
