package com.company.testcamera.web.camera;

public class GetSyncPipe {

    public SyncPipe syncPipe = null;

    public SyncPipe getSyncPipe() {
        return syncPipe;
    }

    public void setSyncPipe(SyncPipe syncPipe) {
        this.syncPipe = syncPipe;
    }

    public void setSyncPipeNull() {
        this.syncPipe = null;
    }
}