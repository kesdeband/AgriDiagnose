package com.agrinett.agridiagnose.broadcasts;

public interface INetworkListener {
    int TYPE_CONNECTED = 0;
    int TYPE_NOT_CONNECTED = -1;
    int TYPE_NOT_SYNC = -2;

    void onNetworkStatusChanged(int status);
}
