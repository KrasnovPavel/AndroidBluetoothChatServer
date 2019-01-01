package com.pkrasnov.BluetoothMessenger;

import java.io.InputStream;

class ListenSocketThread extends Thread {
    private BluetoothMessengerServer bluetoothMessengerServer;
    private InputStream inputStream;

    ListenSocketThread(BluetoothMessengerServer messenger, InputStream inputStream) {
        this.bluetoothMessengerServer = messenger;
        this.inputStream = inputStream;
    }

    public void run() {
        while (true) {
            try {
                byte[] c = new byte[2048];
                int size = inputStream.read(c);
                String s = new String(c, 0, size);
                bluetoothMessengerServer.MessageReceived(s);
            } catch (Exception e) {
                bluetoothMessengerServer.ConnectionClosed();
                return;
            }
        }
    }
}
