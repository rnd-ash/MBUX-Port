package android.microntek;

import android.microntek.ICarManageCallback.Stub;
import android.os.Bundle;
import android.os.RemoteException;

public class CarManageCallback extends Stub {
    public void onStatusChanged(String type, Bundle bundle) throws RemoteException {
    }
}