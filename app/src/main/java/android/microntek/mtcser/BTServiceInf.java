package android.microntek.mtcser;

import android.os.IInterface;
import android.os.RemoteException;

import java.util.List;

public interface BTServiceInf extends IInterface {
    boolean IsConferenceCalling() throws RemoteException;

    boolean IsThreeWayCalling() throws RemoteException;

    void addCall() throws RemoteException;

    void answerCall() throws RemoteException;

    void avPlay() throws RemoteException;

    void avPlayNext() throws RemoteException;

    void avPlayPause() throws RemoteException;

    void avPlayPrev() throws RemoteException;

    void avPlayStop() throws RemoteException;

    void connectBT(String paramString) throws RemoteException;

    void connectOBD(String paramString) throws RemoteException;

    void deleteBT(String paramString) throws RemoteException;

    void deleteHistory(int paramInt) throws RemoteException;

    void deleteHistoryAll() throws RemoteException;

    void deleteOBD(String paramString) throws RemoteException;

    void dialOut(String paramString) throws RemoteException;

    void dialOutSub(char paramChar) throws RemoteException;

    void disconnectBT(String paramString) throws RemoteException;

    void disconnectOBD(String paramString) throws RemoteException;

    byte getAVState() throws RemoteException;

    boolean getAutoAnswer() throws RemoteException;

    boolean getAutoConnect() throws RemoteException;

    byte getBTState() throws RemoteException;

    String getCallInNum() throws RemoteException;

    List<String> getCallingNumberList() throws RemoteException;

    List<String> getDeviceList() throws RemoteException;

    String getDialOutNum() throws RemoteException;

    List<String> getHistoryList() throws RemoteException;

    List<String> getMatchList() throws RemoteException;

    String getModuleName() throws RemoteException;

    String getModulePassword() throws RemoteException;

    String getMusicInfo() throws RemoteException;

    long getNowDevAddr() throws RemoteException;

    String getNowDevName() throws RemoteException;

    List<String> getNowDevUuids() throws RemoteException;

    int getOBDstate() throws RemoteException;

    List<String> getPhoneBookList() throws RemoteException;

    String getPhoneNum() throws RemoteException;

    String getThreeWayCallNum() throws RemoteException;

    void hangupCall() throws RemoteException;

    void init() throws RemoteException;

    void mergeCall() throws RemoteException;

    void musicMute() throws RemoteException;

    void musicUnmute() throws RemoteException;

    void reDial() throws RemoteException;

    void rejectCall() throws RemoteException;

    void requestBtInfo() throws RemoteException;

    void scanStart() throws RemoteException;

    void scanStop() throws RemoteException;

    void setAutoAnswer(boolean paramBoolean) throws RemoteException;

    void setAutoConnect(boolean paramBoolean) throws RemoteException;

    void setModuleName(String paramString) throws RemoteException;

    void setModulePassword(String paramString) throws RemoteException;

    void setPhoneBookList(List<String> paramList) throws RemoteException;

    void swichCall() throws RemoteException;

    void switchVoice() throws RemoteException;

    void syncMatchList() throws RemoteException;

    void syncPhonebook() throws RemoteException;

    void voiceControl() throws RemoteException;
}