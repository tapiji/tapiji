package org.eclipse.e4.tapiji.git.model.push;


public enum GitRemoteStatus {
    NOT_ATTEMPTED,
    UP_TO_DATE,
    REJECTED_NONFASTFORWARD,
    REJECTED_NODELETE,
    REJECTED_REMOTE_CHANGED,
    REJECTED_OTHER_REASON,
    NON_EXISTING,
    AWAITING_REPORT,
    OK

}
