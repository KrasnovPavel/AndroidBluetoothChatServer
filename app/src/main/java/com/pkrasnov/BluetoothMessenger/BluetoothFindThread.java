package com.pkrasnov.BluetoothMessenger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

import static android.content.ContentValues.TAG;

class BluetoothFindThread extends Thread {
    private BluetoothServerSocket mmServerSocket;
    private final String name = BluetoothAdapter.getDefaultAdapter().getName();
    private BluetoothMessengerServer bluetoothMessengerServer;

    BluetoothFindThread(BluetoothMessengerServer messenger, UUID uuid) throws IOException {
        this.bluetoothMessengerServer = messenger;
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);
    }

    public void run() {
        // Keep listening until exception occurs or a socket is returned.
        while (true) {
            try {
                BluetoothSocket socket = mmServerSocket.accept();
                if (socket != null) {
                    bluetoothMessengerServer.ClientConnected(socket);
                    break;
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket's accept() method failed", e);
                break;
            }
        }
    }

    // Closes the connect socket and causes the thread to finish.
    public void cancel() {
        try {
            mmServerSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }
}