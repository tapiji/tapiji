package org.eclipselabs.e4.tapiji.translator.ui.window;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Execute;


public class UpdateHistory {

    @Execute
    public void execute(@Named("changelog_file") String changeLogFile ) {
        URL url;
        try {
            url = new URL(changeLogFile);
            InputStream inputStream = url.openConnection().getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }

            in.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
