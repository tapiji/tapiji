package org.eclipse.e4.tapiji.git.ui.preference;


import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.Reference;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;


public class PreferenceAuthenticationPage extends FieldEditorPreferencePage {

    private String TAG = PreferenceAuthenticationPage.class.getSimpleName();

    @Inject
    IGitService service;

    private StringFieldEditor profileName;
    private StringFieldEditor name;
    private StringFieldEditor email;

    @Override
    protected void createFieldEditors() {
        profileName = new StringFieldEditor("prefColor", "Profile Name: ", getFieldEditorParent());
        name = new StringFieldEditor("prefBoolean", "Name : ", getFieldEditorParent());
        email = new StringFieldEditor("prefString", "Email: ", getFieldEditorParent());

        addField(profileName);
        addField(name);
        addField(email);
    }

    @PostConstruct
    public void onCreate() {
        service.branches(new IGitServiceCallback<List<Reference>>() {

            @Override
            public void onSuccess(GitServiceResult<List<Reference>> response) {
                Log.d(TAG, "" + response);

            }

            @Override
            public void onError(GitServiceException exception) {

            }
        });
    }

}
