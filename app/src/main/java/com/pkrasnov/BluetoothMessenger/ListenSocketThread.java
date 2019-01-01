package com.pkrasnov.BluetoothMessenger;

import java.io.InputStream;

class ListenSocketThread extends Thread {
    private BluetoothMessenger bluetoothMessenger;
    private InputStream inputStream;

    ListenSocketThread(BluetoothMessenger messenger, InputStream inputStream) {
        this.bluetoothMessenger = messenger;
        this.inputStream = inputStream;
    }

    public void run() {
        while (true) {
            try {
                byte[] c = new byte[2048];
                int size = inputStream.read(c);
                String s = new String(c, 0, size);
                bluetoothMessenger.MessageReceived(s);
            } catch (Exception e) {
                bluetoothMessenger.ConnectionClosed();
                return;
            }
        }
    }
}
