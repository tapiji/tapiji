package org.eclipse.e4.tapiji.git.ui.preference;


import javax.inject.Inject;
import org.eclipse.e4.tapiji.git.core.api.IGitService;
import org.eclipse.e4.tapiji.git.model.GitServiceResult;
import org.eclipse.e4.tapiji.git.model.IGitServiceCallback;
import org.eclipse.e4.tapiji.git.model.UserProfile;
import org.eclipse.e4.tapiji.git.model.exception.GitServiceException;
import org.eclipse.e4.tapiji.logger.Log;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Shell;


public class PreferenceProfilePage extends FieldEditorPreferencePage {

    private String TAG = PreferenceAuthenticationPage.class.getSimpleName();
    private StringFieldEditor name;
    private StringFieldEditor email;

    @Inject
    IGitService service;

    @Inject
    UISynchronize sync;

    @Inject
    Shell shell;

    public PreferenceProfilePage() {
        super(GRID);
        setTitle("Profile");
    }

    @Override
    protected void createFieldEditors() {
        name = new StringFieldEditor("prefBoolean", "Name : ", getFieldEditorParent());
        email = new StringFieldEditor("prefString", "Email: ", getFieldEditorParent());

        addField(name);
        addField(email);
        loadProfile();
    }

    public void loadProfile() {
        service.profile(new IGitServiceCallback<UserProfile>() {

            @Override
            public void onSuccess(GitServiceResult<UserProfile> response) {
                sync.syncExec(() -> {
                    UserProfile profile = response.getResult();
                    if (profile.getName() != null) {
                        name.setStringValue(profile.getName());
                    }

                    if (profile.getEmail() != null) {
                        email.setStringValue(profile.getEmail());
                    }
                });
            }

            @Override
            public void onError(GitServiceException exception) {
                Log.d(TAG, "Can not load user profile!" + exception);
                MessageDialog.openError(shell, "Error: ", exception.getMessage());
            }
        });
    }

    public void saveProfile() {
        if (name != null && email != null) {
            service.saveProfile(new IGitServiceCallback<Void>() {

                @Override
                public void onSuccess(GitServiceResult<Void> response) {
                    Log.d(TAG, "User and email saved.");
                }

                @Override
                public void onError(GitServiceException exception) {
                    Log.d(TAG, "Can not save user profile!" + exception);
                    MessageDialog.openError(shell, "Error: ", exception.getMessage());
                }
            }, new UserProfile(name.getStringValue(), email.getStringValue()));
        }
    }

    @Override
    protected void performApply() {
        saveProfile();
        super.performApply();
    }
}
